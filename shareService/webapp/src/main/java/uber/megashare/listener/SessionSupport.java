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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import uber.megashare.base.LoggedClass;
import uber.megashare.model.User;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Синлгтон, хранилище связки id сессии - логин юзера
 *
 * @author alex
 */
public class SessionSupport extends LoggedClass {

    /**
     *
     */
    private static final long serialVersionUID = -5393186807111808503L;
    
    private final BiMap<String, //session
            String //login
            > store = HashBiMap.create();
    private final Map<String, //login
            User //user
            > user_store = new HashMap<>();
    private final Object lock = new Object();

    /**
     *
     * @param login логин юзера
     * @return есть ли у этого юзера сессия
     */
    public boolean isSessionForUser(String login) {
        return store.containsValue(login);
    }

    /**
     *
     * @param sessionID id сессии
     * @return зарегистрирован ли логин юзера для этой сессии
     */
    public boolean isUserForSession(String sessionID) {
        return store.containsKey(sessionID);
    }

    public boolean isUserForLogin(String login) {
        return user_store.containsKey(login);
    }

    public void updateUser(User u) {

        if (!isUserForLogin(u.getUsername())) {
            return;
        }

        String sessionID = getSessionForLogin(u.getUsername());

        add(sessionID, u);

    }

    /**
     * добавить связку id-сессии - логин в хранилище
     *
     * @param sessionID id-сессии
     * @param user юзер
     */
    public void add(String sessionID, User user) {
        /**
         * если такая связка уже есть - удаляем ее
         */
        if (isSessionForUser(user.getUsername())) {
            remove(getSessionForLogin(user.getUsername()));
        }
        synchronized (lock) {
            store.put(sessionID, user.getUsername());
            user_store.put(user.getUsername(), user);
        }
    }

    /**
     * удалить связку id сессии - логин юзера из хранилища
     *
     * @param sessionID
     */
    void remove(String sessionID) {
        synchronized (lock) {
            user_store.remove(store.get(sessionID));
            store.remove(sessionID);
        }
    }

    /**
     *
     * @return список id всех сессий
     */
    public List<User> getSessions() {
        return getSessions(null);
    }

    /**
     *
     * @param skipLogin логин пользователя, который нужно убрать из списка
     * @return список всех id сессий в системе
     */
    public List<User> getSessions(String skipLogin) {

        List<User> out = new ArrayList<>();

        for (Entry<String, User> e : user_store.entrySet()) {
            if (e.getKey().equals(skipLogin)) {
                continue;
            }
            out.add(e.getValue());
        }

        return Collections.unmodifiableList(out);
    }

    /**
     *
     * @param sessionID id сессии
     * @return логин пользователя для указанной сессии
     */
    public String getLoginForSession(String sessionID) {
        return !isUserForSession(sessionID) ? null : store.get(sessionID);
    }

    public User getUserForLogin(String login) {
        return !isUserForLogin(login)? null : user_store.get(login);
    }

    /**
     *
     * @param login логин юзера
     * @return id сессии
     */
    public String getSessionForLogin(String login) {
        return !isSessionForUser(login) ? null :store.inverse().get(login);
    }
    private static final SessionSupport INSTANCE = new SessionSupport();

    public static SessionSupport getInstance() {
        return INSTANCE;
    }
}
