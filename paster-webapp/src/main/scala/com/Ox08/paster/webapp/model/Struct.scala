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
import com.fasterxml.jackson.annotation.JsonIgnore
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import jakarta.persistence._
import org.hibernate.annotations.GenericGenerator
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField

import java.time.{LocalDateTime, ZoneId, ZoneOffset}
import java.util.{Date, Objects}
object Struct extends Logged {
  protected val terms: List[String] = List[String]("id", "name")
  abstract class Builder[T <: java.io.Serializable](obj: T) extends Logged {
    def get(): T = obj
  }
}
/**
 * Struct model, have lastModified  field
 */
trait SearchObject {
  def terms(): List[String]
}
@MappedSuperclass
abstract class Struct extends DBObject with SearchObject with java.io.Serializable {
  @Column(name = "last_modified")
  @Temporal(TemporalType.TIMESTAMP)
  @GenericField
  @XStreamAsAttribute
  var lastModified: LocalDateTime = _
  @Column(name = "created")
  @Temporal(TemporalType.TIMESTAMP)
  @GenericField
  @XStreamAsAttribute
  val created: LocalDateTime = LocalDateTime.now()
  @PrePersist
  @PreUpdate
  def touch(): Unit = {
    lastModified = LocalDateTime.now()
    if (Struct.logger.isDebugEnabled)
      Struct.logger.debug("set lastModified to {} objId={}",lastModified,id)
  }
  def getLastModifiedDt: Date =
    if (lastModified == null.asInstanceOf[LocalDateTime])
      null
  else
      Date.from(lastModified.atZone(ZoneId.systemDefault()).toInstant)
  def getCreatedDt: Date =
    if (created == null.asInstanceOf[LocalDateTime])
      null
    else
      Date.from(created.atZone(ZoneId.systemDefault()).toInstant)
  @JsonIgnore
  def getLastModified: LocalDateTime = lastModified
  @JsonIgnore
  def getCreated: LocalDateTime = created
  def terms(): List[String] = Struct.terms
  def loadFull(): Unit = {}
}
/**
 * Most parent entity class
 */
@MappedSuperclass
abstract class DBObject extends java.io.Serializable {
  /**
   * Primary key.
   *
   */
  @Id
  @GeneratedValue(generator = "AllSequenceStyleGenerator")
  @GenericGenerator(name = "AllSequenceStyleGenerator",
    strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator"
  )
  @XStreamAsAttribute
  var id: Integer = _
  /**
   * sign that record is disabled
   */
  @XStreamAsAttribute
  var disabled: Boolean = _
  @JsonIgnore
  def isBlank: Boolean = id == null
  override def hashCode(): Int = {
    var hash: Int = 53 * 7
    if (id != null)
      hash += Objects.hashCode(id)
    hash
  }
  override def equals(from: Any): Boolean =
    from.isInstanceOf[DBObject] && !isBlank && from.asInstanceOf[DBObject].id.equals(id)
}

