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
import uber.paste.model.{SavedSession, User}
import org.pac4j.core.credentials.Credentials
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import uber.paste.dao.UserDaoImpl
import uber.paste.dao.UserExistsException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.dao.DataAccessException
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.rememberme.{RememberMeAuthenticationException, AbstractRememberMeServices}
import uber.paste.base.Loggered
import javax.servlet.http.{Cookie, HttpServletResponse, HttpServletRequest}
import org.springframework.security.authentication.dao.SaltSource
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import javax.annotation.security.PermitAll
import javax.servlet.ServletException
import org.springframework.security.crypto.codec.Base64
import org.springframework.security.web.authentication.logout.{SimpleUrlLogoutSuccessHandler, LogoutFilter, LogoutHandler}
import scala.collection.JavaConversions._
import org.apache.commons.lang.StringUtils
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.RememberMeAuthenticationProvider


class UserLogoutSuccessHandler(userManager:UserManagerImpl) extends SimpleUrlLogoutSuccessHandler {

   
  @throws(classOf[IOException])
  @throws(classOf[ServletException])
  override def onLogoutSuccess(request:HttpServletRequest, response:HttpServletResponse,
     authentication:Authentication)  {

    if (authentication != null) {
      // call sproc

      logger.debug(String.format("logout url=%s",request.getRequestURI))

      val sessionSso = request.getSession().getAttribute(UserManager.SSO_COOKIE_NAME)
      .asInstanceOf[String]

      if (sessionSso != null){
        userManager.removeSession(UserManager.getCurrentUser().getId(),sessionSso)
      }

      UserManager.invalidateSSOCookie(UserManager.SSO_COOKIE_NAME,response)
    }

    setDefaultTargetUrl("/main/login")
    //below does the 'standard' spring logout handling
    super.onLogoutSuccess(request, response, authentication)
  }

}


class UserAuthenticationProcessingFilter(userManager:UserManagerImpl) extends UsernamePasswordAuthenticationFilter  {


  setFilterProcessesUrl("/act/doAuth")
  setUsernameParameter("login")
  setPasswordParameter("pwd")
  
  
  @throws(classOf[ServletException])
  @throws(classOf[IOException])
  override protected def successfulAuthentication(request:HttpServletRequest, 
                                                  response:HttpServletResponse,
                                                  chain:javax.servlet.FilterChain,
                                                  authResult:Authentication)  {

   /**
    * Warning, this class uses parent logger !
    */
    
   
    val user:User = authResult.getPrincipal.asInstanceOf[User]
   

    val currentCookie:String = UserManager.getCookieValue(request,UserManager.SSO_COOKIE_NAME)

    val sso:Cookie = getOrCreateCookie(user,currentCookie)

   logger.info(String.format(" user %s authenticated, sso key: %s",user,sso.getValue))
 

    response.addCookie(sso)

    request.getSession().setAttribute(UserManager.SSO_COOKIE_NAME, sso.getValue)

    super.successfulAuthentication(request, response, chain,authResult)
  }

  def getOrCreateCookie(user:User,cookie:String):Cookie = {

    val s:SavedSession = if (cookie!=null && user.containsSavedSession(cookie)) {
      user.getSavedSession(cookie)
    } else {
      userManager.createSession(user.getId())
    }

    UserManager.createNewSSOCookie(UserManager.SSO_COOKIE_NAME,s)
  }

  
}

class UserRememberMeService(key:String,userDetails:UserManagerImpl) 
      extends AbstractRememberMeServices(key,userDetails)  {

  
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

  
    var user:User = null
    
    val cookieValue:String = UserManager.getCookieValue(request, getCookieName())
    
    if (cookieValue != null)    {
      
      user = getUserDetailsService().asInstanceOf[UserManagerImpl]
      .getUserBySavedSession(cookieValue)
    }
    if (user != null)
    {
      request.getSession().setAttribute(getCookieName(), cookieValue)
      return user
    }
    else
      throw new UsernameNotFoundException(String.format("Couldn't load user details via cookie: %s ", 
                                          getCookieName()))
  }

  /**
   * On logout, clear the single sign-on cookie (always attached to "/").
   */
  override def logout(request:HttpServletRequest, response:HttpServletResponse, 
                      authentication:Authentication)
  {
    
    val user:User = UserManager.getCurrentUser
    
    val cookieName = getCookieName()
    val sessionSso:String = request.getSession().getAttribute(cookieName).asInstanceOf[String]
    if (sessionSso != null)
    {
      getUserDetailsService().asInstanceOf[UserManagerImpl]
              .removeSession(UserManager.getCurrentUser().getId(),sessionSso)
    }

    UserManager.invalidateSSOCookie(cookieName,response)

       logger.info(String.format("user %s has logged out",user.getName))
 
  }

  def getSSOCookieName = getCookieName

  
 override protected def onLoginSuccess(request:HttpServletRequest, response:HttpServletResponse,
                                       successfulAuthentication:Authentication )
  {
   // empty
  }
  
}

