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
package com.Ox08.paster.webapp.mvc.paste
import com.Ox08.paster.webapp.dao.{ChannelDao, CommentDao, PasteDao, SearchableDaoImpl}
import com.Ox08.paster.webapp.model.{AuthorQuery, Paste}
import com.Ox08.paster.webapp.mvc._
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.beans.support.{MutableSortDefinition, PagedListHolder}
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._

import java.util
import java.util.concurrent.TimeUnit
import java.util.{Date, Locale}
import scala.jdk.CollectionConverters.SetHasAsJava
import scala.math.abs
@Controller
@RequestMapping(value = Array("/paste"))
class PasteListCtrl extends SearchCtrl[Paste, AuthorQuery] {
  @Value("${paster.paste.list.splitter.days:7}")
  val splitDays: Int = 0
  class DateSplitHelper(splitDays: Int, locale: Locale) {
    var prevDate: Date = _
    var curDate: Date = _
    var total: Int = 0
    var title: String = _
    def setCurDate(date: Date): Unit = {
      curDate = date
      if (prevDate == null) {
        prevDate = date
      }
      total.+=(1)
    }
    def getSplitTitle: String = title
    def isSplit: Boolean = {
      if (curDate == null || prevDate == null) {
        return false
      }
      val diff = curDate.getTime - prevDate.getTime
      val d = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
      if (abs(d) > 14) {
        title = PasteListCtrl.this.getResource("paste.list.slider.title",
          Array(curDate, prevDate, total), locale)
        prevDate = curDate
        total = 0
        true
      } else false
    }
  }
  @Autowired
  val channelDao: ChannelDao = null
  @Autowired
  val pasteDao: PasteDao = null
  @Autowired
  val commentDao: CommentDao = null
  override def listPage = "redirect:/main/paste/list"
  override def editPage = "/paste/edit"
  override def viewPage = "/paste/view"
  def manager(): PasteDao = pasteDao
  def getAvailableResults: Array[String] = Array[String]("PASTE", "COMMENT")
  def getManagerBySearchResult(result: String): SearchableDaoImpl[_] = result match {
    case "PASTE" => manager()
    case "COMMENT" => commentDao
  }
  @ModelAttribute("query")
  def newQuery(): AuthorQuery = new AuthorQuery
  protected override def fillListModel(model: Model): Unit = {
    super.fillListModel(model)
    model.addAttribute("title", "Pastas")
    model.addAttribute("availableSourceTypes", channelDao.getAvailableElements.asJava)
    model.addAttribute("splitHelper", new DateSplitHelper(splitDays, Locale.getDefault))
    model.addAttribute("sortDesc", false)
  }
  @RequestMapping(value = Array("/search/{result:[a-z]+}/{page:[0-9]+}",
    "/raw/search/{result:[a-z]+}/{page:[0-9]+}"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  override def searchByPath(@PathVariable("page") page: java.lang.Integer,
                            @PathVariable("result") result: String,
                            request: HttpServletRequest,
                            model: Model): util.List[Paste] = listImpl(request,
    model, page, null, null, null, false, null, result)
  @RequestMapping(value = Array(
    "/raw/search/{result:[a-z]+}"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def searchByPathParam(@RequestParam(required = false) page: java.lang.Integer,
                        @PathVariable("result") result: String,
                        request: HttpServletRequest,
                        model: Model): util.List[Paste] = listImpl(request,
    model, page, null, null, null, false, null, result)
  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}/{page:[0-9]+}",
    "/raw/list/{source:[a-zA-Z0-9]+}/{page:[0-9]+}"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listByPathSource(@PathVariable("page") page: java.lang.Integer,
                       @PathVariable("source") source: String,
                       request: HttpServletRequest,
                       model: Model): util.List[Paste] = listImpl(request, model, page, null,
    null, "lastModified", false, source, null)
  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}/limit/{pageSize:[0-9]+}"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listByPathSizeSource(@PathVariable("pageSize") pageSize: java.lang.Integer,
                           @PathVariable("source") source: String,
                           request: HttpServletRequest,
                           model: Model): util.List[Paste] = listImpl(request, model, null,
    null, pageSize, "lastModified", false, source, null)
  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}/next"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listByPathNextSource(
                            request: HttpServletRequest,
                            @PathVariable("source") source: String,
                            model: Model): util.List[Paste] = listImpl(request, model, null,
    "next",
    null,
    "lastModified",
    false, source, null)
  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}/prev"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listByPathPrevSource(
                            request: HttpServletRequest,
                            @PathVariable("source") source: String,
                            model: Model): util.List[Paste] = listImpl(request, model, null,
    "prev", null, "lastModified", false, source, null)
  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}",
    "/raw/list/{source:[a-zA-Z0-9]+}",
    "/list/{source:[a-zA-Z0-9]+}/earlier"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listSource(request: HttpServletRequest,
                 @PathVariable("source") source: String,
                 model: Model,
                 @RequestParam(required = false) page: java.lang.Integer,
                 @RequestParam(required = false) NPpage: String,
                 @RequestParam(required = false) pageSize: java.lang.Integer): java.util.List[Paste] = {
    listImpl(request, model, page, NPpage, pageSize, null, false, source, null)
  }
  @RequestMapping(value = Array("/list/{source:[a-zA-Z0-9]+}/older"), method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  def listSourceOlder(request: HttpServletRequest,
                      @PathVariable("source") source: String,
                      model: Model,
                      @RequestParam(required = false) page: java.lang.Integer,
                      @RequestParam(required = false) NPpage: String,
                      @RequestParam(required = false) pageSize: java.lang.Integer):
  java.util.List[Paste] = listImpl(request, model, page,
    NPpage, pageSize, "lastModified", true,
    source, null)
  @RequestMapping(value = Array("/list",
    "/raw/list"),
    method = Array(RequestMethod.GET))
  @ModelAttribute(MvcConstants.NODE_LIST_MODEL)
  override def list(request: HttpServletRequest, model: Model,
                    @RequestParam(required = false) page: java.lang.Integer,
                    @RequestParam(required = false) NPpage: String,
                    @RequestParam(required = false) pageSize: java.lang.Integer,
                    @RequestParam(required = false) sortColumn: String,
                    @RequestParam(required = false) sortAsc: Boolean = false): java.util.List[Paste] = {
    listImpl(request, model, page, NPpage, pageSize,
      sortColumn, sortAsc, null, null)
  }
  @RequestMapping(value = Array("/count/{source:[a-zA-Z0-9]+}/{since:[0-9]+}"),
    method = Array(RequestMethod.GET), produces = Array("application/json"))
  @ModelAttribute("count")
  def countAllSince(@PathVariable("source") channelCode: String,
                    @PathVariable("since") dateFrom: java.lang.Long): java.lang.Long = {
    pasteDao.countAllSince(channelCode, dateFrom)
  }
  def listImpl(request: HttpServletRequest, model: Model,
               page: java.lang.Integer,
               NPpage: String,
               pageSize: java.lang.Integer,
               sortColumn: String,
               sortAsc: java.lang.Boolean = false,
               channelCode: String, integrationCode: String): java.util.List[Paste] = {
    logger.debug("_paste listImpl, pageSize {}", pageSize)
    fillListModel(model)
    val ps = if (channelCode != null && channelDao.exist(channelCode)) {
      model.addAttribute("sourceType", channelCode.toLowerCase)
      model.addAttribute("sortDesc", !sortAsc)
      channelCode
    } else null
    processPageListHolder(request, model, page, NPpage, pageSize, sortColumn, sortAsc,
      if (ps == null) pasterListCallback else {
        new PasteListCallback(ps, sortAsc, integrationCode)
      }, MvcConstants.NODE_LIST_MODEL_PAGE)
  }
  @RequestMapping(value = Array("/own/list"))
  @ModelAttribute("items")
  def listOwn(): java.util.List[Paste] = {
    manager().getByAuthor(getCurrentUser)
  }
  @RequestMapping(value = Array("/own/list/body"), method = Array(RequestMethod.GET))
  @ModelAttribute("items")
  @ResponseBody
  def listOwnBody(): java.util.List[Paste] = {
    manager().getByAuthor(getCurrentUser)
  }
  protected val pasterListCallback: PasteListCallback = new PasteListCallback(null, true, null)
  class PasteListCallback(channel: String, sortAsc: Boolean, integrationCode: String)
    extends SourceCallback[Paste] {
    override def invokeCreate(): PagedListHolder[Paste] = {
      val ph = new ExtendedPageListHolder[Paste](
        if (integrationCode != null) {
          manager().getListIntegrated(integrationCode)
        } else {
          if (channel == null) {
            manager().getByChannel(channelDao.getDefault, sortAsc)
          } else
            manager().getByChannel(channel, sortAsc)
        })
      val sort = ph.getSort.asInstanceOf[MutableSortDefinition]

      /**
       * default sort
       */
      sort.setProperty("lastModified")
      sort.setIgnoreCase(false)
      sort.setAscending(sortAsc)
      ph
    }
  }
}
