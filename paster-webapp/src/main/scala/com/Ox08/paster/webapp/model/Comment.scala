package com.Ox08.paster.webapp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import org.hibernate.search.annotations.{Field, Indexed}

import javax.persistence._
import javax.validation.constraints.{NotNull, Size}
import javax.xml.bind.annotation.{XmlRootElement, XmlTransient}
import scala.collection.immutable.List


@Entity
@Indexed(index = "indexes/comments")
@XmlRootElement(name = "comment")
class Comment extends Struct with java.io.Serializable {

  @XStreamAsAttribute
  @Column(nullable = false)
  private var pasteId: java.lang.Long = null

  @Lob
  @NotNull
  @Field
  @Size(min = 3, message = "{struct.name.validator}")
  private var text: String = null

  private var owner: String = null
  private var lineNumber: java.lang.Long = null
  private var parentId: java.lang.Long = null

  @Transient
  private var thumbImage: String = null

  @JsonIgnore
  def getThumbImage() = thumbImage

  def setThumbImage(img: String): Unit =  {
    thumbImage = img
  }

  def setPasteId(id: java.lang.Long): Unit =  {
    pasteId = id
  }

  def getPasteId() = pasteId

  @XmlTransient
  @JsonIgnore
  def getOwner(): String = owner

  def setOwner(u: String): Unit =  {
    owner = u
  }

  def isHasOwner() = owner != null

  def getText = text

  def setText(f: String): Unit =  {
    this.text = f
  }

  def getParentId(): java.lang.Long = parentId
  def setParentId(n: java.lang.Long): Unit =  {
    parentId = n
  }

  def getLineNumber(): java.lang.Long = lineNumber

  def setLineNumber(n: java.lang.Long): Unit =  {
    lineNumber = n
  }

  override def terms(): List[String] =  super.terms() ::: List[String]("text")

}
