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

package uber.paste.model

import javax.persistence._
import javax.validation.constraints.{Size, NotNull}
import org.compass.core.CompassHighlighter
import org.compass.annotations._
import org.codehaus.jackson.annotate.JsonIgnore
import javax.persistence.FetchType
import javax.xml.bind.annotation._
import uber.paste.base.Loggered
import java.util.{Set,HashSet,ArrayList}
import scala.collection.JavaConversions._


/**
 * 
 * This is entity object implements "pasta" : piece of code with id,title and text
 * 
 * @author Alex
 */

object Paste extends Struct {
  
  override val terms = super.terms ::: List[String]("text","tags")
  val TITLE_LENGTH=256
}

class PasteListener extends Loggered{

  /**
   * regenerate title field on save
   */
  @PreUpdate
  @PrePersist
  def onUpdate(obj:Paste) {
    logger.debug("_on update call ")
    obj.setTitle( if (obj.getText().length>Paste.TITLE_LENGTH) {
      obj.getText().substring(0,Paste.TITLE_LENGTH-3)+"..."
    } else {
      obj.getText
    })

    obj.commentsCount = obj.getComments().size()

    logger.debug("_comments count= "+obj.commentsCount)

  }

  /*@PostConstruct
  def onPostLoad(obj:Paste) {

    logger.debug("_on postLoad call")

    obj.setTagsAsString({
      val out =new StringBuilder
      for (s<-obj.getTags()) {
        out.append(s.getKey()).append(" ")
      }
      out.toString })
  } */

}



@Entity
@Searchable
@XmlRootElement(name="paste")
@EntityListeners(Array(classOf[PasteListener]))
class Paste extends Struct with java.io.Serializable{

  
  @Lob
  @NotNull
  @SearchableProperty
  @Size(min=3, message = "{struct.name.validator}")
  private var text: String = null

  //@NotNull
  @Column(length=256)
  @Size(min=3,max=256, message = "{struct.name.validator}")
  private var title: String = null

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "owner_id")
  private var owner:User = null
  
  @NotNull
  private var codeType:String = CodeType.Plain.getCode

  @ElementCollection(fetch = FetchType.EAGER)
  @SearchableProperty
  private var tags:Set[String] = new HashSet[String]()

  @NotNull
  @SearchableProperty
  private var pasteSource:String = PasteSource.FORM.getCode

  @Transient
  var tagsAsString:String = null

  private var normalized:Boolean = false

  @OneToMany(fetch = FetchType.LAZY,cascade = Array(CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE),orphanRemoval = true)
  private var comments:java.util.List[Comment] = new ArrayList[Comment]()

  @SearchableProperty
  private var priority:String = Priority.NORMAL.getCode()

  private var sticked:Boolean = false

  private[model] var commentsCount:java.lang.Integer = null

  override def terms():List[String] = Paste.terms
  
  /**
   * this function will fill object fields from highlighter.
   * This needed to proper display highlighted text in result
   */
  override def fillFromHits(ch:CompassHighlighter)  {
      super.fillFromHits(ch)
    val t = ch.fragment("text")
      if (t!=null) {
        setTitle(t)
      }
  }
  
  /**
   *  paste's owner
   *  Can be null when saved by anonymous user
   *  @return user instance
   */
  @XmlTransient
  @JsonIgnore
  def getOwner(): User = owner
  def setOwner(u:User) {owner = u}

  def getTags(): Set[String] = tags


  def getPriority() : Priority = Priority.valueOf(priority)

  def setPriority(prior:Priority)  = {
      priority = prior.getCode()
  }

  def isSticked() = sticked
  def setSticked(b:Boolean) {this.sticked=b}

  def isNormalized() = normalized
  def setNormalized(b:Boolean) {this.normalized=b}


  def getPasteSource() : PasteSource = PasteSource.valueOf(pasteSource)

  def setPasteSource(s:PasteSource) {
    pasteSource = s.getCode()
  }

  /**
   *  List of supported code types
   * (need to proper syntax highlight)
   *  @return list
   */
  def getCodeType() : CodeType =CodeType.valueOf(codeType)
  
  def setCodeType(f:CodeType) : Unit = {
    codeType = f.getCode
  }
  
  /**
   * Paste's title
   * This field contains small piece of original text, it will be recreated after each object save
   */
  def getTitle() : String = title
 
  def setTitle(f:String) : Unit = {    
    this.title=f
  }
 
  /**
   * Pasta content
   * Blob field stores all text for pasta
   */
  def getText() : String = text

  def setText(f:String) : Unit = {
    this.text = f
  }
  @XmlTransient
  @JsonIgnore
  def getComments():java.util.List[Comment] = comments

  def getCommentCount():java.lang.Integer = commentsCount

  override def loadFull() {
      getText
              for (c<-comments) {
                      c.loadFull()
              }
  }

  override def toString():String = {
    return Loggered.getNewProtocolBuilder(this)
    //  .append("title", title)
    //  .append("text", text)
      .append("tags", tags)

      .append("super",super.toString())
      .toString
  }
  
}
