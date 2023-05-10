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
import com.Ox08.paster.webapp.base.{Boot, Logged}
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.Locale
/**
 * Customized locale resolver, supports both 'Accept-Language' and session-based locales
 */
class PasterLocaleResolver extends SessionLocaleResolver with Logged{
  @Value("${paster.i18n.switchToUserLocale}")
  var switchToUserLocale: Boolean = false // do we have 'switch to browser's locale' feature enabled?
  // via Spring 6 API
  setDefaultLocaleFunction(new java.util.function.Function[HttpServletRequest,Locale]() {
    override def apply(request: HttpServletRequest): Locale = determineDefaultLocaleImpl(request)
  })

  /**
   * Resolves default locale. If not adjusted via '?locale=' parameter
   *    - this locale will be used till session lives
   *
   * @param request
   *      received http request
   * @return
   *    detected locale
   */
  private def determineDefaultLocaleImpl(request: HttpServletRequest): Locale = {
     val systemLocale: Locale = Boot.BOOT.getSystemInfo.getSystemLocale
    // if we have 'switch to user locale' feature enabled
     if (switchToUserLocale) {
       // if request does not contain 'Accept-Language' header - just respond system locale
       if (request.getHeader("Accept-Language") == null)
         return systemLocale
       // otherwise - HttpServletRequest will contain parsed Locale object
       val requestLocale = request.getLocale
       // if so and this object is correct
       if (requestLocale!=null && requestLocale.getLanguage!=null &&
         // try to check if we support this locale
         Boot.BOOT.getSystemInfo.getAvailableLocales.exists(p => {
           p.equals(requestLocale) || p.getLanguage.equals(requestLocale.getLanguage) })) {
         // if locale is supported - use it
         if (logger.isDebugEnabled()) logger.debug("switching to browser's locale: {}",requestLocale)
         return requestLocale
       }
     }
    // otherwise just return system locale
   systemLocale
  }
}
