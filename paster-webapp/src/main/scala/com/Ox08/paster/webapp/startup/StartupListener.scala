package com.Ox08.paster.webapp.startup

import com.Ox08.paster.webapp.base.Logged
import com.Ox08.paster.webapp.dao.PasteDao
import com.Ox08.paster.webapp.manager.UserManager
import com.Ox08.paster.webapp.model.{PasterUser, Role}
import jakarta.servlet.{ServletContextEvent, ServletContextListener}
import org.apache.commons.codec.digest.Md5Crypt
import org.apache.commons.csv.{CSVFormat, CSVRecord}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.support.{SpringBeanAutowiringSupport, WebApplicationContextUtils}

import java.io.InputStreamReader
import java.security.SecureRandom
import scala.jdk.CollectionConverters._

class BootContext {

  @Autowired
  val users: UserManager = null

  @Autowired
  val pasteDao: PasteDao = null

}

class StartupListener extends ServletContextListener with Logged {

  override def contextInitialized(event: ServletContextEvent): Unit = {


    val bootContext = new BootContext()

    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(bootContext,event.getServletContext)

    try {

      setupSecurityContext() // setup security context

      bootContext.users.loadUsers()


//      bootContext.projectDao.persist(project)

//      reindex(bootContext.props)

      logger.info("db generation completed successfully.")

   //   val appProfile = bootContext.props.getProperty("build.profile")


    } catch {
      case e@( _: java.io.IOException) =>
        logger.error(e.getLocalizedMessage, e)
        throw e; // to stop application
    }

  }

  override def contextDestroyed(servletContextEvent: ServletContextEvent): Unit= {
    // not used
  }



  def loadDefaults(csv: String, callback: CSVRecord => Unit): Unit= {
    val r = new InputStreamReader(getClass.getResourceAsStream(csv))
    try {
      val records = CSVFormat.DEFAULT.builder()
        .setHeader().setSkipHeaderRecord(true).build().parse(r)
      for (record <- records.asScala) {
        callback(record)
      }
    } finally r.close()
  }

  def setupSecurityContext(): Unit= {

    val start_user = new PasterUser("System","system",
      Md5Crypt.md5Crypt(SecureRandom.getSeed(20)),
      java.util.Set.of(Role.ROLE_ADMIN))

    // log user in automatically
    val auth = new UsernamePasswordAuthenticationToken(
      "start", "start", start_user.getAuthorities())
    auth.setDetails(start_user)

    SecurityContextHolder.getContext.setAuthentication(auth)
  }

 // def reindex(props: MergedPropertyConfigurer): Unit= {
 //   if (props.getProperty("config.reindex.enabled").equals("1")) {

   /*   for (d <- SearchableDaoImpl.searchableDao.asScala) {
 //       d.indexAll()
      }*/

   /*   logger.info("reindex completed.")
    } else {
      logger.info("reindex was disabled. skipping it.")
    }

  }
*/

}
