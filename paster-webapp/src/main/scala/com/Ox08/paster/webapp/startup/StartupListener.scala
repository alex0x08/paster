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
import com.Ox08.paster.webapp.base.{Boot, Logged}
import com.Ox08.paster.webapp.dao.PasteDao
import com.Ox08.paster.webapp.manager.UserManager
import com.Ox08.paster.webapp.model.{PasterUser, Role}
import jakarta.servlet.{ServletContextEvent, ServletContextListener}
import org.apache.commons.codec.digest.Md5Crypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.support.SpringBeanAutowiringSupport
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.security.SecureRandom
/**
 *  This is Paster's startup listener, used for initialization and internal checks.
 */
class StartupListener extends ServletContextListener with Logged {
  /**
   * Second step initialization.
   * Triggers on ServletContext's initialization success, but AFTER Spring  & Hibernate.
   * So when this listener starts - almost everything is already wired-up.
   * @param event
   */
  override def contextInitialized(event: ServletContextEvent): Unit = {
    // Check for installation mark
    // Only if Paster was installed correctly - try to continue boot.
    if (Boot.BOOT.getSystemInfo.isInstalled) {
      // create boot context
      val bootContext = new BootContext()
      SpringBeanAutowiringSupport
        .processInjectionBasedOnServletContext(bootContext, event.getServletContext)
      try {
        // setup default locale
        bootContext.localeResolver.setDefaultLocale(Boot.BOOT.getSystemInfo.getSystemLocale)
        setupSecurityContext() // setup security context
        bootContext.users.loadUsers()
        logger.info("boot completed successfully.")
      } catch {
        case e@(_: java.io.IOException) =>
          logger.error(e.getLocalizedMessage, e)
          throw e; // to stop application
      }
    }
  }
  override def contextDestroyed(servletContextEvent: ServletContextEvent): Unit = {
    // not used
  }
  def setupSecurityContext(): Unit = {
    val start_user = new PasterUser("System", "system",
      //fake password
      Md5Crypt.md5Crypt(SecureRandom.getSeed(20)),
      java.util.Set.of(Role.ROLE_ADMIN))
    // log user in automatically
    val auth = new UsernamePasswordAuthenticationToken(
      "start", "start", start_user.getAuthorities())
    auth.setDetails(start_user)
    SecurityContextHolder.getContext.setAuthentication(auth)
  }
}
/**
 * A DTO class, with dependencies wired up by Spring at runtime.
 * Used during initialization process.
 */
class BootContext {
  @Autowired
  val users: UserManager = null
  @Autowired
  val pasteDao: PasteDao = null
  @Autowired
  val localeResolver: SessionLocaleResolver = null
}
