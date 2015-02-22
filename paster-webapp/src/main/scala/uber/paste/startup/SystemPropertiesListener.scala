package uber.paste.startup

import uber.paste.base.Loggered
import javax.servlet.ServletContextListener
import java.net.URL
import java.nio.file.FileSystems
import java.util.Locale
import java.util.Properties
import javax.servlet.ServletContextEvent
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.util.ContextInitializer
import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.util.StatusPrinter
import java.io.{IOException, File}
import uber.paste.base.SystemInfo
import uber.paste.base.plugins.PluginUI
import uber.paste.model.AppVersion
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/4/13
 * Time: 12:52 AM
 */

object SystemConstants {
  
  val APP_BASE:String = ".apps"
  val APP_NAME = "paster"
}

class SystemPropertiesListener extends ServletContextListener with Loggered {

  protected var appHome:File =null  
  
  override def contextInitialized(event:ServletContextEvent) {
    
    try {    
      
     
      setupAppHome
      
      setupLogger
      
      setupEhcache     
     
      loadBuildInfo
      
      logger.info("current locale: {0}",Locale.getDefault)
     
      logger.info("application home: {0}" , System.getProperty("paste.app.home"))
    
    } catch {
      case e:IOException => 
         throw new RuntimeException(e)            
    }

  }

  
  override def contextDestroyed(servletContextEvent:ServletContextEvent ) { }
  
  def setupAppHome() {
    
     if (!System.getProperties().containsKey("paste.app.home")) {
  
        val user_home:String = System.getProperty("user.home")
        appHome = FileSystems.getDefault()
                  .getPath(user_home, 
                           SystemConstants.APP_BASE,SystemConstants.APP_NAME).toFile
        
        System.setProperty("paste.app.home", appHome.getAbsolutePath())
      } else 
        appHome = new File(System.getProperty("paste.app.home"))
      
      
      if (!appHome.exists() || !appHome.isDirectory()) 
        if (!appHome.mkdirs()) 
          throw new IllegalStateException(
            "Cannot create application home directory " + appHome.getAbsolutePath())
        
  }
  
  def setupLogger() {
    
    val profileLogger = new File(appHome,"logback.xml")
    
    if (!profileLogger.exists || !profileLogger.isFile) {
        FileUtils.copyURLToFile(getClass().getResource("/logback.xml"), profileLogger)
    }
    
    
     if (!System.getProperties.containsKey("logback.configurationFile")) {
     
        reloadLoggerTo(profileLogger)
     }
    
  }
  
  def reloadLoggerTo(config:File) {
    
    val loggerContext = LoggerFactory.getILoggerFactory().asInstanceOf[LoggerContext]

    val ci = new ContextInitializer(loggerContext)
   
    try {
      
        val configurator = new JoranConfigurator()
        configurator.setContext(loggerContext)
        
        loggerContext.reset(); configurator.doConfigure(config)
         
    } catch {
      case e:JoranException => {}      
            
    }
    
    StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext)

  }
  
  def setupEhcache() {
    
    val ehcacheStore = new File(appHome,"ehcache");
         if (!ehcacheStore.exists() && !ehcacheStore.isDirectory() &&  !ehcacheStore.mkdirs()) {
                throw new IllegalStateException(
                        "Cannot create directory " + ehcacheStore.getAbsolutePath());
            }
         
       System.setProperty("ehcache.disk.store.dir", ehcacheStore.getAbsolutePath())
        
  }
  
  def loadBuildInfo() {
    
     val props = new Properties
      props.load(getClass().getResourceAsStream("/build.properties"))
      
      val  mf_version = new AppVersion().fillFromResource(props)
                
        SystemInfo.instance.setRuntimeVersion(mf_version)
        
        System.setProperty("paste.app.version", mf_version.getImplBuildNum())
      
  }
}


