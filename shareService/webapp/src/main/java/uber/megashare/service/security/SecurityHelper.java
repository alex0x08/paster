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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uber.megashare.base.LoggedClass;
import uber.megashare.listener.SessionSupport;
import uber.megashare.model.User;

/**
 *
 * @author achernyshev
 */
public class SecurityHelper extends LoggedClass{
    
    
     public User getCurrentUser() {

         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         
         if (auth==null) {
             // no auth at all
             return null;
         }
         
        if (auth.getPrincipal() instanceof User) {
            /**
             * this unsual logic is needed to handle properly self profile
             * change
             */
            return SessionSupport.getInstance().getUserForLogin(((User) auth.getPrincipal()).getLogin());
        } else {
            /**
             * this almost all time means that we got anonymous user
             */
            getLogger().debug("getCurrentUser ,unknown principal type: " + auth.getPrincipal());
            return null;
        }
    }

    private static final SecurityHelper instance = new SecurityHelper();
    
    public static SecurityHelper getInstance() {
        return instance;
    }
    
}
