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
class BootContext {
  @Autowired
  val users: UserManager = null
  @Autowired
  val pasteDao: PasteDao = null
  @Autowired
  val localeResolver: SessionLocaleResolver = null
}
class StartupListener extends ServletContextListener with Logged {
  override def contextInitialized(event: ServletContextEvent): Unit = {
    if (Boot.BOOT.getSystemInfo.isInstalled) {
      val bootContext = new BootContext()
      SpringBeanAutowiringSupport
        .processInjectionBasedOnServletContext(bootContext, event.getServletContext)
      try {
        // setup default locale
        bootContext.localeResolver.setDefaultLocale(Boot.BOOT.getSystemInfo.getSystemLocale)
        setupSecurityContext() // setup security context
        bootContext.users.loadUsers()
        logger.info("db generation completed successfully.")
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
      Md5Crypt.md5Crypt(SecureRandom.getSeed(20)),
      java.util.Set.of(Role.ROLE_ADMIN))
    // log user in automatically
    val auth = new UsernamePasswordAuthenticationToken(
      "start", "start", start_user.getAuthorities())
    auth.setDetails(start_user)
    SecurityContextHolder.getContext.setAuthentication(auth)
  }
}
