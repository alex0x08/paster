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
import org.springframework.beans.support.MutableSortDefinition
import org.springframework.beans.support.PagedListHolder
import org.joda.time.{DateTime, DateMidnight, Days}
import org.apache.commons.lang.StringEscapeUtils


object PasteSearchResult extends KeyValueObj[SearchResult] {

  val PASTE = new SearchResult("paste","result.paste.name","pasteItems")
  val COMMENT = new SearchResult("comment","result.comment.name","commentItems")

  add(PASTE)
  add(COMMENT)

}


@Controller
@RequestMapping(value=Array("/paste"))
class PasteListController extends SearchController[Paste,OwnerQuery] {

  class DateSplitHelper(locale:Locale) {

    var prevDate:java.util.Date =null
    var curDate:java.util.Date = null
    var total:Int =0
    var title:String =null

    def setCurDate(date:java.util.Date) {
      curDate = date

      if (prevDate == null) {
        prevDate = curDate
      }
      total.+=(1)
    }

    def getSplitTitle() = title

    def isSplit():Boolean = {

      val d = Days.daysBetween(new DateTime(prevDate), new DateTime(curDate))
      //System.out.println("_split dys="+d.getDays+" prev="+prevDate+" cur="+curDate)
      return if ( scala.math.abs(d.getDays())>14 )  {
        title=  PasteListController.this.getResource("paste.list.slider.title",Array(curDate,prevDate,total),locale)
        prevDate = curDate
        total =0

        true
      } else {
        false
      }

    }

  }

  @Value("${config.smtpd.enabled}")
  val mailSource:Boolean = false

  @Value("${config.skype.enabled}")
  val skypeSource:Boolean = false

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

    if (!mailSource) {
      PasteSource.list.remove(PasteSource.MAIL)
    }
    if (!skypeSource) {
      PasteSource.list.remove(PasteSource.SKYPE)
    }

    model.addAttribute("availableSourceTypes",PasteSource.list)
    model.addAttribute("splitHelper",new DateSplitHelper(locale))
                    //config.share.integration=1
    //config.share.url=https://dev.iqcard.ru/share

