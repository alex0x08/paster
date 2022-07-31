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
  val switchToUserLocale: Boolean = false // do we have 'switch to browser's locale' feature enabled?
  /**
   * Resolves default locale. If not ajusted via '?locale=' parameter - this locale will be used till session lives
   *
   * @param request
   *      received http request
   * @return
   *    detected locale
   */
  override def determineDefaultLocale(request: HttpServletRequest): Locale = {
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
           p.equals(requestLocale) || p.getLanguage.equals(requestLocale.getLanguage)
         })) {
         // if locale is supported - use it
         if (logger.isDebugEnabled())
              logger.debug("switching to browser's locale: {}",requestLocale)
         return requestLocale
       }
     }
    // otherwise just return system locale
   systemLocale
  }
}
