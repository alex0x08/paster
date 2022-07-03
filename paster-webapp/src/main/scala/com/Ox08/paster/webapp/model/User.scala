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

package com.Ox08.paster.webapp.model

import com.Ox08.paster.webapp.base.Loggered
import org.apache.commons.lang3.StringUtils
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util

class User(var name: String,
           username:String,
           var pwd:String,
           roles: util.Set[Role]) extends UserDetails with java.io.Serializable {

  override def isEnabled() = true

  /**
   * @see org.springframework.security.userdetails.UserDetails#isAccountNonExpired()
   */
  override def isAccountNonExpired() = true

  /**
   * @see org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
   */

  override def isAccountNonLocked() = true

  /**
   * @see org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired()
   */
  override def isCredentialsNonExpired() = true

  def getName() = name

  override def getPassword() = pwd

  def setPassword(newPass:String): Unit = {
    pwd = newPass
  }

  def isPasswordEmpty() = StringUtils.isBlank(pwd)

  def isAdmin() = roles.contains(Role.ROLE_ADMIN)

  def getRoles() = roles


  @Override
  def getUsername() = username

  def getAuthorities: util.Collection[_ <: GrantedAuthority] = getRoles()

  override def toString(): String = Loggered.toStringSkip(this,
    Array("password", "passwordRepeat"
    ))

}


object Role {
  val ROLE_ADMIN = new Role("ROLE_ADMIN", "role.admin.name")
  val ROLE_USER = new Role("ROLE_USER", "role.user.name")
}

class Role(code: String, desc: String) extends GrantedAuthority {
  def getAuthority() = code
  def getName() = desc
}

