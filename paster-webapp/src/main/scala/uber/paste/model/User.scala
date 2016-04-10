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

import javax.persistence._
import java.util.HashSet
import java.util.ArrayList
import java.util.Collection
import java.util.Set
import javax.validation.constraints.NotNull

import org.hibernate.envers.Audited
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import scala.collection.JavaConversions._
import uber.paste.base.Loggered
import org.apache.commons.lang3.StringUtils
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util
import javax.xml.bind.annotation.XmlTransient
import org.apache.commons.codec.digest.DigestUtils

object User extends Named(null) {

  def createNew = new Builder(new User(null))

  class Builder(model: User) extends Named.Builder[User](model) {

    def addUsername(username: String): Builder = {
      get.setUsername(username); return this
    }

    def addPassword(password: String): Builder = {
      get.setPassword(password); return this
    }

    def addRole(role: Role): Builder = {
      get.addRole(role); return this
    }
  }

}

@Entity
@Indexed(index = "indexes/users") //@Audited
class User(name: String) extends Named(name) with UserDetails with java.io.Serializable {

  def this() = this(null)

  /**
   * username
   */

  @Length(min = 3, max = 250) // @Pattern(regex = "^\\w*$", message = "not a valid username")
  @NotNull(message = "{validator.not-null}")
  @Field
  @Column(nullable = false, length = 50, unique = true)
  private var username: String = null
  
  
  /**
   *  roles
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(joinColumns = Array(new JoinColumn(name = "USER_ID")))
  @Column(name = "ROLE")
  private var roles: Set[String] = new HashSet[String]()

  @XmlTransient
  private var openID: String = null

  
  /**
   * pass
   */
  @NotNull(message = "{validator.not-null}")
  @Length(min = 3, max = 250)
  @XmlTransient
  private var password: String = null

  @Transient
  private var passwordRepeat: String = null

  //  @Length(min = 3, max = 250)
  // @Pattern(regex = "^\\w*$", message = "not a valid username")
  // @NotNull(message = "{validator.not-null}")
  @Field //@Column(nullable = false, length = 50,unique=true)
  //@Basic
  private var email: String = null

  /**
   * if this user account is expired
   */
  private var accountExpired: Boolean = _

  private var credentialsExpired: Boolean = _

  private var totalPastas: Int = _

  private var totalComments: Int = _

  /**
   * if object editable
   */
  private var editable: Boolean = true

  def getAvatarHash(): String =
    if (email != null) {
      DigestUtils.md5Hex(email)
    } else if (username == null) {
      null
    } else {
      DigestUtils.md5Hex(username)
    }

  def isEditable() = editable

  def setEditable(editable: Boolean) {
    this.editable = editable;
  }

  override def isEnabled() = !isDisabled()

  def isAccountExpired() = accountExpired

  /**
   * @see org.springframework.security.userdetails.UserDetails#isAccountNonExpired()
   */
  override def isAccountNonExpired() = !isAccountExpired()
  
  def isAccountLocked()= isDisabled()
  
  /**
   * @see org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
   */

  override def isAccountNonLocked()= !isAccountLocked()
  
  def isCredentialsExpired() =  credentialsExpired
  

  /**
   * @see org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired()
   */
  override def isCredentialsNonExpired() =  !credentialsExpired
  

  def getEmail() = email
  
  def setEmail(mail: String) {
    this.email = mail
  }

  @JsonIgnore
  override def getPassword()= password
  
  def setPassword(password: String) {
    this.password = password 
  }

   
  

  def getTotalPastas() = totalPastas

  def getTotalComments() = totalComments

   
  def increaseTotalPastas() {
    totalPastas += 1
  }
  def increaseTotalComments() {
    totalComments += 1
  }

  @JsonIgnore
  def getOpenID(): String = openID

  def setOpenID(openid: String) { this.openID = openid }

  @Override
  @JsonIgnore
  def getPasswordRepeat() =passwordRepeat
  

  def setPasswordRepeat(password: String) {
    this.passwordRepeat = password
  }

  def isRemoteUser() = openID != null

  def isPasswordEmpty() = StringUtils.isBlank(password)

  def isAdmin() = roles.contains(Role.ROLE_ADMIN.getCode)

  def getRoles()= roles
  
  def setRoles(roles: Set[String]) {
    this.roles = roles
  }

  def addRole(role: Role) {
    roles.add(role.getCode)
  }

  @Override
  def getUsername()= username
  

  def setUsername(username: String) {
    this.username = username
  }

  override def getAuthorities(): Collection[GrantedAuthority] = {
    val out: Collection[GrantedAuthority] = new ArrayList[GrantedAuthority]

    if (roles == null)
      return out

    for (r <- roles) {
      out.add(Role.valueOf(r).asInstanceOf[GrantedAuthority])
    }
    return out
  }

  override def loadFull() {
    getRoles
    getEmail
  }

  

}
