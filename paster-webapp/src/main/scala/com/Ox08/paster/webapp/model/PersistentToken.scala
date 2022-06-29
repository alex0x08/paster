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

import java.util.{Date, Objects}
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

import java.text.SimpleDateFormat
import javax.persistence.Temporal
import javax.validation.constraints.{NotNull, Size}

object PersistentToken {
  val DATE_TIME_FORMATTER = new SimpleDateFormat("d MMMM yyyy")
  val MAX_USER_AGENT_LEN = 255
}

@Entity
@Table(name = "T_PERSISTENT_TOKEN") //@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class PersistentToken extends java.io.Serializable {

  @Id
  private var series: String = null

  //@JsonIgnore
  @NotNull
  @Column(name = "token_value")
  private var tokenValue: String = null

  // @JsonIgnore
  @Column(name = "token_date") //@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  private var tokenDate: Date = null

  //an IPV6 address max length is 39 characters
  @Size(min = 0, max = 39)
  @Column(name = "ip_address")
  private var ipAddress: String = null

  @Column(name = "user_agent")
  private var userAgent: String = null

  //  @JsonIgnore
  @ManyToOne
  private var user: User = null

  def getSeries() = series

  def setSeries(series: String) {
    this.series = series
  }

  def getTokenValue() = tokenValue

  def setTokenValue(tokenValue: String) {
    this.tokenValue = tokenValue
  }

  def getTokenDate: Date = tokenDate

  def setTokenDate(tokenDate: Date) {
    this.tokenDate = tokenDate;
  }

  def getFormattedTokenDate =
    PersistentToken.DATE_TIME_FORMATTER.format(this.tokenDate.getTime())

  def getIpAddress = ipAddress

  def setIpAddress(ipAddress: String) {
    this.ipAddress = ipAddress
  }

  def getUserAgent = userAgent

  def setUserAgent(userAgent: String) {
    if (userAgent.length() >= PersistentToken.MAX_USER_AGENT_LEN) {
      this.userAgent = userAgent.substring(0, PersistentToken.MAX_USER_AGENT_LEN - 1)
    } else {
      this.userAgent = userAgent
    }
  }

  def getUser = user

  def setUser(user: User) {
    this.user = user
  }

  override def equals(o: Any): Boolean = {
    if (this == o) return true
    val that = o.asInstanceOf[PersistentToken]
    if (!series.equals(that.series)) return false
    true
  }

  override def hashCode() = Objects.hashCode(series)

}
