package uber.paste.model

import org.compass.annotations.{SearchableProperty, Searchable}
import javax.xml.bind.annotation.{XmlTransient, XmlRootElement}
import javax.persistence.{Entity,JoinColumn, FetchType, ManyToOne, Lob}
import javax.validation.constraints.NotNull
import org.codehaus.jackson.annotate.JsonIgnore
import uber.paste.base.Loggered

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 10.02.13
 * Time: 4:20
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Searchable
@XmlRootElement(name="comment")
class Comment extends Struct  with java.io.Serializable{

  @Lob
  @NotNull
  @SearchableProperty
  private var text: String = null

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "owner_id")
  private var owner:User = null

  private var lineNumber:java.lang.Long = null;

  {
      setName(" ")
  }

  @XmlTransient
  @JsonIgnore
  def getOwner(): User = owner
  def setOwner(u:User) {owner = u}

  def getText() : String = text

  def setText(f:String) : Unit = {
    this.text = f
  }

  def getLineNumber():java.lang.Long = lineNumber
  def setLineNumber(n:java.lang.Long) { lineNumber=n}

  override def loadFull() {
    getText
  }

  override def toString():String = {
    return Loggered.getNewProtocolBuilder(this)
      .append("lineNumber", lineNumber)
      .append("text", text)
      .append("owner", owner)

      .append("super",super.toString())
      .toString
  }

}
