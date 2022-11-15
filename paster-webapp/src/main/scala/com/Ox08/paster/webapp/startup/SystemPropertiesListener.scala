/*
 * Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.startup
import com.Ox08.paster.webapp.base.{Boot, Logged, SystemError, SystemMessage}
import jakarta.servlet.{ServletContextEvent, ServletContextListener}
import org.slf4j.bridge.SLF4JBridgeHandler
import java.io.{File, IOException}
import java.util.Locale
/**
 * First step initialization listener
 */
class SystemPropertiesListener extends ServletContextListener with Logged {
  /**
   * Triggers on ServletContext initialization, but before Spring/Hibernate.
   * @param event
   */
  override def contextInitialized(event: ServletContextEvent): Unit = {
    try {
      // do initial checks & home folder preparations
      doBoot()
      // set temp folders
      val scratchDir = new File(Boot.BOOT.getSystemInfo.getTempDir,"servletTmp")
      event.getServletContext.setAttribute("javax.servlet.context.tempdir", scratchDir)
      event.getServletContext.setAttribute("jakarta.servlet.context.tempdir",scratchDir)
      var springProfiles = ""

      /**
       * We have 2 different groups of settings, depend on installation mark.
       * If Paster has been installed correctly - load 'main' profile, with database and continue to boot.
       * Otherwise - load 'setup' profile and Paster will show 'installation' page.
       */
      if (Boot.BOOT.getSystemInfo.isInstalled) {
        springProfiles += "main"

        /**
         * Access mode.
         * 'public' - load additional profile with 'public profile' settings
         * 'private' - .. 'private profile' settings
         *
         */
        if ("public".equals(Boot.BOOT.getSystemInfo
          .getSetting("paster.security.access.mode", "private")))
          springProfiles += ",paster-security-public"
        else
          springProfiles += ",paster-security-private"
      } else
        springProfiles += "setup"
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
    //SLF4JBridgeHandler.removeHandlersForRootLogger()
    // re-install parent logger
    //SLF4JBridgeHandler.install()
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
    System.setProperty("org.jboss.logging.provider", "slf4j")
  }
  /**
   * Holds global constants
   */
  object SystemConstants {
    val APP_BASE: String = ".apps"
    val APP_NAME = "paster"
  }
}
