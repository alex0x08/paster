/*
 * Copyright 2015 Ubersoft, LLC.
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

import jakarta.persistence.{Column, Entity, Id, Table, Temporal, TemporalType}
import jakarta.validation.constraints.{NotNull, Size}

import java.text.SimpleDateFormat
import java.util.{Date, Objects}

object SessionToken {
  val DATE_TIME_FORMATTER = new SimpleDateFormat("d MMMM yyyy")
  val MAX_USER_AGENT_LEN = 255
}

@Entity
@Table(name = "P_USER_SESSIONS")
class SessionToken extends java.io.Serializable {

  @Id
  private var series: String = null

  @NotNull
  @Column(name = "token_val")
  private var tokenValue: String = null

  @Column(name = "token_dt")
  @Temporal(TemporalType.TIMESTAMP)
  private var tokenDate: Date = null

  //an IPV6 address max length is 39 characters
  @Size(min = 0, max = 39)
  @Column(name = "ipaddr")
  private var ipAddress: String = null

  @Column(name = "user_agent")
  private var userAgent: String = null

  @Column(name = "username")
  private var username: String = null

  def getSeries() = series

  def setSeries(series: String): Unit = {
    this.series = series
  }

  def getTokenValue() = tokenValue

  def setTokenValue(tokenValue: String): Unit = {
    this.tokenValue = tokenValue
  }

  def getTokenDate: Date = tokenDate

  def setTokenDate(tokenDate: Date): Unit = {
    this.tokenDate = tokenDate;
  }

  def getFormattedTokenDate =
    SessionToken.DATE_TIME_FORMATTER.format(this.tokenDate.getTime())

  def getIpAddress = ipAddress

  def setIpAddress(ipAddress: String): Unit = {
    this.ipAddress = ipAddress
  }

  def getUserAgent = userAgent

  def setUserAgent(userAgent: String): Unit = {
    if (userAgent.length() >= SessionToken.MAX_USER_AGENT_LEN) {
      this.userAgent = userAgent.substring(0, SessionToken.MAX_USER_AGENT_LEN - 1)
    } else {
      this.userAgent = userAgent
    }
  }

  def getUsername() = username

  def setUsername(user: String): Unit = {
    this.username = user
  }

}