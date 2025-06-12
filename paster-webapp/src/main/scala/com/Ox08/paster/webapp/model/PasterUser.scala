/*
 * Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.model
import com.Ox08.paster.webapp.base.Logged
import org.apache.commons.lang3.StringUtils
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util

/**
 * Paster user entity
 * @param name
 *          full name
 * @param username
 *          login
 * @param pwd
 *          password
 * @param roles
 *          set of roles (see below)
 */
class PasterUser(name: String,
                 username: String,
                 var pwd: String,
                 roles: util.Set[Role]) extends UserDetails with java.io.Serializable {
  override def isEnabled = true // part of Spring Security's UserDetails, mean user is not disabled
  /**
   * user not expired
   * @see org.springframework.security.userdetails.UserDetails#isAccountNonExpired()
   */
  override def isAccountNonExpired = true
  /**
   * user account is not locked
   * @see org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
   */
  override def isAccountNonLocked = true
  /**
   * password is not expired
   * @see org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired()
   */
  override def isCredentialsNonExpired = true
  def getName: String = name
  override def getPassword: String = pwd
  def setPassword(newPass: String): Unit = {
    pwd = newPass
  }
  def isPasswordEmpty: Boolean = StringUtils.isBlank(pwd)
  def isAdmin: Boolean = roles.contains(Role.ROLE_ADMIN)
  def getRoles: util.Set[Role] = roles
  @Override
  def getUsername: String = username
  def getAuthorities: util.Collection[_ <: GrantedAuthority] = getRoles
  override def toString: String =
    Logged.toStringSkip(this, Array("pwd"))
}

/**
 * Set of system roles
 */
object Role {
  // administrator with full access
  val ROLE_ADMIN = new Role("ROLE_ADMIN", "role.admin.name")
  // ordinary user
  val ROLE_USER = new Role("ROLE_USER", "role.user.name")
}

/**
 * System role
 *
 * @param code
 *          code name, ex ROLE_ADMIN
 * @param desc
 *          role description
 */
class Role(code: String, desc: String) extends GrantedAuthority {
  def getAuthority: String = code
  def getName: String = desc
}
