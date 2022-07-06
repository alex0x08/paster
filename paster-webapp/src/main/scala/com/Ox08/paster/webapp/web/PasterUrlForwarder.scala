package com.Ox08.paster.webapp.web

import jakarta.servlet.{Filter, FilterChain, ServletRequest, ServletResponse}
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

class PasterUrlForwarder extends Filter{
  override def doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain): Unit = {

    val request = servletRequest.asInstanceOf[HttpServletRequest]
    val response = servletResponse.asInstanceOf[HttpServletResponse]
    val url = request.getRequestURI.substring(request.getContextPath.length)
      .replaceAll("/","")


      if (url.length>0 && url.matches("[0-9]+")) {
        System.out.println("do redirect url="+url)

          request.getRequestDispatcher("main/paste/"+url).forward(request,response)
        return
      }
      filterChain.doFilter(servletRequest,servletResponse)

  }
}
