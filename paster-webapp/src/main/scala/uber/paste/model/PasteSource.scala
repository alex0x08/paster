package uber.paste.model

/**
 * User: achernyshev
 * Date: 09.02.13
 * Time: 21:41
 */

object PasteSource extends KeyValueObj[PasteSource] {
  val FORM = new PasteSource("FORM","paste.source.form")
  val MAIL = new PasteSource("MAIL","paste.source.mail")
  val SKYPE = new PasteSource("SKYPE","paste.source.skype")
  val REMOTE = new PasteSource("REMOTE","paste.source.remote")

  add(FORM)
  add(MAIL)
  add(SKYPE)
  add(REMOTE)

}

class PasteSource(code:String,desc:String) extends KeyValue(code,null,desc){

 
   def getCodeLowerCase() = super.getCode().toLowerCase
}
