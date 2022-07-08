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

package com.Ox08.paster.webapp.model

import com.Ox08.paster.webapp.base.Logged
import com.fasterxml.jackson.annotation.JsonIgnore
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import jakarta.persistence.{CascadeType, Column, Entity, FetchType, Lob, ManyToMany, MapKey, PrePersist, PreUpdate, Table}
import jakarta.validation.constraints.{NotNull, Size}
import jakarta.xml.bind.annotation.{XmlRootElement, XmlTransient}
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

}

@Entity
@Indexed(index = "indexes/tags")
@XmlRootElement(name = "tag")
@Table(name = "P_TAGS")
class Tag(tagString: String) extends DBObject {

  @NotNull
  @FullTextField
  @Column(name="tag_name",length = 256)
  @Size(min = 3, message = "{struct.name.validator}")
  @XStreamAsAttribute
  var name: String = tagString

  @transient
  var total: Int = 0

  def this() = this(null)

}


/**
 *
 * This is entity object implements "pasta" : piece of code with id,title and text
 *
 * @author Alex
 */

@Entity
@Indexed(index = "indexes/pastas")
@XmlRootElement(name = "paste")
@Table(name = "P_PASTAS")
class Paste(ptitle: String) extends Struct with java.io.Serializable {


  def this() = this(null)


  /**
   * unique paste id
   */
  @NotNull(message = "{validator.not-null}")
  @Column(nullable = false, unique = true, length = 255, updatable = false)
  @KeywordField
  //(index = Index.NO, store = Store.YES, termVector = TermVector.NO)
  val uuid: String = UUID.randomUUID().toString

  /**
   * paste's body
   */
  @Lob
  @NotNull
  @FullTextField
  @Size(min = 3, message = "{struct.name.validator}")
  @Column(name = "paste_text",length = Integer.MAX_VALUE)
  var text: String = _

  /**
   * link to preview image
   */
  @XmlTransient
  //@Field(store = Store.YES, index = Index.NO)
  @Column(name = "p_thumb_img")
  @JsonIgnore
  var thumbImage: String = _


  /**
   * paste title
   */
  //@NotNull
  @Column(name="paste_title",length = 256)
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

 // @Field(name = "tags", index = Index.YES, store = Store.YES, termVector = TermVector.YES) //,boost=@Boost(2f)
  @FullTextField(name="tags")
  @Column(name = "p_tags")
  var tagsAsString: String = _

  @Column(name = "is_norm")
  var normalized: Boolean = _

  /**
   * comments relation
   */
  @transient
  @XmlTransient
  @JsonIgnore
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
  @JsonIgnore
  var reviewImgData: String = _

  /**
   * related tags
   */

  @MapKey(name = "name")
  @ManyToMany(fetch = FetchType.EAGER, cascade = Array(CascadeType.ALL))
  private[model] var tagsMap: java.util.Map[String, Tag] = new java.util.HashMap

  @PrePersist
  @PreUpdate
  @unused
  private def onUpdate(): Unit= {
    commentsCount = comments.size()
  }


  def getPriority:String = priority // for EL
  def getCodeType:String = codeType
  def isStick:Boolean = stick
  def getTitle:String = title
  def getId: Integer = id
  def getThumbImage:String = thumbImage
  def getAuthor: String = author
  def getText: String = text
  def getCommentsCount: Int = commentsCount
  def getWordsCount: Int = wordsCount
  def getSymbolsCount: Int = symbolsCount
  def getIntegrationCode: String = integrationCode
  def getReviewImgData: String = reviewImgData
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

  def getTagsMap: java.util.Map[String, Tag] = tagsMap


  /**
   * loads this instance fully from database
   * MUST be called with opened hibernate session
   */
  override def loadFull(): Unit = {

    Struct.logger.debug("called loadFull for {}",id)

    for (t <- tagsMap.entrySet().asScala) {
        t.getValue.name
    }

    /*for (c <- comments.asScala) {
      c.loadFull()
    }*/
  }


  override def toString: String = Logged.toStringSkip(this,
    Array("reviewImgData",
      "thumbImage",
      "title",
      "text"))

}
