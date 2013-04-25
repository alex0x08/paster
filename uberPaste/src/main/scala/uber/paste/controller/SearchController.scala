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

import uber.paste.manager.GenericSearchManager
import javax.servlet.http.HttpServletRequest
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation._;
import java.util.{Collections, Locale}
import uber.paste.model.{KeyValue, KeyValueObj, Struct, Query}
import org.springframework.beans.support.PagedListHolder
import uber.paste.dao.UserExistsException
import java.io.IOException
import org.apache.lucene.queryParser.ParseException
import org.apache.commons.lang.StringUtils
import scala.collection.JavaConversions._




class SearchResult extends KeyValue{

  private var itemsModel:String=null

  def this(code:String,desc:String,itemsModel:String) = {
    this()
    setCode(code)
    setName(desc)
    this.itemsModel=itemsModel
  }

  def getItemsModel():String = itemsModel
  def getCodeLowerCase() = super.getCode().toLowerCase
}


object SearchController {
  final val SEARCH_ACTION = "/list/search"
  final val TOTAL_FOUND ="totalFound"

}

abstract class SearchController[T <: Struct,QV <: Query ] extends GenericListController[T] {


  protected override def manager():GenericSearchManager[T]
 
   //@ModelAttribute
   def newQuery():QV

  def getAvailableResults():java.util.Collection[SearchResult]

  def getSearchResultByCode(code:String):SearchResult

  def getManagerBySearchResult(result:SearchResult):GenericSearchManager[_]

  override protected def fillListModel(model:Model,locale:Locale) {
           super.fillListModel(model,locale)
          model.addAttribute("availableResults",getAvailableResults())
  }


  protected def fillSearchModel(model:Model,locale:Locale) {
    model.addAttribute(GenericListController.LIST_MODE,"search")
  }

  @RequestMapping(value = Array(SearchController.SEARCH_ACTION),
                  method = Array(RequestMethod.POST,RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def search( request:HttpServletRequest, locale:Locale,
                    @ModelAttribute("query") query:QV,  model:Model,
           @RequestParam(required = false)  page:java.lang.Integer,
           @RequestParam(required = false)  NPpage:String,
           @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[T] = {


    fillListModel(model,locale)
    fillSearchModel(model,locale)

    var out:java.util.List[T]=null
    try {

   //   var totalFound:Int =0

      for (r<-getAvailableResults()) {

       val rout =  processPageListHolder(request,locale,model,page,NPpage,pageSize,new SourceCallback[T]() {

         override def invokeCreate():PagedListHolder[T]= {
           try {
             return new PagedListHolder[T](search(query,r).asInstanceOf[java.util.List[T]])
           } catch {
             case e:ParseException => {
               logger.error(e.getLocalizedMessage,e)
               return new PagedListHolder[T]();
             }
           }
         }
       },r.getItemsModel(),false
       );

      //  totalFound+=rout.size()

        if (out==null && !rout.isEmpty) {
          out= rout
          model.addAttribute(GenericController.NODE_LIST_MODEL_PAGE, model.asMap().get(r.getItemsModel()))
          model.addAttribute("result",r.getCodeLowerCase())
          logger.debug("found "+out.size()+" in "+r.getItemsModel())
         }
      }

     // model.addAttribute(SearchController.TOTAL_FOUND,totalFound)

      return if (out==null) {
       model.addAttribute(GenericController.NODE_LIST_MODEL_PAGE,new PagedListHolder[T]( java.util.Collections.emptyList[T]()))
       logger.debug("no results found in any models")

       java.util.Collections.emptyList[T]()

     } else { out }

    } catch {
      case e:org.compass.core.engine.SearchEngineQueryParseException =>
         model.addAttribute("statusMessageKey", "action.query.incorrect");
         return manager.getList
    }  
    
 //   return super.list(request, locale,model, page, NPpage, pageSize)
  }

  def search(query:Query,result:SearchResult):java.util.List[_]= {

    return if(StringUtils.isBlank(query.getQuery()))
      getManagerBySearchResult(result).getList()
    else
      getManagerBySearchResult(result).search(query)
  }

  override def listImpl( request:HttpServletRequest, locale:Locale,  model:Model,
                page:java.lang.Integer,
                NPpage:String,
                pageSize:java.lang.Integer,result:String):java.util.List[T] = {
    fillSearchModel(model,locale)
    model.addAttribute("result",result.toLowerCase())

    logger.debug("_listImpl(search) pageSize "+pageSize+", result "+model.asMap().get("result"))

    return super.listImpl(request,locale,model,page,NPpage,pageSize,getSearchResultByCode(result).getItemsModel())
  }


  @RequestMapping(value = Array(SearchController.SEARCH_ACTION + "/{result:[a-z]+}/{page:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPath(@PathVariable("page") page:java.lang.Integer,
                   @PathVariable("result") result:String,
                 request:HttpServletRequest,
                 model:Model,
                 locale:Locale) =  listImpl(request,locale, model, page, null, null, result)


  @RequestMapping(value = Array(SearchController.SEARCH_ACTION + "/{result:[a-z]+}/limit/{pageSize:[0-9]+}"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPathSize(
                        @PathVariable("pageSize") pageSize:java.lang.Integer,
                        @PathVariable("result") result:String,
                        request:HttpServletRequest,
                        model:Model,
                        locale:Locale)= listImpl(request,locale, model, null, null, pageSize,
                        result)


  @RequestMapping(value = Array(SearchController.SEARCH_ACTION + "/{result:[a-z]+}/next"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPathNext(
                      request:HttpServletRequest,
                      @PathVariable("result") result:String,
                      model:Model,
                      locale:Locale) = listImpl(request,locale, model, null, GenericListController.NEXT_PARAM, null,
                      result)


  @RequestMapping(value = Array(SearchController.SEARCH_ACTION + "/{result:[a-z]+}/prev"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPathPrev(
                      request:HttpServletRequest,
                      @PathVariable("result") result:String,
                      model:Model,
                      locale:Locale) = listImpl(request,locale, model, null, "prev", null,result)


  /*@RequestMapping(value = Array("/body/list"), method = Array(RequestMethod.GET,RequestMethod.POST))
  @ModelAttribute("items")
  @ResponseBody
  def searchBody(@ModelAttribute("query") query:Query):java.util.List[T] = {
    return search(query)
  }
    */
}
