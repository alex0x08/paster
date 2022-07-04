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


import com.Ox08.paster.webapp.base.{Boot, Loggered}
import com.Ox08.paster.webapp.model.{Role, User}
import org.apache.commons.csv.{CSVFormat, CSVRecord}
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.dao.DataAccessException
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.userdetails.{UserDetails, UserDetailsService, UsernameNotFoundException}
import org.springframework.security.crypto.password.PasswordEncoder

import java.io.{File, FileReader, InputStreamReader}
import java.util
import scala.jdk.CollectionConverters._

object UserManager extends Loggered {

  def getCurrentUser(): User =
    if (SecurityContextHolder.getContext().getAuthentication() == null)
      null
    else
      SecurityContextHolder.getContext().getAuthentication().getPrincipal() match {
        case _: User =>
          SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal().asInstanceOf[User]
        case _ =>
          /**
           * this almost all time means that we got anonymous user
           */
          logger.debug("getCurrentUser ,unknown principal type: {}",
            SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString)
          null
      }
}

class UserManagerImpl extends UserDetailsService with Loggered {

  val users: util.Map[String,User] = new util.HashMap

  @Autowired
  @Qualifier("sessionRegistry")
  val sessionRegistry: SessionRegistry = null

  @Autowired
  val passwordEncoder: PasswordEncoder = null

  def loadUsers(): Unit = {

    val csv = new File(Boot.BOOT.getSystemInfo.getAppHome,"users.csv")

    loadDefaults(csv, (record: CSVRecord) => {
      logger.debug("processing record : {}",record)

      val u = new User(record.get("NAME"),
        record.get("USERNAME"),
        record.get("PASSWORD"),util.Set.of(
        if (record.get("ADMIN").toBoolean) {
          Role.ROLE_ADMIN
        }
        else {
          Role.ROLE_USER
        }))
      save(changePassword(u,u.getPassword()))
    })

  }


  def loadDefaults(csv: File, callback: CSVRecord => Unit): Unit = {
    val r = new FileReader(csv)
    try {
      val records = CSVFormat.DEFAULT.builder().setHeader()
        .setSkipHeaderRecord(true).build().parse(r)
      for (record <- records.asScala) {
        callback(record)
      }
    } finally r.close
  }

  def save(u: User): Unit = {
    users.put(u.getUsername(),u)
    logger.debug("saved {}",u.getUsername())
  }

  @Override
  def getUser(username: String): User = {
      if (users.containsKey(username)) {
        users.get(username)
      } else null
  }

  @Override
  @throws(classOf[UsernameNotFoundException])
  def getUserByUsername(username: String) = getUser(username)

  @PreAuthorize("isAuthenticated()")
  def getUsers = users.values()

  @PreAuthorize("isAuthenticated()")
  def getAllLoggedInUsers() = sessionRegistry.getAllPrincipals()

  @PreAuthorize("isAuthenticated()")
  def saveUser(user: User) = save(user)


  @Secured(Array("ROLE_ADMIN"))
  def removeUser(username: String) = {
    users.remove(username)
  }

  @throws(classOf[UsernameNotFoundException])
  @throws(classOf[DataAccessException])
  override def loadUserByUsername(username: String): UserDetails = {
    val out: User = getUser(username)
    logger.debug("loaded by username {} , user {}", username,out)
    if (out == null)
      throw new UsernameNotFoundException(String.format("User %s not found", username))
    out
  }

  def changePassword(user: User, newPassword: String): User = {
    val encodedPass = passwordEncoder.encode(newPassword)
    logger.debug("changing password for user {}", user.getUsername())
    user.setPassword(encodedPass)

    user
  }


}
