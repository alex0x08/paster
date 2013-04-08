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
import org.springframework.web.bind.annotation._
import uber.paste.model._
import uber.paste.manager.{CommentManager, GenericSearchManager, PasteManager}
import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.{Value, Autowired}
import org.springframework.ui.Model
import java.util.Locale
import org.springframework.beans.support.PagedListHolder

object PasteSearchResult extends KeyValueObj[SearchResult] {
  val PASTE = new SearchResult("paste","result.paste.name","pasteItems")
  val COMMENT = new SearchResult("comment","result.comment.name","commentItems")

  add(PASTE)
  add(COMMENT)

}


@Controller
@RequestMapping(value=Array("/paste"))
class PasteListController extends SearchController[Paste,OwnerQuery] {



  @Autowired
  val pasteManager:PasteManager = null

  @Autowired
  val commentManager:CommentManager = null




  def listPage()="redirect:/main/paste/list"
  def editPage()="/paste/edit"
  def viewPage()="/paste/view"


  def manager():PasteManager = pasteManager

  def getAvailableResults():java.util.Collection[SearchResult] = PasteSearchResult.list

  def getSearchResultByCode(code:String):SearchResult = {
    val out = PasteSearchResult.valueOf(code.toLowerCase)
    return if (out==null) {PasteSearchResult.PASTE} else {out}
  }

  def getManagerBySearchResult(result:SearchResult):GenericSearchManager[_] = {
    return result match {
      case PasteSearchResult.PASTE => {manager()}
      case PasteSearchResult.COMMENT => {commentManager}

    }
  }


  @ModelAttribute("query")
   def newQuery():OwnerQuery = {
     val out =new OwnerQuery
   //  if (!isCurrentUserAdmin) {
   //    out.setOwnerId(getCurrentUser.getId)
   // }
    return out
  }

  protected override def fillListModel(model:Model,locale:Locale) {
    super.fillListModel(model,locale)
    model.addAttribute("title","Pastas")
    model.addAttribute("availableSourceTypes",PasteSource.list)


    //config.share.integration=1
    //config.share.url=https://dev.iqcard.ru/share


  }


  @RequestMapping(value = Array(GenericListController.LIST_ACTION + "/{source}/{page:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathSource(@PathVariable("page") page:java.lang.Integer,
                       @PathVariable("source") source:String,
                 request:HttpServletRequest,
                 model:Model,
                 locale:Locale) =  listImpl(request,locale, model, page, null, null,source,true)




  @RequestMapping(value = Array(GenericListController.LIST_ACTION + "/{source}/limit/{pageSize:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathSizeSource(@PathVariable("pageSize") pageSize:java.lang.Integer,
                           @PathVariable("source") source:String,
                           request:HttpServletRequest,
                     model:Model,
                     locale:Locale)= listImpl(request,locale, model, null, null, pageSize,source,true)


  @RequestMapping(value = Array(GenericListController.LIST_ACTION + "/{source}/next"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathNextSource(
                      request:HttpServletRequest,
                      @PathVariable("source") source:String,
                      model:Model,
                      locale:Locale) = listImpl(request,locale, model, null, GenericListController.NEXT_PARAM, null,source,true)


  @RequestMapping(value = Array(GenericListController.LIST_ACTION + "/{source}/prev"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathPrevSource(
                      request:HttpServletRequest,
                      @PathVariable("source") source:String,
                      model:Model,
                      locale:Locale) = listImpl(request,locale, model, null, "prev", null,source,true)

  @RequestMapping(value = Array(GenericListController.LIST_ACTION+"/{source}",GenericListController.LIST_ACTION+"/{source}/earlier"),method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listSource( request:HttpServletRequest,
                           @PathVariable("source") source:String,
                           locale:Locale,
                           model:Model,
                     @RequestParam(required = false)  page:java.lang.Integer,
                     @RequestParam(required = false)  NPpage:String,
                     @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[Paste] = {

    return listImpl(request,locale,model,page,NPpage,pageSize,source,true)
  }


  @RequestMapping(value = Array(GenericListController.LIST_ACTION+"/{source}/older"),method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listSourceOlder( request:HttpServletRequest,
                  @PathVariable("source") source:String,
                  locale:Locale,
                  model:Model,
                  @RequestParam(required = false)  page:java.lang.Integer,
                  @RequestParam(required = false)  NPpage:String,
                  @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[Paste] = {

    return listImpl(request,locale,model,page,NPpage,pageSize,source,false)
  }



  @RequestMapping(value = Array(GenericListController.LIST_ACTION),method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  override def list( request:HttpServletRequest, locale:Locale,  model:Model,
           @RequestParam(required = false)  page:java.lang.Integer,
           @RequestParam(required = false)  NPpage:String,
           @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[Paste] = {

    return listImpl(request,locale,model,page,NPpage,pageSize,PasteSource.FORM.getCode(),true)
  }


  def listImpl( request:HttpServletRequest, locale:Locale,  model:Model,
                     page:java.lang.Integer,
                     NPpage:String,
                     pageSize:java.lang.Integer,
                     sourceType:String,desc:java.lang.Boolean):java.util.List[Paste] = {

    logger.debug("_paste listImpl, pageSize "+pageSize)

    fillListModel(model,locale)

    val ps = if (sourceType!=null) {
      model.addAttribute("sourceType",sourceType.toLowerCase)
      model.addAttribute("sortDesc",desc)

      PasteSource.valueOf(sourceType.toUpperCase)
    } else {null}

    val order = if (desc == null) {java.lang.Boolean.TRUE} else {desc};

    return processPageListHolder(request,locale,model,page,NPpage,pageSize,
      if (ps==null) {pasterListCallback} else {new PasteListCallback(ps,order)},GenericController.NODE_LIST_MODEL_PAGE)

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

  protected val pasterListCallback:PasteListCallback  = new PasteListCallback(PasteSource.FORM,true)

  class PasteListCallback(sourceType:PasteSource,desc:Boolean) extends SourceCallback[Paste] {
    override def invokeCreate():PagedListHolder[Paste] = {
      return new PagedListHolder[Paste](manager.getBySourceType(sourceType,desc))
    }
  }

}
