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

package uber.paste.model

import javax.persistence.Entity
import org.compass.annotations.Searchable
import javax.validation.constraints.NotNull
import org.compass.annotations._
import javax.persistence.Column
import java.util.HashMap
import java.util.Collection
import org.springframework.security.core.GrantedAuthority
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle

object Role extends KeyValueObj[Role]{

  val ROLE_ADMIN = new Role("ROLE_ADMIN","role.admin.name")
  val ROLE_USER = new Role("ROLE_USER","role.user.name")

  add(ROLE_ADMIN)
  add(ROLE_USER)


}

//@Entity
//@Searchable
class Role extends KeyValue with GrantedAuthority with java.io.Serializable{

  def this(code:String,desc:String) = {
    this()
    setCode(code)
    setName(desc)
  }
  
  def getAuthority():String = getCode

}


class RoleEditor extends KeyValueEditor[Role](Role){

}
