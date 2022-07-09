package com.Ox08.paster.webapp.model
import com.fasterxml.jackson.annotation.JsonIgnore
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import jakarta.persistence.{Column, Entity, Lob, Transient}
import jakarta.validation.constraints.{NotNull, Size}
import jakarta.xml.bind.annotation.XmlRootElement
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.{FullTextField, Indexed}
/**
 * A comment for single line of paste
 */
@Entity
@Indexed(index = "indexes/comments")
@XmlRootElement(name = "comment")
class Comment extends Struct with java.io.Serializable {
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
   * a id of parent comment, used when this comment is answer to another comment
   */
  @XStreamAsAttribute
  @Column(name = "parent_id")
  var parentId: Integer = _

  @Transient
  var thumbImage: String = _
  /**
   * Here and below are set of getters, required when model is used from JSP EL expression
   */
  def getId: Integer = id
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
