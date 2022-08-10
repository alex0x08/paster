/**
 * Copyright (C) 2010 alex <me@alex.0x08.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.manager
import com.Ox08.paster.webapp.base.{Boot, Logged, SystemError}
import com.Ox08.paster.webapp.model.{PasterUser, Role}
import org.apache.commons.csv.{CSVFormat, CSVRecord}
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.dao.DataAccessException
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.userdetails.{UserDetails, UserDetailsService, UsernameNotFoundException}
import org.springframework.security.crypto.password.PasswordEncoder

import java.io.{File, FileReader}
import java.util
import scala.collection.mutable
import scala.jdk.CollectionConverters._
/**
 * Static object to deal with security context and sessions
 */
object UserManager extends Logged {
  /**
   * Retrieve current user, if authenticated
   * @return
   *    PasterUser object or null
   */
  def getCurrentUser: PasterUser = {
    val auth:Authentication = SecurityContextHolder.getContext.getAuthentication
    if (auth == null)
      null
    else {
      val principal = auth.getPrincipal
      principal match {
        case _: PasterUser =>
          principal.asInstanceOf[PasterUser]
        case _ =>
          /**
           * this almost all time means that we got anonymous user
           */
          if (logger.isDebugEnabled)
              logger.debug("getCurrentUser ,unknown principal type: {}",
              principal.toString)
          null
      }
    }
  }

  /**
   * Loads users from CSV file
   * @param csv
   *    source file
   * @param callback
   *    callback for single record
   */
  def loadUsersFromCSV(csv: File, callback: CSVRecord => Unit): Unit = {
    if (!csv.exists() || !csv.isFile)
      throw SystemError.withError(0x6001,
        s"CSV file '${csv.getName}' with users does not exist!")
    if (csv.length()==0)
      throw SystemError.withError(0x6001,
        s"CSV file '${csv.getName}' with users is empty!")
    val r = new FileReader(csv)
    try {
      val records = CSVFormat.DEFAULT.builder().setHeader()
        .setSkipHeaderRecord(true).build().parse(r)
      var usersCount:Int = 0
      for (record <- records.asScala) {
        if (usersCount> 500) {
          throw new RuntimeException(s"Too many users defined: ${records.getRecords.size()} Processed only first 500")
          //return
        }
        callback(record)
        usersCount+=1
      }
    } finally r.close()
  }
}
/**
 * A service class to work with users
 * We store users in .csv file for simplicity, load them during boot and put in hashmap.
 */
class UserManager extends UserDetailsService with Logged {
  // runtime storage for users
  private val users = mutable.Map[String, PasterUser]()
  @Autowired
  @Qualifier("sessionRegistry")
  val sessionRegistry: SessionRegistry = null
  @Autowired
  val passwordEncoder: PasswordEncoder = null
  def loadUsers(): Unit = {
    val csv = new File(Boot.BOOT.getSystemInfo.getAppHome, "users.csv")
    UserManager.loadUsersFromCSV(csv, (record: CSVRecord) => {
      if (logger.isDebugEnabled)
        logger.debug("processing record : {}", record)
      val u = new PasterUser(record.get("NAME"),
        record.get("USERNAME"),
        record.get("PASSWORD"), util.Set.of(
          if (record.get("ISADMIN").toBoolean)
            Role.ROLE_ADMIN
          else
            Role.ROLE_USER))
      save(changePassword(u, u.getPassword()))
    })
  }

  /**
   *  Virtually saves user
   * @param u
   *    paster user object
   */
  def save(u: PasterUser): Unit = {
    users.put(u.getUsername(), u)
    if (logger.isDebugEnabled)
        logger.debug("saved {}", u.getUsername())
  }
  @Override
  def getUser(username: String): PasterUser = {
    if (users contains username)
      users(username)
    else null
  }
  @Override
  @throws(classOf[UsernameNotFoundException])
  def getUserByUsername(username: String): PasterUser = getUser(username)
  @PreAuthorize("isAuthenticated()")
  def getUsers: Iterable[PasterUser] = users.values
  @PreAuthorize("isAuthenticated()")
  def getAllLoggedInUsers: util.List[AnyRef] = sessionRegistry.getAllPrincipals
  @PreAuthorize("isAuthenticated()")
  def saveUser(user: PasterUser): Unit = save(user)
  @Secured(Array("ROLE_ADMIN"))
  def removeUser(username: String): PasterUser = {
    (users remove username).get
  }
  @throws(classOf[UsernameNotFoundException])
  @throws(classOf[DataAccessException])
  override def loadUserByUsername(username: String): UserDetails = {
    val out: PasterUser = getUser(username)
    if (logger.isDebugEnabled)
        logger.debug("loaded by username {} , user {}", username, out)
    if (out == null)
      throw new UsernameNotFoundException(s"User $username not found")
    out
  }
  def changePassword(user: PasterUser, newPassword: String): PasterUser = {
    val encodedPass = passwordEncoder.encode(newPassword)
    if (logger.isDebugEnabled)
      logger.debug("changing password for user {}", user.getUsername)
    user.setPassword(encodedPass)
    user
  }
}
