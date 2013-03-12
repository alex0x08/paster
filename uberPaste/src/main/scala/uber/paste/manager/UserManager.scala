/**
 * Copyright (C) 2010 alex <me@alex.0x08.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.manager

import org.springframework.stereotype.Service
import uber.paste.dao.UserDao
import uber.paste.model.{SavedSession, User}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import uber.paste.dao.UserExistsException
import org.springframework.dao.DataAccessException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.rememberme.{RememberMeAuthenticationException, AbstractRememberMeServices}
import uber.paste.base.{SessionStore, Loggered}
import javax.servlet.http.{Cookie, HttpServletResponse, HttpServletRequest}
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import javax.servlet.ServletException
import org.springframework.security.crypto.codec.Base64
import org.springframework.security.web.authentication.logout.{SimpleUrlLogoutSuccessHandler, LogoutFilter, LogoutHandler}
import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.RememberMeAuthenticationProvider


class UserLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

  //var cookieName:String = null

  var userManager:UserManager =null

  @throws(classOf[IOException])
  @throws(classOf[ServletException])
  override def onLogoutSuccess(request:HttpServletRequest, response:HttpServletResponse,
     authentication:Authentication)  {

    if (authentication != null) {
      // call sproc

      logger.debug("UmsLogoutFilter.requiresLogout url="+request.getRequestURI);

      val sessionSso = request.getSession().getAttribute(UserManager.SSO_COOKIE_NAME).asInstanceOf[String]

      if (sessionSso != null){
        userManager.removeSession(UserManager.getCurrentUser().getId(),sessionSso)
      }

      UserManager.invalidateSSOCookie(UserManager.SSO_COOKIE_NAME,response)

    }

    setDefaultTargetUrl("/main/login");
    //below does the 'standard' spring logout handling
    super.onLogoutSuccess(request, response, authentication);
  }

 /* def getCookieName() = cookieName;
  def setCookieName(c:String) = {this.cookieName = c}
   */
  def getUserManager():UserManager = userManager
  def setUserManager(u:UserManager) {this.userManager =u}
}


class UserAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {

//  val logger:Logger = Loggered.getLogger(this)

  var userManager:UserManager =null

  //var cookieName:String = null

  @throws(classOf[ServletException])
  @throws(classOf[IOException])
  override protected def successfulAuthentication(request:HttpServletRequest, response:HttpServletResponse,
  authResult:Authentication)  {

    logger.debug("__success auth")

    val user:User = authResult.getPrincipal.asInstanceOf[User]

    logger.debug(" user = "+user)

    val currentCookie:String = UserManager.getCookieValue(request,UserManager.SSO_COOKIE_NAME)

    val sso:Cookie = getOrCreateCookie(user,currentCookie)

    logger.debug("__current sso "+sso.getValue)

    response.addCookie(sso);

    request.getSession().setAttribute(UserManager.SSO_COOKIE_NAME, sso.getValue)

    super.successfulAuthentication(request, response, authResult)
  }

  def getOrCreateCookie(user:User,cookie:String):Cookie = {

    val s:SavedSession = if (cookie!=null && user.containsSavedSession(cookie)) {
      user.getSavedSession(cookie)
    } else {
      userManager.createSession(user.getId())
    }

    logger.debug("savedSession "+s)

    return UserManager.createNewSSOCookie(UserManager.SSO_COOKIE_NAME,s)
  }

  /*def getCookieName() = cookieName;
  def setCookieName(c:String) = {this.cookieName = c}
    */
  def getUserManager():UserManager = userManager
  def setUserManager(u:UserManager) {this.userManager =u}
}

class UserRememberMeService extends AbstractRememberMeServices  {

  //val logger:Logger = Loggered.getLogger(this)

  override def getCookieName()= UserManager.SSO_COOKIE_NAME

  /**
   * Attempt to authenticate a user using a UMS single sign-on cookie.
   */
  @throws(classOf[RememberMeAuthenticationException])
  @throws(classOf[UsernameNotFoundException])
  override protected def processAutoLoginCookie( cookieTokens:Array[String],
                                                 request:HttpServletRequest,
     response:HttpServletResponse):UserDetails =
  {

    logger.debug("__process login ")

    var user:User = null;
    val cookieValue:String = UserManager.getCookieValue(request, getCookieName())
    if (cookieValue != null)    {
      user = getUserDetailsService().asInstanceOf[UserManager].getUserBySavedSession(cookieValue)
    }
    if (user != null)
    {
      request.getSession().setAttribute(getCookieName(), cookieValue)
     // request.getSession().setAttribute("user", UserManager.getCurrentUser())
      return user
    }
    else
      throw new UsernameNotFoundException("Couldn't load user details via cookie: " + getCookieName())
  }

