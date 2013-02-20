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

package uber.paste.dao

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import uber.paste.model.{SavedSession, User}
import java.util.UUID


trait UserDao extends StructDao[User]{

  def getUserBySession(sessionId:String):User

  def removeSession(sessionId:String)

  def createSession(userId:java.lang.Long):SavedSession

  def getSession(sessionId:String):SavedSession

  def getUser(username:String):User

  def getUserByOpenID(openid:String):User

  }


@Repository("userDao")
@Transactional(readOnly = true)
class UserDaoImpl extends StructDaoImpl[User](classOf[User]) with UserDao {

  def createSession(userId:java.lang.Long):SavedSession = {
      var user:User  = get(userId);
     val session = new SavedSession
    session.setCode(UUID.randomUUID().toString)

    user.getSavedSessions().add(session)
    user  = save(user)
    return getSession(session.getCode())
  }

  def getSession(sessionId:String):SavedSession = {
  val result = em.createQuery("select s from SavedSession s where s.key = :sessionId")
      .setParameter("sessionId", sessionId)
      .getResultList()

    return if (result==null || result.isEmpty()) null else result.get(0).asInstanceOf[SavedSession]

  }

  def removeSession(sessionId:String) {
    em.remove(getSession(sessionId))
 }

  def getUserBySession(sessionId:String):User = {

    val result =  em.createQuery("select u from User u where u.savedSession.key = :sessionId")
      .setParameter("sessionId", sessionId)
      .getResultList()

    if (logger.isDebugEnabled)
      logger.debug("_getUser for session="+sessionId+",results="+result.size());

    return if (result==null || result.isEmpty()) null else result.get(0).asInstanceOf[User]
  }


  def getUser(username:String):User = {

    val result =  em.createQuery("select u from User u where u.username = :username")
      .setParameter("username", username)
      .getResultList()

    if (logger.isDebugEnabled)
      logger.debug("_getUser for name="+username+",results="+result.size());

    return if (result==null || result.isEmpty()) null else result.get(0).asInstanceOf[User]
  }

  def getUserByOpenID(openid:String):User = {

    val result = em.createQuery("select u from User u where u.openID = :openid")
            .setParameter("openid", openid)
            .getResultList()

    if (logger.isDebugEnabled)
      logger.debug("_getUser for openid="+openid+",results="+result.size());

    return if (result==null || result.isEmpty()) null else result.get(0).asInstanceOf[User]
  }

}

class UserExistsException extends Exception with java.io.Serializable{

}
