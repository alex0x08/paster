/**
 * Copyright (C) 2011 alex <alex@0x08.tk>
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
package uber.megashare.listener;


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
import uber.megashare.model.User;


/**
 * UserCounterListener class used to count the current number
 * of active users for the applications.  Does this by counting
 * how many user objects are stuffed into the session.  It also grabs
 * these users and exposes them in the servlet context.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class UserSessionListener implements ServletContextListener, HttpSessionAttributeListener {
   
    /**
     * The default event we're looking to trap.
     */
    public static final String EVENT_KEY =  HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
   // private transient ServletContext servletContext;
   

    /**
     * Initialize the context
     *
     * @param sce the event
     */
    public synchronized void contextInitialized(ServletContextEvent sce) {
       // servletContext = sce.getServletContext();
      
    }

    /**
     * Set the servletContext, users and counter to null
     *
     * @param event The servletContextEvent
     */
    public synchronized void contextDestroyed(ServletContextEvent event) {
      //  servletContext = null;
       
    }

    

    /**
     * This method is designed to catch when user's login and record their name
     *
     * @param event the event to process
     * @see javax.servlet.http.HttpSessionAttributeListener#attributeAdded(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (event.getName().equals(EVENT_KEY) && !isAnonymous()) {
            SecurityContext securityContext = (SecurityContext) event.getValue();
            if (securityContext!=null && securityContext.getAuthentication()!=null && securityContext.getAuthentication().getPrincipal() instanceof User) {
               
                  SessionSupport.getInstance().add(event.getSession().getId(), ((User) securityContext.getAuthentication().getPrincipal()));

            }
        }
    }

    private boolean isAnonymous() {
        AuthenticationTrustResolver resolver = new AuthenticationTrustResolverImpl();
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx != null) {
            Authentication auth = ctx.getAuthentication();
            return resolver.isAnonymous(auth);
        }
        return true;
    }

    /**
     * When user's logout, remove their name from the hashMap
     *
     * @param event the session binding event
     * @see javax.servlet.http.HttpSessionAttributeListener#attributeRemoved(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (event.getName().equals(EVENT_KEY) && !isAnonymous()) {
           // SecurityContext securityContext = (SecurityContext) event.getValue();
           // Authentication auth = securityContext.getAuthentication();
           // if (auth != null && (auth.getPrincipal() instanceof User)) {
                  SessionSupport.getInstance().remove(event.getSession().getId());
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
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (event.getName().equals(EVENT_KEY) && !isAnonymous()) {
            final SecurityContext securityContext = (SecurityContext) event.getValue();
            if (securityContext.getAuthentication() != null
                    && securityContext.getAuthentication().getPrincipal() instanceof User) {
                final User user = (User) securityContext.getAuthentication().getPrincipal();
                
                SessionSupport.getInstance().remove(event.getSession().getId());
                SessionSupport.getInstance().add(event.getSession().getId(), user);
               
            }
        }
    }
}
