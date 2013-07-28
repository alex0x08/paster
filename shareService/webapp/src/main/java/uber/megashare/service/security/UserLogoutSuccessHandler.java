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
package uber.megashare.service.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import uber.megashare.model.SavedSession;
import uber.megashare.service.UserManager;

/**
 *
 * @author achernyshev
 */
public class UserLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler{
    
    private UserManager userManager;

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }   
    
    
    @Override
    public void onLogoutSuccess(HttpServletRequest request,HttpServletResponse response,Authentication auth) throws IOException, ServletException {
 
        if (auth!=null) {        
            String sessionSSO = (String)request.getSession().getAttribute(SavedSession.SSO_COOKIE_NAME);
            if (sessionSSO!=null) {
                userManager.removeSession(SecurityHelper.getInstance().getCurrentUser().getId(), sessionSSO);
            }
        userManager.invalidateSSOCookie(response);    
        }
        
        setDefaultTargetUrl("/main/login");
        
        super.onLogoutSuccess(request, response, auth);
    }
    
}
