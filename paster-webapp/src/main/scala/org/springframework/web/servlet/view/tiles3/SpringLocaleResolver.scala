package org.springframework.web.servlet.view.tiles3

import org.apache.tiles.locale.impl.DefaultLocaleResolver
import org.apache.tiles.request.Request
import org.apache.tiles.request.servlet.ServletUtil

class SpringLocaleResolver extends DefaultLocaleResolver {

  import jakarta.servlet.http.HttpServletRequest
  import org.apache.tiles.request.servlet.NotAServletEnvironmentException
  import org.springframework.web.servlet.support.RequestContextUtils
  import java.util.Locale

  override def resolveLocale(request: Request): Locale = {
    try {
      val servletRequest = ServletUtil.getServletRequest(request).getRequest
      if (servletRequest != null) return RequestContextUtils.getLocale(servletRequest)
    } catch {
      case ex: NotAServletEnvironmentException =>

      // ignore
    }
    super.resolveLocale(request)
  }
}
