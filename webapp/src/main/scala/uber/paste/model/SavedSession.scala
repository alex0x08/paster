package uber.paste.model

import javax.persistence.{Entity, Table}
import org.hibernate.envers.Audited

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 19.02.13
 * Time: 21:02
 */
@Entity
@Audited
class SavedSession extends Key with java.io.Serializable{


  def this(code:String) = {
    this()
    setCode(code)
  }

}
