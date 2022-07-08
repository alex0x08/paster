/*
 * Copyright 2011 WorldWide Conferencing, LLC.
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

import com.Ox08.paster.webapp.dao._
import com.Ox08.paster.webapp.manager.ResourceManager
import com.Ox08.paster.webapp.model._
import com.Ox08.paster.webapp.mvc.{GenericEditCtrl, MvcConstants}
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.Valid
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.{StringEscapeUtils, WordUtils}
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation._
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import java.util
import java.util.Locale
import scala.jdk.CollectionConverters._

/**
 * Paste model controller
 * Support for all operations related to one paste model: edit,view,delete,add comments..
 */
@Controller
@RequestMapping(Array("/paste"))
class PasteEditCtrl extends GenericEditCtrl[Paste] {

  @Autowired
  val codeTypeDao: CodeTypeDao = null

  @Autowired
  val channelDao: ChannelDao = null

  @Autowired
  val priorities: PriorityDao = null

  @Autowired
  val tagDao: TagDao = null

  @Autowired
  val pasteManager: PasteDao = null

  @Autowired
  val commentManager: CommentDao = null

  @Autowired
  val resourcePathHelper: ResourceManager = null

  @Autowired
  @Qualifier("mimeTypeSource")
  protected val mimeSource: MessageSource = null

  @Autowired
  @Qualifier("mimeExtSource")
  protected val mimeExtSource: MessageSource = null

  def listPage = "redirect:/main/paste/list"
  def editPage = "paste/edit"
  def viewPage = "paste/view"

  def manager(): PasteDao = pasteManager

  @InitBinder
  def initBinder(binder: WebDataBinder): Unit = {
    binder.initDirectFieldAccess()
  }

  override def fillEditModel(obj: Paste, model: Model, locale: Locale): Unit = {
    super.fillEditModel(obj, model, locale)

    if (obj.isBlank) {
      model.addAttribute("title", getResource("paste.new", locale))
    } else {
      model.addAttribute("title", StringEscapeUtils.escapeHtml4(
        getResource("paste.edit.title", Array(obj.id, obj.title), locale)))

      obj.comments.addAll(
        commentManager.getCommentsForPaste(obj.id))

      if (!model.containsAttribute("comment")) {
        model.addAttribute("comment", getNewCommentInstance(obj))
      }

    }
    model.addAttribute("availableCodeTypes", codeTypeDao.getAvailableElements)
    model.addAttribute("availablePriorities", priorities.getAvailableElements)
    model.addAttribute("availableChannels", channelDao.getAvailableElements)

    if (!obj.comments.isEmpty) {
      val commentLines = new util.ArrayList[Long]()
      for (c <- obj.comments.asScala) {
        commentLines.add(c.lineNumber)
      }

      model.addAttribute("commentedLinesList", StringUtils.join(commentLines, ","))
    }

    if (!obj.isBlank) {
      val pnext = manager().getNextPaste(obj)
      val pprev = manager().getPreviousPaste(obj)

      model.addAttribute("availableNext", pnext)
      model.addAttribute("availablePrev", pprev)
      model.addAttribute("availablePrevList", new IdList(manager().getPreviousPastasIdList(obj)))

    } else {
      model.addAttribute("availableNext", null)
      model.addAttribute("availablePrev", null)
    }
  }

  override def getNewModelInstance: Paste = {
    val p = new Paste()
    if (isCurrentUserLoggedIn) {
      p.author = getCurrentUser.getUsername()
    }
    p.channel =channelDao.getDefault
    p
  }

  def getNewCommentInstance(pp: Paste): Comment = {
    val p = new Comment()
    if (getCurrentUser!=null) {
      p.author =getCurrentUser.getUsername()
    }
    p.pasteId = pp.id
    p
  }


