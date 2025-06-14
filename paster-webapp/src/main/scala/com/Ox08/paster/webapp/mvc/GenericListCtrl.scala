/*
 * Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.mvc
import com.Ox08.paster.webapp.dao.StructDaoImpl
import com.Ox08.paster.webapp.model.Struct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.support.{MutableSortDefinition, PagedListHolder}
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._
import java.util
import java.util.Objects
/**
 * Single column used for sorting
 *
 * @param property
 * column title
 * @param name
 * sorting key
 */
class SortColumn(property: String, name: String) {
  def getName: String = name
  def getProperty: String = property
  override def equals(from: Any): Boolean =
    from.isInstanceOf[SortColumn] && property != null &&
      property.equals(from.asInstanceOf[SortColumn].getProperty)
  override def hashCode(): Int =
    42 + Objects.hashCode(this.property)
}
/**
 *
 * @param source
 * list of source elements to apply pagination
 * @tparam T
 * type of element
 */
class ExtendedPageListHolder[T <: Struct](source: java.util.List[T])
  extends PagedListHolder[T](source) {
  def getFirstElement: T = if
  (getFirstElementOnPage >= 0) getSource.get(getFirstElementOnPage)
  else null.asInstanceOf[T]
  def getLastElement: T = if
  (getLastElementOnPage >= 0) getSource.get(getLastElementOnPage)
  else null.asInstanceOf[T]
  def getElementsOnPage: Int = getPageList.size()
}
/**
 * Abstract Controller to deal with lists
 * Based on Spring PageListHolder heavy usage
 *
 * @tparam T model class
 */
