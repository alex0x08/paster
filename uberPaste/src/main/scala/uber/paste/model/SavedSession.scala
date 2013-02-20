package uber.paste.model

import javax.persistence.Entity

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 19.02.13
 * Time: 21:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
class SavedSession extends Key with java.io.Serializable{

  def this(code:String) = {
    this()
    setCode(code)
  }

}
