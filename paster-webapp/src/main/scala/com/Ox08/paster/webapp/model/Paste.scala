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
import com.thoughtworks.xstream.annotations.XStreamOmitField
import com.thoughtworks.xstream.annotations.XStreamAlias
import jakarta.persistence.{CascadeType, Column, Entity, FetchType, GeneratedValue, GenerationType, Id, Lob, ManyToMany, MapKey, PrePersist, PreUpdate, SequenceGenerator, Table}
import jakarta.validation.constraints.{NotNull, Size}
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.{FullTextField, Indexed, KeywordField}

import java.util
import java.util.UUID
import scala.annotation.unused
import scala.jdk.CollectionConverters._
/**
 * paste model constants
 */
object Paste extends Struct {
  /**
   * max title length, if greater - will be cut
   */
  val TITLE_LENGTH = 256

  // not used
  override def getId: Integer = 1
  override def setId(id:Integer): Unit = {}
}

/**
 * Tag entity
 * @since 1.0
 * @author 0x08
 * @param tagString
 */
@Entity
@Indexed(index = "indexes/tags")
@XStreamAlias("Tag")
@Table(name = "P_TAGS")
class Tag(tagString: String) extends DBObject {

  @Id
  @GeneratedValue(generator = "tags_id_seq", strategy=GenerationType.SEQUENCE)
  @SequenceGenerator(name = "tags_id_seq", allocationSize = 50)
  @XStreamAsAttribute
  var id: Integer = _

  @NotNull
  @FullTextField
  @Column(name = "tag_name", length = 256)
  @Size(min = 3, message = "{struct.name.validator}")
  @XStreamAsAttribute
  var name: String = tagString // single tag
  @transient
  var total: Int = 0 // count of occurrences
  def this() = this(null)
  override def getId: Integer = id
  override def setId(id:Integer): Unit = {
    this.id = id;
  }
}
/**
 *
 * This is entity object implements "pasta" : piece of code with id,title and text
 *
 * @author 0x08
 * @since 1.0
 *
 */
@Entity
@Indexed(index = "indexes/pastas")
@XStreamAlias("Paste")
@Table(name = "P_PASTAS")
class Paste extends Struct with java.io.Serializable {

  @Id
  @GeneratedValue(generator = "paste_id_seq", strategy=GenerationType.SEQUENCE)
  @SequenceGenerator(name = "paste_id_seq", allocationSize = 50)
  @XStreamAsAttribute
  var id: Integer = _

  /**
   * unique paste id
   */
  @NotNull(message = "{validator.not-null}")
  @Column(nullable = false, unique = true, length = 255, updatable = false)
  @KeywordField
  val uuid: String = UUID.randomUUID().toString
  /**
   * paste's body
   */
  @Lob
  @NotNull
  @FullTextField
  @Size(min = 3, message = "{struct.name.validator}")
  @Column(name = "paste_text", length = Integer.MAX_VALUE)
  var text: String = _
  /**
   * link to preview image (file id)
   */
  @XStreamOmitField
  @Column(name = "p_thumb_img")
  @JsonIgnore
  var thumbImage: String = _
  /**
   * paste title
   */
  @Column(name = "paste_title", length = 256)
  @Size(min = 3, max = 256, message = "{struct.name.validator}")
  @FullTextField
  var title: String = _
  /**
   * paste owner (author)
   */
  @Column(name = "author_username")
  @KeywordField
  var author: String = _
  /**
   * type of paste, used almost to highlight it correctly
   */
  @NotNull
  @KeywordField
  @Column(name = "code_type")
  var codeType: String = _
  /**
   * integration code, used when paste was created in/for some integrated system
   */
  @Column(name = "ext_code")
  var integrationCode: String = _
  /**
   * remote url, used when paste was loaded from external site
   */
  @Column(name = "remote_url")
  var remoteUrl: String = _
  /**
   * paste's  source, describes where it came from
   */
  @NotNull
  @Column(name = "p_channel")
  var channel: String = _
  @FullTextField(name = "tags")
  @Column(name = "p_tags")
  var tagsAsString: String = _
  @Column(name = "is_norm")
  var normalized: Boolean = _
  /**
   * comments relation
   */
  @transient
  @XStreamOmitField
  val comments: java.util.List[Comment] = new util.ArrayList[Comment]()
  @KeywordField
  @Column(name = "p_prior")
  var priority: String = _
  @Column(name = "is_stick")
  var stick: Boolean = _
  @Column(name = "comments_count")
  var commentsCount: Int = _
  @Column(name = "symbols_count")
  var symbolsCount: Int = _
  @Column(name = "words_count")
  var wordsCount: Int = _
  @Column(name = "review_img")
  @XStreamOmitField
  @JsonIgnore
  var reviewImgData: String = _
  /**
   * related tags
   */
  @MapKey(name = "name")
  @ManyToMany(fetch = FetchType.EAGER, cascade = Array(CascadeType.ALL))
  @XStreamOmitField
  private[model] var tagsMap: java.util.Map[String, Tag] = new java.util.HashMap
  @PrePersist
  @PreUpdate
  @unused
  private def onUpdate(): Unit = {
    commentsCount = comments.size()
  }

  override def getId:Integer = id
  override def setId(id:Integer): Unit = {
    this.id = id;
  }
  def getPriority: String = priority // for EL
  def getCodeType: String = codeType
  def isStick: Boolean = stick
  def getTitle: String = title
  def getThumbImage: String = thumbImage
  def getAuthor: String = author
  def getText: String = text
  def getCommentsCount: Int = commentsCount
  def getWordsCount: Int = wordsCount
  def getSymbolsCount: Int = symbolsCount
  def getIntegrationCode: String = integrationCode
  def getReviewImgData: String = reviewImgData
  @JsonIgnore
  def getComments: util.List[Comment] = comments
  override def terms(): List[String] = super.terms() ::: List[String]("text", "tags")
  /**
   * paste's owner
   * Can be null when saved by anonymous user
   *
   * @return user instance
   */
  def isHasAuthor: Boolean = author != null
  def getTags: java.util.Set[String] = tagsMap.keySet()
  @JsonIgnore
  def getTagsMap: java.util.Map[String, Tag] = tagsMap
  /**
   * Loads this instance fully from database.
   * MUST be called inside opened transaction!
   */
  override def loadFull(): Unit = {
    if (Struct.logger.isDebugEnabled)
      Struct.logger.debug("called loadFull for {}", id)
    for (t <- tagsMap.entrySet().asScala)
      t.getValue.name
  }
  override def toString: String = Logged.toStringSkip(this,
    Array("reviewImgData",
      "thumbImage",
      "tagsMap",
      "title",
      "text"))
}
