package uber.paste.model

import javax.persistence.{Entity, Table}

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 19.02.13
 * Time: 21:02
 */
@Entity
class SavedSession extends Key with java.io.Serializable{


  def this(code:String) = {
    this()
    setCode(code)
  }

}
