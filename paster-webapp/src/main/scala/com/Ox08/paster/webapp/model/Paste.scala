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

import com.Ox08.paster.webapp.base.Loggered
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.{CascadeType, Column, Entity, FetchType, Lob, ManyToMany, MapKey, PrePersist, PreUpdate}
import jakarta.validation.constraints.{NotNull, Size}
import jakarta.xml.bind.annotation.{XmlRootElement, XmlTransient}
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.{FullTextField, Indexed, KeywordField}

import java.util.{ArrayList, UUID}
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
class Tag(tagString: String) extends Named(tagString) {

  @transient
  private var total: Int = 0
  def this() = this(null)
  def getTotal() = total
  def setTotal(i: Int) {
    total = i
  }

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
class Paste(ptitle: String) extends Named(ptitle) with java.io.Serializable {


  def this() = this(null)


  /**
   * unique paste id
   */
  @NotNull(message = "{validator.not-null}")
  @Column(nullable = false, unique = true, length = 255, updatable = false)
  @KeywordField
  //(index = Index.NO, store = Store.YES, termVector = TermVector.NO)
  private val uuid = UUID.randomUUID().toString()

  /**
   * paste's body
   */
  @Lob
  @NotNull
  @FullTextField
  @Size(min = 3, message = "{struct.name.validator}")
  @Column(length = Integer.MAX_VALUE)
  private var text: String = null

  /**
   * link to preview image
   */
  @XmlTransient
  //@Field(store = Store.YES, index = Index.NO)
  private var thumbImage: String = null


  /**
   * paste title
   */
  //@NotNull
  @Column(name="title",length = 256)
  @Size(min = 3, max = 256, message = "{struct.name.validator}")
  @FullTextField
  private var title: String = null

  /**
   * paste owner (author)
   */
  private var owner: String = null

  /**
   * type of paste, used almost to highlight it correctly
   */
  @NotNull
  @KeywordField
  private var codeType: String = CodeType.Plain.getCode()

  /**
   * integration code, used when paste was created in/for some integrated system
   */
  private var integrationCode: String = null

  /**
   * remote url, used when paste was loaded from external site
   */
  private var remoteUrl: String = null

  /**
   * paste's  source, describes where it came from
   */
  @NotNull
  private var channel: String = null

 // @Field(name = "tags", index = Index.YES, store = Store.YES, termVector = TermVector.YES) //,boost=@Boost(2f)
  @KeywordField
  private var tagsAsString: String = null

  private var normalized: Boolean = false

  /**
   * comments relation
   */
  @transient
  private var comments: java.util.List[Comment] = new ArrayList[Comment]()

  @KeywordField
  private var priority: String = null

  private var sticked: Boolean = false

  private[model] var commentsCount: java.lang.Integer = null

  private var symbolsCount: java.lang.Integer = null

  private var wordsCount: java.lang.Integer = null

  private var reviewImgData: String = null

  /**
   * related tags
   */

  @MapKey(name = "name")
  @ManyToMany(fetch = FetchType.EAGER, cascade = Array(CascadeType.ALL))
  private[model] var tagsMap: java.util.Map[String, Tag] = new java.util.HashMap

  @PrePersist
  @PreUpdate
  @unused
  private def onUpdate() {
    commentsCount = getComments().size()
  }

  override def terms(): List[String] = super.terms() ::: List[String]("text", "tags")

  def getSymbolsCount() = symbolsCount

  def setSymbolsCount(c: java.lang.Integer): Unit = {
    this.symbolsCount = c
  }

  def getWordsCount() = wordsCount

  def setWordsCount(c: java.lang.Integer): Unit = {
    this.wordsCount = c
  }


  def getTagsAsString() = tagsAsString

  def setTagsAsString(str: String): Unit = {
    this.tagsAsString = str
  }

  /**
   * paste's owner
   * Can be null when saved by anonymous user
   *
   * @return user instance
   */
  @XmlTransient
  @JsonIgnore
  def getOwner(): String = owner

  def setOwner(u: String): Unit = {
    owner = u
  }

  def isHasOwner() = owner != null

  def getIntegrationCode(): String = integrationCode

  def setIntegrationCode(code: String): Unit = {
    integrationCode = code
  }

  def getTags(): java.util.Set[String] = tagsMap.keySet()

  def getTagsMap(): java.util.Map[String, Tag] = tagsMap

  def getUuid(): String = uuid

  def getPriority(): String = priority

  def setPriority(prior: String): Unit = {
    priority = prior
  }

  @JsonIgnore
  def getThumbImage() = thumbImage

  def setThumbImage(img: String): Unit = {
    thumbImage = img
  }

  @JsonIgnore
  def getReviewImgData() = this.reviewImgData

  def setReviewImgData(img: String): Unit = {
    reviewImgData = img
  }


  def isSticked() = sticked

  def setSticked(b: Boolean): Unit = {
    this.sticked = b
  }

  def isNormalized() = normalized

  def setNormalized(b: Boolean): Unit = {
    this.normalized = b
  }

  def getRemoteUrl() = remoteUrl

  def setRemoteUrl(url: String): Unit = {
    this.remoteUrl = url
  }

  def getChannel() = channel

  def setChannel(s: String): Unit = {
    channel = s
  }

  /**
   * List of supported code types
   * (need to proper syntax highlight)
   *
   * @return list
   */
  def getCodeType(): CodeType = CodeType.valueOf(codeType)

  def setCodeType(f: CodeType): Unit = {
    codeType = f.getCode
  }

  /**
   * Paste's title
   * This field contains small piece of original text, it will be recreated after each object save
   */
  def getTitle() = getName()

  def setTitle(f: String) = setName(f)

  /**
   * Pasta content
   * Blob field stores all text for pasta
   *
   * @return big text
   */
  def getText(): String = text

  def setText(f: String): Unit = {
    this.text = f
  }

  /**
   * @return list of comments
   */
  @XmlTransient
  @JsonIgnore
  def getComments(): java.util.List[Comment] = comments

  def getCommentCount(): java.lang.Integer = commentsCount

  /**
   * loads this instance fully from database
   * MUST be called with opened hibernate session
   */
  override def loadFull(): Unit = {

    getText
    for (c <- comments.asScala) {
      c.loadFull()
    }
  }


  override def toString(): String = Loggered.toStringSkip(this,
    Array("reviewImgData",
      "thumbImage",
      "title",
      "text"))

}
