package com.Ox08.paster.webapp.startup
import com.Ox08.paster.webapp.base.{Boot, Logged, SystemError, SystemMessage}
import jakarta.servlet.{ServletContextEvent, ServletContextListener}
import org.slf4j.bridge.SLF4JBridgeHandler
import org.springframework.context.i18n.LocaleContextHolder

import java.io.{File, IOException}
import java.util.Locale
object SystemConstants {
  val APP_BASE: String = ".apps"
  val APP_NAME = "paster"
}
class SystemPropertiesListener extends ServletContextListener with Logged {
  override def contextInitialized(event: ServletContextEvent): Unit = {
    try {
      doBoot()

      val scratchDir = new File(Boot.BOOT.getSystemInfo.getTempDir,"servletTmp")

      event.getServletContext.setAttribute("javax.servlet.context.tempdir", scratchDir)
      event.getServletContext.setAttribute("jakarta.servlet.context.tempdir",scratchDir)

      var springProfiles = ""
      if (Boot.BOOT.getSystemInfo.isInstalled)
        springProfiles += "main"
      else
        springProfiles += "setup"
      if ("public".equals(Boot.BOOT.getSystemInfo.getSetting("paster.security.access.mode", "private")))
        springProfiles += ",paster-security-public"
      else
        springProfiles += ",paster-security-private"
      logger.info("profiles: {}", springProfiles)
      System.setProperty("spring.profiles.active", springProfiles)
      System.setProperty("paste.app.id", System.currentTimeMillis().toString)
      logger.info("current locale: {}", Locale.getDefault)
      logger.info("application home: {}", System.getProperty("paster.app.home"))
    } catch {
      case e: IOException =>
        throw new RuntimeException(e)
    }
  }
  override def contextDestroyed(servletContextEvent: ServletContextEvent): Unit = {}
  def doBoot(): Unit = {
    // re-initialize parent logger
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    // re-install parent logger
    SLF4JBridgeHandler.install()
    // use English locale as default
    val en: Locale = Locale.ENGLISH
    //System.out.println("locale=" + en)
    // all system errors will be in English
    SystemError.instance.setLocale(en)
    // all system messages will be in English
    SystemMessage.instance.setLocale(en)
    // explicitly disable devtools restart
    System.setProperty("spring.devtools.restart.enabled", "false")
    // run boot sequence before Spring container starts
    Boot.BOOT.doBootSequence("paster")
    // add additional i18n bundles
    //SystemError.instance.addBundle("bundles/errorMessagesWeb")
    //SystemMessage.instance.addBundle("bundles/systemMessagesWeb")
    //System.setProperty("org.jboss.logging.provider", "slf4j")
  }
}


