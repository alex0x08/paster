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
package com.Ox08.paster.webapp.web.security
import com.Ox08.paster.webapp.base.Logged
import com.Ox08.paster.webapp.dao.SessionTokensDao
import com.Ox08.paster.webapp.manager.UserManager
import com.Ox08.paster.webapp.model.{PasterUser, SessionToken}
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.dao.DataAccessException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.{UserDetails, UserDetailsService}
import org.springframework.security.web.authentication.rememberme.{AbstractRememberMeServices, InvalidCookieException, RememberMeAuthenticationException}
import java.security.SecureRandom
import java.time.{LocalDate, ZoneId}
import java.util
import java.util.{Base64, Date}
object CPRConstants {
  // Token is valid for one month
  val TOKEN_VALIDITY_DAYS = 31
  val TOKEN_VALIDITY_SECONDS: Int = 60 * 60 * 24 * TOKEN_VALIDITY_DAYS
  val DEFAULT_SERIES_LENGTH = 16
  val DEFAULT_TOKEN_LENGTH = 16
}
class PasterPersistentRememberMeServices(key: String, uds: UserDetailsService, tokenDao: SessionTokensDao)
  extends
    AbstractRememberMeServices(key, uds) {
  private val log = Logged.getLogger(getClass)
  private val random = new SecureRandom()
  protected def processAutoLoginCookie(
                                        cookieTokens: Array[String],
                                        request: HttpServletRequest,
                                        response: HttpServletResponse): UserDetails = {
    val token = getPersistentToken(cookieTokens)
    // Token also matches, so login is valid. Update the token value, keeping the *same* series number.
    val login = token.username
    if (log.isDebugEnabled()) {
      log.debug("Refreshing persistent login token for user '{}', series '{}'",
        Array(login, token.series))
    }
    token.tokenDate = new Date()
    token.tokenValue = generateTokenData()
    token.ipAddress = request.getRemoteAddr
    token.setUserAgent(request.getHeader("User-Agent"))
    try {
      tokenDao.save(token)
      addCookie(token, request, response)
    } catch {
      case e@(_: DataAccessException
        ) =>
        throw new RememberMeAuthenticationException(
          s"Autologin failed due to data access problem: ${e.getMessage}")
    }
    getUserDetailsService.loadUserByUsername(login)
  }
  protected def onLoginSuccess(
                                request: HttpServletRequest,
                                response: HttpServletResponse,
                                successfulAuthentication: Authentication): Unit = {
    val login = successfulAuthentication.getName
    if (log.isDebugEnabled)
      log.debug("Creating new persistent login for user {}", login)
    val user = getUserDetailsService.loadUserByUsername(login).asInstanceOf[PasterUser]
    val token = new SessionToken()
    token.series = generateSeriesData()
    token.username = user.getUsername()
    token.tokenValue = generateTokenData()
    token.tokenDate = new Date()
    token.ipAddress = request.getRemoteAddr
    token.setUserAgent(request.getHeader("User-Agent"))
    try {
      tokenDao.save(token)
      addCookie(token, request, response)
    } catch {
      case e@(_: DataAccessException
        ) =>
        log.error(e.getMessage, e)
    }
  }
  /**
   * When logout occurs, only invalidate the current token, and not all user sessions.
   * <p/>
   * The standard Spring Security implementations are too basic: they invalidate all tokens for the
   * current user, so when he logs out from one browser, all his other sessions are destroyed.
   */
  override def logout(request: HttpServletRequest,
                      response: HttpServletResponse,
                      authentication: Authentication): Unit = {
    val rememberMeCookie = extractRememberMeCookie(request)
    if (rememberMeCookie != null && rememberMeCookie.nonEmpty) try {
      val cookieTokens = decodeCookie(rememberMeCookie)
      val token = getPersistentToken(cookieTokens)
      tokenDao.remove(token.series)
    } catch {
      case e@(_: InvalidCookieException
              | _: RememberMeAuthenticationException
        ) =>
        log.error(e.getMessage, e)
    }
    super.logout(request, response, authentication)
  }
  /**
   * Validate the token and return it.
   */
  private def getPersistentToken(cookieTokens: Array[String]): SessionToken = {
    if (cookieTokens.length != 2) {
      throw new InvalidCookieException(
        s"Cookie token did not contain 2 tokens, but contained '${util.Arrays.asList(cookieTokens)}'")
    }
    val presentedSeries = cookieTokens(0)
    val presentedToken = cookieTokens(1)
    val token = tokenDao.get(presentedSeries)
    if (token == null) {
      // No series match, so we can't authenticate using this cookie
      throw new RememberMeAuthenticationException(
        s"No persistent token found for series id: $presentedSeries")
    }
    // We have a match for this user/series combination
    log.info("presentedToken={} / tokenValue={}",
      Array(presentedToken, token.tokenValue))
    if (!presentedToken.equals(token.tokenValue)) {
      // Token doesn't match series value. Delete this session and throw an exception.
      tokenDao.remove(token.series)
      throw new RememberMeAuthenticationException(
        "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack.")
    }
    if (LocalDate.ofInstant(token.tokenDate.toInstant, ZoneId.systemDefault())
      .plusDays(CPRConstants.TOKEN_VALIDITY_DAYS).isBefore(LocalDate.now())) {
      tokenDao.remove(token.series)
      throw new RememberMeAuthenticationException("Remember-me login has expired")
    }
    token
  }
  private def generateSeriesData(): String = {
    val newSeries = new Array[Byte](CPRConstants.DEFAULT_SERIES_LENGTH)
    random.nextBytes(newSeries)
    Base64.getEncoder.encodeToString(newSeries)
  }
  private def generateTokenData(): String = {
    val newToken = new Array[Byte](CPRConstants.DEFAULT_TOKEN_LENGTH)
    random.nextBytes(newToken)
    Base64.getEncoder.encodeToString(newToken)
  }
  private def addCookie(
                         token: SessionToken,
                         request: HttpServletRequest,
                         response: HttpServletResponse): Unit = {
    setCookie(
      Array[String](token.series, token.tokenValue),
      CPRConstants.TOKEN_VALIDITY_SECONDS, request, response)
  }
  def getUserManager: UserManager = getUserDetailsService.asInstanceOf[UserManager]
}