  /**
   * On logout, clear the single sign-on cookie (always attached to "/").
   */
  override def logout(request:HttpServletRequest, response:HttpServletResponse, authentication:Authentication)
  {
    logger.debug("UmsRememberMeServices.logout");
    val cookieName = getCookieName()
    val sessionSso:String = request.getSession().getAttribute(cookieName).asInstanceOf[String]
    if (sessionSso != null)
    {
      getUserDetailsService().asInstanceOf[UserManager].removeSession(UserManager.getCurrentUser().getId(),sessionSso);
    }

    UserManager.invalidateSSOCookie(cookieName,response)

  }

  def getSSOCookieName() = getCookieName()


 override protected def onLoginSuccess(request:HttpServletRequest, response:HttpServletResponse,
                                       successfulAuthentication:Authentication )
  {
    logger.debug("_onLoginSuccess");
  }
}

object UserManager extends Loggered{

  val SSO_COOKIE_NAME = "PASTER_SSO_COOKIE"

  def createNewSSOCookie(cookieName:String,s:SavedSession):Cookie = {

    System.out.println("__cookie name "+cookieName+" value="+new String(Base64.encode(s.getCode().getBytes())))

    val c=new Cookie(cookieName,new String(Base64.encode(s.getCode().getBytes())))
    c.setMaxAge(60 * 60 *60 *60)
    c.setPath("/")
    //c.setVersion(1)

    logger.debug("createCookie savedSession "+s)

    return c
  }

  def invalidateSSOCookie(cookieName:String,response:HttpServletResponse) {
    val c=new Cookie(cookieName,null)
    c.setMaxAge(-1)
    c.setPath("/")
    response.addCookie(c)
  }

  def  getCurrentUser():User = {

    return SecurityContextHolder.getContext().getAuthentication().getPrincipal() match {
      case u:User => {
        SessionStore.instance.getUserForLogin(
          (SecurityContextHolder.getContext().getAuthentication().getPrincipal()).asInstanceOf[User].getUsername())
      }
      case _ => {
        /**
         * this almost all time means that we got anonymous user
         */
        logger.debug("getCurrentUser ,unknown principal type: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString)
        return null;
      }
    }

  }

  def getCookieValue(request:HttpServletRequest, cookieName:String):String =
  {

    val cookies = request.getCookies()
    if (cookies != null)
      for (cookie <- cookies)     {
        if (cookie.getName().equals(cookieName) && !StringUtils.isBlank(cookie.getValue()))
        {
          val value= new String(Base64.decode(cookie.getValue().getBytes()))
          logger.debug("UmsRememberMeServices.getCookieValue - cookieName: " + cookieName+" value="+value);

          return value
        }
      }
    return null
  }

}

trait UserManager extends StructManager[User] {

    def getUserByUsername(username:String): User

    def getUserByOpenID(openid:String): User

    def getUserBySavedSession(sessionId:String): User

    def removeSession(userId:java.lang.Long,sessionId:String)

    def createSession(userId:java.lang.Long):SavedSession

    def getSession(sessionId:String):SavedSession

    def getSSOCookieName():String


  }

@Service("userManager")
class UserManagerImpl extends StructManagerImpl[User] with UserManager with UserDetailsService {

  @Autowired
  val userDao:UserDao = null

  protected override def getDao:UserDao = {
    return userDao
  }

  def getSSOCookieName():String = {
    System.out.println("_call get sso cookie name")
    return UserManager.SSO_COOKIE_NAME
  }


  @Override
  def getUser(userId:String): User = {
    return userDao.get(java.lang.Long.valueOf(userId));
  }

  @Override
  @throws(classOf[UsernameNotFoundException])
  def getUserByUsername(username:String): User = {
    return userDao.getUser(username)
  }

  def getUserByOpenID(openid:String): User = {
    return userDao.getUserByOpenID(openid)
  }

  def getUserBySavedSession(sessionId:String): User = {
    return userDao.getUserBySession(sessionId)
  }

  def createSession(userId:java.lang.Long):SavedSession = {
    return userDao.createSession(userId)
  }

  def getSession(sessionId:String):SavedSession = {
    return userDao.getSession(sessionId)
  }

  def removeSession(userId:java.lang.Long,sessionId:String) = {
    userDao.removeSession(userId,sessionId)
  }

  @Override
  def getUsers(): java.util.List[User] = {
    return userDao.getList()
  }

  @Override
  @throws(classOf[UserExistsException])
  def saveUser(user:User): User = {
    return userDao.save(user)
  }

  @Secured(Array("ROLE_ADMIN"))
  override def remove(id:Long) = super.remove(id)

  @Secured(Array("ROLE_ADMIN"))
  @Override
  def removeUser(userId:String) = {
    val u:User = userDao.get(java.lang.Long.valueOf(userId))
    userDao.remove(u.getId)
  }

  @Override
  @throws(classOf[UsernameNotFoundException])
  @throws(classOf[DataAccessException])
  def loadUserByUsername(username:String): UserDetails = {
    val out:User=getUserByUsername(username)

    if (out==null || out.getPassword()==null)
      throw new UsernameNotFoundException("User not found")
    return out
  }

  @Override
  def getUserPassword(username: String): String = {
    val u:User = userDao.getUser(username)
    return if (u==null)
      null
    else u.getPassword()
  }

}
