package com.Ox08.paster.webapp.startup

import com.Ox08.paster.webapp.base.{Loggered, MergedPropertyConfigurer}
import com.Ox08.paster.webapp.dao.PasteDaoImpl
import com.Ox08.paster.webapp.manager.UserManagerImpl
import com.Ox08.paster.webapp.model.{Role, User}
import org.apache.commons.csv.{CSVFormat, CSVRecord}
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils

import java.io.InputStreamReader
import javax.servlet.{ServletContextEvent, ServletContextListener}
import scala.jdk.CollectionConverters._

class BootContext(ctx: ApplicationContext) {

  val pasteDao: PasteDaoImpl = ctx.getBean(classOf[PasteDaoImpl])

  val users: UserManagerImpl = ctx.getBean(classOf[UserManagerImpl])


  val props: MergedPropertyConfigurer = ctx.getBean("propertyConfigurer-app")
    .asInstanceOf[MergedPropertyConfigurer]
}

class StartupListener extends ServletContextListener with Loggered {

  override def contextInitialized(event: ServletContextEvent) {


    val bootContext = new BootContext(WebApplicationContextUtils
      .getRequiredWebApplicationContext(event.getServletContext()))



    try {

      setupSecurityContext // setup security context

      bootContext.users.loadUsers()


//      bootContext.projectDao.persist(project)

//      reindex(bootContext.props)

      logger.info("db generation completed successfully.")

   //   val appProfile = bootContext.props.getProperty("build.profile")


    } catch {
      case e@( _: java.io.IOException) => {
        logger.error(e.getLocalizedMessage, e)
        throw e; // to stop application
      }
    }

  }

  override def contextDestroyed(servletContextEvent: ServletContextEvent) {
    // not used
  }



  def loadDefaults(csv: String, callback: CSVRecord => Unit) {
    val r = new InputStreamReader(getClass().getResourceAsStream(csv))
    try {
      val records = CSVFormat.DEFAULT.withHeader().parse(r)
      for (record <- records.asScala) {
        callback(record)
      }
    } finally r.close
  }

  def setupSecurityContext() {

    val start_user = User.createNew
      .addRole(Role.ROLE_ADMIN)
      .addUsername("start")
      .addName("Initial scheme creator")
      .get


    // log user in automatically
    val auth = new UsernamePasswordAuthenticationToken(
      "start", "start", start_user.getAuthorities())
    auth.setDetails(start_user)

    SecurityContextHolder.getContext().setAuthentication(auth)
  }

  def reindex(props: MergedPropertyConfigurer) {
    if (props.getProperty("config.reindex.enabled").equals("1")) {

   /*   for (d <- SearchableDaoImpl.searchableDao.asScala) {
 //       d.indexAll()
      }*/

      logger.info("reindex completed.")
    } else {
      logger.info("reindex was disabled. skipping it.")
    }

  }


}
