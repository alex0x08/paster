/*
 * Copyright 2014 Ubersoft, LLC.
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


import java.util.Locale
import javax.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import uber.paste.manager.ProjectManager
import uber.paste.model.Project

@Controller
@RequestMapping(Array("/admin/settings"))
class SettingsController extends GenericEditController[Project]{

  @Autowired
  val projectManager:ProjectManager = null

  def editPage()="/admin/settings/edit"
   def listPage()="/admin/settings/edit"
  def viewPage()="/admin/settings/edit"

  def getNewModelInstance():Project = null
  
  def manager():ProjectManager = return projectManager
 
  @RequestMapping(value = Array(GenericEditController.EDIT_ACTION), method = Array(RequestMethod.GET))
  def edit(model:Model,locale:Locale):String= {

    model.addAttribute(GenericController.MODEL_KEY, projectManager.getCurrentProject)

    return editPage
  }

   @RequestMapping(value = Array(GenericEditController.SAVE_ACTION), method = Array(RequestMethod.POST))
   override def save(@RequestParam(required = false) cancel:String,
            @Valid @ModelAttribute(GenericController.MODEL_KEY) b:Project,
            result:BindingResult, model:Model,locale:Locale,
            redirectAttributes:RedirectAttributes):String = {

        if (cancel != null) {
            redirectAttributes.addFlashAttribute("statusMessageKey", "action.cancelled")
            return editPage
        }

        if (result.hasErrors()) {
              logger.debug("form has errors " + result.getErrorCount())
              
             fillEditModel(b,model,locale)
           /* for ( f:FieldError : result.getFieldErrors()) {
                getLogger().debug("field=" + f.getField() + ",rejected value=" + f.getRejectedValue()+",message="+f.getDefaultMessage());
            }*/
            return editPage
        }

      
       

      redirectAttributes.addFlashAttribute("statusMessageKey", "action.success")

      return editPage
    }

}
