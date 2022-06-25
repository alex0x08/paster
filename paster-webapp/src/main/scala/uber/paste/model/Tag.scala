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
@XmlRootElement(name = "tag")
class Tag(tagString: String) extends Named(tagString) {

  @transient
  private var total: Int = 0
  def this() = this(null)
  def getTotal() = total
  def setTotal(i: Int) {
    total = i
  }

}
