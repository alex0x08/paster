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

package org.h2.server.web
import jakarta.servlet.http.{HttpServletRequest, HttpServletRequestWrapper, HttpServletResponse}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils

import java.sql.{Connection, SQLException}
import java.util
import javax.sql.DataSource
/**
 * This is extended version of JDBC Database Console Servlet,
 * packed with H2 Driver
 *
 * It was created to automatically bind connection from local datasource
 * to user's session and open console automatically.
 *
 */
class H2ConsoleExtendedServlet extends JakartaWebServlet  {

  def logger: Logger = LoggerFactory.getLogger(getClass.getName)

  private var isInstalled: Boolean = false
  private var dataSource: DataSource = _
  private var server:WebServer = _
  override def destroy(): Unit = {
    if (server!=null)
        server.stop()
  }
  override def init(): Unit = {

    isInstalled = getServletContext.getAttribute("pasterInstalled") != null
    if (!isInstalled) return
    server = new WebServer
    if (logger.isDebugEnabled) {
      logger.debug("h2 servlet init..")
    }
    val ctx: ApplicationContext = WebApplicationContextUtils
      .getWebApplicationContext(getServletContext)

      dataSource = ctx.getBean("dataSource").asInstanceOf[DataSource]
    try {
      server.setAllowChunked(false)
      server.init()
      val f = getClass.getSuperclass.getDeclaredField("server")
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
              | _: IllegalArgumentException) =>
        logger.error(e.getLocalizedMessage, e)
    }
  }
  @throws(classOf[java.io.IOException])
  override def doGet(req: HttpServletRequest, res: HttpServletResponse): Unit = {
    if (!isInstalled)
      return
    val h2console_db_session_id = getH2SessionIdFromContext
    var session: WebSession = null
    if (h2console_db_session_id != null) {
      session = server.getSession(h2console_db_session_id)
      if (session == null)
        session = createAndRegLocalSession()
      else
        if (logger.isDebugEnabled)
          logger.debug("using existing session {}", h2console_db_session_id)
    } else
      session = createAndRegLocalSession()
    /**
     * refresh connection if closed
     *
     */
    var con = session.getConnection
    if (con == null || con.isClosed) {
      con = dataSource.getConnection //.unwrap(Class[org.h2.jdbc.JdbcConnection])
      session.setConnection(con)
      session.put("url", con.getMetaData.getURL)
      if (logger.isDebugEnabled)
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
    val map = new util.HashMap[String, String]()
    map.put("jsessionid", getH2SessionIdFromContext)
    super.doGet(new ExtendedHttpServletRequestWrapper(req, map), res)
  }
  private def getH2SessionIdFromContext: String = getServletContext
    .getAttribute("h2console-session-id").asInstanceOf[String]
  private def createAndRegLocalSession(): WebSession = {
    this.synchronized {
      val conn = dataSource.getConnection()
      val unwrappedConnection: Connection = conn.unwrap(classOf[Connection])
      if (logger.isDebugEnabled)
        logger.debug("driver: {}", unwrappedConnection.getClass.getCanonicalName)
      val session = server.createNewSession("local")
      session.setShutdownServerOnDisconnect()
      session.setConnection(unwrappedConnection)
      session.put("url", conn.getMetaData.getURL)
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
      if (logger.isDebugEnabled)
        logger.debug("created h2 session {}", ss)
      getServletContext.setAttribute("h2console-session-id", ss)
      return session
    }
  }
  class ExtendedHttpServletRequestWrapper(req: HttpServletRequest, params: java.util.Map[String, String])
    extends HttpServletRequestWrapper(req) {
    override def getParameter(name: String): String = {
      // if we added one, return that one
      val out = if (params.containsKey(name)) params.get(name) else {
        // otherwise return what's in the original request
        super.getParameter(name)
      }
      out
    }
  }
}
