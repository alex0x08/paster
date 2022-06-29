
package org.h2.server.web

import com.Ox08.paster.webapp.base.Loggered
import java.sql.SQLException
import java.util.HashMap
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils

/**
 * This is extended version of JDBC Database Console Servlet,
 * packed with H2 Driver
 *
 * It was created to automatically bind connection from local datasource
 * to user's session and open console automatically.
 *
 */
class H2ConsoleExtendedServlet extends WebServlet with Loggered {

  private var dataSource: DataSource = null

  private val server = new WebServer

  override def init(): Unit = {

    logger.debug("h2 servlet init..")

    val ctx: ApplicationContext = WebApplicationContextUtils
      .getWebApplicationContext(getServletContext())

    dataSource = ctx.getBean("dataSource").asInstanceOf[DataSource]

    try {
      server.setAllowChunked(false)
      server.init()

      val f = getClass().getSuperclass().getDeclaredField("server")
      f.setAccessible(true)
      f.set(this, server)

      //    val session = createAndRegLocalSession()

      //   val ss = session.get("sessionId").asInstanceOf[String]

      //  logger.debug("h2 sessionId: {}",ss);

      //  getServletContext().setAttribute("h2console-session-id",ss);   

    } catch {
      case e@(_: NoSuchFieldException
              | _: IllegalAccessException
              | _: SQLException
              | _: SecurityException
              | _: IllegalArgumentException) => {
        logger.error(e.getLocalizedMessage, e)
      }
    }

  }

  @throws(classOf[java.io.IOException])
  override def doGet(req: HttpServletRequest, res: HttpServletResponse) {


    val h2console_db_session_id = getH2SessionIdFromContext()

    var session: WebSession = null

    if (h2console_db_session_id != null) {

      session = server.getSession(h2console_db_session_id)

      if (session == null) {
        session = createAndRegLocalSession()
      } else {
        logger.debug("using existing session {}", h2console_db_session_id)
      }

    } else {
      session = createAndRegLocalSession()
    }

    /**
     * refresh connection if closed
     *
     */
    var con = session.getConnection

    if (con == null || con.isClosed) {
      con = dataSource.getConnection

      session.setConnection(con)
      session.put("url", con.getMetaData.getURL)
      logger.debug("reopened connection to db")
    }


    /**
     * attributes are filled from both httpservlet attributes and params
     * so easy solution like
     *
     * //req.g.setAttribute("jsessionid", h2console_db_session_id)
     *
     * will not work
     *
     * WebSession session = null;
     * String sessionId = attributes.getProperty("jsessionid");
     * if (sessionId != null) {
     * session = server.getSession(sessionId);
     * }
     */


    val map = new HashMap[String, String]()
    map.put("jsessionid", getH2SessionIdFromContext())

    super.doGet(new ExtendedHttpServletRequestWrapper(req, map), res)
  }

  private def getH2SessionIdFromContext() = getServletContext()
    .getAttribute("h2console-session-id").asInstanceOf[String]

  private def createAndRegLocalSession(): WebSession = {

    this.synchronized {
      val conn = dataSource.getConnection()
      val session = server.createNewSession("local")
      session.setShutdownServerOnDisconnect()
      session.setConnection(conn)
      session.put("url", conn.getMetaData().getURL())

      /**
       *
       * sessionId here is INTERNAL id, not container's jsessionId
       *
       * WebSession createNewSession(String hostAddr) {
       * String newId;
       * do {
       * newId = generateSessionId();
       * } while (sessions.get(newId) != null);
       * WebSession session = new WebSession(this);
       * session.lastAccess = System.currentTimeMillis();
       * session.put("sessionId", newId);
       *
       */

      val ss = session.get("sessionId").asInstanceOf[String]

      logger.debug("created h2 session {}", ss)

      getServletContext().setAttribute("h2console-session-id", ss)

      return session
    }


  }


  class ExtendedHttpServletRequestWrapper(req: HttpServletRequest, params: java.util.Map[String, String])
    extends HttpServletRequestWrapper(req) {


    override def getParameter(name: String): String = {

      // if we added one, return that one
      val out = if (params.containsKey(name)) {
        params.get(name)
      } else {
        // otherwise return what's in the original request
        super.getParameter(name)

      }

      System.out.println("name=" + name + " val=" + out)
      return out
    }


  }

}
