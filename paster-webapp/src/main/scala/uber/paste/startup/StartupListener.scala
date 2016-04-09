package uber.paste.startup


import java.util.Calendar
import java.util.Properties
import javax.servlet.{ServletContextListener, ServletContext, ServletContextEvent}
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import uber.paste.manager.UserManagerImpl
import uber.paste.model._
import uber.paste.dao._
import java.io.IOException
import uber.paste.dao.UserExistsException
import uber.paste.base.{MergedPropertyConfigurer, Loggered, SystemInfo, AppProfile}
import java.io.InputStreamReader
import java.nio.file._
import scala.collection.JavaConversions._


class BootContext(ctx:ApplicationContext) {
  
  val systemInfo:SystemInfo = ctx.getBean(classOf[SystemInfo])
  val configDao:ConfigDaoImpl = ctx.getBean(classOf[ConfigDaoImpl])
  val pasteDao:PasteDaoImpl = ctx.getBean(classOf[PasteDaoImpl])
  val projectDao:ProjectDaoImpl = ctx.getBean(classOf[ProjectDaoImpl])
  val channelDao:ChannelDao = ctx.getBean(classOf[ChannelDao])
  
  val users:UserManagerImpl = ctx.getBean(classOf[UserManagerImpl])

  
  val props:MergedPropertyConfigurer = ctx.getBean("propertyConfigurer-app")
                                              .asInstanceOf[MergedPropertyConfigurer]
}

class StartupListener extends ServletContextListener with Loggered{
  
  override def contextInitialized( event:ServletContextEvent) {
   
   
    val bootContext = new BootContext(WebApplicationContextUtils
                                      .getRequiredWebApplicationContext(event.getServletContext()))
    
   

     /*PluginUI.load(getClass().getResourceAsStream("/paster-ui-definitions.xml"))
      
      val pluginDefs=  
        ctx.getResources("classpath*:META-INF/resources/paster-ui-definition.xml")
      
    if (pluginDefs!=null && pluginDefs.length>0) {
        logger.info(String.format("Found %s plugin definitions",pluginDefs.length+""))
         for (d<-pluginDefs) {
           logger.info("loading "+d.getURL)
           PluginUI.append(d.getURL)
         }
      }
      
      if (logger.isDebugEnabled) {
        logger.debug(PluginUI.getXml)
      }
     
    */
    

    if (bootContext.configDao.isPropertySet(ConfigProperty.IS_INSTALLED.getCode, "1")) {

        bootInstalled(bootContext)
      
       bootContext.systemInfo.doLock
         
      logger.info("completed")
     return
    }


    try {

      setupSecurityContext // setup security context
      
      // loading default users
      loadDefaults("/defaultData/default_users.csv", (record:CSVRecord) => {
             bootContext.users.save(bootContext.users.changePassword(
           User.createNew
        .addRole(if (record.get("ADMIN").toBoolean) 
                  {Role.ROLE_ADMIN} 
                  else {Role.ROLE_USER})
        .addUsername(record.get("USERNAME"))
        .addPassword(record.get("PASSWORD"))
        .addName(record.get("NAME"))
        .get(),record.get("PASSWORD")))
        })
      
     
       loadDefaults("/defaultData/default_channels.csv", (record:CSVRecord) => {           
          
        val ch =new Channel(
              record.get("CODE"),
              record.get("DESC"),
              record.get("ISDEFAULT").toBoolean)
            ch.setTranslated(true)
            bootContext.channelDao.save(ch)
        })

      val project = new Project
      project.setName("Sample project")
      project.setDescription("Full project description")
      
      bootContext.projectDao.persist(project)
        
      bootContext.configDao.persist(ConfigProperty.IS_INSTALLED)
      
      val installDate = Calendar.getInstance.getTime.getTime
      ConfigProperty.INSTALL_DATE.setValue(installDate+"")
      bootContext.configDao.persist(ConfigProperty.INSTALL_DATE)
 
      bootContext.systemInfo.setDateInstall(new java.util.Date(installDate))
      
      ConfigProperty.APP_VERSION.setValue(bootContext.systemInfo.getRuntimeVersion.toDbString)
      bootContext.configDao.persist(ConfigProperty.APP_VERSION)

      logger.debug("saved version {}",ConfigProperty.APP_VERSION.getValue)

      reindex(bootContext.props)
   
      logger.info("db generation completed successfully.")

          val appProfile = bootContext.props.getProperty("build.profile")
    
      bootContext.systemInfo.setAppProfile(AppProfile.valueOf(appProfile))
    

        bootContext.systemInfo.setDateStart(Calendar.getInstance.getTime)

        bootContext.systemInfo.doLock
      
    } catch {
      case e @ (_ : UserExistsException | _ : java.io.IOException) => {
          logger.error(e.getLocalizedMessage,e)
          throw e; // to stop application
        }
    }  
   
  }