    model.addAttribute("sortDesc",false)
  }

  
  @RequestMapping(value = Array("/integrated/list/{integrationCode:[a-z0-9_]+}/{source:[a-zA-Z0-9]+}/{page:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathSourceIntegrated(@PathVariable("page") page:java.lang.Integer,
                                 @PathVariable("source") source:String,
                                 @PathVariable("integrationCode") integrationCode:String,
                                 request:HttpServletRequest,
                                 model:Model,
                                 locale:Locale) =  listImpl(request,locale, model, page, null, null,null,false,source,integrationCode)
  
  
  
  @RequestMapping(value = Array("/integrated/list/{integrationCode:[a-z0-9_]+}/{source:[a-zA-Z0-9]+}/limit/{pageSize:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathSizeSourceIntegrated(@PathVariable("pageSize") pageSize:java.lang.Integer,
                           @PathVariable("source") source:String,
                           @PathVariable("integrationCode") integrationCode:String,
                           request:HttpServletRequest,
                           model:Model,
                           locale:Locale)= listImpl(request,locale, model, null, null, pageSize,null,false,source,integrationCode)


  @RequestMapping(value = Array("/integrated/list/{integrationCode:[a-z0-9_]+}/{source:[a-zA-Z0-9]+}/next"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathNextSourceIntegrated(
                            request:HttpServletRequest,
                            @PathVariable("source") source:String,
                            @PathVariable("integrationCode") integrationCode:String,
                            model:Model,
                            locale:Locale) = listImpl(request,locale, model, null, GenericListController.NEXT_PARAM, null,null,false,source,integrationCode)


  @RequestMapping(value = Array("/integrated/list/{integrationCode:[a-z0-9_]+}/{source:[a-zA-Z0-9]+}/prev"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathPrevSourceIntegrated(
                            request:HttpServletRequest,
                            @PathVariable("source") source:String,
                            @PathVariable("integrationCode") integrationCode:String,
                            model:Model,
                            locale:Locale) = listImpl(request,locale, model, null, "prev", null,null,false,source,integrationCode)

  @RequestMapping(value = Array(
    "/integrated/list/{integrationCode:[a-z0-9_]+}/{source:[a-zA-Z0-9]+}/earlier",
   "/integrated/list/{integrationCode:[a-z0-9_]+}/{source:[a-zA-Z0-9]+}"
  ),method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listSourceIntegrated( request:HttpServletRequest,
                  @PathVariable("source") source:String,
                  @PathVariable("integrationCode") integrationCode:String,
                  locale:Locale,
                  model:Model,
                  @RequestParam(required = false)  page:java.lang.Integer,
                  @RequestParam(required = false)  NPpage:String,
                  @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[Paste] = {

    return listImpl(request,locale,model,page,NPpage,pageSize,null,false,source,integrationCode)
  }


  @RequestMapping(value = Array(
   "/integrated/list/{integrationCode:[a-z0-9_]+}/{source:[a-zA-Z0-9]+}/older"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listSourceOlderIntegrated( request:HttpServletRequest,
                       @PathVariable("source") source:String,
                       @PathVariable("integrationCode") integrationCode:String,
                       locale:Locale,
                       model:Model,
                       @RequestParam(required = false)  page:java.lang.Integer,
                       @RequestParam(required = false)  NPpage:String,
                       @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[Paste] = {

    return listImpl(request,locale,model,page,NPpage,pageSize,null,false,source,integrationCode)
  }
  
  
  
  @RequestMapping(value = Array(
      "/integrated/list/{integrationCode:[a-z0-9_]+}"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listIntegrated( request:HttpServletRequest, locale:Locale,  model:Model,
                     @PathVariable("integrationCode") integrationCode:String,
                     @RequestParam(required = false)  page:java.lang.Integer,
                     @RequestParam(required = false)  NPpage:String,
                     @RequestParam(required = false)  pageSize:java.lang.Integer,
                     @RequestParam(required = false)  sortColumn:String,
                     @RequestParam(required = false)  sortAsc:Boolean):java.util.List[Paste] = {
    
    return listImpl(request,locale,model,page,NPpage,pageSize,sortColumn,sortAsc,PasteSource.FORM.getCode(),integrationCode)
  }
  

  @RequestMapping(value = Array("/search/{result:[a-z]+}/{page:[0-9]+}",
                                 "/raw/search/{result:[a-z]+}/{page:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  override def searchByPath(@PathVariable("page") page:java.lang.Integer,
                            @PathVariable("result") result:String,
                            request:HttpServletRequest,
                            model:Model,
                            locale:Locale) =  listImpl(request,locale, model, page, null, null,null,false,null, result)
  
  @RequestMapping(value = Array(
       "/raw/search/{result:[a-z]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPathParam(@RequestParam(required = false) page:java.lang.Integer,
                        @PathVariable("result") result:String,
                        request:HttpServletRequest,
                        model:Model,
                        locale:Locale) =  listImpl(request,locale, model, page, null, null,null,false, null, result)
  
  
  
  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}/{page:[0-9]+}",
                                "/raw/list/{source:[a-zA-Z0-9]+}/{page:[0-9]+}"), 
                  method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathSource(@PathVariable("page") page:java.lang.Integer,
                       @PathVariable("source") source:String,
                       request:HttpServletRequest,
                       model:Model,
                       locale:Locale) =  listImpl(request,locale, model, page, null, null,"lastModified",false,source,null)
  
  
  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}/limit/{pageSize:[0-9]+}"), 
                  method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathSizeSource(@PathVariable("pageSize") pageSize:java.lang.Integer,
                           @PathVariable("source") source:String,
                           request:HttpServletRequest,
                           model:Model,
                           locale:Locale)= listImpl(request,locale, model, null, null, pageSize,"lastModified",false,source,null)
  
  
  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}/next"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathNextSource(
                      request:HttpServletRequest,
                      @PathVariable("source") source:String,
                      model:Model,
                      locale:Locale) = listImpl(request,locale, model, null, GenericListController.NEXT_PARAM, null,"lastModified",false,source,null)


  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}/prev"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathPrevSource(
                      request:HttpServletRequest,
                      @PathVariable("source") source:String,
                      model:Model,
                      locale:Locale) = listImpl(request,locale, model, null, "prev", null,"lastModified",false,source,null)

  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}",
                                "/raw/list/{source:[a-zA-Z0-9]+}",
                                "/list/{source:[a-zA-Z0-9]+}/earlier"),method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listSource( request:HttpServletRequest,
                           @PathVariable("source") source:String,
                           locale:Locale,
                           model:Model,
                     @RequestParam(required = false)  page:java.lang.Integer,
                     @RequestParam(required = false)  NPpage:String,
                     @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[Paste] = {

    return listImpl(request,locale,model,page,NPpage,pageSize,null,false,source,null)
  }


  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}/older"),method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listSourceOlder( request:HttpServletRequest,
                  @PathVariable("source") source:String,
                  locale:Locale,
                  model:Model,
                  @RequestParam(required = false)  page:java.lang.Integer,
                  @RequestParam(required = false)  NPpage:String,
                  @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[Paste] = {

    return listImpl(request,locale,model,page,NPpage,pageSize,"lastModified",true,source,null)
  }

  @RequestMapping(value = Array("/list",
                                "/raw/list"),
                  method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  override def list( request:HttpServletRequest, locale:Locale,  model:Model,
           @RequestParam(required = false)  page:java.lang.Integer,
           @RequestParam(required = false)  NPpage:String,
           @RequestParam(required = false)  pageSize:java.lang.Integer,
           @RequestParam(required = false)  sortColumn:String,
           @RequestParam(required = false)  sortAsc:Boolean =false):java.util.List[Paste] = {

    return listImpl(request,locale,model,page,NPpage,pageSize,sortColumn,sortAsc,PasteSource.FORM.getCode(),null)
  }

  @RequestMapping(value = Array( "/count/{source:[a-zA-Z0-9]+}/{since:[0-9]+}"), 
                  method = Array(RequestMethod.GET),produces =Array("application/json")) 
  @ModelAttribute(GenericController.NODE_COUNT_KEY)
   def countAllSince(@PathVariable("source") source:String, 
                    @PathVariable("since")dateFrom:java.lang.Long):java.lang.Long = {
               //System.out.println("_count all from "+dateFrom)   
     return pasteManager.countAllSince(PasteSource.valueOf(source.toUpperCase),dateFrom)
  }
  

  def listImpl( request:HttpServletRequest, locale:Locale,  model:Model,
                     page:java.lang.Integer,
                     NPpage:String,
                     pageSize:java.lang.Integer,
                     sortColumn:String,
                     sortAsc:java.lang.Boolean = false,
                     sourceType:String,integrationCode:String):java.util.List[Paste] = {

    logger.debug("_paste listImpl, pageSize "+pageSize)

    fillListModel(model,locale)

    if (integrationCode!=null) {
      model.addAttribute("integrationCode",integrationCode)
    }

    val ps = if (sourceType!=null) {
      model.addAttribute("sourceType",sourceType.toLowerCase)
      model.addAttribute("sortDesc",!sortAsc)

      PasteSource.valueOf(sourceType.toUpperCase)
    } else {null}

    //System.out.println("ps="+ps+" sourceType="+sourceType)
   
    return processPageListHolder(request,locale,model,page,NPpage,pageSize,sortColumn,sortAsc,
      if (ps==null) {pasterListCallback} else {new PasteListCallback(ps,sortAsc,integrationCode)},
      GenericController.NODE_LIST_MODEL_PAGE)
      
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

  protected val pasterListCallback:PasteListCallback  = new PasteListCallback(PasteSource.FORM,true,null)

  class PasteListCallback(sourceType:PasteSource,sortAsc:Boolean,integrationCode:String) extends SourceCallback[Paste] {
    override def invokeCreate():PagedListHolder[Paste] = {
      val ph =new ExtendedPageListHolder[Paste](if (integrationCode!=null) {
        manager.getListIntegrated(integrationCode)} else {
        manager.getBySourceType(sourceType,sortAsc)})
    
      val sort =ph.getSort().asInstanceOf[MutableSortDefinition]
            
                /**
                 * default sort
                 */
                sort.setProperty("lastModified")
		sort.setIgnoreCase(false)
                sort.setAscending(sortAsc)
      
      return ph
    }
  }

}
