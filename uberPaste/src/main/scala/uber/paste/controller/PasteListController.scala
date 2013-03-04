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

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import uber.paste.model.Paste
import uber.paste.model.OwnerQuery
import uber.paste.manager.PasteManager
import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.ui.Model
import java.util.Locale


@Controller
@RequestMapping(value=Array("/paste"))
class PasteListController extends SearchController[Paste,OwnerQuery] {

  @Autowired
  val pasteManager:PasteManager = null
  
  
  def listPage()="redirect:/main/paste/list"
  def editPage()="/paste/edit"
  def viewPage()="/paste/view"


  def manager():PasteManager = return pasteManager
 
   @ModelAttribute("query")
   def newQuery():OwnerQuery = {
     val out =new OwnerQuery
   //  if (!isCurrentUserAdmin) {
   //    out.setOwnerId(getCurrentUser.getId)
   // }
    return out
  }

  protected override def fillListModel(model:Model,locale:Locale) {
    model.addAttribute("title","Pastas")

  }


  @RequestMapping(value = Array(GenericListController.LIST_ACTION),method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  override def list( request:HttpServletRequest, locale:Locale,  model:Model,
           @RequestParam(required = false)  page:java.lang.Integer,
           @RequestParam(required = false)  NPpage:String,
           @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[Paste] = {


    return super.list(request, locale,model, page, NPpage, pageSize)
  }
  
 
  
  @RequestMapping(value = Array("/own/list"))
  @ModelAttribute("items")
  def listOwn(model:Model,locale:Locale):java.util.List[Paste] = {
    return manager.getByOwner(getCurrentUser)
  }
  @RequestMapping(value = Array("/own/list/body"), method = Array(RequestMethod.GET))
  @ModelAttribute("items")
  @ResponseBody
  def listOwnBody():java.util.List[Paste] = {
    return manager.getByOwner(getCurrentUser)
  }
  
}
