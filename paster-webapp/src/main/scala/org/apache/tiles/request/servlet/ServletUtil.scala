
package org.apache.tiles.request.servlet

import org.apache.tiles.request.ApplicationContext
import jakarta.servlet.ServletContext
import jakarta.servlet.ServletException
import org.apache.tiles.request.{ApplicationAccess, Request, RequestWrapper}
import java.io.IOException


/**
 * Utilities for Tiles request servlet support.
 *
 * @version $Rev$ $Date$
 * @since 3.0.0
 */
object  ServletUtil {

  /**
   * Wraps a ServletException to create an IOException with the root cause if present.
   *
   * @param ex      The exception to wrap.
   * @param message The message of the exception.
   * @return The wrapped exception.
   * @since 2.1.1
   */
  def wrapServletException(ex: ServletException, message: String): IOException = {
    var retValue:IOException = null
    val rootCause = ex.getRootCause
    if (rootCause != null) { // Replace the ServletException with an IOException, with the root
      // cause of the first as the cause of the latter.
      retValue = new IOException(message, rootCause)
    }
    else retValue = new IOException(message, ex)
    retValue
  }

  def getApplicationContext(servletContext: ServletContext): ApplicationContext
  = servletContext.getAttribute(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE)
    .asInstanceOf[ApplicationContext]

  /**
   * Opens a TilesRequestContext until it finds a ServletTilesRequestContext.
   *
   * @param request The request to open.
   * @return The servlet-based request context.
   * @throws NotAServletEnvironmentException If a servlet-based request
   *                                         context could not be found.
   * @since 2.2.0
   */
  def getServletRequest(request: Request): ServletRequest = {
    var currentRequest = request
    while ( {
      true
    }) {
      if (currentRequest == null)
          throw new NotAServletEnvironmentException("Last Tiles request context is null")
      if (currentRequest.isInstanceOf[RequestWrapper])
        return currentRequest.asInstanceOf[ServletRequest]
      if (!currentRequest.isInstanceOf[RequestWrapper])
        throw new NotAServletEnvironmentException("Not a Servlet environment, not supported")
      currentRequest = currentRequest.asInstanceOf[RequestWrapper].getWrappedRequest
    }
    null.asInstanceOf[ServletRequest]
  }

  /**
   * Gets a servlet context from a TilesApplicationContext.
   *
   * @param applicationContext The application context to analyze.
   * @return The servlet context.
   * @throws NotAServletEnvironmentException If the application context is not
   *                                         servlet-based.
   * @since 2.2.0
   */
  def getServletContext(applicationContext: ApplicationContext): ServletContext = {
    if (applicationContext.isInstanceOf[ServletApplicationContext])
      return applicationContext.asInstanceOf[ServletApplicationContext].getContext.asInstanceOf[ServletContext]
    throw new NotAServletEnvironmentException("Not a Servlet-based environment")
  }

}
