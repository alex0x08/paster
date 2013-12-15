package uber.paste.startup

import uber.paste.base.Loggered
import javax.servlet.ServletContextListener
import java.util.Collections
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import com.jcabi.manifests.Manifests
import com.thoughtworks.xstream.XStream
import java.io.{IOException, File}
import uber.paste.base.SystemInfo
import uber.paste.base.plugins.PluginUI
import uber.paste.base.plugins.UIElement
import uber.paste.base.plugins.UIExtension
import uber.paste.base.plugins.UISection
import uber.paste.model.AppVersion
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/4/13
 * Time: 12:52 AM
 * To change this template use File | Settings | File Templates.
 */

object SystemConstants {
  
  val APP_BASE:String = ".apps"
  val APP_NAME = "paste"
}

class SystemPropertiesListener extends ServletContextListener with Loggered {

  protected var appHome:File =null

  override def contextInitialized(event:ServletContextEvent) {
    try {

      Manifests.append(event.getServletContext())

      
      if (!System.getProperties().containsKey("paste.app.home")) {
  
        /*val appName:String = Manifests.read("AppName")
     
        if (appName == null) {
          throw new IllegalStateException("Cannot find application name property in META-INF/MANIFEST.MF. This is build bug.")
        }*/

        val user_home:String = System.getProperty("user.home")
        appHome = new File(user_home, SystemConstants.APP_BASE)

        appHome = new File(appHome,SystemConstants.APP_NAME)

        
        System.setProperty("paste.app.home", appHome.getAbsolutePath())
      } else {
        appHome = new File(System.getProperty("paste.app.home"))
      }

      
      if (!appHome.exists() || !appHome.isDirectory()) {

        if (!appHome.mkdirs()) {
          throw new IllegalStateException(
            "Cannot create application home directory " + appHome.getAbsolutePath())
        }
      }

      logger.info("application home:" + System.getProperty("paste.app.home"))
 
    
      val  mf_version = new AppVersion().fillFromManifest()
                
        SystemInfo.instance.setRuntimeVersion(mf_version)
        
        System.setProperty("paste.app.version", mf_version.getImplBuildNum())
        
      PluginUI.load(getClass().getResourceAsStream("/paster-ui-definitions.xml"))
      
      val pluginDefs=  Collections.list(
        event.getServletContext.getClassLoader.getResources("META-INF/resources/paster-ui-definition.xml"))
      
      if (pluginDefs!=null && !pluginDefs.isEmpty) {
        logger.info(String.format("Found %s plugin definitions",pluginDefs.size+""))
         for (d<-pluginDefs) {
            PluginUI.append(d)
         }
      }
      
      logger.debug(PluginUI.getXml)
      
      
    } catch {
     case e:IOException => {
      logger.error(e.getLocalizedMessage,e)

    }
    }

  }

  override def contextDestroyed(servletContextEvent:ServletContextEvent ) {
  }
}
