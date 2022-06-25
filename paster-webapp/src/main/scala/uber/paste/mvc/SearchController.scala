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

package uber.paste.mvc

import uber.paste.dao.SearchableDaoImpl
import javax.servlet.http.HttpServletRequest
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation._
import java.util.{Collections, Locale}
import uber.paste.model.{Struct, Query, Key}
import org.springframework.beans.support.PagedListHolder
import org.apache.lucene.queryparser.classic.ParseException
import org.apache.commons.lang3.StringUtils
import  scala.jdk.CollectionConverters._



/**
 * Search result object
 */
class SearchResult(code:String,desc:String,kitemsModel:String) extends Key(code,desc){

  /**
   * name of the model (used from web)
   */
  private var itemsModel:String=kitemsModel

  //private var resultCount:Int =0 
  
 override  def create(code:String) = new SearchResult(code,null,null)
  
  def getItemsModel():String = itemsModel
  def getCodeLowerCase() = super.getCode().toLowerCase
 // def getResultCount() = resultCount
}


object SearchController {
  final val SEARCH_ACTION = "/list/search"
  final val TOTAL_FOUND ="totalFound"

}

/**
 * Abstract Search Controller.
 * All children will have +1 search ability
 * @tparam T model class
 * @tparam QV query class
 */
abstract class SearchController[T <: Struct,QV <: Query ] extends GenericListController[T] {

  /**
   * linked search manager
   * @return
   */
  protected override def manager():SearchableDaoImpl[T]
 
   //@ModelAttribute
   def newQuery():QV

  /**
   *
   * @return search results if any
   */
  def getAvailableResults():java.util.Collection[SearchResult]

  def getSearchResultByCode(code:String):SearchResult

  def getManagerBySearchResult(result:SearchResult):SearchableDaoImpl[_]

  override protected def fillListModel(model:Model,locale:Locale) {
           super.fillListModel(model,locale)
          model.addAttribute("availableResults",getAvailableResults())
  }


  protected def fillSearchModel(model:Model,locale:Locale) {
    model.addAttribute(GenericListController.LIST_MODE,"search")
  }

  /**
   * main search function
   * @param request
   * @param locale
   * @param query
   * @param model
   * @param page
   * @param NPpage
   * @param pageSize
   * @return
   */
  @RequestMapping(value = Array(SearchController.SEARCH_ACTION),
                  method = Array(RequestMethod.POST,RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def search( request:HttpServletRequest, locale:Locale,
                    @ModelAttribute("query") query:QV,  model:Model,
           @RequestParam(required = false)  page:java.lang.Integer,
           @RequestParam(required = false)  NPpage:String,
           @RequestParam(required = false)  pageSize:java.lang.Integer,
           @RequestParam(required = false)  sortColumn:String,
           @RequestParam(required = false)  sortAsc:Boolean):java.util.List[T] = {


    fillListModel(model,locale)
    fillSearchModel(model,locale)

    var out:java.util.List[T]=null
    try {

   //   var totalFound:Int =0

      for (r<-getAvailableResults().asScala) {

       val rout =  processPageListHolder(request,
                                         locale,
                                         model,
                                         page,
                                         NPpage,
                                         pageSize,
                                         sortColumn,sortAsc,new SourceCallback[T]() {

         override def invokeCreate():PagedListHolder[T]= {
           try {
             new PagedListHolder[T](search(query,r).asInstanceOf[java.util.List[T]])
           } catch {
             case e:ParseException => {
               logger.error(e.getLocalizedMessage,e)
               new PagedListHolder[T]()
             }
           }
         }
       },r.getItemsModel(),false
       )

      //  totalFound+=rout.size()

        if (out==null && !rout.isEmpty) {
          out= rout
          model.addAttribute(GenericController.NODE_LIST_MODEL_PAGE, 
                             model.asMap().get(r.getItemsModel()))
          model.addAttribute("result",r.getCodeLowerCase())
          logger.debug("found {} in {}",out.size(),r.getItemsModel())
         }
      }

     // model.addAttribute(SearchController.TOTAL_FOUND,totalFound)

      return if (out==null) {
       model.addAttribute(GenericController.NODE_LIST_MODEL_PAGE,
         new PagedListHolder[T]( java.util.Collections.emptyList[T]()))
        model.addAttribute("result","")          //tiles bug

        logger.debug("no results found in any models")

       java.util.Collections.emptyList[T]()

     } else { out }

    } catch {
      case e:ParseException =>
         model.addAttribute("statusMessageKey", "action.query.incorrect")
         return manager.getList
    }  
  }


  def search(query:Query,result:SearchResult):java.util.List[_]= {
    logger.debug("_search {}",query.getQuery())

    return if(StringUtils.isBlank(query.getQuery()))
      getManagerBySearchResult(result).getList()
    else
      getManagerBySearchResult(result).search(query.getQuery)
  }

  override def listImpl( request:HttpServletRequest, locale:Locale,  model:Model,
                page:java.lang.Integer,
                NPpage:String,
                pageSize:java.lang.Integer,
              sortColumn:String,sortAsc:Boolean,result:String):java.util.List[T] = {
    fillSearchModel(model,locale)
    model.addAttribute("result",result.toLowerCase())

    if (logger.isDebugEnabled()) {
      logger.debug("_listImpl(search) pageSize {} , result {}",Array(pageSize,model.asMap().get("result")))
    }
    
    return super.listImpl(request,locale,model,page,NPpage,pageSize,sortColumn,sortAsc,getSearchResultByCode(result).getItemsModel())
  }


  @RequestMapping(value = Array("/search/{result:[a-z]+}/{page:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPath(@PathVariable("page") page:java.lang.Integer,
                   @PathVariable("result") result:String,
                 request:HttpServletRequest,
                 model:Model,
                 locale:Locale) =  listImpl(request,locale, model, page, null, null,null,false, result)


  @RequestMapping(value = Array("/search/{result:[a-z]+}/limit/{pageSize:[0-9]+}"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPathSize(
                        @PathVariable("pageSize") pageSize:java.lang.Integer,
                        @PathVariable("result") result:String,
                        request:HttpServletRequest,
                        model:Model,
                        locale:Locale)= listImpl(request,locale, model, null, null, pageSize,null,false,
                        result)


  @RequestMapping(value = Array("/search/{result:[a-z]+}/next"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPathNext(
                      request:HttpServletRequest,
                      @PathVariable("result") result:String,
                      model:Model,
                      locale:Locale) = listImpl(request,locale, model, 
                                                null, GenericListController.NEXT_PARAM, null,null,false,
                      result)


  @RequestMapping(value = Array("/search/{result:[a-z]+}/prev"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPathPrev(
                      request:HttpServletRequest,
                      @PathVariable("result") result:String,
                      model:Model,
                      locale:Locale) = listImpl(request,locale, model, null, 
                                                "prev", null,null,false,result)


  
}
