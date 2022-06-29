package com.Ox08.paster.webapp.startup

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.util.ContextInitializer
import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.util.StatusPrinter
import com.Ox08.paster.webapp.base.Loggered
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.{File, IOException}
import java.nio.file.FileSystems
import java.util.Locale
import javax.servlet.{ServletContextEvent, ServletContextListener}

object SystemConstants {
  val APP_BASE: String = ".apps"
  val APP_NAME = "paster"
}

class SystemPropertiesListener extends ServletContextListener with Loggered {

  protected var appHome: File = null

  override def contextInitialized(event: ServletContextEvent) {
    try {

      setupAppHome

      setupConfig

      setupLogger

      setupEhcache

      System.setProperty("paste.app.id", System.currentTimeMillis + "")

      System.setProperty("spring.profiles.active", "main,auth-social")

      logger.info("current locale: {}", Locale.getDefault)

      logger.info("application home: {}", System.getProperty("paste.app.home"))

    } catch {
      case e: IOException =>
        throw new RuntimeException(e)
    }

  }


  override def contextDestroyed(servletContextEvent: ServletContextEvent) {}

  def setupAppHome() {

    if (!System.getProperties().containsKey("paste.app.home")) {

      val user_home: String = System.getProperty("user.home")
      appHome = FileSystems.getDefault()
        .getPath(user_home,
          SystemConstants.APP_BASE, SystemConstants.APP_NAME).toFile

      System.setProperty("paste.app.home", appHome.getAbsolutePath())
    } else
      appHome = new File(System.getProperty("paste.app.home"))


    if (!appHome.exists() || !appHome.isDirectory())
      if (!appHome.mkdirs())
        throw new IllegalStateException(
          "Cannot create application home directory " + appHome.getAbsolutePath())

  }

  def setupConfig() {

    val appConf = new File(appHome, "app.properties")

    if (!appConf.exists || !appConf.isFile)
      FileUtils.copyURLToFile(getClass().getResource("/default.properties"), appConf)


  }

  def setupLogger() {

    val profileLogger = new File(appHome, "logback.xml")

    if (!profileLogger.exists || !profileLogger.isFile) {
      FileUtils.copyURLToFile(getClass().getResource("/logback.xml"), profileLogger)
    }

    if (!System.getProperties.containsKey("logback.configurationFile")) {
      reloadLoggerTo(profileLogger)
    }

  }

  def reloadLoggerTo(config: File) {

    val loggerContext = LoggerFactory.getILoggerFactory().asInstanceOf[LoggerContext]

    val ci = new ContextInitializer(loggerContext)

    try {

      val configurator = new JoranConfigurator()
      configurator.setContext(loggerContext)

      loggerContext.reset();
      configurator.doConfigure(config)

    } catch {
      case e: JoranException => {}
    }

    StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext)

  }

  def setupEhcache() {

    val ehcacheConfig = new File(appHome, "ehcache.xml")

    if (!ehcacheConfig.exists || !ehcacheConfig.isFile) {
      FileUtils.copyURLToFile(getClass().getResource("/ehcache.xml"), ehcacheConfig)
    }

    val ehcacheStore = new File(appHome, "ehcache")
    if (!ehcacheStore.exists && !ehcacheStore.mkdirs()) {
      throw new IllegalStateException(
        String.format("Cannot create directory %s",
          ehcacheStore.getAbsolutePath()))
    }

    System.setProperty("ehcache.disk.store.dir", ehcacheStore.getAbsolutePath())

  }


}


