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

package com.Ox08.paster.webapp.mvc

import com.Ox08.paster.webapp.model.Struct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.support.{MutableSortDefinition, PagedListHolder}
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._
import java.util

object GenericListController {

  final val INTEGRATED = "/integrated"
  
  final val RAW = "/raw"  

  final val LIST_ACTION = "/list"

  final val COUNT_ACTION = "/count"

  final val NEXT_PARAM = "next"

  final val PAGE_SET = "pageSet"

  final val pageSet:Array[Int] = Array(5,10,50,100,500)
  /**
   * model attribute to specify list mode
   * value can be 'search' or 'list'
   */
  final val LIST_MODE = "listMode"
  
  
  final val defaultSortColumns:List[SortColumn] = 
            List[SortColumn](new SortColumn("id","struct.id"),
                              new SortColumn("name","struct.name"),
                              new SortColumn("lastModified","struct.lastModified"))
}

import java.util.Objects

class SortColumn(property:String, name:String) {

  def getName: String = name
  def getProperty: String = property

  override def equals(from:Any): Boolean =
    from.isInstanceOf[SortColumn] && property != null &&
      property.equals(from.asInstanceOf[SortColumn].getProperty)

  override def hashCode():Int =
    42 + Objects.hashCode(this.property)

}


class ExtendedPageListHolder[T <: Struct ](source:java.util.List[T]) 
                                          extends PagedListHolder[T](source) {
  
  def getFirstElement:T = if
      (getFirstElementOnPage>=0) getSource.get(getFirstElementOnPage)
      else null.asInstanceOf[T]
  def getLastElement:T = if
      (getLastElementOnPage>=0) getSource.get(getLastElementOnPage)
      else null.asInstanceOf[T]
  def getElementsOnPage:Int = getPageList.size()
  
}

/**
 * Abstract Controller to deal with lists
 * Based on Spring PageListHolder heavy usage
 * @tparam T model class
 */
abstract class GenericListController[T <: Struct ] extends GenericController[T] {

  /**
   * default callback object: will simply use getList from attached manager
   */
  protected val defaultListCallback:SourceCallback[T]  = new SourceCallback[T]() {
    override def invokeCreate():PagedListHolder[T] = 
     new PagedListHolder[T](manager().getList)
  }


  protected def fillListModel(model:Model): Unit= {
    /**
     * set default list mode
     */
    if (!model.containsAttribute(GenericListController.LIST_MODE)) {
      model.addAttribute(GenericListController.LIST_MODE,"list")
    }
  }

  /**
   * process pagination
   *
   */
  protected def processPageListHolder(request:HttpServletRequest,
                                      model:Model,
                                      page:java.lang.Integer,
                                      NPpage:String,
                                      pageSize:java.lang.Integer,sortColumn:String,sortAsc:Boolean,
                                      callback:SourceCallback[T],
                                      pageHolderName:String,
                                      createDefaultItemModel:Boolean = true):java.util.List[T] = {

      var pagedListHolder:PagedListHolder[T] = request.getSession()
            .getAttribute(getClass.getName+"_"+pageHolderName)
            .asInstanceOf[PagedListHolder[T]]

    /**
     * if no pageListHolder found or no page controls is set - recreate pageListHolder (load data from db)
     */
    if (pagedListHolder == null || 
        (page == null && NPpage == null && pageSize==null && sortColumn == null)) {
      pagedListHolder = callback.invokeCreate()
      logger.debug("pagedListHolder created pageSize={}",pageSize)
    } else {
      
       if (sortColumn!=null) {
               val sort =pagedListHolder.getSort.asInstanceOf[MutableSortDefinition]
                sort.setProperty(sortColumn)
                sort.setIgnoreCase(false)
                sort.setAscending(sortAsc)
                pagedListHolder.resort()
            }
      /**
       * NPage is string like NEXT or PREV
       * check if exist and use it
       */
      if (NPpage != null) {
        
        if (NPpage.equals(GenericListController.NEXT_PARAM)) {
          pagedListHolder.nextPage()
        } else {
          pagedListHolder.previousPage()
        }

        /**
         * if page number was specified
         */
      } else if (page!=null){
       
        pagedListHolder.setPage(
          (
          if (page < 1) 1
          else if (page > pagedListHolder.getPageCount)
            pagedListHolder.getPageCount
          else page
        ).asInstanceOf[Integer]-1)
      }

    }

    /**
     * if items per page parameter was specified
     */
    if (pageSize != null) {
      pagedListHolder.setPageSize(pageSize)
    }

    request.getSession().setAttribute(getClass.getName+"_"+pageHolderName, pagedListHolder)
    
    model.addAttribute(pageHolderName, pagedListHolder)

    if (createDefaultItemModel && !pageHolderName.equals(GenericController.NODE_LIST_MODEL_PAGE)) {
      model.addAttribute(GenericController.NODE_LIST_MODEL_PAGE, pagedListHolder)
    }

    model.addAttribute(GenericListController.PAGE_SET, GenericListController.pageSet)

    pagedListHolder.getPageList
    }
    
