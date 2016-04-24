/*
 * Copyright 2014 Ubersoft, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.web

import java.util.Collection
import org.pac4j.core.client.Client
import org.pac4j.core.client.Clients
import org.pac4j.core.credentials.Credentials
import org.pac4j.core.profile.UserProfile
import org.pac4j.core.util.CommonHelper
import org.pac4j.oauth.profile.google2.Google2Profile
import org.pac4j.oauth.profile.linkedin.LinkedInProfile
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile
import org.pac4j.springframework.security.authentication.ClientAuthenticationProvider
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken
import org.springframework.beans.factory.InitializingBean
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetailsChecker
import uber.paste.base.Loggered
import uber.paste.manager.UserManagerImpl
import uber.paste.model.{User,Role}

class PasteClientAuthProvider extends AuthenticationProvider with InitializingBean with Loggered {

    
    private var clients:Clients = null
    
    private var userDetailsService:UserManagerImpl = null
    
    private var userDetailsChecker:UserDetailsChecker = new AccountStatusUserDetailsChecker()
    
    
    
    @throws(classOf[AuthenticationException])
    override def authenticate( authentication:Authentication): Authentication =  {
        
      logger.debug("authentication : {}", authentication)
        
        if (!supports(authentication.getClass())) {
            logger.debug("unsupported authentication class : {}", authentication.getClass())
            return null
        }
       val token  = authentication.asInstanceOf[ClientAuthenticationToken]
        
        // get the credentials
       val  credentials =  authentication.getCredentials().asInstanceOf[Credentials]
        
    logger.debug("credentials : {}", credentials)
        
        
        // get the right client
        val clientName = token.getClientName()
    
    val client= clients.findClient(clientName).asInstanceOf[Client[Credentials, UserProfile]]
        // get the user profile
        val userProfile:UserProfile = client.getUserProfile(credentials, null)
        logger.debug("userProfile : {}", userProfile)
         
    
    val currentUser =userDetailsService.getUserByOpenID(userProfile.getId)
  
  
    
    /*if (currentUser.isDisabled) {
        logger.debug("user disabled")
      return null
    }*/
    var user =if (currentUser==null) {
         val user = new User("unnamed user")
         user.addRole(Role.ROLE_USER)
         user.setPassword(System.currentTimeMillis() + "_google")
      user
      
    } else {
        this.userDetailsChecker.check(currentUser)

      currentUser
    }
    
    userProfile match {
      
      case gp : Google2Profile => {
        
          user.setUsername(gp.getEmail)
          user.setEmail(gp.getEmail)
          user.setName(gp.getDisplayName)
          user.setOpenID(gp.getId)
          logger.debug("_match google "+gp.getDisplayName+" |username"+gp.getUsername+" email "+gp.getEmail)
      }
      case ln : LinkedIn2Profile => {
       
          user.setUsername(ln.getId)
          user.setName(ln.getDisplayName)
          user.setEmail("fuck@off.com")
          user.setOpenID(ln.getId)
          logger.debug("_match linkedin "+ln.getDisplayName+" |username"+ln.getUsername+" email "+ln.getEmail)
      }
    }
    
    user =userDetailsService.save(user)
    
         val  result = new UsernamePasswordAuthenticationToken(
      user,
      user.getPassword(),
      user.getAuthorities())
    result.setDetails(user)
  
  /*      // new token with credentials (like previously) and user profile and
        // authorities
        val result = new ClientAuthenticationToken(credentials, clientName, userProfile,
                                                   user.getAuthorities);
        
        result.setDetails(user);*/
        logger.debug("result : {}", result);
        return result;
    }
    
    def supports(authentication:Class[_]) =classOf[ClientAuthenticationToken].isAssignableFrom(authentication)
    
    
    def  afterPropertiesSet() {
        CommonHelper.assertNotNull("clients", this.clients)
        this.clients.init()
        
    }
    
    def getClients()= this.clients
    
    def setClients(clients:Clients) {
        this.clients = clients;
    }
    
    def getUserDetailsService() = this.userDetailsService
    
    def setUserDetailsService(userDetailsService:UserManagerImpl) {
        this.userDetailsService = userDetailsService
    }
    
    def getUserDetailsChecker():UserDetailsChecker = userDetailsChecker
    
    def setUserDetailsChecker(userDetailsChecker:UserDetailsChecker) {
        this.userDetailsChecker = userDetailsChecker;
    }
  
}
