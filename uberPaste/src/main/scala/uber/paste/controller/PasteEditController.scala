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
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import uber.paste.model._
import org.springframework.beans.factory.annotation.{Value, Autowired}
import java.io.IOException
import java.io.File
import javax.servlet.http.HttpServletResponse
import uber.paste.dao.ConfigDao
import uber.paste.manager.{CommentManager, PasteManager}
import org.springframework.ui.Model
import uber.paste.base.Loggered

import org.springframework.validation.BindingResult;
import javax.validation.Valid;
import java.util.HashMap
import java.util.Locale
import scala.collection.JavaConversions._
import org.codehaus.jackson.annotate.JsonIgnore
import javax.xml.bind.annotation.XmlTransient
import org.apache.commons.lang.{WordUtils, StringUtils}
import scala.Array
import com.google.gson.{JsonParser, GsonBuilder}
import org.codehaus.jackson.map.ObjectMapper

@Controller
@RequestMapping(Array("/paste"))
//@SessionAttributes(Array(GenericController.MODEL_KEY))
class PasteController extends GenericEditController[Paste]   {

  @Autowired
  val pasteManager:PasteManager = null

  @Autowired
  val commentManager:CommentManager = null

  @Value("${config.share.integration}")
  val shareIntegration:Boolean = false

  @Value("${config.share.url}")
  val shareUrl:String = null


  def listPage()="redirect:/main/paste/list"
  def editPage()="paste/edit"
  def viewPage()="paste/view"
  

  def manager():PasteManager = return pasteManager
 
  
  @InitBinder
  def initBinder(binder:WebDataBinder):Unit = {
    binder.initDirectFieldAccess()
    binder.registerCustomEditor(classOf[CodeType], new CodeTypeEditor())
  //  binder.setDisallowedFields("id","lastModified")
  }
  
  override def fillEditModel(obj:Paste,model:Model,locale:Locale)  {
         super.fillEditModel(obj,model,locale)


    if (obj.isBlank) {
      model.addAttribute("title",getResource("paste.new",locale))
    } else {
      model.addAttribute("title",getResource("paste.edit.title",Array(obj.getId,obj.getName()),locale))
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


  @RequestMapping(value = Array("/removeComment"), method = Array(RequestMethod.POST,RequestMethod.GET))
  def removeComment(@RequestParam(required = true) pasteId:java.lang.Long,
                    @RequestParam(required = true) commentId:java.lang.Long,
                    @RequestParam(required = true) lineNumber:java.lang.Long,
                    model:Model,locale:Locale):String = {

    logger.debug("_removeComment pasteId="+pasteId+" commentId="+commentId+" lineNumber="+lineNumber)

    val p = manager.getFull(pasteId)

    if (p==null) {
      return page404
    }


    val c = new Comment
    c.setId(commentId)

    p.getComments().remove(c)

    manager.save(p)

    return "redirect:/main/paste/" + pasteId + "#line_" + lineNumber
  }


  @RequestMapping(value = Array("/saveComment"), method = Array(RequestMethod.POST))
  def saveComment(@RequestParam(required = true) pasteId:java.lang.Long,
                    @Valid b:Comment,
                    result:BindingResult, model:Model,locale:Locale):String = {

        val p = manager.getFull(pasteId)

        if (p==null) {
          return page404
        }

        p.setTitle(null) // magic to invoke event listeners

        logger.debug("adding comment "+b)

    if (result.hasErrors()) {
      logger.debug("form has errors " + result.getErrorCount());
      model.addAttribute("comment", b)
      fillEditModel(p,model,locale)

      return viewPage
    }

      p.getComments().add(b)

      manager.save(p)

    return "redirect:/main/paste/" + pasteId + "#line_" + b.getLineNumber
  }

    @RequestMapping(value = Array("/save"), method = Array(RequestMethod.POST))
   override def save(@RequestParam(required = false) cancel:String,
           @Valid @ModelAttribute(GenericController.MODEL_KEY) b:Paste,
           result:BindingResult, model:Model,locale:Locale):String = {

      /**
       * copy fields not filled in form
       */
      if (!b.isBlank()) {
        val current = manager.getFull(b.getId());
        b.getComments().addAll(current.getComments())
        b.setPasteSource(b.getPasteSource())
      }

     val tags =  b.tagsAsString

     if (tags!=null) {
       b.getTags().clear()
       for (s<-tags.split(" ")) {
         if (!StringUtils.isBlank(s)) {
           b.getTags().add(s)
         }
       }
     }

      if (b.isNormalized()) {

        b.getCodeType() match {
          case CodeType.JavaScript => {

            val o = new JsonParser().parse(b.getText()).getAsJsonObject()
            b.setText(new GsonBuilder().setPrettyPrinting().create().toJson(o))
          }
          case CodeType.Plain => {
               b.setText(WordUtils.wrap(b.getText(),80))
          }

            case _ => {}
        }
      }

      if (b.isBlank()) {
        if (isCurrentUserLoggedIn()) {
          b.setOwner(getCurrentUser())
        }
      }

      logger.debug("__found comments "+b.getComments().size())

       val out =super.save(cancel,b,result,model,locale)
      return if (out.equals(listPage))  {"redirect:/main/paste/"+b.getId()} else {out}
   }



  @RequestMapping(value = Array("/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  override def getByPath(@PathVariable("id") id:java.lang.Long,model:Model,locale:Locale):String = {

    val r = super.getByPath(id,model,locale)
         if (!r.equals(viewPage))
           return r;

    val p = model.asMap().get(GenericController.MODEL_KEY).asInstanceOf[Paste];

    model.addAttribute("shareIntegration",shareIntegration)
    model.addAttribute("shareUrl",shareUrl)

    model.addAttribute("title",getResource("paste.view.title",Array(p.getId,p.getName()),locale))
    
    return viewPage
  }

  @RequestMapping(value = Array("/codetypes"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getAvailableCodeTypes():java.util.Collection[CodeType] = {
              return CodeType.list
  }

  @RequestMapping(value = Array("/plain/{id:[0-9]+}"), method = Array(RequestMethod.GET), produces = Array("text/plain;charset=UTF-8"))
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
