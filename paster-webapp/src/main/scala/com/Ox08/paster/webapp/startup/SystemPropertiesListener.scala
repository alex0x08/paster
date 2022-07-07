package com.Ox08.paster.webapp.startup

import com.Ox08.paster.webapp.base.Logged
import jakarta.servlet.{ServletContextEvent, ServletContextListener}

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


      System.setProperty("paste.app.id", System.currentTimeMillis().toString)

      System.setProperty("spring.profiles.active", "main")

      logger.info("current locale: {}", Locale.getDefault)

      logger.info("application home: {}", System.getProperty("paster.app.home"))

    } catch {
      case e: IOException =>
        throw new RuntimeException(e)
    }

  }


  override def contextDestroyed(servletContextEvent: ServletContextEvent):Unit = {}

  def doBoot(): Unit = {

    import com.Ox08.paster.webapp.base.{Boot, SystemError, SystemMessage}
    // re-initialize parent logger
    //SLF4JBridgeHandler.removeHandlersForRootLogger
    // re-install parent logger
    //SLF4JBridgeHandler.install
    // use English locale as default
    val en: Locale = Locale.ENGLISH //.f.forLanguageTag("en_US")
    System.out.println("locale="+en)
    // all system errors will be in English
    SystemError.instance.setErrorLocale(en)
    // all system messages will be in English
    SystemMessage.instance.setErrorLocale(en)
    // explicitly disable devtools restart
    System.setProperty("spring.devtools.restart.enabled", "false")
    // run boot sequence before Spring container starts
    Boot.BOOT.doBootSequence("paster")
    // add additional i18n bundles
    //SystemError.instance.addBundle("bundles/errorMessagesWeb")
    //SystemMessage.instance.addBundle("bundles/systemMessagesWeb")


  }



}


