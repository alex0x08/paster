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

package uber.paste.build

import uber.paste.model.User
import uber.paste.model.Role
import uber.paste.base.Loggered
import com.dyuproject.openid.OpenIdUser

object UserBuilder extends Loggered{
  
  def createNew():UserBuilder = {
    return new UserBuilder(new User)
  }
}

class UserBuilder(model:User) extends NamedBuilder[User](model) {

    def addUsername(username:String): UserBuilder =  {
        get.setUsername(username)
        return this
    }   

   def addPassword(password:String):  UserBuilder = {
        get.setPassword(password)
        return this
    }

    def addRole(role:Role): UserBuilder = {
      get.addRole(role)
        return this
    }

  def fillFromOpenIdUser(user:OpenIdUser):UserBuilder= {

    get.setOpenID(user.getIdentity())

    val axschema = user.getAttribute("info").asInstanceOf[java.util.Map[String, String]]

    if (axschema == null || axschema.isEmpty) {
      throw new SecurityException("__axschema:  is empty. This is a bug!")
    }

    addUsername("openid_" + user.getIdentity())

    if (axschema.containsKey("fullname")) {
      val full:String = axschema.get("fullname")
      get.setName(full);
    }

    if (axschema.containsKey("firstname")) {
      get.setName(axschema.get("firstname"))
    }

    if (axschema.containsKey("lastname")) {
      get.setName(get().getName+" "+axschema.get("lastname"))
    }

    if (axschema.containsKey("email")) {
      val mail = axschema.get("email")
      get.setUsername(mail)
      if (get.getName()==null) {
        get.setName(mail)
      }
    }

    get().setUsername(get.getUsername())

    if (logger.isDebugEnabled()) {
      logger.debug("user login="+get.getUsername()+", name={"+get.getName()+"}")
    }

    if (get.getUsername().contains("yandex")) {

      val id = get.getUsername().substring(get.getUsername().lastIndexOf("/"))
      logger.debug("yandex id="+id)

      var p = get.getUsername().substring(0,get.getUsername().lastIndexOf("/"))
      p = p.substring(p.lastIndexOf("/")+1)

      logger.debug("yandex username="+p)
      get.setName(p)
    }

    addPassword(System.currentTimeMillis() + "_openid");
    addRole(Role.ROLE_USER);
    return this;
  }

}
