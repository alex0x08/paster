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


import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Locale
import javax.imageio.ImageIO
import javax.validation.Valid
import org.imgscalr.Scalr
import org.imgscalr.Scalr.Mode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import uber.paste.base.SystemInfo
import uber.paste.manager.ProjectManager
import uber.paste.model.Project
import org.apache.commons.codec.binary.Base64

@Controller
@RequestMapping(Array("/admin/settings"))
class SettingsController extends GenericEditController[Project]{

  @Autowired
  val projectManager:ProjectManager = null

  def editPage()="/admin/settings/edit"
  def listPage()="/paste/list"
  def viewPage()="/admin/settings/edit"

  def getNewModelInstance():Project = null
  
  def manager():ProjectManager = return projectManager
 
  @RequestMapping(value = Array(GenericEditController.EDIT_ACTION), method = Array(RequestMethod.GET))
  def edit(model:Model,locale:Locale):String= {
    model.addAttribute(GenericController.MODEL_KEY, projectManager.getCurrentProject)
    return editPage
  }

  @RequestMapping(value = Array("/save_not_used"), method = Array(RequestMethod.POST))
   override def save(@RequestParam(required = false) cancel:String,
                    
            @Valid @ModelAttribute(GenericController.MODEL_KEY) b:Project,
            result:BindingResult, model:Model,locale:Locale,
            redirectAttributes:RedirectAttributes):String = {
            return save(cancel,b,result,model,locale,redirectAttributes)
  }
  
   @RequestMapping(value = Array(GenericEditController.SAVE_ACTION), method = Array(RequestMethod.POST))
   def save(@RequestParam(required = false) cancel:String,
                     @RequestParam(required = false) reset:String,
            @Valid @ModelAttribute(GenericController.MODEL_KEY) b:Project,
            result:BindingResult, model:Model,locale:Locale,
            redirectAttributes:RedirectAttributes):String = {

        if (cancel != null) {
            redirectAttributes.addFlashAttribute("statusMessageKey", "action.cancelled")
            return "redirect:/main/paste/list"
        }

        if (reset != null) {
          
             val current:Project = manager.getCurrentProject
             current.setName(getResource("project.name.default", locale))
             current.setDescription(getResource("project.description.default", locale))
             current.setClientImage(null)
             current.setIconImage(null)
   
            SystemInfo.instance.setProject(manager.save(current))
             
            redirectAttributes.addFlashAttribute("statusMessageKey", "action.settings.reset")
            
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
        
       val current:Project = manager.getCurrentProject
       current.setName(b.getName)
       current.setDescription(b.getDescription)
    
        if (b.getClientImageFile!=null && !b.getClientImageFile.isEmpty) {
            val img:BufferedImage = ImageIO.read(b.getClientImageFile.getInputStream)
            val baar = new ByteArrayOutputStream()
            
            ImageIO.write(Scalr.resize(img, Mode.AUTOMATIC, 640, 640), "JPEG", baar)
            current.setClientImage("data:image/jpeg;base64,"+Base64.encodeBase64String(baar.toByteArray))
        }
     
 
    if (b.getIconImageFile!=null && !b.getIconImageFile.isEmpty) {
            val img:BufferedImage = ImageIO.read(b.getIconImageFile.getInputStream)
            val baar = new ByteArrayOutputStream()
            
            ImageIO.write(Scalr.resize(img, Mode.FIT_EXACT, 32, 32), "JPEG", baar)
            current.setIconImage("data:image/jpeg;base64,"+Base64.encodeBase64String(baar.toByteArray))
        }
        
      SystemInfo.instance.setProject(manager.save(current))
      
      redirectAttributes.addFlashAttribute("statusMessageKey", "action.success")
      
      return "redirect:/main"+editPage
    }

}
