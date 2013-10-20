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
package uber.megashare.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uber.megashare.model.SavedSession;
import uber.megashare.model.User;
import uber.megashare.model.UserSearchQuery;

public interface UserManager extends GenericSearchableManager<User,UserSearchQuery> {

    /**
     * Gets users information based on login name.
     * @param username the user's username
     * @return userDetails populated userDetails object
     * @throws org.springframework.security.userdetails.UsernameNotFoundException thrown when user not found in database
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * change user's password
     * @param user
     *          selected user
     * @param newPassword
     *          new password
     * @return 
     *         saved user with new password
     */
    User changePassword(User user, String newPassword);

    /**
     * Finds a user by their username.
     * @param username the user's username used to login
     * @return User a populated user object
     * @throws org.springframework.security.userdetails.UsernameNotFoundException
     *         exception thrown when user not found
     */
    User getUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * get user by email
     * @param mail
     *      specified email
     * @return 
     */
    User getUserByEmail(String mail);
    
    SavedSession createSession(Long userId);
    
    SavedSession getSavedSession(String sessionId);
    
    void removeSession(Long userId,String sessionId);
    
    User getUserBySession(String sessionId);
   
    void invalidateSSOCookie(HttpServletResponse response);
   
    String getSSOCookie(HttpServletRequest request);
    
    Cookie createNewSSOCookie(SavedSession s);
    
    String getSSOCookieName();
    
    


}
