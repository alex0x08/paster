/*
 * Copyright 2011 WorldWide Conferencing, LLC.
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

import org.springframework.stereotype.Controller
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import uber.paste.model._
import org.springframework.beans.factory.annotation.Autowired
import java.io.IOException
import java.io.File
import javax.servlet.http.HttpServletResponse
import uber.paste.dao.ConfigDao
import uber.paste.manager.PasteManager
import org.springframework.ui.Model
import uber.paste.base.Loggered
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.validation.BindingResult;
import javax.validation.Valid;
import java.util.HashMap
import java.util.Locale
import scala.collection.JavaConversions._
import org.codehaus.jackson.annotate.JsonIgnore
import javax.xml.bind.annotation.XmlTransient

@Controller
@RequestMapping(Array("/paste"))
class PasteController extends GenericEditController[Paste]   {

  @Autowired
  val pasteManager:PasteManager = null
  
 
  def listPage()="redirect:/main/paste/list"
  def editPage()="paste/edit"
  def viewPage()="paste/view"
  

  def manager():PasteManager = return pasteManager
 
  
  @InitBinder
  def initBinder(binder:WebDataBinder):Unit = {
    binder.initDirectFieldAccess()
    binder.registerCustomEditor(classOf[CodeType], new CodeTypeEditor())
  }
  
  override def fillEditModel(obj:Paste,model:Model,locale:Locale)  {
         super.fillEditModel(obj,model,locale)


    if (obj.isBlank) {
      model.addAttribute("title",getResource("paste.new",locale))
    } else {
      model.addAttribute("title","Edit #"+obj.getId())
    }
    model.addAttribute("availableCodeTypes", CodeType.list)
    model.addAttribute("availablePriorities", Priority.list)

    obj.tagsAsString={
      val out =new StringBuilder
      for (s<-obj.getTags()) {
        out.append(s).append(" ")
      }
      out.toString }
  }

  def getNewModelInstance():Paste = {
    val p = new Paste
    p.setOwner(getCurrentUser)
    return p
  }

  @ModelAttribute("comment")
  def getNewCommentInstance():Comment = {
    val p = new Comment
    p.setOwner(getCurrentUser)
    return p
  }

  @RequestMapping(value = Array("/saveComment"), method = Array(RequestMethod.POST))
  def saveComment(@RequestParam(required = true) pasteId:java.lang.Long,
                    @Valid b:Comment,
                    result:BindingResult, model:Model,locale:Locale):String = {

        val p = manager.getFull(pasteId)

        if (p==null) {
          return page404
        }

        logger.debug("adding comment "+b)

        p.getComments().add(b)

        manager.save(p)

    return "redirect:/main/paste/" + pasteId + "#line_" + b.getLineNumber
  }

    @RequestMapping(value = Array("/save"), method = Array(RequestMethod.POST))
   override def save(@RequestParam(required = false) cancel:String,
           @Valid b:Paste,
           result:BindingResult, model:Model,locale:Locale):String = {

     val tags =  b.tagsAsString

     if (tags!=null) {
       b.getTags().clear()
       for (s<-tags.split(" ")) {
         if (s!=null) {
           b.getTags().add(s)
         }
       }
     }

      if (isCurrentUserLoggedIn()) {
        b.setOwner(getCurrentUser())
      }

       return super.save(cancel,b,result,model,locale)
   }



    @RequestMapping(value = Array("/{id}"), method = Array(RequestMethod.GET))
  override def getByPath(@PathVariable("id") id:java.lang.Long,model:Model,locale:Locale):String = {

    val r = super.getByPath(id,model,locale)
         if (!r.equals(viewPage))
           return r;

    val p = model.asMap().get(GenericController.MODEL_KEY).asInstanceOf[Paste];



    model.addAttribute("title",getResource("paste.view.title",Array(p.getId),locale))
    
    return viewPage
  }


  @RequestMapping(value = Array("/plain/{id}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getBodyPlain(@PathVariable("id") id:java.lang.Long,model:Model,locale:Locale):String = {
    return loadModel(id).getText();
  }

  @ModelAttribute("query")
  def newQuery():OwnerQuery = {
    val out =new OwnerQuery
    //  if (!isCurrentUserAdmin) {
    //    out.setOwnerId(getCurrentUser.getId)
    // }
    return out
  }

}
