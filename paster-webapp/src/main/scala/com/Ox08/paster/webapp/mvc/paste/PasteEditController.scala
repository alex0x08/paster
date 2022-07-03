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

import com.Ox08.paster.webapp.dao.{ChannelDao, CommentDaoImpl, PasteDaoImpl, PriorityDao, TagDao}
import com.Ox08.paster.webapp.manager.ResourcePathHelper
import com.Ox08.paster.webapp.model.{CodeType, CodeTypeEditor, Comment, KeyEditor, OwnerQuery, Paste, Tag}
import com.Ox08.paster.webapp.mvc.{GenericController, GenericEditController}
import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation._
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.apache.commons.text.StringEscapeUtils
import org.apache.commons.text.WordUtils

import java.util.{ArrayList, Locale}
import javax.validation.Valid
import scala.jdk.CollectionConverters._

/**
 * Paste model controller
 * Support for all operations related to one paste model: edit,view,delete,add comments..
 */
@Controller
@RequestMapping(Array("/paste"))
class PasteEditController extends GenericEditController[Paste] {

  @Autowired
  val channelDao: ChannelDao = null

  @Autowired
  val priorities: PriorityDao = null

  @Autowired
  val tagDao: TagDao = null

  @Autowired
  val pasteManager: PasteDaoImpl = null

  @Autowired
  val commentManager: CommentDaoImpl = null

  @Autowired
  val resourcePathHelper: ResourcePathHelper = null

  @Autowired
  @Qualifier("mimeTypeSource")
  protected val mimeSource: MessageSource = null

  @Autowired
  @Qualifier("mimeExtSource")
  protected val mimeExtSource: MessageSource = null

  def listPage = "redirect:/main/paste/list"
  def editPage = "paste/edit"
  def viewPage = "paste/view"

  def manager() = pasteManager

  @InitBinder
  def initBinder(binder: WebDataBinder): Unit = {
    binder.initDirectFieldAccess()
    binder.registerCustomEditor(classOf[CodeType], new CodeTypeEditor())
  }

  override def fillEditModel(obj: Paste, model: Model, locale: Locale): Unit = {
    super.fillEditModel(obj, model, locale)

    if (obj.isBlank()) {
      model.addAttribute("title", getResource("paste.new", locale))
    } else {
      model.addAttribute("title", StringEscapeUtils.escapeHtml4(
        getResource("paste.edit.title", Array(obj.getId, obj.getName()), locale)))

      obj.getComments.addAll(
        commentManager.getCommentsForPaste(obj.getId()))

      if (!model.containsAttribute("comment")) {
        model.addAttribute("comment", getNewCommentInstance(obj, model))
      }

    }
    model.addAttribute("availableCodeTypes", CodeType.list)
    model.addAttribute("availablePriorities", priorities.getAvailablePriorities())
    model.addAttribute("availableChannels", channelDao.getAvailableChannels())

    if (!obj.getComments().isEmpty()) {
      val commentLines = new ArrayList[Long]
      for (c <- obj.getComments.asScala) {
        commentLines.add(c.getLineNumber())
      }

      model.addAttribute("commentedLinesList", StringUtils.join(commentLines, ","))
    }

    if (!obj.isBlank()) {
      val pnext = manager.getNextPaste(obj)
      val pprev = manager.getPreviousPaste(obj)

      model.addAttribute("availableNext", pnext)
      model.addAttribute("availablePrev", pprev)
      model.addAttribute("availablePrevList", new IdList(manager.getPreviousPastasIdList(obj)))

    } else {
      model.addAttribute("availableNext", null)
      model.addAttribute("availablePrev", null)
    }
  }

  override def getNewModelInstance(): Paste = {
    val p = new Paste()
    p.setOwner(getCurrentUser().getUsername())
    p.setChannel(channelDao.getDefault())
    p
  }

  def getNewCommentInstance(pp: Paste, model: Model): Comment = {
    val p = new Comment()
    p.setOwner(getCurrentUser().getUsername())
    p.setPasteId(pp.getId())
    p
  }


