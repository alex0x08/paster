package uber.paste.model

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 11.02.13
 * Time: 22:41
 */

object Priority extends KeyObj[Priority]{

  val BLOCKER = new Priority("BLOCKER","priority.blocker.name","priority_blocker")
  val NORMAL = new Priority("NORMAL","priority.normal.name","priority_normal")
  val TRIVIAL = new Priority("TRIVIAL","priority.trivial.name","priority_trivial")

  add(BLOCKER)
  add(NORMAL)
  add(TRIVIAL)

}


class Priority(code:String,desc:String,kcssClass:String) extends Key(code,desc) with java.io.Serializable{

  private var cssClass:String = kcssClass


def getCssClass() =cssClass
def setCssClass(css:String) = { this.cssClass =css}
}
