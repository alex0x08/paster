package org.h2.server.web

import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import org.h2.util.NetworkConnectionInfo
import java.io.IOException
import java.net.{InetAddress, UnknownHostException}
import java.nio.charset.StandardCharsets
import java.util
import java.util.Properties
import scala.collection.mutable

class WebServlet extends HttpServlet{

  private val serialVersionUID = 1L
  private var server:WebServer = null

  override def init(): Unit = {
    val config = getServletConfig
    val en = config.getInitParameterNames
    val list = new util.ArrayList[String]
    while ( {
      en.hasMoreElements
    }) {
      var name = en.nextElement.toString
      val value = config.getInitParameter(name)
      if (!name.startsWith("-")) name = "-" + name
      list.add(name)
      if (value.length > 0) list.add(value)
    }
    val args = list.toArray(new Array[String](0))
    server = new WebServer
    server.setAllowChunked(false)
    server.init(args: _*)
  }

  override def destroy(): Unit = {
    server.stop()
  }

  private def allow(req: HttpServletRequest): Boolean = {
    if (server.getAllowOthers) return true
    val addr = req.getRemoteAddr
    try {
      val address = InetAddress.getByName(addr)
      address.isLoopbackAddress
    } catch {
      case e@(_: UnknownHostException | _: NoClassDefFoundError) =>
        // Google App Engine does not allow java.net.InetAddress
        false
    }
  }

  private def getAllowedFile(req: HttpServletRequest, requestedFile: String): String = {
    if (!allow(req)) return "notAllowed.jsp"
    if (requestedFile.length == 0) return "index.do"
    requestedFile
  }

  @throws[IOException]
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    req.setCharacterEncoding("utf-8")
    var file = req.getPathInfo
    if (file == null) {
      resp.sendRedirect(req.getRequestURI + "/")
      return
    }
    else if (file.startsWith("/")) file = file.substring(1)
    file = getAllowedFile(req, file)
    // extract the request attributes
    val attributes = new Properties
    var en = req.getAttributeNames
    while ( {
      en.hasMoreElements
    }) {
      val name = en.nextElement.toString
      val value = req.getAttribute(name).toString
      attributes.put(name, value)
    }
    en = req.getParameterNames
    while ( {
      en.hasMoreElements
    }) {
      val name = en.nextElement.toString
      val value = req.getParameter(name)
      attributes.put(name, value)
    }
    var session: WebSession = null
    val sessionId = attributes.getProperty("jsessionid")
    if (sessionId != null) session = server.getSession(sessionId)
    val app = new WebApp(server)
    app.setSession(session, attributes)
    val ifModifiedSince = req.getHeader("if-modified-since")
    val scheme = req.getScheme
    val builder = new mutable.StringBuilder(scheme).append("://").append(req.getServerName)
    val serverPort = req.getServerPort
    if (!(serverPort == 80 && scheme == "http" || serverPort == 443 && scheme == "https")) builder.append(':').append(serverPort)
    val path = builder.append(req.getContextPath).toString
    file = app.processRequest(file, new NetworkConnectionInfo(path, req.getRemoteAddr, req.getRemotePort))
    session = app.getSession()
    val mimeType = app.getMimeType
    val cache = app.getCache
    if (cache && server.getStartDateTime == ifModifiedSince) {
      resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED)
      return
    }
    var bytes = server.getFile(file)
    if (bytes == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND)
      bytes = ("File not found: " + file).getBytes(StandardCharsets.UTF_8)
    }
    else {
      if (session != null && file.endsWith(".jsp")) {
        var page = new String(bytes, StandardCharsets.UTF_8)
        page = PageParser.parse(page, session.map)
        bytes = page.getBytes(StandardCharsets.UTF_8)
      }
      resp.setContentType(mimeType)
      if (!cache) resp.setHeader("Cache-Control", "no-cache")
      else {
        resp.setHeader("Cache-Control", "max-age=10")
        resp.setHeader("Last-Modified", server.getStartDateTime)
      }
    }
    if (bytes != null) {
      val out = resp.getOutputStream
      out.write(bytes)
    }
  }

  @throws[IOException]
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    doGet(req, resp)
  }

}