  @RequestMapping(value = Array("/removeComment"),
    method = Array(RequestMethod.POST, RequestMethod.GET))
  def removeComment(@RequestParam(required = true) commentId: java.lang.Long,
                    @RequestParam(required = true) pasteId: java.lang.Long,
                    @RequestParam(required = true) lineNumber: java.lang.Long,
                    model: Model, locale: Locale): String = {

    if (logger.isDebugEnabled) logger.debug("_removeComment commentId={} , lineNumber ={} {}", commentId, lineNumber, "")

    commentManager.remove(commentId)
    model.asMap().clear()
    "redirect:/main/paste/" + pasteId + "#line_" + lineNumber
  }

  @RequestMapping(value = Array("/saveReviewDraw"), method = Array(RequestMethod.POST))
  def saveReviewDraw(
                      @RequestParam(required = true) pasteId: java.lang.Long,
                      @RequestParam(required = true) reviewImgData: String,
                      @RequestParam(required = true) thumbImgData: String,
                      model: Model, locale: Locale): String = {

    if (!isCurrentUserLoggedIn() && !allowAnonymousCommentsCreate) return page403

    var p = pasteManager.get(pasteId)

    if (p == null) return page404

    if (reviewImgData == null || thumbImgData == null) return page500

    logger.info("adding reviewImg to {}, data {}", Array(pasteId, reviewImgData))

    p.setReviewImgData(resourcePathHelper.saveResource("r", p.getUuid(),reviewImgData))
    p.setThumbImage(resourcePathHelper.saveResource("t", p.getUuid(),thumbImgData))

    p.touch
    p = manager.save(p);

    model.asMap().clear()
    "redirect:/main/paste/" + pasteId
  }

  /**
   * add new comment
   *
   * @param pasteId
   * @param b
   * @param result
   * @param model
   * @param locale
   * @return
   */
  @RequestMapping(value = Array("/saveComment"), method = Array(RequestMethod.POST))
  def saveComment(@Valid b: Comment,
                  result: BindingResult, model: Model, locale: Locale): String = {

    if (!isCurrentUserLoggedIn() && !allowAnonymousCommentsCreate) return page403

    val p = manager.get(b.getPasteId)

    if (p == null) return page404

    logger.debug("adding comment {}", b)

    if (result.hasErrors()) {
      logger.debug("form has errors {}", result.getErrorCount())
      model.addAttribute("comment", b)
      fillEditModel(p, model, locale)
      return viewPage
    }

    if (isCurrentUserLoggedIn()) {
      b.setOwner(getCurrentUser().getUsername())
     // b.getOwner().increaseTotalComments()
    }
    commentManager.save(b)

    if (b.getThumbImage() != null)
      p.setThumbImage(resourcePathHelper.saveResource("t", p.getUuid(),b.getThumbImage()))
    p.touch()
    manager.save(p)
    model.asMap().clear()
    "redirect:/main/paste/" + b.getPasteId() + "#line_" + b.getLineNumber()
  }


