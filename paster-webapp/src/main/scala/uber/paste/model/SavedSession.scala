package uber.paste.model

import javax.persistence.{ Entity }
import org.apache.commons.codec.binary.Base64


@Entity //@Audited
class SavedSession(code: String) extends Named(code) with java.io.Serializable {

  def this() = this(null)

  def getBase64Encoded = if (code != null) {
    Base64.encodeBase64String(code.getBytes)
  } else { null }

 
}
