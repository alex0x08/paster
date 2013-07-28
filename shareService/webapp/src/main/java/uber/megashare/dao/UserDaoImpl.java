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

import java.util.List;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uber.megashare.model.QUser;
import uber.megashare.model.SavedSession;
import uber.megashare.model.User;

@Repository("userDao")
//@Transactional(readOnly = true,value= "transactionManager")
public class UserDaoImpl extends GenericSearchableDaoImpl<User> implements UserDao {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5893883405835538321L;

	public UserDaoImpl() {
        super(User.class);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,value= "transactionManager",rollbackFor = Exception.class)
    public SavedSession createSession(Long userId) {
    
        User user = get(userId);
        if (user==null) {
            throw new IllegalStateException("user not found!");
        }
        SavedSession s = new SavedSession();
        s.setCode(UUID.randomUUID().toString());
        s.setName("----");
        
        user.getSavedSessions().add(s);
        user = saveObject(user);
        
        return user.getSavedSession(s.getCode());        
    }
    
    public SavedSession getSavedSession(String sessionId) {
      
        List<SavedSession> result = getEntityManager().createQuery("select s from SavedSession s where s.code = :sessionId")
                .setParameter("sessionId", sessionId).getResultList();
                
        // return findOne(QSavedSession.savedSession.code.eq(sessionId));
        return result==null || result.isEmpty() ? null : result.get(0);
    }
    
    public void removeSession(Long userId,String sessionId) {    
        User user = get(userId);        
        user.getSavedSessions().remove(new SavedSession(sessionId));        
        saveObject(user);
        
    }
    
    public User getUserBySession(String sessionId) {
       List users = getEntityManager()
        		.createQuery("select u from User u join u.savedSessions as s where s.code = :sessionId")
        		.setParameter("sessionId", sessionId)
        		.getResultList();
       
       return users.isEmpty() ? null :(User) users.get(0);
        
 //       return findOne(QUser.user.savedSessions..savedSessions...any().code.eq(sessionId));    
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = findOne(QUser.user.login.eq(username));
       
        if (user == null ) {
            throw new UsernameNotFoundException("user '" + username + "' not found.");
        } else {
            return user;
        }
    }

    /** 
     * {@inheritDoc}
     */
    public String getUserPassword(String username) {

        User u = getUserByUsername(username);
        return u != null ? u.getPassword() : null;
    }

    public User getUserByUsername(String username) {
        return findOne(QUser.user.login.eq(username));
    }

    public User getUserByEmail(String mail) {
       return  findOne(QUser.user.email.eq(mail));
    }
 
}
