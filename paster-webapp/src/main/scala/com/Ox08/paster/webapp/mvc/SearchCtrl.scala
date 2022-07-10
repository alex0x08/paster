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

import com.Ox08.paster.webapp.dao.SearchableDaoImpl
import com.Ox08.paster.webapp.model.{Query, Struct}
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.lang3.StringUtils
import org.apache.lucene.queryparser.classic.ParseException
import org.springframework.beans.support.PagedListHolder
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._
import java.util


/**
 * Abstract Search Controller.
 * All children will have +1 search ability
 *
 * @tparam T  model class
 * @tparam QV query class
 */
abstract class SearchCtrl[T <: Struct, QV <: Query] extends GenericListCtrl[T] {

  /**
   * linked search manager
   *
   * @return
   */
  protected override def manager(): SearchableDaoImpl[T]

  def newQuery(): QV

  /**
   *
   * @return search results if any
   */
  def getAvailableResults: Array[String]

  def getManagerBySearchResult(result: String): SearchableDaoImpl[_]

  override protected def fillListModel(model: Model): Unit = {
    super.fillListModel(model)
    model.addAttribute("availableResults", getAvailableResults)
  }

  protected def fillSearchModel(model: Model): Unit = {
    model.addAttribute("listMode", "search")
  }

  /**
   * main search function
   *
   * @return
   */
  @RequestMapping(value = Array("/list/search"),
    method = Array(RequestMethod.POST, RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def search(request: HttpServletRequest,
             @ModelAttribute("query") query: QV, model: Model,
             @RequestParam(required = false) page: java.lang.Integer,
             @RequestParam(required = false) NPpage: String,
             @RequestParam(required = false) pageSize: java.lang.Integer,
             @RequestParam(required = false) sortColumn: String,
             @RequestParam(required = false) sortAsc: Boolean): java.util.List[T] = {


    fillListModel(model)
    fillSearchModel(model)

    var out: java.util.List[T] = null
    try {

      for (r <- getAvailableResults) {

        val rout = processPageListHolder(request,
          model,
          page,
          NPpage,
          pageSize,
          sortColumn, sortAsc, new SourceCallback[T]() {

            override def invokeCreate(): PagedListHolder[T] = {
              try {
                new PagedListHolder[T](search(query, r).asInstanceOf[java.util.List[T]])
              } catch {
                case e: ParseException =>
                  logger.error(e.getLocalizedMessage, e)
                  new PagedListHolder[T]()
              }
            }
          }, s"${r}_ITEMS", createDefaultItemModel = false
        )

        if (out == null && !rout.isEmpty) {
          out = rout
          model.addAttribute(MvcConstants.NODE_LIST_MODEL_PAGE,
            model.asMap().get(s"${r}_ITEMS"))
          model.addAttribute("result", r)
          if (logger.isDebugEnabled)
              logger.debug("found {} in {}", out.size(), s"${r}_ITEMS")
        }
      }

      if (out == null) {
        model.addAttribute(MvcConstants.NODE_LIST_MODEL_PAGE,
          new PagedListHolder[T](java.util.Collections.emptyList[T]()))
        model.addAttribute("result", "") //tiles bug
        if (logger.isDebugEnabled)
          logger.debug("no results found in any models")
        java.util.Collections.emptyList[T]()
      } else out

    } catch {
      case _: ParseException =>
        model.addAttribute("statusMessageKey", "action.query.incorrect")
        manager().getList
    }
  }


  def search(query: Query, result: String): java.util.List[_] = {
    logger.debug("_search {}", query.getQuery)

    if (StringUtils.isBlank(query.getQuery))
      getManagerBySearchResult(result).getList
    else
      getManagerBySearchResult(result).search(query.getQuery)
  }

  override def listImpl(request: HttpServletRequest,
                        model: Model,
                        page: java.lang.Integer,
                        NPpage: String,
                        pageSize: java.lang.Integer,
                        sortColumn: String, sortAsc: Boolean, result: String): java.util.List[T] = {
    fillSearchModel(model)
    model.addAttribute("result", result.toLowerCase())

    if (logger.isDebugEnabled()) {
      logger.debug("_listImpl(search) pageSize {} , result {}", Array(pageSize, model.asMap().get("result")))
    }

    super.listImpl(request, model, page, NPpage, pageSize, sortColumn, sortAsc, s"${result}_ITEMS" )
  }


  @RequestMapping(value = Array("/search/{result:[a-z]+}/{page:[0-9]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def searchByPath(@PathVariable("page") page: java.lang.Integer,
                   @PathVariable("result") result: String,
                   request: HttpServletRequest,
                   model: Model): util.List[T] = listImpl(request, model, page, null,
    null, null, sortAsc = false, result)


  @RequestMapping(value = Array("/search/{result:[a-z]+}/limit/{pageSize:[0-9]+}"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def searchByPathSize(
                        @PathVariable("pageSize") pageSize: java.lang.Integer,
                        @PathVariable("result") result: String,
                        request: HttpServletRequest,
                        model: Model): util.List[T] = listImpl(request, model, null,
    null, pageSize, null, sortAsc = false,
    result)


  @RequestMapping(value = Array("/search/{result:[a-z]+}/next"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def searchByPathNext(
                        request: HttpServletRequest,
                        @PathVariable("result") result: String,
                        model: Model): util.List[T] = listImpl(request,model,
    null, "next",
    null, null, sortAsc = false,
    result)


  @RequestMapping(value = Array("/search/{result:[a-z]+}/prev"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def searchByPathPrev(
                        request: HttpServletRequest,
                        @PathVariable("result") result: String,
                        model: Model): util.List[T] = listImpl(request, model, null,
    "prev", null, null, sortAsc = false, result)


}