object UserManager extends Loggered{

  val SSO_COOKIE_NAME = "PASTER_SSO_COOKIE"

  def createNewSSOCookie(cookieName:String,s:SavedSession):Cookie = {

  
    val c=new Cookie(cookieName,s.getBase64Encoded)
    c.setMaxAge(60 * 60 *60 *60)
    c.setPath("/")
    //c.setVersion(1)

    logger.debug("create new SSO cookie {}, {}",Array(cookieName,s.getBase64Encoded))

    return c
  }

  def invalidateSSOCookie(cookieName:String,response:HttpServletResponse) {
    val c=new Cookie(cookieName,null)
    c.setMaxAge(-1)
    c.setPath("/")
    response.addCookie(c)
  }

  def  getCurrentUser():User = 
    
    if (SecurityContextHolder.getContext().getAuthentication() ==null) 
      null
        else
    
      SecurityContextHolder.getContext().getAuthentication().getPrincipal() match {
      case u:User => 
          SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal().asInstanceOf[User]
      
      case _ => {
        /**
         * this almost all time means that we got anonymous user
         */
        //logger.debug("getCurrentUser ,unknown principal type: " + 
          //           SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString)
        null
      }
    }
  

  def getCookieValue(request:HttpServletRequest, cookieName:String):String =
  {

    val cookies = request.getCookies()
    if (cookies != null)
      for (cookie <- cookies)     {
        if (cookie.getName().equals(cookieName) )
        {
          if (StringUtils.isBlank(cookie.getValue())) return null
              
          val value= new String(Base64.decode(cookie.getValue().getBytes()))
          logger.debug("getCookieValue - cookieName: {} value = {}" , Array(cookieName,value))

          return value
        }
      }
    return null
  }

}


//@Service("userManager")
class UserManagerImpl extends UserDetailsService 
                         with AuthenticationUserDetailsService[ClientAuthenticationToken] with Loggered{

  @Autowired
  val userDao:UserDaoImpl = null

  @Autowired
  @Qualifier("sessionRegistry")
  val sessionRegistry:SessionRegistry = null
  
  @Autowired
  val passwordEncoder:PasswordEncoder = null
  
  
  def getSSOCookieName() = UserManager.SSO_COOKIE_NAME
  
  @PreAuthorize("isAuthenticated()")
  def save(u:User) = userDao.save(u)
  
  @Override
  def getUser(userId:String) = userDao.get(java.lang.Long.valueOf(userId));
  
  @Override
  @throws(classOf[UsernameNotFoundException])
  def getUserByUsername(username:String)= userDao.getUser(username)
  
  def getUserByOpenID(openid:String)= userDao.getUserByOpenID(openid)
  
  def getUserBySavedSession(sessionId:String)= userDao.getUserBySession(sessionId)
  
  def createSession(userId:java.lang.Long):SavedSession = userDao.createSession(userId)

  def getSession(sessionId:String):SavedSession = userDao.getSession(sessionId)

  @PreAuthorize("isAuthenticated()")
  def removeSession(userId:java.lang.Long,sessionId:String) = {
    userDao.removeSession(userId,sessionId)
  }
  
  @PreAuthorize("isAuthenticated()")
  def getUsers= userDao.getList()
  
  @PreAuthorize("isAuthenticated()")
  def getAllLoggedInUsers() = sessionRegistry.getAllPrincipals()
   
  
  @throws(classOf[UserExistsException])
  @PreAuthorize("isAuthenticated()")
  def saveUser(user:User)= userDao.save(user)

  @Secured(Array("ROLE_ADMIN"))
  def remove(id:Long) = userDao.remove(id)

  @Secured(Array("ROLE_ADMIN"))
  def removeUser(userId:String) = {
    val u:User = userDao.get(java.lang.Long.valueOf(userId))
    userDao.remove(u.getId)
  }

  @throws(classOf[UsernameNotFoundException])
  override def loadUserDetails(token:ClientAuthenticationToken):UserDetails = {
    val out = userDao.getUserByOpenID(token.getCredentials.asInstanceOf[Credentials].getClientName)
   //if (out==null )
    //  throw new UsernameNotFoundException("User not found (oauth)")
    return out
    
  }
  
  @throws(classOf[UsernameNotFoundException])
  @throws(classOf[DataAccessException])
  override def loadUserByUsername(username:String): UserDetails = {
    val out:User=getUserByUsername(username)
    
    logger.debug("loaded by username {} , user {}",Array(username,out))
    
    if (out==null)
      throw new UsernameNotFoundException(String.format("User %s not found",username))
     
    return out
  }
  
  def changePassword(user:User, newPassword:String):User = {
    logger.debug("changing password for user {}",user.getName)
    
    val encodedPass = passwordEncoder.encode(newPassword)
    
    if (user.isBlank || user.isPasswordEmpty || !user.getPassword.equals(encodedPass)) {
       user.setPassword(encodedPass)
    }
    
    return user
  }
  
  
 

}
