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
import org.codehaus.jackson.annotate.JsonIgnore
import org.compass.annotations.Searchable
import org.compass.annotations.SearchableProperty;
import org.hibernate.envers.Audited
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import scala.collection.JavaConversions._
import uber.paste.base.Loggered
import uber.paste.openid.MD5Util
import org.apache.commons.lang.StringUtils
import java.util
import javax.xml.bind.annotation.XmlTransient

@Entity
@Searchable
@Audited
class User extends Named with UserDetails with java.io.Serializable{

  
  /**
   * логин пользователя 
   */
    
  @Length(min = 3, max = 250)
  // @Pattern(regex = "^\\w*$", message = "not a valid username")
  @NotNull(message = "{validator.not-null}")
  @SearchableProperty
  @Column(nullable = false, length = 50,unique=true)
  private var username:String = null
  /**
   *  набор ролей
   */
   @ElementCollection(fetch = FetchType.EAGER)
   @CollectionTable(joinColumns=Array(new JoinColumn(name="USER_ID")))
   @Column(name="ROLE")
   private var roles:Set[String] = new HashSet[String]()

  @XmlTransient
  private var openID:String  =null


  @OneToMany(fetch = FetchType.EAGER,cascade = Array(CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE))
  @JoinTable(joinColumns = Array(new JoinColumn( name="USER_ID")),
            inverseJoinColumns = Array(new JoinColumn( name="SESSION_ID"))
    )
  @XmlTransient
  private var savedSessions:java.util.List[SavedSession] = new ArrayList[SavedSession]()

  /**
   * пароль
   */
  @NotNull(message = "{validator.not-null}")
  @Length(min = 3, max = 250)
  @XmlTransient
  private var password:String = null
    
  @Transient
  private var passwordRepeat:String = null

//  @Length(min = 3, max = 250)
  // @Pattern(regex = "^\\w*$", message = "not a valid username")
 // @NotNull(message = "{validator.not-null}")
  @SearchableProperty
  //@Column(nullable = false, length = 50,unique=true)
  //@Basic
  private var email:String = null


  /**
   * if this user account is expired
   */
  private var accountExpired:Boolean = _

  private var credentialsExpired:Boolean = _

  private var totalPastas:Int = _

  private var totalComments:Int = _


  /**
   * if object editable
   */
  private var editable:Boolean = true

  def getAvatarHash():String = {
    return if (email!=null) {
      MD5Util.instance.md5Hex(email)
    } else if (username==null) {
      null
    } else {
      MD5Util.instance.md5Hex(username)
    }

  }

  def isEditable():Boolean = {
    return editable
  }

  def setEditable(editable:Boolean)  {
    this.editable = editable;
  }   

  @Override
  def isEnabled():Boolean = {
    return !isDisabled()
  }

  def isAccountExpired():Boolean = {
    return accountExpired;
  }

  /**
   * @see org.springframework.security.userdetails.UserDetails#isAccountNonExpired()
   */

  @Override
  def isAccountNonExpired():Boolean = {
    return !isAccountExpired()
  }


  def isAccountLocked():Boolean = {
    return isDisabled()
  }

  /**
   * @see org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
   */

  @Override
  def isAccountNonLocked():Boolean = {
    return !isAccountLocked()
  }


  def isCredentialsExpired():Boolean = {
    return credentialsExpired
  }

  /**
   * @see org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired()
   */

  @Override
  def isCredentialsNonExpired(): Boolean = {
    return !credentialsExpired;
  }

  def getEmail():String = {
    return email;
  }

  def setEmail(mail:String)  {
    this.email = mail;
  }

  @Override
  @JsonIgnore
  def getPassword():String = {
    return password;
  }

  def setPassword(password:String)  {
    this.password = password;
  }

  @JsonIgnore
  def getSavedSessions():java.util.List[SavedSession] = savedSessions

  @JsonIgnore
  def containsSavedSession(key:String):Boolean = savedSessions.contains(new SavedSession(key))

  @JsonIgnore
  def getSavedSession(key:String):SavedSession = {
    for (s<-savedSessions) {
      if (s.getCode().equals(key)) {
        return s
      }
    }
    return null
  }

  def getTotalPastas() = totalPastas

  def getTotalComments() = totalComments

  def increaseTotalPastas() {
    totalPastas+=1
  }
  def increaseTotalComments() {
    totalComments+=1
  }

  @JsonIgnore
  def getOpenID():String = openID

  def setOpenID(openid:String) { this.openID = openid  }

  @Override
  @JsonIgnore
  def getPasswordRepeat():String = {
    return passwordRepeat;
  }

  def setPasswordRepeat(password:String)  {
    this.passwordRepeat = password;
  }

  def isPasswordEmpty():Boolean = StringUtils.isBlank(password)
    
  def isAdmin():Boolean = return roles.contains(Role.ROLE_ADMIN.getCode)
  
  def getRoles():Set[String] = {
    return roles;
  }

  def setRoles(roles:Set[String] )  {
    this.roles = roles;
  }
    
  def addRole(role:Role) {
    roles.add(role.getCode)
  }

  @Override
  def getUsername():String = {
    return username;
  }

  def setUsername(username:String) {
    this.username = username;
  }

  @Override
  def getAuthorities():Collection[GrantedAuthority] = {
    val out:Collection[GrantedAuthority] =  new ArrayList[GrantedAuthority]
        
    if (roles==null)
      return out;

    for (r <- roles) {
      out.add(Role.valueOf(r).asInstanceOf[GrantedAuthority])
    }
    return out;
  }    
  
  override def loadFull() {
    getRoles
    getEmail
  }
  
  override def toString():String = {
    return Loggered.getNewProtocolBuilder(this)
    .append("username", username)
    .append("roles", roles)
     .append("super",super.toString())
    .toString()
  }
  
  
}