    @ModelAttribute("availableSortColumns")
    def getAvailableSortColumns:List[SortColumn] = GenericListController.defaultSortColumns

  
    @RequestMapping(value = Array("/list/sort/{sortColumn:[a-z0-9A-Z]+}",
                             "/list/sort/{sortColumn:[a-z0-9A-Z]+}/up"), 
                    method = Array(RequestMethod.GET))
    @ModelAttribute(GenericController.NODE_LIST_MODEL)
    def listWithSort(@PathVariable("sortColumn") sortColumn:String,
             request:HttpServletRequest,
             model:Model): util.List[T] = list(request,model, null, null,null, sortColumn,sortAsc = false)
    
    @RequestMapping(value = Array("/list/sort/{sortColumn:[a-z0-9A-Z]+}/down"), 
                    method = Array(RequestMethod.GET))
    @ModelAttribute(GenericController.NODE_LIST_MODEL)
    def listWithSortDown(@PathVariable("sortColumn") sortColumn:String,
            request:HttpServletRequest,
            model:Model): util.List[T] = list(request,model, null, null,null, sortColumn,sortAsc = true)
  
  @RequestMapping(value = Array("/list/{page:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPath(@PathVariable("page") page:java.lang.Integer,
  request:HttpServletRequest,
  model:Model): util.List[T] =  list(request,model, page, null, null, null,sortAsc = false)

  @RequestMapping(value = Array("/list/limit/{pageSize:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathSize(@PathVariable("pageSize") pageSize:java.lang.Integer,
  request:HttpServletRequest,
  model:Model): util.List[T] = list(request, model, null, null, pageSize, null,sortAsc = false)

  @RequestMapping(value = Array("/list/next"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathNext(
    request:HttpServletRequest,
    model:Model): util.List[T] = list(request,model, null, GenericListController.NEXT_PARAM, null, null,sortAsc = false)

  @RequestMapping(value = Array("/list/prev"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathPrev(
    request:HttpServletRequest,
    model:Model): util.List[T] = list(request, model, null, "prev",null, null,sortAsc = false)

  @RequestMapping(value = Array(GenericListController.LIST_ACTION),method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def list( request:HttpServletRequest,
            model:Model,
           @RequestParam(required = false)  page:java.lang.Integer,
           @RequestParam(required = false)  NPpage:String,
           @RequestParam(required = false)  pageSize:java.lang.Integer,
           @RequestParam(required = false)  sortColumn:String,
           @RequestParam(required = false)  sortAsc:Boolean):java.util.List[T] = 
  listImpl(request,model,page,NPpage,pageSize,sortColumn,sortAsc,GenericController.NODE_LIST_MODEL_PAGE)
  


  def listImpl( request:HttpServletRequest,
                model:Model,
             page:java.lang.Integer,
              NPpage:String,
              pageSize:java.lang.Integer,
              sortColumn:String,sortAsc:Boolean,result:String):java.util.List[T] = {

    fillListModel(model)
    processPageListHolder(request,model,page,NPpage,pageSize,sortColumn,sortAsc,defaultListCallback,
      result)
  }

  /**
   * this trait is used for lazy pageholder creation
   */
  protected trait SourceCallback[TC ] {
    def invokeCreate():PagedListHolder[TC]
  }

}
