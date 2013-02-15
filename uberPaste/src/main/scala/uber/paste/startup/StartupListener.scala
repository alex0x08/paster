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


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.apache.commons.io.FileUtils;
import org.springframework.web.context.support.WebApplicationContextUtils
import java.io.File
import uber.paste.model._
import uber.paste.dao._
import uber.paste.build._
import org.compass.gps.CompassGps;
import java.io.IOException
import uber.paste.dao.UserExistsException
import uber.paste.base.Loggered
import uber.paste.build._
import uber.paste.mail.EmbeddedSMTPServer
//import org.hibernate.event.{PostLoadEvent, PostLoadEventListener, LoadEvent, LoadEventListener}
//import org.hibernate.event.LoadEventListener.LoadType
//import org.hibernate.impl.SessionFactoryImpl
//import org.hibernate.event.`def`.{DefaultPostLoadEventListener, DefaultLoadEventListener}
import scala.collection.JavaConversions._
import org.hibernate.SessionFactory

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
    val configDao:ConfigDao = ctx.getBean("configDao").asInstanceOf[ConfigDao]

    if (configDao.isPropertySet(ConfigProperty.IS_INSTALLED.getCode, "1")) {
      logger.info("Database already created. skipping db generation stage..")
      reindex(ctx)
      startSmtpServer(ctx)
      return;
    }


    val userDao:UserDao = ctx.getBean("userDao").asInstanceOf[UserDao]
    val pasteDao:PasteDao = ctx.getBean("pasteDao").asInstanceOf[PasteDao]
   

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


      configDao.persist(ConfigProperty.IS_INSTALLED)
      configDao.persist(ConfigProperty.UPLOADS_DIR)

      reindex(ctx)

      logger.info("db generation completed successfully.")

      startSmtpServer(ctx)

    } catch {
      case e:UserExistsException => {
          logger.error(e.getLocalizedMessage,e)

        } case e:IOException => {
          logger.error(e.getLocalizedMessage,e)

        }
    }  
   
  }

  override def contextDestroyed(servletContextEvent:ServletContextEvent):Unit = {
    
  }

  def startSmtpServer(ctx:ApplicationContext) {

    EmbeddedSMTPServer.createInstance(ctx)

    EmbeddedSMTPServer.getInstance().start()

  }

  def reindex( ctx:ApplicationContext):Unit = {

    val compassGps:CompassGps = ctx.getBean("compassGps").asInstanceOf[CompassGps]

    compassGps.index()

  }

}