  @RequestMapping(value = Array("/removeComment"),
    method = Array(RequestMethod.POST, RequestMethod.GET))
  def removeComment(@RequestParam(required = true) commentId: Integer,
                    @RequestParam(required = true) pasteId: Integer,
                    @RequestParam(required = true) lineNumber: Long,
                    model: Model): String = {

    if (logger.isDebugEnabled)
      logger.debug("_removeComment commentId={} , lineNumber ={} {}", commentId, lineNumber, "")

    commentManager.remove(commentId)
    model.asMap().clear()
    s"redirect:/main/paste/$pasteId#line_$lineNumber"
  }

  @RequestMapping(value = Array("/saveReviewDraw"), method = Array(RequestMethod.POST))
  def saveReviewDraw(
                      @RequestParam(required = true) pasteId: Integer,
                      @RequestParam(required = true) reviewImgData: String,
                      @RequestParam(required = true) thumbImgData: String,
                      model: Model): String = {

    if (!isCurrentUserLoggedIn && !allowAnonymousCommentsCreate) return MvcConstants.page403

    var p = pasteManager.get(pasteId)

    if (p == null) return MvcConstants.page404

    if (reviewImgData == null || thumbImgData == null) return MvcConstants.page500

    logger.info("adding reviewImg to {}, data {}", Array(pasteId, reviewImgData))

    p.reviewImgData = resourcePathHelper.saveResource('r', p.uuid,reviewImgData)
    p.thumbImage =resourcePathHelper.saveResource('t', p.uuid,thumbImgData)

    p.touch()
    p = manager().save(p)

    model.asMap().clear()
    s"redirect:/main/paste/$pasteId"
  }

  /**
   * add new comment
   *
   * @return
   */
  @RequestMapping(value = Array("/saveComment"), method = Array(RequestMethod.POST))
  def saveComment(@Valid b: Comment,
                  result: BindingResult, model: Model, locale: Locale): String = {

    logger.debug("adding comment {}", b)

    if (!isCurrentUserLoggedIn && !allowAnonymousCommentsCreate) return MvcConstants.page403

    val p = manager().get(b.pasteId)

    if (p == null) return MvcConstants.page404

    if (result.hasErrors) {
      logger.debug("form has errors {}", result.getErrorCount)
      model.addAttribute("comment", b)
      fillEditModel(p, model, locale)
      return viewPage
    }

    if (isCurrentUserLoggedIn) {
      b.author = getCurrentUser.getUsername()
     // b.getOwner().increaseTotalComments()
    }
    commentManager.save(b)

    if (b.getThumbImage != null)
      p.thumbImage =resourcePathHelper.saveResource('t', p.uuid,b.getThumbImage)
    p.touch()
    manager().save(p)
    model.asMap().clear()
    s"redirect:/main/paste/${b.pasteId}#line_${b.lineNumber}"
  }