  override def contextDestroyed(servletContextEvent:ServletContextEvent) { 
    // not used
 }
 
  def bootInstalled(ctx:BootContext) {
    
      ctx.systemInfo.setDateInstall({
        val installDate=ctx.configDao.getProperty(ConfigProperty.INSTALL_DATE)
        if (installDate!=null && installDate.getValue != null) {
          new java.util.Date(java.lang.Long.valueOf(installDate.getValue))
        } else 
          null        
        })

      
      val appProfile = ctx.props.getProperty("build.profile")
    
      ctx.systemInfo.setAppProfile(AppProfile.valueOf(appProfile))
    
      val dbVersion = new AppVersion().fillFromConfigProperty(
        ctx.configDao.getProperty(ConfigProperty.APP_VERSION.getCode))
      
           
            if (dbVersion != null) {
      
              logger.debug("current version: {}" , dbVersion.getFull)
                
                val check =  ctx.systemInfo.getRuntimeVersion().compareTo(dbVersion)
                
                check match {
           
                    case 0 => {
                        logger.info("Application and db versions match.")
                      }
                    case 1 => {
                        logger.warn("DB version is older than application: {} | {} . You can get problems.",
                                    Array(dbVersion.getFull(),
                                          ctx.systemInfo.getRuntimeVersion().getFull()))
                    }
                    case -1 =>   {
                        logger.warn("Application version is older than database: {} | {}. You can get problems.",
                                    Array(dbVersion.getFull(),ctx.systemInfo.getRuntimeVersion().getFull()))
                    }
                     
                    case _ => {
                    logger.error("Uncomparable db and application versios: {} | {}. Cannot continue.",
                                 Array(dbVersion.getFull(),ctx.systemInfo.getRuntimeVersion.getFull))
                    return
                    }    
                        
                }
                                
                
                 logger.info("Loading application ver. {} ,db ver. {} " , 
                             Array(ctx.systemInfo.getRuntimeVersion().getFull(),dbVersion.getFull()))
               
            }
      
      
      logger.info("Database already created. skipping db generation stage..")

        ctx.systemInfo.setProject(ctx.projectDao.getLast)
        
      reindex(ctx.props)


      ctx.systemInfo.setDateStart(Calendar.getInstance.getTime)
    
  }
  
  
  def loadDefaults(csv:String, callback: CSVRecord => Unit) {
    
     val r = new InputStreamReader(getClass().getResourceAsStream(csv))
      try {
      val records = CSVFormat.DEFAULT.withHeader().parse(r)
      
      for (record <- records) {
        callback(record)
      }
    } finally {
      r.close
    }
    
   
  }
  
  def setupSecurityContext() {
    
    val start_user = User.createNew
                      .addRole(Role.ROLE_ADMIN)
                      .addUsername("start")
                      .addName("Initial scheme creator")
                      .get
    
    
            // log user in automatically
            val auth = new UsernamePasswordAuthenticationToken(
                    "start", "start", start_user.getAuthorities())
            auth.setDetails(start_user)
            
            SecurityContextHolder.getContext().setAuthentication(auth)
  }

  def reindex( props:MergedPropertyConfigurer) {
    
    if (props.getProperty("config.reindex.enabled").equals("1")) {
      
      for (d<-SearchableDaoImpl.searchableDao) {        
        d.indexAll()
      }
     
      logger.info("reindex completed.")
    } else {
      logger.info("reindex was disabled. skipping it.")
    }

  }



}
