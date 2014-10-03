package uber.paste.model

import javax.persistence._
import javax.xml.bind.annotation.XmlRootElement
import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.IndexedEmbedded

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 09.02.13
 * Time: 20:02
 */
@Entity
@Indexed
@XmlRootElement(name="tag")
class Tag {

  @Id
 // @SearchableId(name="id")
  @GeneratedValue
  private var id:java.lang.Long = null

  @Column(name="tag_text")
  private var key:String = null

  def getKey():String  = key
  def setKey(k:String) { key =k}

}
