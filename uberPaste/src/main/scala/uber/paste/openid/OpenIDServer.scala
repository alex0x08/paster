package uber.paste.openid

import uber.paste.model.{KeyValueObj, KeyValue}

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 13.02.13
 * Time: 20:07
 * To change this template use File | Settings | File Templates.

   yandex("http://openid.yandex.ru","/images/openid/yandex.png","Yandex"),
    //mailru("http://openid.mail.ru","/img/openid/mailru.ico","Mail.ru"),
    yahoo("https://me.yahoo.com","/images/openid/yahoo.png","Yahoo"),

    google("https://www.google.com/accounts/o8/id","/images/openid/google.ico","Google");


 */
object OpenIDServer extends KeyValueObj[OpenIDServer] {
     val YANDEX = new OpenIDServer("http://openid.yandex.ru","Yandex","img-openid-yandex")
   //  val MAILRU = new OpenIDServer("http://openid.mail.ru","Mail.ru","mailru.ico")
     val GOOGLE = new OpenIDServer("https://www.google.com/accounts/o8/id","Google","img-openid-google")
     val YAHOO = new OpenIDServer("https://me.yahoo.com","Yahoo","img-openid-yahoo")

    add(YANDEX)
 //   add(MAILRU)
    add(GOOGLE)
    add(YAHOO)



}

class OpenIDServer extends KeyValue {

  private var icon:String =null

  def this(code:String,desc:String,icon:String) = {
    this()
    setCode(code)
    setName(desc)
    this.icon = icon
  }

  def getIcon():String = icon
  def setIcon(icon:String) = { this.icon=icon }
}
