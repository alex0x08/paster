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
package com.Ox08.paster.webapp.web
import jakarta.servlet.{Filter, FilterChain, ServletRequest, ServletResponse}
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
/**
 * Just forwards to main/paste/view/xx from /xx
 */
class PasterUrlForwarder extends Filter {
  override def doFilter(servletRequest: ServletRequest,
                        servletResponse: ServletResponse,
                        filterChain: FilterChain): Unit = {
    val request = servletRequest.asInstanceOf[HttpServletRequest]
    //request.setAttribute("org.eclipse.jetty.server.Request.queryEncoding", "UTF-8")
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
