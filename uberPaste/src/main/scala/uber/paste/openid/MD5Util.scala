package uber.paste.openid

import uber.paste.base.Loggered
import java.security.{NoSuchAlgorithmException, MessageDigest}
import java.io.UnsupportedEncodingException

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 13.02.13
 * Time: 20:50
 * To change this template use File | Settings | File Templates.
 */

object MD5Util {
  val instance = new MD5Util

}

class MD5Util extends Loggered{


  def hex(array:Array[Byte]):String = {

    val sb = new StringBuilder()

    for (b<-array) {
      sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3))
    }
    return sb.toString();
  }

  def md5Hex(message:String):String = {
    try {
      val md = MessageDigest.getInstance("MD5")
      return hex(md.digest(message.getBytes("UTF-8")))
    } catch{
      case e @ (_ : NoSuchAlgorithmException | _ : UnsupportedEncodingException) =>{
        logger.error(e.getLocalizedMessage,e)
        return null
      }

    }

  }


}



