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
import uber.paste.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import uber.paste.dao.UserExistsException
import org.springframework.dao.DataAccessException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.rememberme.{RememberMeAuthenticationException, AbstractRememberMeServices}
import uber.paste.base.Loggered
import javax.servlet.http.{Cookie, HttpServletResponse, HttpServletRequest}
import org.springframework.security.core.Authentication
import java.lang.String
import scala.Predef.String

class UserRememberMeService extends AbstractRememberMeServices(token:String,userDetailsService:UserManager) with Loggered {

  /**
   * Attempt to authenticate a user using a UMS single sign-on cookie.
   */
  @throws(classOf[RememberMeAuthenticationException])
  @throws(classOf[UsernameNotFoundException])
  override protected def processAutoLoginCookie( cookieTokens:Array[String], request:HttpServletRequest,
     response:HttpServletResponse):UserDetails =
  {
    logger.debug("UmsRememberMeServices.processAutoLoginCookie");

    UserDetails user = null;
    String cookieValue = getCookieValue(request, getCookieName());
    if (cookieValue != null)
   //   user = userDetailsService.loadUserByCookie(cookieValue);
    if (user != null)
    {
      request.getSession().setAttribute(getCookieName(), cookieValue);
    //  request.getSession().setAttribute("user", userDetailsService.getCurrentUser());
      return user;
    }
    else
      throw new UsernameNotFoundException("Couldn't load user details via cookie: " + getCookieName());
  }

  /**
   * On logout, clear the single sign-on cookie (always attached to "/").
   */
  def logout(request:HttpServletRequest, response:HttpServletResponse, authentication:Authentication)
  {
    logger.debug("UmsRememberMeServices.logout");
    val cookieName = getCookieName()
    val sessionSso = request.getSession().getAttribute(cookieName)
    if (sessionSso != null)
    {
      getUserDetailsService().asInstanceOf[UserManager].removeSSOSession(sessionSso);
    }
  }

  protected def getCookieValue(request:HttpServletRequest, cookieName:String):String =
  {
    logger.debug("UmsRememberMeServices.getCookieValue - cookieName: " + cookieName);

    val cookies = request.getCookies()
    if (cookies != null)
      for (cookie <- cookies)
        if (cookie.getName().equals(cookieName))
          {
              return cookie.getValue()
          }
    return null
  }

 override protected def onLoginSuccess(request:HttpServletRequest, response:HttpServletResponse,
                                       successfulAuthentication:Authentication )
  {
    logger.debug("UmsRememberMeServices.onLoginSuccess");
  }
}

trait UserManager extends StructManager[User] {

    def getUserByUsername(username:String): User

    def getUserByOpenID(openid:String): User

    def getUserBySavedSession(sessionId:String): User

  }

@Service("userManager")
class UserManagerImpl extends StructManagerImpl[User] with UserManager with UserDetailsService {

  @Autowired
  val userDao:UserDao = null

  protected override def getDao:UserDao = {
    return userDao
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
    return userDao.getUserBySession(openid)
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
