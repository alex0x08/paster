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



  

import org.pac4j.core.credentials.Credentials
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.dao.DataAccessException
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import uber.paste.base.Loggered
import uber.paste.dao.UserDaoImpl
import uber.paste.dao.UserExistsException
import uber.paste.model.User

object UserManager extends Loggered{

  

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
        logger.debug("getCurrentUser ,unknown principal type: {}" , 
                     SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString)
        null
      }
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
  
  
  
  @PreAuthorize("isAuthenticated()")
  def save(u:User) = userDao.save(u)
  
  @Override
  def getUser(userId:String) = userDao.get(java.lang.Long.valueOf(userId));
  
  @Override
  @throws(classOf[UsernameNotFoundException])
  def getUserByUsername(username:String)= userDao.getUser(username)
  
  def getUserByOpenID(openid:String)= userDao.getUserByOpenID(openid)
  
  
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
