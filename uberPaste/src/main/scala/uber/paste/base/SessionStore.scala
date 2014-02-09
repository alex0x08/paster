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

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.List
import java.util.Map
import uber.paste.model.User
import scala.collection.JavaConversions._

object SessionStore {
  
  val instance = new SessionStore()
 
}

class SessionStore {

  private val store:BiMap[String,String] = HashBiMap.create()

  private val user_store:Map[String,User] = new HashMap[String,User]

  private val lock = new Object
    

  /**
   *
   * @param login логин юзера
   * @return есть ли у этого юзера сессия
   */
  def isSessionForUser(login:String):Boolean = {
    return store.containsValue(login)
  }

  /**
   *
   * @param sessionID id сессии
   * @return зарегистрирован ли логин юзера для этой сессии
   */
  def isUserForSession(sessionID:String):Boolean = {
    return store.containsKey(sessionID);
  }

  def isUserForLogin(login:String):Boolean = {
    return user_store.containsKey(login)
  }

  def updateUser(u:User) {

    if (!isUserForLogin(u.getUsername())) {
      return;
    }

    //var sessionID:String = getSessionForLogin(u.getUsername())

    add(getSessionForLogin(u.getUsername()), u);
      
  }

  /**
   * добавить связку id-сессии - логин в хранилище
   * @param sessionID id-сессии
   * @param user  юзер
   */
  def add(sessionID:String, user:User) {
    /**
     * если такая связка уже есть - удаляем ее 
     */
    if (isSessionForUser(user.getUsername())) {
      remove(getSessionForLogin(user.getUsername()));
    }
        
    lock.synchronized
    {
       
      store.put(sessionID, user.getUsername());
      user_store.put(user.getUsername(), user);
    
      lock.notifyAll()
    }

  }

  /**
   * удалить связку id сессии - логин юзера из хранилища
   * @param sessionID
   */
  def remove(sessionID:String) {
      
    lock.synchronized
    {
       
      user_store.remove(store.get(sessionID));
      store.remove(sessionID);
    
      lock.notifyAll()
    }

  }

  /**
   *
   * @return список id всех сессий
   */
  def getSessions():List[User] = {
    return getSessions(null);
  }

  /**
   *
   * @param skipLogin логин пользователя, который нужно убрать из списка
   * @return список всех id сессий в системе
   */
  def getSessions(skipLogin:String):java.util.List[User] = {

    val out = new ArrayList[User]        

    for (i <- user_store.entrySet()) {
     
      if (!i.getKey().equals(skipLogin)) 
                out.add(i.getValue());
    
    }

    return Collections.unmodifiableList(out);
  }

  /**
   *
   * @param sessionID id сессии
   * @return логин пользователя для указанной сессии
   */
  def getLoginForSession(sessionID:String):String = {

    return if (!isUserForSession(sessionID)) {
      null
    } else {
      store.get(sessionID)
    }
 }

  def getUserForLogin(login:String):User = {
    
    return if (!isUserForLogin(login)) {
     null
    } else {
      user_store.get(login)
    }
  }


  /**
   *
   * @param login логин юзера
   * @return id сессии
   */
  def getSessionForLogin(login:String):String = {
    return if (!isSessionForUser(login)) {
      null
    } else {
      store.inverse().get(login)
    }
  }
    
  
}
