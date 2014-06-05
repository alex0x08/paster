/*
 * Copyright 2011 Ubersoft, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.controller

import uber.paste.model.Struct
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation._
import javax.validation.Valid
import java.util.Locale
import org.springframework.web.servlet.mvc.support.RedirectAttributes

object GenericEditController {

  final val EDIT_ACTION = "/edit"
  final val VIEW_ACTION = "/view"
  final val SAVE_ACTION= "/save"
  final val NEW_ACTION= "/new"
  final val DELETE_ACTION= "/delete"

}

abstract class GenericEditController[T <: Struct ] extends StructController[T] {    
  
  
    protected def getNewModelInstance():T

    protected def fillEditModel(obj:T,rev:Long,model:Model,locale:Locale) {
      model.addAttribute(GenericController.MODEL_KEY, obj)
    }

    def loadModel(@RequestParam(required = false) id:java.lang.Long):T = {
      logger.debug("_new request id="+id)

        return if (id != null)
          manager.getFull(id)
        else
          null.asInstanceOf[T]
    }

  @RequestMapping(value = Array(GenericEditController.NEW_ACTION), method = Array(RequestMethod.GET))
  @ResponseStatus(HttpStatus.CREATED)
  def createNew(model:Model,locale:Locale):String= {

    fillEditModel(getNewModelInstance(),-1,model,locale)

    return editPage
  }


  @RequestMapping(value = Array("/edit/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  def editWithId(model:Model,@PathVariable("id") id:Long,locale:Locale):String= {

      fillEditModel(loadModel(id),-1,model,locale)

    return editPage
  }


    @RequestMapping(value = Array(GenericEditController.SAVE_ACTION), method = Array(RequestMethod.POST))
    def save(@RequestParam(required = false) cancel:String,
            @Valid @ModelAttribute(GenericController.MODEL_KEY) b:T,
            result:BindingResult, model:Model,locale:Locale,
            redirectAttributes:RedirectAttributes):String = {

        if (cancel != null) {
            redirectAttributes.addFlashAttribute("statusMessageKey", "action.cancelled")
            return listPage
        }

        if (result.hasErrors()) {
              logger.debug("form has errors " + result.getErrorCount())
              
             fillEditModel(b,-1,model,locale)
           /* for ( f:FieldError : result.getFieldErrors()) {
                getLogger().debug("field=" + f.getField() + ",rejected value=" + f.getRejectedValue()+",message="+f.getDefaultMessage());
            }*/
            return editPage
        }

        val r:T = manager.save(b)

        // set id from create
        if (b.isBlank) {
            b.setId(r.getId())
        }

      redirectAttributes.addFlashAttribute("statusMessageKey", "action.success")

      return listPage
    }

   

    @RequestMapping(value = Array(GenericEditController.DELETE_ACTION), method = Array(RequestMethod.GET,RequestMethod.POST))
    def delete(@RequestParam(required = false) id:Long,
            model:Model):String = {
        manager.remove(id);
        return listPage;
    }



  @RequestMapping(value = Array("/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  def getByPath(@PathVariable("id") id:java.lang.Long,model:Model,locale:Locale):String = {

    val m = loadModel(id)

    if (m==null)
      return page404

    model.addAttribute(GenericController.MODEL_KEY, m)

    fillEditModel(m,-1,model,locale)

    return viewPage
  }


  @RequestMapping(value = Array("/xml/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getBody(@PathVariable("id") id:java.lang.Long,model:Model,locale:Locale):T = {
    return loadModel(id);
}


}
