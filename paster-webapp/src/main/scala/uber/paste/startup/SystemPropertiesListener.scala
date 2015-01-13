package uber.paste.startup

import uber.paste.base.Loggered
import javax.servlet.ServletContextListener
import java.nio.file.FileSystems
import java.sql.SQLException
import java.util.Collections
import java.util.Locale
import javax.servlet.ServletContextEvent
import com.jcabi.manifests.Manifests
import java.io.{IOException, File}
import uber.paste.base.SystemInfo
import uber.paste.base.plugins.PluginUI
import uber.paste.model.AppVersion
import org.h2.tools.Server
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

      val ehcacheStore = new File(appHome,"ehcache");
         if (!ehcacheStore.exists() && !ehcacheStore.isDirectory() &&  !ehcacheStore.mkdirs()) {
                throw new IllegalStateException(
                        "Cannot create directory " + ehcacheStore.getAbsolutePath());
            }
         
       System.setProperty("ehcache.disk.store.dir", ehcacheStore.getAbsolutePath())
        
      
      logger.info("application home:" + System.getProperty("paste.app.home"))
     
      val  mf_version = new AppVersion().fillFromManifest()
                
        SystemInfo.instance.setRuntimeVersion(mf_version)
        
        System.setProperty("paste.app.version", mf_version.getImplBuildNum())
        
      
      logger.info("current locale: "+Locale.getDefault)
     
    } catch {
     case e:IOException => {
         throw new RuntimeException(e)
            }
    }

  }

  
  override def contextDestroyed(servletContextEvent:ServletContextEvent ) {
    
  }
}


