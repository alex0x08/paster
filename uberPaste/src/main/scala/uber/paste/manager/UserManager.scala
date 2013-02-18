/**
 * Copyright (C) 2010 alex <me@alex.0x08.org>
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

package uber.paste.manager

import org.springframework.stereotype.Service
import uber.paste.dao.UserDao
import uber.paste.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import uber.paste.dao.UserExistsException
import org.springframework.dao.DataAccessException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

trait UserManager extends StructManager[User] {

    def getUserByUsername(username:String): User

    def getUserByOpenID(openid:String): User

  }

@Service("userManager")
class UserManagerImpl extends StructManagerImpl[User] with UserManager with UserDetailsService {

  @Autowired
  val userDao:UserDao = null

  protected override def getDao:UserDao = {
    return userDao
  }

  @Override
  def getUser(userId:String): User = {
    return userDao.get(java.lang.Long.valueOf(userId));
  }

  @Override
  @throws(classOf[UsernameNotFoundException])
  def getUserByUsername(username:String): User = {
    return userDao.getUser(username)
  }

  def getUserByOpenID(openid:String): User = {
    return userDao.getUserByOpenID(openid)
  }

  @Override
  def getUsers(): java.util.List[User] = {
    return userDao.getList()
  }

  @Override
  @throws(classOf[UserExistsException])
  def saveUser(user:User): User = {
    return userDao.save(user)
  }

  @Override
  def removeUser(userId:String) = {
    val u:User = userDao.get(java.lang.Long.valueOf(userId))
    userDao.remove(u.getId)
  }

  @Override
  @throws(classOf[UsernameNotFoundException])
  @throws(classOf[DataAccessException])
  def loadUserByUsername(username:String): UserDetails = {
    val out:User=getUserByUsername(username)

    if (out==null || out.getPassword()==null)
      throw new UsernameNotFoundException("User not found")
    return out
  }

  @Override
  def getUserPassword(username: String): String = {
    val u:User = userDao.getUser(username)
    return if (u==null)
      null
    else u.getPassword()
  }

}
