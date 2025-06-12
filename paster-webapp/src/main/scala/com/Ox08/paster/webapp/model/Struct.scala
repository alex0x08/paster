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
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField
import java.time.{LocalDateTime, ZoneOffset}
import java.util.{Date, Objects}
object Struct extends Logged {
  protected val terms: List[String] = List[String]("id", "name")
  abstract class Builder[T <: java.io.Serializable](obj: T) extends Logged {
    def get(): T = obj
  }
}
trait SearchObject {
  def terms(): List[String]
}
/**
 * Struct model, contains 'lastModified' and 'created'  fields
 * @since 1.0
 * @author 0x08
 */
@MappedSuperclass
abstract class Struct extends DBObject with SearchObject with java.io.Serializable {
  @Column(name = "last_modified")
  @GenericField
  @XStreamAsAttribute
  var lastModified: LocalDateTime = _ // stores date & time of last modification
  @Column(name = "created")
  @GenericField
  @XStreamAsAttribute
  val created: LocalDateTime = LocalDateTime.now() // stores creation date & time

  /**
   * Triggers before persist or merge, used to update modification dates
   */
  @PrePersist
  @PreUpdate
  def touch(): Unit = {
    lastModified = LocalDateTime.now()
    if (Struct.logger.isDebugEnabled)
      Struct.logger.debug("set lastModified to {} objId={}",lastModified,id)
  }

  /**
   * Provides 'last modified' date as java.util.Date,
   * which is used on JSP page by fmt.formatDate function
   * @return
   *    date&time of last modification as java.util.Date object
   */
  def getLastModifiedDt: Date =
    if (lastModified == null.asInstanceOf[LocalDateTime])
      null
  else
      Date.from(lastModified.toInstant(ZoneOffset.UTC))
  def getCreatedDt: Date =
    if (created == null.asInstanceOf[LocalDateTime])
      null
    else
      Date.from(created.toInstant(ZoneOffset.UTC))
  @JsonIgnore
  def getLastModified: LocalDateTime = lastModified
  @JsonIgnore
  def getCreated: LocalDateTime = created
  def terms(): List[String] = Struct.terms
  def loadFull(): Unit = {}
}

/**
 * Abstact database entity
 * @since 1.0
 * @author 0x08
 */
@MappedSuperclass
abstract class DBObject extends java.io.Serializable {
  @Id
  @GeneratedValue(generator = "id_sequence", strategy=GenerationType.SEQUENCE)
  @SequenceGenerator(name = "id_sequence", allocationSize = 10)
  @XStreamAsAttribute
  var id: Integer = _ // unique id
  @XStreamAsAttribute
  var disabled: Boolean = _ // if true - record is disabled (ex. user entity)

  /**
   * Check if record has not been persisted yet by
   * simply compare id with null
   * @return
   *    if true - record is not yet persisted in database
   */
  @JsonIgnore
  def isBlank: Boolean = id == null
  override def hashCode(): Int = {
    var hash: Int = 53 * 7
    if (id != null)
      hash += Objects.hashCode(id)
    hash
  }

  /**
   * Equality is based only on id check, no any other fields will be compared
   * @param from
   * @return
   */
  override def equals(from: Any): Boolean =
    from.isInstanceOf[DBObject] && !isBlank &&
                          from.asInstanceOf[DBObject].id.equals(id)
}

