/**
 * Copyright (C) 2010 alex <me@alex.0x08.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.startup


import java.util.Calendar
import javax.servlet.{ServletContextListener, ServletContext, ServletContextEvent}
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils
import uber.paste.model._
import uber.paste.dao._
import uber.paste.build._
import java.io.IOException
import uber.paste.dao.UserExistsException
import uber.paste.base.{MergedPropertyConfigurer, Loggered, SystemInfo}
import uber.paste.build._
import java.nio.file._
import uber.paste.manager.PasteManager
import scala.collection.JavaConversions._

/*
class AdvEventLoadListener extends DefaultPostLoadEventListener with Loggered{


  override def onPostLoad(p1: PostLoadEvent) {
    logger.debug("_on Load call for class "+p1.getEntity.getClass.getName)


    super.onPostLoad(p1)

    if (p1.getEntity.isInstanceOf[Paste]==false) {
        return
    }

      val obj:Paste =p1.getEntity.asInstanceOf[Paste]

      obj.setTagsAsString({
        val out =new StringBuilder
        for (s<-obj.getTags()) {
          out.append(s).append(" ")
        }
        out.toString })

      logger.debug("filled tagsAsString "+obj.getTagsAsString())
    }

}
*/
class StartupListener extends ServletContextListener with Loggered{
  
  override def contextInitialized( event:ServletContextEvent): Unit = {

    val context:ServletContext = event.getServletContext()

    val  ctx:ApplicationContext =
      WebApplicationContextUtils.getRequiredWebApplicationContext(context)

  /*  val sf:SessionFactory = ctx.getBean("sessionFactory").asInstanceOf[SessionFactory]

    val listeners:Array[PostLoadEventListener] = new Array(1)
    //listeners(0) =new DefaultPostLoadEventListener
    listeners(0) =new AdvEventLoadListener()


    sf.asInstanceOf[SessionFactoryImpl].getEventListeners.setPostLoadEventListeners(listeners)
    */

    //System.setProperty ("jsse.enableSNIExtension", "false")
    
    var props:MergedPropertyConfigurer =  ctx.getBean("propertyConfigurer")
                                              .asInstanceOf[MergedPropertyConfigurer]


    val configDao:ConfigDao = ctx.getBean("configDao").asInstanceOf[ConfigDao]
    val pasteDao:PasteDao = ctx.getBean("pasteDao").asInstanceOf[PasteDao]
    val projectDao:ProjectDao = ctx.getBean("projectDao").asInstanceOf[ProjectDao]

    
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

      SystemInfo.instance.setDateInstall({
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
                
                val check =  SystemInfo.instance.getRuntimeVersion().compareTo(dbVersion)
                
                check match {
           
                    case 0 => {
                        logger.info("Application and db versions match.")
                      }
                    case 1 => {
                        logger.warn("DB version is older than application: "
                                +dbVersion.getFull()+" | "+SystemInfo.instance.getRuntimeVersion().getFull()+". You can get problems.")
                    }
                    case -1 =>   {
                        logger.warn("Application version is older than database: "
                                +dbVersion.getFull()+" | "+SystemInfo.instance.getRuntimeVersion().getFull()+". You can get problems.")
                    }
                     
                    case _ => {
                    logger.error("Uncomparable db and application versios: "
                             +dbVersion.getFull()+" | "+SystemInfo.instance.getRuntimeVersion().getFull()+". Cannot continue.")
                    }    
                        
                }
                                
                
                 logger.info("Loading application ver. "+SystemInfo.instance.getRuntimeVersion().getFull()
                             +" db ver. "+dbVersion.getFull())
               
            }
      
      
      logger.info("Database already created. skipping db generation stage..")

        SystemInfo.instance.setProject(projectDao.getLast)
        
      reindex(ctx,props)


      

      for(p<-Priority.list) {
        
        PasteManager.Stats.valueOf(p.getCode).increment(pasteDao.countAll(p).toInt)
        
      }

      SystemInfo.instance.setDateStart(Calendar.getInstance.getTime)

      logger.info("completed")
     return
    }


    val userDao:UserDao = ctx.getBean("userDao").asInstanceOf[UserDao]


    try {

      
      val admin = userDao.save(
        UserBuilder.createNew()
        .addRole(Role.ROLE_ADMIN)
        .addUsername("admin")
        .addPassword("admin")
        .addName("Default")
        .get())


      val alex = userDao.save(
        UserBuilder.createNew()
        .addRole(Role.ROLE_USER)
        .addUsername("alex")
        .addPassword("login")
        .addName("Alex")
        .get());


      var p1:Paste = new Paste
      p1.setOwner(admin)
      p1.setName("first test record")
      p1.setText("test record body")
     
     
      var p2:Paste = new Paste
      p2.setOwner(alex)
      p2.setName("second test record")
      p2.setText("test second record body")
     
      pasteDao.save(p1)
      pasteDao.save(p2)

      val project = new Project
      project.setName("Sample project")
      project.setDescription("Full project description")
      
      projectDao.persist(project)
        
      configDao.persist(ConfigProperty.IS_INSTALLED)
      configDao.persist(ConfigProperty.UPLOADS_DIR)
      
      val installDate = Calendar.getInstance.getTime.getTime
      ConfigProperty.INSTALL_DATE.setValue(installDate+"")
      configDao.persist(ConfigProperty.INSTALL_DATE)
 
      SystemInfo.instance.setDateInstall(new java.util.Date(installDate))
      
      ConfigProperty.APP_VERSION.setValue(SystemInfo.instance.getRuntimeVersion.toDbString)
      configDao.persist(ConfigProperty.APP_VERSION)

      logger.debug("saved version {0}",ConfigProperty.APP_VERSION.getValue)

      reindex(ctx,props)
   
      logger.info("db generation completed successfully.")

        

        SystemInfo.instance.setDateStart(Calendar.getInstance.getTime)

      
    } catch {
      case e @ (_ : UserExistsException | _ : java.io.IOException) => {
          logger.error(e.getLocalizedMessage,e)

        }
    }  
   
  }

  override def contextDestroyed(servletContextEvent:ServletContextEvent):Unit = {
    
  }

  def reindex( ctx:ApplicationContext,props:MergedPropertyConfigurer):Unit = {
    if (props.getProperty("config.reindex.enabled").equals("1")) {
      
      for (d<-SearchableDao.searchableDao) {
        
        d.indexAll()
      }
     
      logger.info("reindex completed.")
    } else {
      logger.info("reindex was disabled. skipping it.")
    }

  }



}
