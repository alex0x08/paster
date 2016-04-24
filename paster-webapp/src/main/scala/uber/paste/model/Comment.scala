package uber.paste.model

import javax.xml.bind.annotation.{XmlTransient, XmlRootElement}
import com.fasterxml.jackson.annotation.JsonIgnore
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

import javax.persistence.{Entity,JoinColumn, FetchType, ManyToOne, Lob,CascadeType, Column, Transient}
import javax.validation.constraints.{Size, NotNull}

import org.hibernate.envers.Audited
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import uber.paste.base.Loggered



object Comment extends Struct {

  override val terms = super.terms ::: List[String]("text")
}


@Entity
@Indexed(index = "indexes/comments")
@XmlRootElement(name="comment")
//@Audited
class Comment extends Struct  with java.io.Serializable{

  @XStreamAsAttribute
  @Column(nullable=false)
  private var pasteId:java.lang.Long = null
 
 
  
  @Lob
  @NotNull
  @Field
  @Size(min=3, message = "{struct.name.validator}")
  private var text: String = null

  @ManyToOne(fetch = FetchType.EAGER,cascade= Array(CascadeType.PERSIST,CascadeType.MERGE))
  @JoinColumn(name = "owner_id")
  private var owner:User = null

  
  private var lineNumber:java.lang.Long = null

  private var parentId:java.lang.Long = null

//  @ManyToOne
//  @JoinColumn(name = "paste_ref", insertable = false, updatable = false)
//  private var paste:Paste = null

 @Transient
  private var thumbImage:String = null
  
  @JsonIgnore
  def getThumbImage() = thumbImage
  def setThumbImage(img:String) {thumbImage = img}
  
  def setPasteId(id:java.lang.Long) {pasteId=id}
  def getPasteId() = pasteId
  
  
  @XmlTransient
  @JsonIgnore
  def getOwner(): User = owner
  def setOwner(u:User) {owner = u}

  def isHasOwner() = owner!=null

 // def getPaste = paste
 // def setPaste(p:Paste) { paste = p}

  def getText = text

  def setText(f:String) {
    this.text = f
  }


  def getParentId():java.lang.Long = parentId
  def setParentId(n:java.lang.Long) { parentId=n}

  def getLineNumber():java.lang.Long = lineNumber
  def setLineNumber(n:java.lang.Long) { lineNumber=n}

  override def terms():List[String] = Comment.terms

  /**
   * this function will fill object fields from highlighter.
   * This needed to proper display highlighted text in result
   */
  /*override def fillFromHits(ch:CompassHighlighter)  {
    super.fillFromHits(ch)
    val t = ch.fragment("text")
    if (t!=null) {
      setText(t)
    }
  }*/

  override def loadFull() {
    
    if (owner!=null)
      {
        getOwner.loadFull
      }
    getText
  }


}
