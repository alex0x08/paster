package uber.paste.model

import org.compass.annotations.{SearchableProperty, Searchable}
import javax.xml.bind.annotation.{XmlTransient, XmlRootElement}
import javax.persistence.{Entity,JoinColumn, FetchType, ManyToOne, Lob,CascadeType}
import javax.validation.constraints.{Size, NotNull}
import org.codehaus.jackson.annotate.JsonIgnore
import uber.paste.base.Loggered
import org.compass.core.CompassHighlighter


object Comment extends Struct {

  override val terms = super.terms ::: List[String]("text")
}


@Entity
@Searchable
@XmlRootElement(name="comment")
class Comment extends Struct  with java.io.Serializable{

  @Lob
  @NotNull
  @SearchableProperty
  @Size(min=3, message = "{struct.name.validator}")
  private var text: String = null

  @ManyToOne(fetch = FetchType.EAGER,cascade= Array(CascadeType.PERSIST,CascadeType.MERGE))
  @JoinColumn(name = "owner_id")
  private var owner:User = null

  private var lineNumber:java.lang.Long = null

  private var parentId:java.lang.Long = null


  {
      setName("---")
  }

  @XmlTransient
  @JsonIgnore
  def getOwner(): User = owner
  def setOwner(u:User) {owner = u}

  def isHasOwner() = owner!=null


  def getText() : String = text

  def setText(f:String) : Unit = {
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
  override def fillFromHits(ch:CompassHighlighter)  {
    super.fillFromHits(ch)
    val t = ch.fragment("text")
    if (t!=null) {
      setText(t)
    }
  }

  override def loadFull() {
    getText
  }

  override def toString():String = {
    return Loggered.getNewProtocolBuilder(this)
      .append("lineNumber", lineNumber)
      .append("parentId", parentId)

      .append("text", text)
      .append("owner", owner)

      .append("super",super.toString())
      .toString
  }

}