  @RequestMapping(value = Array("/save"), method = Array(RequestMethod.POST))
  override def save(@RequestParam(required = false) cancel: String,
                    @Valid @ModelAttribute(GenericController.MODEL_KEY) b: Paste,
                    result: BindingResult, model: Model, locale: Locale,
                    redirectAttributes: RedirectAttributes): String = {

    if (!isCurrentUserLoggedIn() && !allowAnonymousPasteCreate) return page403

    logger.debug("saving paste..")

    /**
     * copy fields not filled in form
     */
    if (!b.isBlank()) {
      val current = manager.getFull(b.getId())
      b.getComments().addAll(current.getComments())
      b.setIntegrationCode(current.getIntegrationCode())
      // b.setThumbImg(current.getThumbImg())
    }

    if (b.getChannel() == null || !channelDao.exist(b.getChannel()))
          b.setChannel(channelDao.getDefault())



    /**
     * concatenate all model tags objects to one string
     */
    b.getTagsMap().clear()

    val allTags = tagDao.getTagsMap()

    for (s <- b.getTagsAsString().split(" ")) {
      if (!StringUtils.isBlank(s)
        && s.length >= 3 // name min size
      ) if (allTags.containsKey(s)) b.getTagsMap().put(s, allTags.get(s)) else b.getTagsMap().put(s, new Tag(s))
    }

    /**
     * check if normalized was set
     */
    if (b.isNormalized()) {

      b.getCodeType() match {

        case CodeType.JavaScript => {
          /**
           * try to normalize json
           */
          /*try {

            val o = new JsonParser().parse(b.getText()).getAsJsonObject()
            b.setText(new GsonBuilder().setPrettyPrinting().create().toJson(o))

          } catch{
            case e @ (_ : MalformedJsonException | _ :Exception) => {
                    //ignore
              }
            }*/
        }
        case CodeType.Plain =>
          /**
           * set word wrap for plain
           */

          b.setText(WordUtils.wrap(b.getText(), 80))
        case _ =>
      }
    }

    if (b.isBlank()) if (isCurrentUserLoggedIn()) {
      b.setOwner(getCurrentUser().getUsername())
     // b.getOwner().increaseTotalPastas()
    }


    b.setTitle(
      f = if (b.getText().length > Paste.TITLE_LENGTH) {

        val summary: String = b.getText()

        if (summary == null || summary.length < 3)
          b.getText().substring(0, Paste.TITLE_LENGTH - 3) + "..."
        else if (summary.length > Paste.TITLE_LENGTH)
          summary.substring(0, Paste.TITLE_LENGTH - 3) + "..."
        else
          summary
      } else b.getText())

    logger.debug("__found thumbnail {} comments {}", b.getThumbImage(), b.getComments().size())

    if (b.getThumbImage() != null) {
      b.setThumbImage(resourcePathHelper.saveResource("t", b.getUuid(),b.getThumbImage()))
    }

    val out = super.save(cancel, b, result, model, locale, redirectAttributes)
    if (out.equals(listPage)) {
      model.asMap().clear();
      "redirect:/main/paste/" + b.getId()
    } else
      out
  }

  def getMimeResource(key: String): String = mimeSource.getMessage(key,
    new Array[java.lang.Object](0), null, Locale.getDefault)

  def getMimeExtResource(key: String): String = mimeExtSource.getMessage(key,
    new Array[java.lang.Object](0), null, Locale.getDefault)

  @RequestMapping(value = Array("/raw/view"), method = Array(RequestMethod.GET))
  def getByPathRaw(@RequestParam(required = true) id: java.lang.Long, model: Model, locale: Locale): String = {
    val r = getByPath(id, model, locale)
    if (!r.equals(viewPage))
      r
    else
      "/paste/raw/view"
  }


  @RequestMapping(value = Array("/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  override def getByPath(@PathVariable("id") id: java.lang.Long, model: Model, locale: Locale): String = {
    val r = super.getByPath(id, model, locale)
    if (!r.equals(viewPage))
      return r

    val p = model.asMap().get(GenericController.MODEL_KEY).asInstanceOf[Paste]

    model.addAttribute("title", getResource("paste.view.title",
      Array(p.getId(), StringEscapeUtils.escapeHtml4(p.getName())),
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
  def getAvailableCodeTypes(): java.util.Collection[CodeType] = CodeType.list

  @RequestMapping(value = Array("/tags/all"), method = Array(RequestMethod.GET))
  @ResponseBody
  @JsonIgnore
  def getAvailableTags() = tagDao.getTags()

  @RequestMapping(value = Array("/tags/names"), method = Array(RequestMethod.GET, RequestMethod.POST))
  @ResponseBody
  @JsonIgnore
  def getAvailableTagsNames(): java.util.List[String] = {
    val out = new ArrayList[String]

    for (s <- tagDao.getAll().asScala) {
      out.add(s.getName())
    }

    out
  }


  /**
   * return plain paste text
   *
   * @param id
   * @param model
   * @param locale
   * @return
   */
  @RequestMapping(value = Array("/{id:[0-9]+}.txt"),
    method = Array(RequestMethod.GET), produces = Array("text/plain;charset=UTF-8"))
  @ResponseBody
  def getBodyPlain(@PathVariable("id") id: java.lang.Long,
                   model: Model,
                   locale: Locale): String = return loadModel(id).getText()

  /**
   *
   * @return new search query object
   */
  @ModelAttribute("query")
  def newQuery(): OwnerQuery = new OwnerQuery
}

class IdList(list: java.util.List[Long]) {

  def getCount = list.size
  def getItems = list
  def getItemsAsString = StringUtils.join(list, ",")

}
