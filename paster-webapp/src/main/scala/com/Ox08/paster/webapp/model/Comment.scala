package com.Ox08.paster.webapp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import jakarta.persistence.{Column, Entity, Lob, Transient}
import jakarta.validation.constraints.{NotNull, Size}
import jakarta.xml.bind.annotation.XmlRootElement
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.{FullTextField, Indexed}


@Entity
@Indexed(index = "indexes/comments")
@XmlRootElement(name = "comment")
class Comment extends Struct with java.io.Serializable {

  @XStreamAsAttribute
  @Column(name = "paste_id",nullable = false)
  var pasteId: Integer = _

  @Lob
  @NotNull
  @FullTextField
  @Size(min = 3, message = "{struct.name.validator}")
  @Column(name = "comment_text")
  var text: String = _

  @Column(name = "author_username")
  var author: String = _

  @Column(name = "line_num")
  var lineNumber: Long = _

  @XStreamAsAttribute
  @Column(name = "parent_id")
  var parentId: Long = _

  @Transient
  private var thumbImage: Option[String] = None

  @JsonIgnore
  def getThumbImage: String = thumbImage.get

  def setThumbImage(img: String): Unit =  {
    thumbImage = Some(img)
  }


  def isHasOwner: Boolean = author != null


  override def terms(): List[String] =  super.terms() ::: List[String]("text")

}