  @RequestMapping(value = Array("/save"), method = Array(RequestMethod.POST))
  override def save(@RequestParam(required = false) cancel: String,
                    @Valid @ModelAttribute(MvcConstants.MODEL_KEY) b: Paste,
                    result: BindingResult, model: Model, locale: Locale,
                    redirectAttributes: RedirectAttributes): String = {

    if (!isCurrentUserLoggedIn && !allowAnonymousPasteCreate) return MvcConstants.page403

    logger.debug("saving paste..")

    /**
     * copy fields not filled in form
     */
    if (!b.isBlank) {
      val current = manager().getFull(b.id)
      b.comments.addAll(current.comments)
      b.integrationCode = current.integrationCode
      // b.setThumbImg(current.getThumbImg())
    }

    if (b.channel == null || !channelDao.exist(b.channel))
          b.channel = channelDao.getDefault

    /**
     * concatenate all model tags objects to one string
     */
    b.getTagsMap.clear()

    val allTags = tagDao.getTagsMap

    for (s <- b.tagsAsString.split(" ")) {
      if (!StringUtils.isBlank(s)
        && s.length >= 3 // name min size
      ) if (allTags.containsKey(s))
          b.getTagsMap.put(s, allTags.get(s))
        else
          b.getTagsMap.put(s, new Tag(s))
    }

    /**
     * check if normalized was set
     */
    if (b.normalized) {

      b.codeType match {

        case "js" =>
          /**
           * try to normalize json
           */
          try {

          import com.fasterxml.jackson.databind.ObjectMapper
          val mapper = new ObjectMapper

          val n =mapper.readTree(b.text)

          b.text = mapper.writerWithDefaultPrettyPrinter.writeValueAsString(n)

          } catch{
            case _ :Exception =>
              //ignore
          }
        case "plain" =>
          /**
           * set word wrap for plain
           */
          b.text = WordUtils.wrap(b.text, 80)
        case _ =>
      }
    }

    if (b.isBlank)
      if (isCurrentUserLoggedIn) {
      b.author = getCurrentUser.getUsername()
     // b.getOwner().increaseTotalPastas()
    }


    b.title =
      if (b.text.length > Paste.TITLE_LENGTH) {

        val summary: String = b.text

        if (summary == null || summary.length < 3)
          b.text.substring(0, Paste.TITLE_LENGTH - 3) + "..."
        else if (summary.length > Paste.TITLE_LENGTH)
          summary.substring(0, Paste.TITLE_LENGTH - 3) + "..."
        else
          summary
      } else b.text

    logger.debug("__found thumbnail {} comments {}", b.thumbImage, b.commentsCount)

    if (b.thumbImage != null) {
      b.thumbImage =resourcePathHelper.saveResource('t', b.uuid,b.thumbImage)
    }

    val out = super.save(cancel, b, result, model, locale, redirectAttributes)
    if (out.equals(listPage)) {
      model.asMap().clear()
      s"redirect:/main/paste/${b.id}"
    } else
      out
  }

  @RequestMapping(value = Array("/raw/view"), method = Array(RequestMethod.GET))
  def getByPathRaw(@RequestParam(required = true) id: Integer, model: Model, locale: Locale): String = {
    val r = getByPath(id, model, locale)
    if (!r.equals(viewPage))
      r
    else
      "/paste/raw/view"
  }


  @RequestMapping(value = Array("/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  override def getByPath(@PathVariable("id") id: Integer, model: Model, locale: Locale): String = {
    val r = super.getByPath(id, model, locale)
    if (!r.equals(viewPage))
      return r

    val p = model.asMap().get(MvcConstants.MODEL_KEY).asInstanceOf[Paste]

    model.addAttribute("title", getResource("paste.view.title",
      Array(p.id, StringEscapeUtils.escapeHtml4(p.title)),
      locale))

    viewPage
  }


  /**
   *
   * @return available code types
   */
  @RequestMapping(value = Array("/codetypes"), method = Array(RequestMethod.GET))
  @ResponseBody
  @JsonIgnore
  def getAvailableCodeTypes: java.util.Collection[String] = codeTypeDao.getAvailableElements

  @RequestMapping(value = Array("/tags/all"), method = Array(RequestMethod.GET))
  @ResponseBody
  @JsonIgnore
  def getAvailableTags: util.List[Tag] = tagDao.getTags

  @RequestMapping(value = Array("/tags/names"), method = Array(RequestMethod.GET, RequestMethod.POST))
  @ResponseBody
  @JsonIgnore
  def getAvailableTagsNames: java.util.List[String] = {
    val out = new util.ArrayList[String]

    for (s <- tagDao.getAll.asScala) {
      out.add(s.name)
    }

    out
  }


  /**
   * return plain paste text
   *
   * @return
   */
  @RequestMapping(value = Array("/{id:[0-9]+}.txt"),
    method = Array(RequestMethod.GET),
    produces = Array("text/plain;charset=UTF-8"))
  @ResponseBody
  def getBodyPlain(@PathVariable("id") id: Integer): String = loadModel(id).text

  /**
   *
   * @return new search query object
   */
  @ModelAttribute("query")
  def newQuery(): AuthorQuery = new AuthorQuery()
}

class IdList(list: java.util.List[Integer]) {

  def getCount: Int = list.size()
  def getItems: util.List[Integer] = list
  def getItemsAsString: String = StringUtils.join(list, ",")

}
