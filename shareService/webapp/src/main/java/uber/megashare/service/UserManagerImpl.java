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
package uber.megashare.service;

import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import uber.megashare.base.logging.LoggedCall;
import uber.megashare.dao.UserDao;
import uber.megashare.listener.SessionHelper;
import uber.megashare.model.SavedSession;
import uber.megashare.model.User;
import uber.megashare.model.UserSearchQuery;

@Service("userManager")
public class UserManagerImpl extends GenericSearchableManagerImpl<User, UserSearchQuery> implements UserManager, UserDetailsService {

    /**
     *
     */
    private static final long serialVersionUID = 5771857264720031121L;
    
    private UserDao userRepository;
    
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserManagerImpl(UserDao userRepo, PasswordEncoder passwordEncoder) {
        super(userRepo);

        this.userRepository = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public String getSSOCookieName() {
        return SavedSession.SSO_COOKIE_NAME;
    }
    

    @Override
    public void invalidateSSOCookie(HttpServletResponse response) {

        Cookie c = new Cookie(SavedSession.SSO_COOKIE_NAME, null);
        c.setMaxAge(-1);
        c.setPath("/");
        response.addCookie(c);
    }

    @Override
    public String getSSOCookie(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie c : request.getCookies()) {
            if (SavedSession.SSO_COOKIE_NAME.equals(c.getName()) && !StringUtils.isBlank(c.getValue())) {
                String cv = new String(Base64.decode(c.getValue().getBytes()));
                getLogger().debug("found sso cookie value " + cv);
                return cv;
            }

        }
        return null;
    }

    @Override
    public Cookie createNewSSOCookie(SavedSession s) {
    
        Cookie out = new Cookie(SavedSession.SSO_COOKIE_NAME,new String(Base64.encode(s.getCode().getBytes())));
        out.setMaxAge(60 * 60 * 60 * 60);
        out.setPath("/");
        
        return out;
    }
    
    //@Override
    @Override
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username);

    }

    //@Override
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        return userRepository.loadUserByUsername(username);
    }

    @Override
    public SavedSession createSession(Long userId) {
        return userRepository.createSession(userId);
    }

    @Override
    public SavedSession getSavedSession(String sessionId) {
        return userRepository.getSavedSession(sessionId);
    }

    public void removeSession(Long userId, String sessionId) {
        userRepository.removeSession(userId, sessionId);
    }

    public User getUserBySession(String sessionId) {
        return userRepository.getUserBySession(sessionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public User save(User object) {
        getLogger().debug("___UserManagerImpl.save obj=" + object);

         //object = changePassword(object, object.getPassword());

        object=dao.saveObject(object);
       
        SessionHelper.getInstance().updateUser(object);

       
        return object;
    }

    @Override
    public User changePassword(final User user, final String newPassword) {

        return new LoggedCall<User>() {
            @Override
            public User callImpl(ToStringBuilder log) throws Exception {
                log.append("changePassword for user" + user);

                // Get and prepare password management-related artifacts
                boolean passwordChanged = false;

                if (passwordEncoder != null) {

                    log.append("user blank=" + user.isBlank() + ",passwordSet=" + user.isPasswordSet());

                    // Check whether we have to encrypt (or re-encrypt) the password
                    if (user.isBlank() || !user.isPasswordSet() || !user.getPassword().equals(newPassword)) {
                        passwordChanged = true;
                    }

                    // If password was changed (or new user), encrypt it
                    if (passwordChanged) {
                        user.setPassword(passwordEncoder.encodePassword(newPassword,null));
                       // user.setPassword(passwordEncoder.encode(newPassword));
                        log.append("__password was encrypted");
                    }


                } else {
                    log.append("PasswordEncoder not set, skipping password encryption...");
                }

                return user;

            }
        }.call();

    }

    //@Secured("ROLE_ADMIN")
    public User getUserByEmail(String mail) {
        return userRepository.getUserByEmail(mail);
    }

    @Secured({"ROLE_ADMIN","ROLE_USER","ROLE_MANAGER"})
    @Override
    public List<User> getAll() {
        return super.getAll();
    }

    @Override
    public User getFull(Long id) {
        return userRepository.getFull(id);
    }
}