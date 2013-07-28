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
package uber.megashare.dao;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uber.megashare.model.SavedSession;
import uber.megashare.model.User;

public interface UserDao extends GenericSearchableDao<User> {

    /**
     * Retrieves the password in DB for a user
     * @param username the user's username
     * @return the password in DB, if the user is already persisted
     */
    String getUserPassword(String username);

    User getUserByUsername(String username);

    /**
     * Gets users information based on login name.
     * @param username the user's username
     * @return userDetails populated userDetails object
     * @throws org.springframework.security.userdetails.UsernameNotFoundException thrown when user not found in database
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    User getUserByEmail(String mail);
    
    SavedSession createSession(Long userId);
    
    SavedSession getSavedSession(String sessionId);
    
    void removeSession(Long userId,String sessionId);
    
    User getUserBySession(String sessionId);
   
   
}
