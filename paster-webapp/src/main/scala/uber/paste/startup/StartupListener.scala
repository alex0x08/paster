package uber.paste.startup


import java.util.Calendar
import java.util.Properties
import javax.servlet.{ServletContextListener, ServletContext, ServletContextEvent}
import org.apache.commons.csv.CSVFormat
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import uber.paste.manager.UserManagerImpl
import uber.paste.model._
import uber.paste.dao._
import java.io.IOException
import uber.paste.dao.UserExistsException
import uber.paste.base.{MergedPropertyConfigurer, Loggered, SystemInfo}
import java.io.InputStreamReader
import java.nio.file._
import scala.collection.JavaConversions._

class StartupListener extends ServletContextListener with Loggered{
  
  override def contextInitialized( event:ServletContextEvent) {

    val context:ServletContext = event.getServletContext()

    val  ctx:ApplicationContext =
      WebApplicationContextUtils.getRequiredWebApplicationContext(context)

   
    var props:MergedPropertyConfigurer =  ctx.getBean("propertyConfigurer-app")
                                              .asInstanceOf[MergedPropertyConfigurer]

    val systemInfo:SystemInfo = ctx.getBean(classOf[SystemInfo])

      
    
    val configDao:ConfigDaoImpl = ctx.getBean(classOf[ConfigDaoImpl])
    val pasteDao:PasteDaoImpl = ctx.getBean(classOf[PasteDaoImpl])
    val projectDao:ProjectDaoImpl = ctx.getBean(classOf[ProjectDaoImpl])

    
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
    

    if (configDao.isPropertySet(ConfigProperty.IS_INSTALLED.getCode, "1")) {

      systemInfo.setDateInstall({
        val installDate=configDao.getProperty(ConfigProperty.INSTALL_DATE)
        if (installDate!=null && installDate.getValue != null) {
          new java.util.Date(java.lang.Long.valueOf(installDate.getValue))
        } else 
          null        
        })

      
      val dbVersion = new AppVersion().fillFromConfigProperty(
        configDao.getProperty(ConfigProperty.APP_VERSION.getCode))
      
           
            if (dbVersion != null) {
      
              logger.debug("__currentSettings:" + dbVersion.getFull)
                
                val check =  systemInfo.getRuntimeVersion().compareTo(dbVersion)
                
                check match {
           
                    case 0 => {
                        logger.info("Application and db versions match.")
                      }
                    case 1 => {
                        logger.warn("DB version is older than application: {} | {} . You can get problems.",
                                    Array(dbVersion.getFull(),
                                          systemInfo.getRuntimeVersion().getFull()))
                    }
                    case -1 =>   {
                        logger.warn("Application version is older than database: {} | {}. You can get problems.",
                                    Array(dbVersion.getFull(),systemInfo.getRuntimeVersion().getFull()))
                    }
                     
                    case _ => {
                    logger.error("Uncomparable db and application versios: {} | {}. Cannot continue.",
                                 Array(dbVersion.getFull(),systemInfo.getRuntimeVersion.getFull))
                    return
                    }    
                        
                }
                                
                
                 logger.info("Loading application ver. {} ,db ver. {} " , 
                             Array(systemInfo.getRuntimeVersion().getFull(),dbVersion.getFull()))
               
            }
      
      
      logger.info("Database already created. skipping db generation stage..")

        systemInfo.setProject(projectDao.getLast)
        
      reindex(ctx,props)


      

      for(p<-Priority.list) {
        
      //  PasteManager.Stats.valueOf(p.getCode).increment(pasteDao.countAll(p).toInt)
        
      }

      systemInfo.setDateStart(Calendar.getInstance.getTime)

      logger.info("completed")
     return
    }


    val users:UserManagerImpl = ctx.getBean(classOf[UserManagerImpl])


    try {

      setupSecurityContext
      
      val r = new InputStreamReader(getClass().getResourceAsStream("/default_users.csv"))
      
      val records = CSVFormat.DEFAULT.withHeader().parse(r)
      
      for (record <- records) {
        
         users.save(users.changePassword(
           User.createNew
        .addRole(if (record.get("ADMIN").toBoolean) 
                  {Role.ROLE_ADMIN} 
                  else {Role.ROLE_USER})
        .addUsername(record.get("USERNAME"))
        .addPassword(record.get("PASSWORD"))
        .addName(record.get("NAME"))
        .get(),record.get("PASSWORD")))

      }
      
      r.close
      
   

      val project = new Project
      project.setName("Sample project")
      project.setDescription("Full project description")
      
      projectDao.persist(project)
        
      configDao.persist(ConfigProperty.IS_INSTALLED)
      
      val installDate = Calendar.getInstance.getTime.getTime
      ConfigProperty.INSTALL_DATE.setValue(installDate+"")
      configDao.persist(ConfigProperty.INSTALL_DATE)
 
      systemInfo.setDateInstall(new java.util.Date(installDate))
      
      ConfigProperty.APP_VERSION.setValue(systemInfo.getRuntimeVersion.toDbString)
      configDao.persist(ConfigProperty.APP_VERSION)

      logger.debug("saved version {}",ConfigProperty.APP_VERSION.getValue)

      reindex(ctx,props)
   
      logger.info("db generation completed successfully.")

        

        systemInfo.setDateStart(Calendar.getInstance.getTime)

      
    } catch {
      case e @ (_ : UserExistsException | _ : java.io.IOException) => {
          logger.error(e.getLocalizedMessage,e)

        }
    }  
   
  }

  override def contextDestroyed(servletContextEvent:ServletContextEvent) { }
  
  
  def setupSecurityContext() {
    val start_user = User.createNew
    .addRole(Role.ROLE_ADMIN)
    .addUsername("start")
    .addName("Initial scheme creator").get
    
    
            // log user in automatically
            val auth = new UsernamePasswordAuthenticationToken(
                    "start", "start", start_user.getAuthorities())
            auth.setDetails(start_user)
            SecurityContextHolder.getContext().setAuthentication(auth);
  }

  def reindex( ctx:ApplicationContext,props:MergedPropertyConfigurer):Unit = {
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
