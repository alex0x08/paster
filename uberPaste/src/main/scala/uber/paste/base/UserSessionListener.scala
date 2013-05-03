/*
 * Copyright 2011 WorldWide Conferencing, LLC.
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

package uber.paste.base


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import uber.paste.model.User;

object UserSessionListener {
  
  val EVENT_KEY:String = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
  val lock:AnyRef = new Object  
}

class UserSessionListener extends Loggered with ServletContextListener with HttpSessionAttributeListener{

    @transient
    private var servletContext:ServletContext = null
   
  /**
     * Initialize the context
     *
     * @param sce the event
     */
    def contextInitialized(sce:ServletContextEvent) {
      
      UserSessionListener.lock.synchronized {
        servletContext = sce.getServletContext()
      }
      
    }

    /**
     * Set the servletContext, users and counter to null
     *
     * @param event The servletContextEvent
     */
    def contextDestroyed(event:ServletContextEvent) {
        servletContext = null
    }

  
    /**
     * This method is designed to catch when user's login and record their name
     *
     * @param event the event to process
     * @see javax.servlet.http.HttpSessionAttributeListener#attributeAdded(javax.servlet.http.HttpSessionBindingEvent)
     */
    def attributeAdded(event:HttpSessionBindingEvent) {

        if (event.getName().equals(UserSessionListener.EVENT_KEY) && !isAnonymous()) {
          
            val  securityContext = event.getValue().asInstanceOf[SecurityContext]

            logger.debug("__attributeAdded "+securityContext.getAuthentication().getPrincipal());

            if (securityContext.getAuthentication().getPrincipal().isInstanceOf[User]) {
               
                  SessionStore.instance.add(
                    event.getSession().getId(), 
                    (securityContext.getAuthentication().getPrincipal()).asInstanceOf[User])

                  logger.debug("_stored user="+
                               SessionStore.instance.getLoginForSession(event.getSession().getId()))
            }
        }
    }

    private def isAnonymous():Boolean = {
         val resolver = new AuthenticationTrustResolverImpl
         val ctx = SecurityContextHolder.getContext()

        return if (ctx != null) {
          resolver.isAnonymous(ctx.getAuthentication())
        } else {
          true
        }
    }

    /**
     * When user's logout, remove their name from the hashMap
     *
     * @param event the session binding event
     * @see javax.servlet.http.HttpSessionAttributeListener#attributeRemoved(javax.servlet.http.HttpSessionBindingEvent)
     */
    def attributeRemoved(event:HttpSessionBindingEvent) = {
        if (event.getName().equals(UserSessionListener.EVENT_KEY) && !isAnonymous()) {
           // SecurityContext securityContext = (SecurityContext) event.getValue();
           // Authentication auth = securityContext.getAuthentication();
           // if (auth != null && (auth.getPrincipal() instanceof User)) {
                  SessionStore.instance.remove(event.getSession().getId());
           // }
        }
    }

    /**
     * Needed for Acegi Security 1.0, as it adds an anonymous user to the session and
     * then replaces it after authentication. http://forum.springframework.org/showthread.php?p=63593
     *
     * @param event the session binding event
     * @see javax.servlet.http.HttpSessionAttributeListener#attributeReplaced(javax.servlet.http.HttpSessionBindingEvent)
     */
    def attributeReplaced(event:HttpSessionBindingEvent) {
        if (event.getName().equals(UserSessionListener.EVENT_KEY) && !isAnonymous()) {
          
            val securityContext = event.getValue().asInstanceOf[SecurityContext]
            
            if (securityContext.getAuthentication() != null
                && securityContext.getAuthentication().getPrincipal().isInstanceOf[User]) {

              val user = securityContext.getAuthentication().getPrincipal().asInstanceOf[User]
                
             //   SessionStore.instance.remove(event.getSession().getId())
                SessionStore.instance.add(event.getSession().getId(), user)
               
            }
        }
    }
  
}
