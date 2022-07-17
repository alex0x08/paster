package com.Ox08.paster.webapp.web
import jakarta.servlet.{Filter, FilterChain, ServletRequest, ServletResponse}
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
/**
 * Just forwards to main/paste/view/xx from /xx
 */
class PasterUrlForwarder extends Filter {
  override def doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain): Unit = {
    val request = servletRequest.asInstanceOf[HttpServletRequest]
    val response = servletResponse.asInstanceOf[HttpServletResponse]
    val url = request.getRequestURI.substring(request.getContextPath.length)
      .replaceAll("/", "")
    // we allow numbers only
    if (url.nonEmpty && url.matches("[0-9]+")) {
      request.getRequestDispatcher(s"main/paste/$url").forward(request, response)
      return
    } else if ("main".equalsIgnoreCase(url)) {
      request.getRequestDispatcher(s"main/").forward(request, response)
      return
    }
    filterChain.doFilter(servletRequest, servletResponse)
  }
}
