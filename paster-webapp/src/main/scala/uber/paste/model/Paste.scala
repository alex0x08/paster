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

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.search.annotations.{Index, _}
import uber.paste.base.Loggered
import java.util.{ArrayList, UUID}
import javax.persistence._
import javax.validation.constraints.{NotNull, Size}
import javax.xml.bind.annotation._
import scala.annotation.unused
import scala.jdk.CollectionConverters._

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
 * 
 * This is entity object implements "pasta" : piece of code with id,title and text
 * 
 * @author Alex
 */

@Entity
@Indexed(index = "indexes/pastas")
@XmlRootElement(name="paste")
//@Audited
//@org.hibernate.annotations.Entity(dynamicUpdate = true)
class Paste(title:String) extends Named(title) with java.io.Serializable{

  
  def this() = this(null)
  
  
  /**
   * unique paste id
   */
  @NotNull(message = "{validator.not-null}")
  @Column(nullable = false, unique = true, length = 255,updatable=false)
  @Field(index = Index.NO, store =Store.YES, termVector = TermVector.NO)
  private var uuid = UUID.randomUUID().toString()
  
  /**
   * paste's body
   */
  @Lob
  @NotNull
  @Field
  //(analyzer = )
  @Size(min=3, message = "{struct.name.validator}")
  @Column(length = Integer.MAX_VALUE)
  private var text: String = null

  /**
   *  link to preview image
   */
  @XmlTransient
  @Field(store=Store.YES,index=Index.NO)
  //@transient
  private var thumbImage:String =null

 
  /**
   * paste title
   */
  //@NotNull
  @Column(length=256)
  @Size(min=3,max=256, message = "{struct.name.validator}")
  //@NotAudited
  @Field
  private var title: String = null

  /**
   * paste owner (author)
   */
  @ManyToOne(fetch = FetchType.EAGER,cascade= Array(CascadeType.PERSIST,CascadeType.MERGE))
  @JoinColumn(name = "owner_id")
  //@NotAudited
  private var owner:User = null
  
  /**
   * type of paste, used almost to highlight it correctly
   */
  @NotNull
  @Field
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
   * paste's  source, describes where it came from
   */
  @NotNull
  //@Field
  @ManyToOne(fetch = FetchType.EAGER,cascade= Array(CascadeType.PERSIST,CascadeType.MERGE))
  @JoinColumn(name = "channel_id")
  private var channel:Channel = null
  //PasteSource.FORM.getCode

  //@Transient
  @Field(name="tags",index = Index.YES, store =Store.YES, termVector = TermVector.YES) //,boost=@Boost(2f)
  private var tagsAsString:String = null

  private var normalized:Boolean = false

  /**
   * comments relation
   */
 // @OneToMany(fetch = FetchType.LAZY,
   // cascade = Array(CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE),
   // orphanRemoval = true)
 // @JoinColumn(name = "paste_ref")
 // @AuditMappedBy(mappedBy = "paste")
	//@NotAudited
//  @Audited
  //(withModifiedFlag = false)
  //@NotAudited
  @transient
  private var comments:java.util.List[Comment] = new ArrayList[Comment]()

  @Field
  private var priority:String = Priority.NORMAL.getCode()

  private var sticked:Boolean = false

  //@NotAudited
  private[model] var commentsCount:java.lang.Integer = null

  private var symbolsCount:java.lang.Integer = null
  
  private var wordsCount:java.lang.Integer = null

  private var reviewImgData:String = null

 /**
   * related tags
   */
 
  @MapKey(name="name")
  @ManyToMany(fetch=FetchType.EAGER,cascade= Array(CascadeType.ALL))
  private[model] var tagsMap:java.util.Map[String,Tag] = new java.util.HashMap
  
  @PrePersist
  @PreUpdate
  @unused
  private def onUpdate() {    
     commentsCount = getComments().size()
  }
  
  override def terms():List[String] = Paste.terms

  def getSymbolsCount() = symbolsCount
  def setSymbolsCount(c:java.lang.Integer) { this.symbolsCount = c}
  
  def getWordsCount() = wordsCount
  def setWordsCount(c:java.lang.Integer) { this.wordsCount = c}
  
  
  def getTagsAsString() = tagsAsString
  def setTagsAsString(str:String) { this.tagsAsString=str}
  
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

  def getTags(): java.util.Set[String] = tagsMap.keySet
  
  def getTagsMap():java.util.Map[String,Tag] = tagsMap
  
  def getUuid():String = uuid
  
  def getPriority() : Priority = Priority.valueOf(priority)

  def setPriority(prior:Priority)  = {
      priority = prior.getCode()
  }

  @JsonIgnore
  def getThumbImage() = thumbImage
 
  def setThumbImage(img:String) {thumbImage = img}
   
  @JsonIgnore
  def getReviewImgData() = this.reviewImgData 
  def setReviewImgData(img:String) {reviewImgData = img}
 

  def isSticked() = sticked
  def setSticked(b:Boolean) {this.sticked=b}

  def isNormalized() = normalized
  def setNormalized(b:Boolean) {this.normalized=b}

  def getRemoteUrl() = remoteUrl
  def setRemoteUrl(url:String) {this.remoteUrl = url}

  
  def getChannel() = channel

  def setChannel(s:Channel) {
    channel = s
  }

  /**
   *  List of supported code types
   * (need to proper syntax highlight)
   *  @return list
   */
  def getCodeType() : CodeType =CodeType.valueOf(codeType)
  
  def setCodeType(f:CodeType)  {
    codeType = f.getCode
  }
  
  /**
   * Paste's title
   * This field contains small piece of original text, it will be recreated after each object save
   */
  def getTitle() = getName()
 
  def setTitle(f:String) = setName(f)
 
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
    //    System.out.println("_loadFull paste "+owner)
      
     if (owner!=null)
      {
        getOwner.loadFull
      }
      getText
              for (c<-comments.asScala) {
                      c.loadFull()
              }
  }

   
  override def toString():String =  Loggered.toStringSkip(this, 
                                                          Array("reviewImgData",
                                                                "thumbImage",
                                                                "title",
                                                                "text"))
  
}
