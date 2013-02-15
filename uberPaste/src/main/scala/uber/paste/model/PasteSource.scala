package uber.paste.model

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 09.02.13
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */

object PasteSource extends KeyValueObj[PasteSource] {
  val FORM = new PasteSource("FORM","paste.source.form")
  val MAIL = new PasteSource("MAIL","paste.source.mail")
  val SKYPE = new PasteSource("SKYPE","paste.source.skype")

  add(FORM)
  add(MAIL)
  add(SKYPE)
}

class PasteSource extends KeyValue{

  def this(code:String,desc:String) = {
    this()
    setCode(code)
    setName(desc)
  }
}
