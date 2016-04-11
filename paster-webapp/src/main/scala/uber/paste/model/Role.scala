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

import org.springframework.security.core.GrantedAuthority

object Role extends KeyObj[Role] {

  val ROLE_ADMIN = new Role("ROLE_ADMIN", "role.admin.name")
  val ROLE_USER = new Role("ROLE_USER", "role.user.name")

  add(ROLE_ADMIN)
  add(ROLE_USER)

}

//@Entity
//@Searchable
class Role(code: String, desc: String) extends Key(code, desc)
  with GrantedAuthority with java.io.Serializable {

  def getAuthority(): String = getCode

  override def create(code: String) = new Role(code, null)

}

class RoleEditor extends KeyEditorEnum[Role](Role) {}
