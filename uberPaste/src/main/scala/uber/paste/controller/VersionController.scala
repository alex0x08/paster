package uber.paste.controller

import uber.paste.model.Struct
import uber.paste.manager.{VersionManager, GenericSearchManager}
import org.springframework.web.bind.annotation.{RequestParam, RequestMethod, RequestMapping}
import java.util.Locale
import org.springframework.ui.Model
import javax.servlet.http.HttpServletRequest

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 09.04.13
 * Time: 14:56
 
 */
object VersionController {

  protected final val REVERT_ACTION = "/revert"

}

abstract class VersionController[T <: Struct ] extends GenericEditController[T] {


  protected override def manager():VersionManager[T]


  protected override def fillEditModel(obj:T, rev:Long, model:Model,locale:Locale) {

    super.fillEditModel(obj,rev, model,locale)

    logger.debug("__putModel obj id=" + obj.getId()+" rev="+rev)

    if (!obj.isBlank()) {
        val revs = manager.getRevisions(obj.getId())
      // skip revisions display if only one revision exist
      model.addAttribute("availableRevisions", if (revs!=null && revs.size()>1) revs else null)
      val lastRev= manager.getCurrentRevisionNumber(obj.getId())
      model.addAttribute("lastRevision", lastRev)
      model.addAttribute("revision", if (rev>0) rev else lastRev)
      
    }
  }


  @RequestMapping(value = Array(GenericEditController.EDIT_ACTION), method = Array(RequestMethod.GET))
  def editWithRevision(model:Model,
  @RequestParam(required = true) id:java.lang.Long,
  @RequestParam(required = true) revision:java.lang.Long, locale:Locale):String = {

    val out = viewWithRevision(model,id,revision,locale)

    return if (!out.equals(viewPage)) { out } else { editPage }
  }

  @RequestMapping(value = Array(GenericEditController.VIEW_ACTION), method = Array(RequestMethod.GET))
  def viewWithRevision(model:Model,
                       @RequestParam(required = true) id:java.lang.Long,
                       @RequestParam(required = true) revision:java.lang.Long, locale:Locale):String = {

    val omodel:T = if (revision!=null) { manager.getRevision(id, revision) } else { manager.getFull(id) }

    if (omodel==null) {
      return page404;
    }

    model.addAttribute(GenericController.MODEL_KEY, omodel)

    
    fillEditModel(omodel, revision,model,locale)

    
    return viewPage
  }


  @RequestMapping(value = Array(VersionController.REVERT_ACTION), method = Array(RequestMethod.POST, RequestMethod.GET))
  def revert(@RequestParam(required = false) cancel:String,
             @RequestParam(value = "revision", required = true) revision:java.lang.Long,
             @RequestParam(value = "id", required = true) id:java.lang.Long,
             model:Model, request:HttpServletRequest,locale:Locale):String = {

    manager.revertToRevision(id, revision)

    editWithId(model, id, locale)

    return viewPage
  }


}
