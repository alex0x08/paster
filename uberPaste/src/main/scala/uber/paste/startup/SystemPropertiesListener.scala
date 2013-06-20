package uber.paste.startup

import uber.paste.base.Loggered
import javax.servlet.ServletContextListener
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import com.jcabi.manifests.Manifests
import java.io.{IOException, File}

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/4/13
 * Time: 12:52 AM
 * To change this template use File | Settings | File Templates.
 */

object SystemConstants {
  
  val APP_BASE:String = ".apps"
}

class SystemPropertiesListener extends ServletContextListener with Loggered {

  protected var appHome:File =null

  override def contextInitialized(event:ServletContextEvent) {
    try {

      Manifests.append(event.getServletContext());

      
      if (!System.getProperties().containsKey("app.home")) {
  
        val appName:String = Manifests.read("AppName")
     
        if (appName == null) {
          throw new IllegalStateException("Cannot find application name property in META-INF/MANIFEST.MF. This is build bug.")
        }

        val user_home:String = System.getProperty("user.home")
        appHome = new File(user_home, SystemConstants.APP_BASE)

        appHome = new File(appHome,appName)

        
        System.setProperty("app.home", appHome.getAbsolutePath())
      } else {
        appHome = new File(System.getProperty("app.home"))
      }

      
      if (!appHome.exists() || !appHome.isDirectory()) {

        if (!appHome.mkdirs()) {
          throw new IllegalStateException(
            "Cannot create application home directory " + appHome.getAbsolutePath())
        }
      }

      logger.info("application home:" + System.getProperty("app.home"))
    } catch {
     case e:IOException => {
      logger.error(e.getLocalizedMessage,e)

    }
    }

  }

  override def contextDestroyed(servletContextEvent:ServletContextEvent ) {
  }
}
