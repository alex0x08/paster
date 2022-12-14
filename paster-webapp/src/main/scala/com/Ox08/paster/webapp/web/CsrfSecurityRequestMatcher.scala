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
import java.util.regex.Pattern
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import jakarta.servlet.http.HttpServletRequest
/**
 * This class checks where possible to disable CSRF protection.
 */
class CsrfSecurityRequestMatcher extends RequestMatcher {
  // list of allowed HTTP methods
  private val allowedMethods: Pattern = Pattern.compile("^(GET|POST|HEAD|TRACE|OPTIONS)$")
  // list of patterns allowed to bypass CSRF
  private val unprotectedMatcher = new AntPathRequestMatcher("/act/admin/dbconsole/**")
  /**
   * Run checks
   * @param request
   *        servlet request
   * @return
   */
  override def matches(request: HttpServletRequest): Boolean = {
    // check if method is allowed
    if (allowedMethods.matcher(request.getMethod).matches()) return false
    // check if url could ignore CSRF
    !unprotectedMatcher.matches(request)
  }
}
