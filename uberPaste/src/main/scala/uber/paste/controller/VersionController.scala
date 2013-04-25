package uber.paste.controller

import uber.paste.model.{Query, Struct}
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
 * To change this template use File | Settings | File Templates.
 */
object VersionController {

  protected final val REVERT_ACTION = "/revert"

}

abstract class VersionController[T <: Struct ] extends GenericEditController[T] {


  protected override def manager():VersionManager[T]


  protected override def fillEditModel(obj:T, model:Model,locale:Locale) {

    super.fillEditModel(obj, model,locale)

    logger.debug("__putModel obj id=" + obj.getId())

    if (!obj.isBlank()) {
      model.addAttribute("availableRevisions", manager.getRevisions(obj.getId()))
      model.addAttribute("lastRevision", manager.getCurrentRevisionNumber(obj.getId()))
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

    fillEditModel(omodel, model,locale)

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
