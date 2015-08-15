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

import scala.collection.JavaConversions._
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import uber.paste.model.{SavedSession, User}
import java.util.UUID



@Repository("userDao")
@Transactional(readOnly = true)
class UserDaoImpl extends StructDaoImpl[User](classOf[User])  {

  def createSession(userId:java.lang.Long):SavedSession = {
      var user:User  = get(userId)

     val session = new SavedSession(UUID.randomUUID().toString)
     

    user.getSavedSessions().add(session)
    user  = save(user)

    logger.debug("user session saved. user sessions:")
    for (s<-user.getSavedSessions()) {
      logger.debug("session:"+s)
    }

    return user.getSavedSession(session.getName())
  }

  def getSession(sessionId:String):SavedSession = {
    
  val result = em.createQuery("select s from SavedSession s where s.name = :sessionId")
      .setParameter("sessionId", sessionId)
      .getResultList()

    return if (result==null || result.isEmpty()) null else result.get(0).asInstanceOf[SavedSession]

  }

  def removeSession(userId:java.lang.Long,sessionId:String) {

    val user:User = get(userId)

    user.getSavedSessions().remove(new SavedSession(sessionId))

    save(user)


 }

  def getUserBySession(sessionId:String):User = {

    val result =  em.createQuery("select u from User u join u.savedSessions as s where s.name = :sessionId")
      .setParameter("sessionId", sessionId)
      .getResultList()

    if (logger.isDebugEnabled)
      logger.debug("_getUser for session={}, results {}",sessionId,result.size())

    return if (result==null || result.isEmpty()) null else result.get(0).asInstanceOf[User]
  }


  def getUser(username:String) = getSingleByKeyValue("username", username)
 

  def getUserByOpenID(openid:String) = getSingleByKeyValue("openID", openid)
  

}

class UserExistsException extends Exception with java.io.Serializable{

}
