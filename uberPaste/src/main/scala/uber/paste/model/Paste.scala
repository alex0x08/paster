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
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.codehaus.jackson.annotate.JsonIgnore
import javax.xml.bind.annotation._
import uber.paste.base.Loggered
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.FileSystems
import java.util.{Set,HashSet,ArrayList, UUID}
import scala.collection.JavaConversions._
import org.hibernate.envers.{NotAudited, Audited}

/**
 * paste model constants
 */
object Paste extends Struct {
  
  /**
   *  a set of properties to search by default (without field prefix)
   */
  override val terms = super.terms ::: List[String]("text","tags")
  /**
   *  max title length, if greater - will be cut
   */
  val TITLE_LENGTH=256
}

/**
 * Entity listener for paste 
 */
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
}

/**
 * 
 * This is entity object implements "pasta" : piece of code with id,title and text
 * 
 * @author Alex
 */

@Entity
@Searchable
@XmlRootElement(name="paste")
@EntityListeners(Array(classOf[PasteListener]))
@Audited
class Paste extends Struct with java.io.Serializable{

  /**
   * unique paste id
   */
  @NotNull(message = "{validator.not-null}")
  @Column(nullable = false, unique = true, length = 255)
  @SearchableProperty(store=Store.YES,index=Index.NO)
  private var uuid = UUID.randomUUID().toString()
  
  /**
   * paste's body
   */
  @Lob
  @NotNull
  @SearchableProperty
  //(analyzer = )
  @Size(min=3, message = "{struct.name.validator}")
  @Column(length = Integer.MAX_VALUE)
  private var text: String = null

  /**
   *  link to preview image
   */
  //@Lob
  //@NotNull
  //@Column(length=1024,name="thumb_img")
  @XmlTransient
  //@NotAudited
  @SearchableProperty(store=Store.YES,index=Index.NO)
  //@transient
  private var thumbImage:String =null

  //@Transient
  //private var thumbUpload:MultipartFile = null

  /**
   * paste title
   */
  //@NotNull
  @Column(length=256)
  @Size(min=3,max=256, message = "{struct.name.validator}")
  @NotAudited
  @SearchableProperty
  private var title: String = null

  /**
   * paste owner (author)
   */
  @ManyToOne(fetch = FetchType.EAGER,cascade= Array(CascadeType.PERSIST,CascadeType.MERGE))
  @JoinColumn(name = "owner_id")
  @NotAudited
  private var owner:User = null
  
  /**
   * type of paste, used almost to highlight it correctly
   */
  @NotNull
  @SearchableProperty
  private var codeType:String = CodeType.Plain.getCode

  /**
   * integration code, used when paste was created in/for some integrated system
   */
  private var integrationCode:String = null

  /**
   *  remote url, used when paste was loaded from external site
   */
  private var remoteUrl:String = null

  /**
   * related tags
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @SearchableProperty
  //@NotAudited
  private var tags:Set[String] = new HashSet[String]()

  /**
   * paste's  source, describes where it came from
   */
  @NotNull
  @SearchableProperty
  private var pasteSource:String = PasteSource.FORM.getCode

  @Transient
  var tagsAsString:String = null

  private var normalized:Boolean = false

  /**
   * comments relation
   */
  @OneToMany(fetch = FetchType.LAZY,
    cascade = Array(CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE),
    orphanRemoval = true)
  @NotAudited
  private var comments:java.util.List[Comment] = new ArrayList[Comment]()

  @SearchableProperty
  private var priority:String = Priority.NORMAL.getCode()

  private var sticked:Boolean = false

  @NotAudited
  private[model] var commentsCount:java.lang.Integer = null

  private var symbolsCount:java.lang.Integer = null
  
  private var wordsCount:java.lang.Integer = null
  
  @transient
  private var thumbData:String = null
  
  override def terms():List[String] = Paste.terms
  
  /**
   * this function will fill object fields from highlighter.
   * This needed to proper display highlighted text in result
   */
  override def fillFromHits(ch:CompassHighlighter)  {
      super.fillFromHits(ch)
    val t = ch.fragment("text")
      if (!StringUtils.isBlank(t)) {
        setTitle(t)
      }
  }
  
  def getSymbolsCount() = symbolsCount
  def setSymbolsCount(c:java.lang.Integer) { this.symbolsCount = c}
  
  def getWordsCount() = wordsCount
  def setWordsCount(c:java.lang.Integer) { this.wordsCount = c}
  
  
  /**
   *  paste's owner
   *  Can be null when saved by anonymous user
   *  @return user instance
   */
  @XmlTransient
  @JsonIgnore
  def getOwner(): User = owner
  def setOwner(u:User) {owner = u}

  def isHasOwner() = owner!=null

  def getIntegrationCode(): String = integrationCode
  def setIntegrationCode(code:String) { integrationCode = code}

  def getTags(): Set[String] = tags

  def getUuid():String = uuid
  
  def getPriority() : Priority = Priority.valueOf(priority)

  def setPriority(prior:Priority)  = {
      priority = prior.getCode()
  }

  @JsonIgnore
  def getThumbImage() = thumbImage
  
  @JsonIgnore
  def getThumbImageRead():String = {
    
    if ( thumbData==null) {    
      val fimg = FileSystems.getDefault().getPath(System.getProperty("paste.app.home"),"images",uuid).toFile
     if (fimg.exists && fimg.isFile) {      
       thumbData =FileUtils.readFileToString(fimg)
     }      
    }
    return  thumbData
  }
  
 
  def setThumbImage(img:String) {thumbImage = img}

 /* @XmlTransient
  @JsonIgnore
  def getThumbUpload() = thumbUpload
  def setThumbUpload(file:MultipartFile) {thumbUpload = file}
   */
  def isSticked() = sticked
  def setSticked(b:Boolean) {this.sticked=b}

  def isNormalized() = normalized
  def setNormalized(b:Boolean) {this.normalized=b}

  def getRemoteUrl() = remoteUrl
  def setRemoteUrl(url:String) {this.remoteUrl = url}

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
   * @return big text
   */
  def getText() : String = text

  def setText(f:String) : Unit = {
    this.text = f
  }
  /**
   * @return list of comments
   */
  @XmlTransient
  @JsonIgnore
  def getComments():java.util.List[Comment] = comments

  def getCommentCount():java.lang.Integer = commentsCount

  /**
   * loads this instance fully from database
   * MUST be called with opened hibernate session
   */
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
