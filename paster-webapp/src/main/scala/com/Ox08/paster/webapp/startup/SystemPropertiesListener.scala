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
import org.slf4j.{Logger, LoggerFactory}

import java.io.{File, IOException}
import java.util.Locale

/**
 * This servlet listener used to read configuration properties and put them into environment,
 * so loaded *before* Spring and other frameworks
 */
class SystemPropertiesListener extends ServletContextListener {
  object SystemConstants {
    val APP_BASE: String = ".apps"
    val APP_NAME = "paster"
  }
  override def contextInitialized(event: ServletContextEvent): Unit = {
    try {
      doBoot()

      val logger: Logger = LoggerFactory.getLogger(getClass.getName)
      // configure 'scratch dir', used for JSP compiler
      val scratchDir = new File(Boot.BOOT.getSystemInfo.getTempDir,"servletTmp")
      if (!scratchDir.exists() && !scratchDir.mkdirs())
        throw new RuntimeException(s"Cannot create scratchDir: ${scratchDir.getAbsolutePath}")
      // set scratch dir as context attribute
      event.getServletContext.setAttribute("jakarta.servlet.context.tempdir",scratchDir)
      // setup Spring profiles
      var springProfiles = ""
      // if Paster is installed - use 'main' profile
      if (Boot.BOOT.getSystemInfo.isInstalled)
        springProfiles += "main"
      // otherwise use 'setup' profile
      else
        springProfiles += "setup"
      // select Spring Security profile
      if ("public".equals(Boot.BOOT.getSystemInfo
        .getSetting("paster.security.access.mode", "private")))
        springProfiles += ",paster-security-public"
      else
        springProfiles += ",paster-security-private"
      System.setProperty("spring.profiles.active", springProfiles)
      // this property is used as 'seed' in URLs, to have unique resource URLs for resources
      System.setProperty("paste.app.id", System.currentTimeMillis().toString)

      logger.info("profiles: {} ,current locale: {} ,application home: {}",
        springProfiles,
        Locale.getDefault, System.getProperty("paster.app.home"))
    } catch {
      case e: IOException =>
        throw new RuntimeException(e)
    }
  }
  override def contextDestroyed(servletContextEvent: ServletContextEvent): Unit = {}
  private def doBoot(): Unit = {
    // re-initialize parent logger
    //org.slf4j.bridge.SLF4JBridgeHandler.removeHandlersForRootLogger()
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
}