abstract class GenericListCtrl[T <: Struct] extends AbstractCtrl {
  /**
   * link to list page
   */
  protected def listPage: String
  /**
   * edit page
   */
  protected def editPage: String
  protected def viewPage: String
  /**
   * binded DAO service
   */
  protected def manager(): StructDaoImpl[T]
  /**
   * default callback object: will simply use getList from attached manager
   */
  private val defaultListCallback: SourceCallback[T] = new SourceCallback[T]() {
    override def invokeCreate(): PagedListHolder[T] =
      new PagedListHolder[T](manager().getList)
  }
  protected def fillListModel(model: Model): Unit = {
    /**
     * set default list mode
     */
    if (!model.containsAttribute("listMode"))
      model.addAttribute("listMode", "list")
  }
  /**
   * process pagination
   *
   */
  protected def processPageListHolder(request: HttpServletRequest,
                                      model: Model,
                                      page: java.lang.Integer,
                                      NPpage: String,
                                      pageSize: java.lang.Integer, sortColumn: String, sortAsc: Boolean,
                                      callback: SourceCallback[T],
                                      pageHolderName: String,
                                      createDefaultItemModel: Boolean = true): java.util.List[T] = {
    var pagedListHolder: PagedListHolder[T] = request.getSession()
      .getAttribute(s"${getClass.getName}_$pageHolderName")
      .asInstanceOf[PagedListHolder[T]]
    /**
     * if no pageListHolder found or no page controls is set - recreate pageListHolder (load data from db)
     */
    if (pagedListHolder == null ||
      (page == null && NPpage == null && pageSize == null && sortColumn == null)) {
      pagedListHolder = callback.invokeCreate()
      if (logger.isDebugEnabled)
        logger.debug("pagedListHolder created pageSize={}", pageSize)
    } else {
      if (sortColumn != null) {
        val sort = pagedListHolder.getSort.asInstanceOf[MutableSortDefinition]
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
        if (NPpage.equals("next"))
          pagedListHolder.nextPage()
        else
          pagedListHolder.previousPage()
        /**
         * if page number was specified
         */
      } else if (page != null) pagedListHolder.setPage(
        (if (page < 1) 1
          else if (page > pagedListHolder.getPageCount)
            pagedListHolder.getPageCount
          else page ).asInstanceOf[Integer] - 1)
    }
    /**
     * if items per page parameter was specified
     */
    if (pageSize != null)
      pagedListHolder.setPageSize(pageSize)
    request.getSession()
      .setAttribute("%s_%s".format(getClass.getName, pageHolderName), pagedListHolder)
    model.addAttribute(pageHolderName, pagedListHolder)
    // assign paged list with page model
    if (createDefaultItemModel && !pageHolderName.equals(MvcConstants.NODE_LIST_MODEL_PAGE))
      model.addAttribute(MvcConstants.NODE_LIST_MODEL_PAGE, pagedListHolder)
    model.addAttribute("pageSet", Array(5, 10, 50, 100, 500))
    pagedListHolder.getPageList
  }
  @ModelAttribute("availableSortColumns")
  def getAvailableSortColumns: List[SortColumn] = List[SortColumn](
    new SortColumn("id", "struct.id"),
    new SortColumn("name", "struct.name"),
    new SortColumn("lastModified", "struct.lastModified"))
  @RequestMapping(value = Array("/list/sort/{sortColumn:[a-z0-9A-Z]+}",
    "/list/sort/{sortColumn:[a-z0-9A-Z]+}/up"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listWithSort(@PathVariable("sortColumn") sortColumn: String,
                   request: HttpServletRequest,
                   model: Model): util.List[T] = list(request, model,
    null, null, null, sortColumn, sortAsc = false)
  @RequestMapping(value = Array("/list/sort/{sortColumn:[a-z0-9A-Z]+}/down"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listWithSortDown(@PathVariable("sortColumn") sortColumn: String,
                       request: HttpServletRequest,
                       model: Model): util.List[T] = list(request, model,
    null, null, null, sortColumn, sortAsc = true)

  /*@RequestMapping(value = Array("/list/{page:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listByPath(@PathVariable("page") page: java.lang.Integer,
                 request: HttpServletRequest,
                 model: Model): util.List[T] = list(request, model, page,
    null, null, null, sortAsc = false)*/
  @RequestMapping(value = Array("/list/limit/{pageSize:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listByPathSize(@PathVariable("pageSize") pageSize: java.lang.Integer,
                     request: HttpServletRequest,
                     model: Model): util.List[T] = list(request, model,
    null, null, pageSize, null, sortAsc = false)
  @RequestMapping(value = Array("/list/next"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listByPathNext(
                      request: HttpServletRequest,
                      model: Model): util.List[T] = list(request, model, null,
    "next", null, null, sortAsc = false)
  @RequestMapping(value = Array("/list/prev"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listByPathPrev(
                      request: HttpServletRequest,
                      model: Model): util.List[T] = list(request, model, null, "prev",
    null, null, sortAsc = false)
  @RequestMapping(value = Array("/list"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def list(request: HttpServletRequest,
           model: Model,
           @RequestParam(required = false) page: java.lang.Integer,
           @RequestParam(required = false) NPpage: String,
           @RequestParam(required = false) pageSize: java.lang.Integer,
           @RequestParam(required = false) sortColumn: String,
           @RequestParam(required = false) sortAsc: Boolean): java.util.List[T] =
    listImpl(request, model, page, NPpage, pageSize,
      sortColumn, sortAsc, MvcConstants.NODE_LIST_MODEL_PAGE)
  def listImpl(request: HttpServletRequest,
               model: Model,
               page: java.lang.Integer,
               NPpage: String,
               pageSize: java.lang.Integer,
               sortColumn: String, sortAsc: Boolean, result: String): java.util.List[T] = {
    fillListModel(model)
    processPageListHolder(request,
      model,
      page,
      NPpage,
      pageSize,
      sortColumn,
      sortAsc,
      defaultListCallback,
      result)
  }
  /**
   * this trait is used for lazy page holder creation
   */
  protected trait SourceCallback[TC] {
    def invokeCreate(): PagedListHolder[TC]
  }
}
