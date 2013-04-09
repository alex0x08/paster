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
  protected val REVERT_ACTION = "/revert"

}

abstract class VersionController[T <: Struct ] extends GenericEditController[T] {


  protected override def manager():VersionManager[T]


  protected override def fillEditModel(obj:T, model:Model,locale:Locale) {

    super.fillEditModel(obj, model,locale)

    System.out.println("__putModel obj id=" + obj.getId())

    if (!obj.isBlank()) {
      model.addAttribute("availableRevisions", manager.getRevisions(obj.getId()))
      model.addAttribute("lastRevision", manager.getCurrentRevisionNumber(obj.getId()))
    }
  }

      /*
  @RequestMapping(value = Array(EDIT_ACTION), method = Array(RequestMethod.GET))
  override def edit(model:Model,
  @RequestParam(required = true) id:java.lang.Long,
  @RequestParam(required = false) revision:java.lang.Long, locale:Locale):String = {

    T omodel = revision!=null ? smanager.getRevision(id, revision) : manager.getFull(id);

    if (omodel==null) {
      return page404;
    }

    if (!checkAccess(omodel,model)) {
      return page403;
    }

    model.addAttribute(MODEL_KEY, omodel);

    putModel(omodel, model);

    return editPage
  }

  @RequestMapping(value = EDIT_ACTION+"-no-revision", method = RequestMethod.GET)
  public String edit(Model model,@RequestParam(required = true) Long id, Locale locale) {
    return super.edit(model, id, locale);
  }
        */

  @RequestMapping(value = Array(VersionController.REVERT_ACTION), method = Array(RequestMethod.POST, RequestMethod.GET))
  def revert(@RequestParam(required = false) cancel:String,
             @RequestParam(value = "revision", required = true) revision:java.lang.Long,
             @RequestParam(value = "id", required = true) id:java.lang.Long,
             model:Model, request:HttpServletRequest,locale:Locale):String = {

    manager.revertToRevision(id, revision)

    return super.editWithId(model, id, locale)

  }


}
