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

import uber.paste.model.Struct
import javax.servlet.http.HttpServletRequest
import org.springframework.beans.support.PagedListHolder
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation._;
import java.util.Locale

object GenericListController {

  final val INTEGRATED = "/integrated"

  final val LIST_ACTION = "/list"

  final val NEXT_PARAM = "next"

  final val PAGE_SET = "pageSet"

  final val pageSet:Array[Int] = Array(5,10,50,100,500)
  /**
   * model attribute to specify list mode
   * value can be 'search' or 'list'
   */
  final val LIST_MODE = "listMode"
}

/**
 * Abstract Controller to deal with lists
 * Based on Spring PageListHolder heavy usage
 * @tparam T model class
 */
abstract class GenericListController[T <: Struct ] extends StructController[T] {

  /**
   * default callback object: will simply use getList from attached manager
   */
  protected val defaultListCallback:SourceCallback[T]  = new SourceCallback[T]() {

    override def invokeCreate():PagedListHolder[T] = {
      return new PagedListHolder[T](manager.getList())
    }
  }


  protected def fillListModel(model:Model,locale:Locale) {
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
   * @param request
   * @param locale
   * @param model
   * @param page
   * @param NPpage
   * @param pageSize
   * @param callback
   * @param pageHolderName
   * @param createDefaultItemModel
   * @return
   */
  protected def processPageListHolder(request:HttpServletRequest,
                                      locale:Locale,
                                      model:Model,
                                      page:java.lang.Integer,
                                      NPpage:String,
                                      pageSize:java.lang.Integer,
                                      callback:SourceCallback[T],
                                      pageHolderName:String,createDefaultItemModel:Boolean = true):java.util.List[T] = {

      var pagedListHolder:PagedListHolder[T] = request.getSession()
            .getAttribute(pageHolderName)
            .asInstanceOf[PagedListHolder[T]]

    /**
     * if no pageListHolder found or no page controls is set - recreate pageListHolder (load data from db)
     */
    if (pagedListHolder == null || (page == null && NPpage == null && pageSize==null)) {
      pagedListHolder = callback.invokeCreate()
      logger.debug("pagedListHolder created pageSize="+pageSize)
    } else {
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

        var npage = page
        
        if (npage < 1) {
          npage = 1
        }
        if (npage > pagedListHolder.getPageCount()) {
          npage = pagedListHolder.getPageCount()
        }

        pagedListHolder.setPage(npage-1)
      }

    }

    /**
     * if items per page parameter was specified
     */
    if (pageSize != null) {
      pagedListHolder.setPageSize(pageSize)
    }

    request.getSession().setAttribute(pageHolderName, pagedListHolder)
    model.addAttribute(pageHolderName, pagedListHolder)


    if (createDefaultItemModel && !pageHolderName.equals(GenericController.NODE_LIST_MODEL_PAGE)) {
      model.addAttribute(GenericController.NODE_LIST_MODEL_PAGE, pagedListHolder)
    }

    model.addAttribute(GenericListController.PAGE_SET, GenericListController.pageSet)

    return pagedListHolder.getPageList()
  }


  @RequestMapping(value = Array(GenericListController.LIST_ACTION + "/{page:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPath(@PathVariable("page") page:java.lang.Integer,
  request:HttpServletRequest,
  model:Model,
  locale:Locale) =  list(request,locale, model, page, null, null)


  @RequestMapping(value = Array(GenericListController.LIST_ACTION + "/limit/{pageSize:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathSize(@PathVariable("pageSize") pageSize:java.lang.Integer,
  request:HttpServletRequest,
  model:Model,
  locale:Locale)= list(request,locale, model, null, null, pageSize)


  @RequestMapping(value = Array(GenericListController.LIST_ACTION + "/next"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathNext(
    request:HttpServletRequest,
    model:Model,
    locale:Locale) = list(request,locale, model, null, GenericListController.NEXT_PARAM, null)


  @RequestMapping(value = Array(GenericListController.LIST_ACTION + "/prev"), method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def listByPathPrev(
    request:HttpServletRequest,
    model:Model,
    locale:Locale) = list(request,locale, model, null, "prev", null)

  @RequestMapping(value = Array(GenericListController.LIST_ACTION),method = Array(RequestMethod.GET))
  @ModelAttribute(GenericController.NODE_LIST_MODEL)
  def list( request:HttpServletRequest, locale:Locale,  model:Model,
           @RequestParam(required = false)  page:java.lang.Integer,
           @RequestParam(required = false)  NPpage:String,
           @RequestParam(required = false)  pageSize:java.lang.Integer):java.util.List[T] = {
  return listImpl(request,locale,model,page,NPpage,pageSize,GenericController.NODE_LIST_MODEL_PAGE)
  }


  def listImpl( request:HttpServletRequest, locale:Locale,  model:Model,
             page:java.lang.Integer,
              NPpage:String,
              pageSize:java.lang.Integer,result:String):java.util.List[T] = {

    fillListModel(model,locale)
    // putListModel(model);

    return processPageListHolder(request,locale,model,page,NPpage,pageSize,defaultListCallback,
      result)
    // return manager.getAll();
  }


  @RequestMapping(value = Array("/list/body"))
  //@ModelAttribute("items")
  //@ResponseBody
  def listBody():java.util.List[T] = {
    return manager.getList()
  }


  protected trait SourceCallback[T ] {
    def invokeCreate():PagedListHolder[T];
  }


}
