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
import jakarta.persistence.{Column, Entity, Id, Table}
import jakarta.validation.constraints.{NotNull, Size}

import java.time.LocalDate
object SessionToken {
  val MAX_USER_AGENT_LEN = 255
}
@Entity
@Table(name = "P_USER_SESSIONS")
class SessionToken extends java.io.Serializable {
  @Id
  var series: String = _
  @NotNull
  @Column(name = "token_val")
  var tokenValue: String = _
  @Column(name = "token_dt")
  var tokenDate: LocalDate = _
  //an IPV6 address max length is 39 characters
  @Size(min = 0, max = 39)
  @Column(name = "ipaddr")
  var ipAddress: String = _
  @Column(name = "user_agent")
  private var userAgent: String = _
  @Column(name = "username")
  var username: String = _
  def getUserAgent: String = userAgent
  def setUserAgent(userAgent: String): Unit = {
    if (userAgent.length() >= SessionToken.MAX_USER_AGENT_LEN) {
      this.userAgent = userAgent.substring(0, SessionToken.MAX_USER_AGENT_LEN - 1)
    } else {
      this.userAgent = userAgent
    }
  }
}
