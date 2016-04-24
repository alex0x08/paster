/*
 * Copyright 2015 Ubersoft, LLC.
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

package uber.paste.web.security

import java.security.SecureRandom
import java.util.Arrays
import java.util.Base64
import java.util.Date
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.joda.time.LocalDate
import org.springframework.dao.DataAccessException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices
import org.springframework.security.web.authentication.rememberme.InvalidCookieException
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException
import uber.paste.base.Loggered
import uber.paste.dao.TokenDaoImpl
import uber.paste.manager.UserManagerImpl
import uber.paste.model.PersistentToken
import uber.paste.model.User

object CPRConstants {
  
   // Token is valid for one month
    val TOKEN_VALIDITY_DAYS = 31

    val TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * TOKEN_VALIDITY_DAYS

    val DEFAULT_SERIES_LENGTH = 16

    val DEFAULT_TOKEN_LENGTH = 16

  
}

class CustomPersistentRememberMeServices(key:String,uds:UserDetailsService,tokenDao:TokenDaoImpl) 
      extends
        AbstractRememberMeServices(key,uds){

  private val log = Loggered.getLogger(getClass())

  private val random= new SecureRandom()
  
  
  protected def processAutoLoginCookie(
      cookieTokens:Array[String], 
      request:HttpServletRequest, 
      response:HttpServletResponse):UserDetails= {

        val token = getPersistentToken(cookieTokens)
        val login = token.getUser.getUsername
        

        // Token also matches, so login is valid. Update the token value, keeping the *same* series number.
        log.debug("Refreshing persistent login token for user '{}', series '{}'", 
               Array(login, token.getSeries()))
        
        token.setTokenDate(new Date())
        token.setTokenValue(generateTokenData())
        token.setIpAddress(request.getRemoteAddr())
        token.setUserAgent(request.getHeader("User-Agent"))
        
        try {
            tokenDao.save(token)
            addCookie(token, request, response)
            
              } catch {
           case e @ (_ : DataAccessException                    
                    ) => {    
               log.error(e.getLocalizedMessage,e)
               throw new RememberMeAuthenticationException("Autologin failed due to data access problem: " + e.getMessage())
                }      
      
            }      
       
        return getUserDetailsService().loadUserByUsername(login)
    }
  
  protected def onLoginSuccess(
     request:HttpServletRequest,  response:HttpServletResponse, 
     successfulAuthentication:Authentication) {
        
        val login = successfulAuthentication.getName()

        log.debug("Creating new persistent login for user {}", login)
        
        val user = getUserDetailsService().loadUserByUsername(login).asInstanceOf[User]

        val token = new PersistentToken()
        token.setSeries(generateSeriesData())
        token.setUser(user)
        token.setTokenValue(generateTokenData())
        token.setTokenDate(new Date())
        token.setIpAddress(request.getRemoteAddr())
        token.setUserAgent(request.getHeader("User-Agent"))
        
    
        try {
            tokenDao.save(token)
            addCookie(token, request, response)
      
            } catch {
           case e @ (_ : DataAccessException                    
                    ) => {    
               log.error(e.getLocalizedMessage,e)
                }      
      
            }
    }
  
  
   /**
     * When logout occurs, only invalidate the current token, and not all user sessions.
     * <p/>
     * The standard Spring Security implementations are too basic: they invalidate all tokens for the
     * current user, so when he logs out from one browser, all his other sessions are destroyed.
     */
   
    override def logout(request:HttpServletRequest,response:HttpServletResponse, authentication:Authentication) {
       
      val rememberMeCookie = extractRememberMeCookie(request)
       
        if (rememberMeCookie != null && rememberMeCookie.length() != 0) {
            try {
                val cookieTokens = decodeCookie(rememberMeCookie)
                
              val token = getPersistentToken(cookieTokens)
                
               tokenDao.remove(token.getSeries)
             
               } catch {
                  case e @ (_ : InvalidCookieException
                     | _ : RememberMeAuthenticationException 
                    ) => {    
               log.error(e.getLocalizedMessage,e)
                }
          }  
       
        
        }
        super.logout(request, response, authentication)
    }
  
  
  /**
     * Validate the token and return it.
     */
   def getPersistentToken(cookieTokens:Array[String]):PersistentToken = {
        if (cookieTokens.length != 2) {          
            throw new InvalidCookieException("Cookie token did not contain " + 2 +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'")
        }

        val presentedSeries = cookieTokens(0)
        val presentedToken = cookieTokens(1)

        val token = tokenDao.get(presentedSeries)

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries)
        }

        // We have a match for this user/series combination
        log.info("presentedToken={} / tokenValue={}", 
              Array(presentedToken, token.getTokenValue()))
        
        if (!presentedToken.equals(token.getTokenValue())) {
            // Token doesn't match series value. Delete this session and throw an exception.
            tokenDao.remove(token.getSeries)
            throw new RememberMeAuthenticationException("Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack.")
        }

        if (new LocalDate(token.getTokenDate)
            .plusDays(CPRConstants.TOKEN_VALIDITY_DAYS).isBefore(LocalDate.now())) {
            tokenDao.remove(token.getSeries)      
            throw new RememberMeAuthenticationException("Remember-me login has expired")
        }
        return token
    }
 
  
   private def generateSeriesData():String =  {
        val newSeries = new Array[Byte](CPRConstants.DEFAULT_SERIES_LENGTH)
        random.nextBytes(newSeries)
        return Base64.getEncoder.encodeToString(newSeries)
    }

    private def generateTokenData():String = {
        val newToken = new Array[Byte](CPRConstants.DEFAULT_TOKEN_LENGTH)
        random.nextBytes(newToken)
        return Base64.getEncoder.encodeToString(newToken)
    }

    private def addCookie(
       token:PersistentToken,  request:HttpServletRequest,  response:HttpServletResponse) {
      
        setCookie(
                Array[String](token.getSeries(), token.getTokenValue()),
                CPRConstants.TOKEN_VALIDITY_SECONDS, request, response);
    }
    
    def getUserManager():UserManagerImpl = getUserDetailsService().asInstanceOf[UserManagerImpl]
    

  
}
