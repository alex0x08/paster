package com.Ox08.paster.webapp.web.security

import com.Ox08.paster.webapp.base.Logged
import com.Ox08.paster.webapp.model.{PasterUser, Role}
import jakarta.servlet._
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.codec.digest.Md5Crypt
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.{SecurityContextHolder, SecurityContextHolderStrategy}
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import java.security.SecureRandom

/**
 * This class is used for simple API Token authentication
 * Token is provided via HTTP header 'X-Paster-API-Key'
 * @since 3.0
 * @author 0x08
 */
class APIAuthFilter extends Filter with Logged {

  private var apiKey:String = _

  private var schs:SecurityContextHolderStrategy = _

  private val scr = new HttpSessionSecurityContextRepository();

  @throws[ServletException]
  override def init(filterConfig: FilterConfig): Unit = {
    schs = SecurityContextHolder.getContextHolderStrategy
    apiKey = filterConfig.getServletContext.getAttribute("pasterApiKey").asInstanceOf[String]
  }
  override def doFilter(servletRequest: ServletRequest,
                        servletResponse: ServletResponse,
                        filterChain: FilterChain):  Unit = {
    if (apiKey==null) {
      logger.debug("API Key not defined, skip")
      filterChain.doFilter(servletRequest, servletResponse)
      return
    }

    val request = servletRequest.asInstanceOf[HttpServletRequest]
    val response = servletResponse.asInstanceOf[HttpServletResponse]
    val url = request.getRequestURI.substring(request.getContextPath.length)

     if (!url.startsWith("/main")) {
      filterChain.doFilter(servletRequest, servletResponse)
      return
    }

    val authK = request.getHeader("X-Paster-API-Key")

    if (authK!=null && !authK.isBlank && apiKey.equals(authK)) {
      // this is system user, used for API auth
      val api_user = new PasterUser("Api", "api",
        // generate fake password
        Md5Crypt.md5Crypt(SecureRandom.getSeed(20)),
        java.util.Set.of(Role.ROLE_USER))
      // log user in automatically
      val auth = new UsernamePasswordAuthenticationToken(
        "api", "api", api_user.getAuthorities())
      auth.setDetails(api_user)
      SecurityContextHolder.getContext.setAuthentication(auth)

      val context = schs.createEmptyContext
      context.setAuthentication(auth)
      schs.setContext(context)
      scr.saveContext(context, request, response);

      logger.debug("authenticated as API user")
    }
    filterChain.doFilter(servletRequest, servletResponse)
  }
}
