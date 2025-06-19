/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.view.tiles3

import org.apache.tiles.locale.impl.DefaultLocaleResolver
import org.apache.tiles.request.Request
import org.apache.tiles.request.servlet.{NotAServletEnvironmentException, ServletUtil}
import org.springframework.web.servlet.support.RequestContextUtils
import java.util.Locale


/**
 * Tiles LocaleResolver adapter that delegates to a Spring
 * [[org.springframework.web.servlet.LocaleResolver]], exposing the
 * DispatcherServlet-managed locale.
 *
 * <p>This adapter gets automatically registered by [[TilesConfigurer]].
 *
 * @author Nicolas Le Bas
 * @since 3.2
 *        see org.apache.tiles.definition.UrlDefinitionsFactory#LOCALE_RESOLVER_IMPL_PROPERTY
 */
class SpringLocaleResolver extends DefaultLocaleResolver {
  override def resolveLocale(request: Request): Locale = {
    try {
      val servletRequest = ServletUtil.getServletRequest(request).getRequest
      if (servletRequest != null) return RequestContextUtils.getLocale(servletRequest)
    } catch {
      case _: NotAServletEnvironmentException =>
      // ignore
    }
    super.resolveLocale(request)
  }
}
