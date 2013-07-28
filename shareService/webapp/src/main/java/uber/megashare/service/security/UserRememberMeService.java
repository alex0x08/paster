/**
 * Copyright (C) 2011 Alex <alex@0x08.tk>
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
package uber.megashare.service.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import uber.megashare.model.SavedSession;
import uber.megashare.model.User;
import uber.megashare.service.UserManager;

/**
 *
 * @author achernyshev
 */
public class UserRememberMeService extends AbstractRememberMeServices {

    
    
    @Override
    protected void onLoginSuccess(HttpServletRequest hsr, HttpServletResponse hsr1, Authentication a) {
        
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] strings, HttpServletRequest hsr, HttpServletResponse hsr1) throws RememberMeAuthenticationException, UsernameNotFoundException {
  
        User user=null;
        String ssoVal = getUserManager().getSSOCookie(hsr);
        if (ssoVal!=null) {
            user = getUserManager().getUserBySession(ssoVal);
        }
        
        if (user!=null) {
            hsr.getSession().setAttribute(SavedSession.SSO_COOKIE_NAME, ssoVal);
            return user;
        } else {
            throw new UsernameNotFoundException("Cannot find user for saved session "+ssoVal);
        }
    }

    @Override
     public void logout(HttpServletRequest hsr, HttpServletResponse hsr1, Authentication a) {
     
        String ssoVal = (String)hsr.getSession().getAttribute(SavedSession.SSO_COOKIE_NAME);
        
        if (ssoVal!=null) {
            getUserManager().removeSession(SecurityHelper.getInstance().getCurrentUser().getId(), ssoVal);
        }
        
        getUserManager().invalidateSSOCookie(hsr1);
     }
    
    
    public UserManager getUserManager() {
        return (UserManager)getUserDetailsService();
    }

   
    
    
    
}
