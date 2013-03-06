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
import java.util.Locale
import uber.paste.model.Struct
import uber.paste.model.Query
import org.springframework.beans.support.PagedListHolder
import uber.paste.dao.UserExistsException
import java.io.IOException
import org.apache.lucene.queryParser.ParseException


object SearchController {
  final val SEARCH_ACTION = "/list/search"
}

abstract class SearchController[T <: Struct,QV <: Query ] extends GenericListController[T] {


  protected override def manager():GenericSearchManager[T]
 
   //@ModelAttribute
   def newQuery():QV

  
  @RequestMapping(value = Array(SearchController.SEARCH_ACTION),
                  method = Array(RequestMethod.POST,RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def search( request:HttpServletRequest, locale:Locale,
                    @ModelAttribute("query") query:QV,  model:Model,
           @RequestParam(required = false)  page:java.lang.Integer,
           @RequestParam(required = false)  NPpage:String,
           @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[T] = {


    fillListModel(model,locale)

    try {
       return processPageListHolder(request,locale,model,page,NPpage,pageSize,new SourceCallback[T]() {

         override def invokeCreate():PagedListHolder[T]= {
           try {
             return new PagedListHolder[T](search(query))
           } catch {
             case e:ParseException => {
               logger.error(e.getLocalizedMessage,e)
               return new PagedListHolder[T]();
             }
           }
         }
       }
       );

    } catch {
      case e:org.compass.core.engine.SearchEngineQueryParseException =>
         model.addAttribute("statusMessageKey", "action.query.incorrect");
         return manager.getList
    }  
    
 //   return super.list(request, locale,model, page, NPpage, pageSize)
  }

  def search(query:Query):java.util.List[T]= {

    return if(query.getQuery()==null || query.getQuery().trim().length==0)
      manager.getList()
    else
      manager.search(query)
  }


  @RequestMapping(value = Array(SearchController.SEARCH_ACTION + "/{page}/{query}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPath(@PathVariable("page") page:java.lang.Integer,
                 request:HttpServletRequest,
                 model:Model,
                 locale:Locale) =  list(request,locale, model, page, null, null)


  @RequestMapping(value = Array(SearchController.SEARCH_ACTION + "/limit/{pageSize}/{query}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPathSize(@PathVariable("pageSize") pageSize:java.lang.Integer,
                     request:HttpServletRequest,
                     model:Model,
                     locale:Locale)= list(request,locale, model, null, null, pageSize)


  @RequestMapping(value = Array(SearchController.SEARCH_ACTION + "/next/{query}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPathNext(
                      request:HttpServletRequest,
                      model:Model,
                      locale:Locale) = list(request,locale, model, null, GenericListController.NEXT_PARAM, null)


  @RequestMapping(value = Array(SearchController.SEARCH_ACTION + "/prev/{query}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def searchByPathPrev(
                      request:HttpServletRequest,
                      model:Model,
                      locale:Locale) = list(request,locale, model, null, "prev", null)


  @RequestMapping(value = Array("/body/list"), method = Array(RequestMethod.GET,RequestMethod.POST))
  @ModelAttribute("items")
  @ResponseBody
  def searchBody(@ModelAttribute("query") query:Query):java.util.List[T] = {
    return search(query);
  }

}
