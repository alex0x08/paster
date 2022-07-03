package org.apache.tiles.request.servlet

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.tiles.request.{AbstractClientRequest, ApplicationContext, Request}
import jakarta.servlet.ServletException
import org.apache.tiles.request.collection.{AddableParameterMap, HeaderValuesMap, ReadOnlyEnumerationMap, ScopeMap}
import org.apache.tiles.request.servlet.extractor.{HeaderExtractor, ParameterExtractor, RequestScopeExtractor, SessionScopeExtractor}

import org.apache.tiles.request.attribute.Addable

import java.io.IOException
import java.io.OutputStream
import java.io.PrintWriter
import java.util.{Collections, Locale}
import java.util


class ServletRequest(applicationContext: ApplicationContext,
                     request: HttpServletRequest,
                     response: HttpServletResponse) extends AbstractClientRequest(applicationContext){


  /**
   * The native available scopes: request, session and application.
   */
  private val SCOPES = Collections.unmodifiableList(
    util.Arrays.asList(Request.REQUEST_SCOPE, "session", Request.APPLICATION_SCOPE))


  /**
   * The request objects, lazily initialized.
   */
  private var requestObjects: Array[_] = null

  /**
   * The response output stream, lazily initialized.
   */
  private var outputStream: OutputStream = null

  /**
   * The response writer, lazily initialized.
   */
  private var writer: PrintWriter = null

  /**
   * <p>The lazily instantiated <code>Map</code> of header name-value
   * combinations (immutable).</p>
   */
  private var header: java.util.Map[String,String] = null

  /**
   * <p>The lazily instantiated <code>Map</code> of header name-value
   * combinations (write-only).</p>
   */
  private var responseHeaders: Addable[String] = null

  /**
   * <p>The lazily instantitated <code>Map</code> of header name-values
   * combinations (immutable).</p>
   */
  private var headerValues: java.util.Map[String,Array[String]] = null


  /**
   * <p>The lazily instantiated <code>Map</code> of request
   * parameter name-value.</p>
   */
  private var param: java.util.Map[String,String] = null


  /**
   * <p>The lazily instantiated <code>Map</code> of request scope
   * attributes.</p>
   */
  private var requestScope: java.util.Map[String,Object] = null

  /**
   * <p>The lazily instantiated <code>Map</code> of session scope
   * attributes.</p>
   */
  private var sessionScope: java.util.Map[String,Object] = null



  /** {@inheritDoc } */
  def getHeader: java.util.Map[String,String] = {
    if ((header == null) && (request != null))
          header = new AddableParameterMap(
            new HeaderExtractor(request, response))
    header
  }

  import org.apache.tiles.request.attribute.Addable
  import org.apache.tiles.request.servlet.extractor.HeaderExtractor

  /** {@inheritDoc } */
  def getResponseHeaders: Addable[String] = {
    if ((responseHeaders == null) && (response != null))
      responseHeaders = new HeaderExtractor(null, response)
    responseHeaders
  }


  def getHeaderValues:  java.util.Map[String,Array[String]] = {
    if ((headerValues == null) && (request != null))
      headerValues = new HeaderValuesMap(new HeaderExtractor(request, response))
    headerValues
  }


  def getParam: java.util.Map[String,String] = {
    if ((param == null) && (request != null))
      param = new ReadOnlyEnumerationMap[String](new ParameterExtractor(request))
    param
  }


  @SuppressWarnings(Array("unchecked"))
  def getParamValues:  java.util.Map[String,Array[String]] = request.getParameterMap

  def getContext(scope: String): java.util.Map[String,Object] = {
    if (Request.REQUEST_SCOPE.equals(scope)) return getRequestScope
    else if ("session" == scope) return getSessionScope
    else if (Request.APPLICATION_SCOPE.equals(scope)) return getApplicationScope
    throw new IllegalArgumentException(scope + " does not exist. Call getAvailableScopes() first to check.")
  }

  def getRequestScope:  java.util.Map[String,Object] = {
    System.out.println("getRequestscope " +requestScope)
    if ((requestScope == null) && (request != null))
      requestScope = new ScopeMap(new RequestScopeExtractor(request))
    requestScope
  }


  def getSessionScope: java.util.Map[String,Object]  = {
    if ((sessionScope == null) && (request != null))
      sessionScope = new ScopeMap(new SessionScopeExtractor(request))
    sessionScope
  }

  def getAvailableScopes: java.util.List[String] = SCOPES


  @throws[IOException]
  def doForward(path: String): Unit = {
    if (response.isCommitted) doInclude(path)
    else forward(path)
  }


  @throws[IOException]
  def doInclude(path: String): Unit = {
    val rd = request.getRequestDispatcher(path)
    if (rd == null)
        throw new IOException("No request dispatcher returned for path '" + path + "'")
    try rd.include(request, response)
    catch {
      case ex: ServletException =>
        throw ServletUtil.wrapServletException(ex, "ServletException including path '" + path + "'.")
    }
  }

  /**
   * Forwards to a path.
   *
   * @param path The path to forward to.
   * @throws IOException If something goes wrong during the operation.
   */
  @throws[IOException]
  private def forward(path: String): Unit = {
    val rd = request.getRequestDispatcher(path)
    if (rd == null) throw new IOException("No request dispatcher returned for path '" + path + "'")
    try rd.forward(request, response)
    catch {
      case ex: ServletException =>
        throw ServletUtil.wrapServletException(ex, "ServletException including path '" + path + "'.")
    }
  }

  @throws[IOException]
  def getOutputStream: OutputStream = {
    if (outputStream == null)
        outputStream = response.getOutputStream
    outputStream
  }

  @throws[IOException]
  def getWriter: PrintWriter = getPrintWriter

  @throws[IOException]
  def getPrintWriter: PrintWriter = {
    if (writer == null)
        writer = response.getWriter
    writer
  }

  def isResponseCommitted: Boolean = response.isCommitted

  def setContentType(contentType: String): Unit = {
    response.setContentType(contentType)
  }

  def getRequestLocale: Locale = request.getLocale


  def getRequest: HttpServletRequest = request

  def getResponse: HttpServletResponse = response

  def isUserInRole(role: String): Boolean = request.isUserInRole(role)


}
