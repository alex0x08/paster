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
import jakarta.servlet.ServletException
import org.springframework.web.servlet.mvc.WebContentInterceptor
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
 * This is used to tune HTTP headers for static resources,
 * see https://www.rfc-editor.org/rfc/rfc9111#name-private
 *
 */
class StaticResourcesContentInterceptor extends WebContentInterceptor {
  @throws(classOf[ServletException])
  override def preHandle(request: HttpServletRequest,
                         response:  HttpServletResponse, handler: Object): Boolean = {
    super.preHandle(request, response, handler)
    val h = response.getHeader("Cache-Control")
    if (h != null)
        response.setHeader("Cache-Control", s"$h private")
    // remove Cookie header, if present
    if (response.containsHeader("Cookie"))
        response.setHeader("Cookie", null)
    true
  }
}
