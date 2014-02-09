package uber.paste.startup

import uber.paste.base.Loggered
import javax.servlet.ServletContextListener
import java.nio.file.FileSystems
import java.util.Collections
import java.util.Locale
import javax.servlet.ServletContextEvent;
import com.jcabi.manifests.Manifests
import java.io.{IOException, File}
import uber.paste.base.SystemInfo
import uber.paste.base.plugins.PluginUI
import uber.paste.model.AppVersion
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/4/13
 * Time: 12:52 AM
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
  
        val user_home:String = System.getProperty("user.home")
        appHome = FileSystems.getDefault()
        .getPath(user_home, SystemConstants.APP_BASE,SystemConstants.APP_NAME).toFile
        
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
      
      if (logger.isDebugEnabled) {
        logger.debug(PluginUI.getXml)
      }
      
      logger.info("current locale: "+Locale.getDefault)
      
      Locale.setDefault(Locale.ENGLISH)
      
    } catch {
     case e:IOException => {
      logger.error(e.getLocalizedMessage,e)
            }
    }

  }

  override def contextDestroyed(servletContextEvent:ServletContextEvent ) {
  }
}
