package uber.paste.model

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 11.02.13
 * Time: 22:41
 */

object Priority extends KeyValueObj[Priority]{

  val BLOCKER = new Priority("BLOCKER","priority.blocker.name","priority_blocker")
  val NORMAL = new Priority("NORMAL","priority.normal.name","priority_normal")
  val TRIVIAL = new Priority("TRIVIAL","priority.trivial.name","priority_trivial")

  add(BLOCKER)
  add(NORMAL)
  add(TRIVIAL)

}


class Priority extends KeyValue with java.io.Serializable{

  private var cssClass:String = null

def this(code:String,desc:String,cssClass:String) = {
  this()
  setCode(code)
  setName(desc)

  this.cssClass = cssClass
}

def getCssClass():String = this.cssClass
def setCssClass(css:String) = { this.cssClass =css}
}
