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
import com.fasterxml.jackson.annotation.JsonIgnore
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import jakarta.persistence.{Column, Entity, GeneratedValue, GenerationType, Id, Lob, SequenceGenerator, Transient}
import jakarta.validation.constraints.{NotNull, Size}
import jakarta.xml.bind.annotation.XmlRootElement
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.{FullTextField, Indexed}
/**
 * A comment for single line of paste
 * @since 1.0
 *
 */
@Entity
@Indexed(index = "indexes/comments")
@XmlRootElement(name = "comment")
class Comment extends Struct with java.io.Serializable {
  @Id
  @GeneratedValue(generator = "comment_id_seq", strategy=GenerationType.SEQUENCE)
  @SequenceGenerator(name = "comment_id_seq", allocationSize = 50)
  @XStreamAsAttribute
  var id: Integer = _

  /**
   * id of paste entity that owns this comment
   */
  @XStreamAsAttribute
  @Column(name = "paste_id", nullable = false)
  var pasteId: Integer = _
  /*
    comment text
   */
  @Lob
  @NotNull
  @FullTextField
  @Size(min = 3, message = "{struct.name.validator}")
  @Column(name = "comment_text")
  var text: String = _
  /**
   * a username of comment's author, if any
   */
  @Column(name = "author_username")
  var author: String = _
  /**
   * a line number, which this comment related to
   */
  @Column(name = "line_num")
  var lineNumber: Int = _
  /**
   * an id of parent comment,
   * used when this comment is saved as response to another comment
   */
  @XStreamAsAttribute
  @Column(name = "parent_id")
  var parentId: Integer = _
  /**
   * link to preview image
   */
  @Transient
  var thumbImage: String = _
  /**
   * Below are some getters,
   * which required when this model would be accessed from EL-expression
   */
  override def getId: Integer = id
  override def setId(id:Integer): Unit = {
    this.id = id;
  }
  def getText: String = text
  def getAuthor: String = author
  def getLineNumber: Int = lineNumber
  def getParentId: Integer = parentId
  def getPasteId: Integer = pasteId
  @JsonIgnore
  def getThumbImage: String = thumbImage
  def isHasAuthor: Boolean = author != null
  override def terms(): List[String] = super.terms() ::: List[String]("text")
}
