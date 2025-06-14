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
package com.Ox08.paster.webapp.mvc.paste
import com.Ox08.paster.webapp.dao._
import com.Ox08.paster.webapp.manager.ResourceManager
import com.Ox08.paster.webapp.model._
import com.Ox08.paster.webapp.mvc.{GenericEditCtrl, MvcConstants}
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.Valid
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.{StringEscapeUtils, WordUtils}
import org.springframework.beans.factory.annotation.Autowired
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
 * Support for all operations related to one paste model:
 *    edit,view,delete,add comments and so on.
 * @since 1.0
 * @author 0x08
 */
@Controller
@RequestMapping(Array("/paste"))
class PasteEditCtrl extends GenericEditCtrl[Paste] {
  @Autowired
  private val codeTypeDao: CodeTypeDao = null
  @Autowired
  val channelDao: ChannelDao = null
  @Autowired
  private val priorities: PriorityDao = null
  @Autowired
  private val tagDao: TagDao = null
  @Autowired
  val pasteDao: PasteDao = null
  @Autowired
  val commentDao: CommentDao = null
  @Autowired
  private val resourceDao: ResourceManager = null
  def listPage = "redirect:/main/paste/list"
  def editPage = "paste/edit"
  def viewPage = "paste/view"
  def manager(): PasteDao = pasteDao
  @InitBinder
  def initBinder(binder: WebDataBinder): Unit = {
    // this is required on Scala
    binder.initDirectFieldAccess()
  }

  /**
   * Prepare page model
   * @param obj
   *      model instance
   * @param model
   *     page model (Spring MVC)
   * @param locale
   *    request context locale
   */
  override def fillEditModel(obj: Paste, model: Model, locale: Locale): Unit = {
    super.fillEditModel(obj, model, locale)
    // set page title
    if (obj.isBlank)
      model.addAttribute("title", getResource("paste.new", locale))
    else {
      model.addAttribute("title", StringEscapeUtils.escapeHtml4(
        getResource("paste.edit.title", Array(obj.id, obj.title), locale)))
      // fetch comments if paste entity is loaded from database (persisted)
      obj.comments.addAll(
        commentDao.getCommentsForPaste(obj.id))
      // add new comment DTO
      if (!model.containsAttribute("comment"))
        model.addAttribute("comment", getNewCommentInstance(obj))
    }
    // add list of available code types, used for syntax highlight
    model.addAttribute("availableCodeTypes", codeTypeDao.getAvailableElements.asJava)
    // add list of priorities
    model.addAttribute("availablePriorities", priorities.getAvailableElements.asJava)
    // add channels
    model.addAttribute("availableChannels", channelDao.getAvailableElements.asJava)
    // add list of lines that has been commented
    if (!obj.comments.isEmpty) {
      val commentLines = new util.ArrayList[Long]()
      for (c <- obj.comments.asScala)
        commentLines.add(c.lineNumber)
      model.addAttribute("commentedLinesList", StringUtils.join(commentLines, ","))
    }
    // add links to previous and next pastas (if exist)
    if (!obj.isBlank) {
      model.addAttribute("availableNext", manager().getNextPaste(obj))
      model.addAttribute("availablePrev", manager().getPreviousPaste(obj))
      model.addAttribute("availablePrevList",
        new IdList(manager().getPreviousPastasIdList(obj)))
    } else {
      model.addAttribute("availableNext", null)
      model.addAttribute("availablePrev", null)
    }
  }

  /**
   * Build template for new Paste
   *  @return
   *      new Paste template
   */
  override def getNewModelInstance: Paste = {
    val p = new Paste()
    // put username of current user as paste's author, if logged in
    if (isCurrentUserLoggedIn)
      p.author = getCurrentUser.getUsername()
    // set current channel
    p.channel = channelDao.getDefault
    // set default code type
    p.codeType = codeTypeDao.getDefault
    // set default priority
    p.priority = priorities.getDefault
    // return
    p
  }

  /**
   * Removes selected comment
   * @param commentId
   *        comment id
   * @param pasteId
   *        paste id
   * @param lineNumber
   *        commented line
   * @param model
   *        page model
   * @return
   */
  @RequestMapping(value = Array("/removeComment"),
    method = Array(RequestMethod.POST, RequestMethod.GET))
  def removeComment(@RequestParam(required = true) commentId: Integer,
                    @RequestParam(required = true) pasteId: Integer,
                    @RequestParam(required = true) lineNumber: Long,
                    model: Model): String = {
    if (logger.isDebugEnabled)
      logger.debug("removing comment commentId={} ,pasteId={},  lineNumber ={} ",
        commentId, pasteId, lineNumber)
    // check that comment with provided id exists
    if (!commentDao.exists(commentId)) {
      logger.warn("comment with id {} not found", commentId)
      model.asMap().clear()
      return s"redirect:/main/paste/$pasteId#line_$lineNumber"
    }
    // remove all responses to that comment first
    commentDao.deleteCommentsFor(pasteId,commentId)
    // then remove comment
    commentDao.remove(commentId)

    model.asMap().clear()
    s"redirect:/main/paste/$pasteId#${pasteId}_line_$lineNumber"
  }

  /**
   * Save image review
   * @param pasteId
   *          paste id
   * @param reviewImgData
   *          review image, base64 encoded
   * @param thumbImgData
   *          preview image, base64 encoded
   * @param model
   *          page model
   * @return
   */
  @RequestMapping(value = Array("/saveReviewDraw"), method = Array(RequestMethod.POST))
  def saveReviewDraw(
                      @RequestParam(required = true) pasteId: Integer,
                      @RequestParam(required = true) reviewImgData: String,
                      @RequestParam(required = true) thumbImgData: String,
                      model: Model): String = {
    // if there is no active user session and 'anonymous comments' feature was not enabled -
    // respond 403 and exit
    if (!isCurrentUserLoggedIn && !allowAnonymousCommentsCreate)
      return MvcConstants.page403

    var p = pasteDao.get(pasteId)
    // null means record with this id was not found in database
    if (p == null)
      return MvcConstants.page404
    // check for broken request
    if (reviewImgData == null || thumbImgData == null
                              || reviewImgData.isBlank || thumbImgData.isBlank)
      return MvcConstants.page500

    if (logger.isDebugEnabled)
      logger.debug("adding reviewImg to {}, data sz {}",
        Array(pasteId, reviewImgData.length))
    // save review image as file and link to paste entity
    p.reviewImgData = resourceDao.saveResource('r', p.uuid, reviewImgData)
    // save preview image as file and link...
    p.thumbImage = resourceDao.saveResource('t', p.uuid, thumbImgData)
    // trigger modification datetime update
    p.touch()
    // persist record
    p = manager().save(p)
    model.asMap().clear()
    s"redirect:/main/paste/$pasteId"
  }

  /**
   * Saves new comment for paste
   * @param b
   *        comment object
   * @param result
   *        binding result
   * @param model
   *        page model
   * @param locale
   *        request locale
   * @return
   */
  @RequestMapping(value = Array("/saveComment"), method = Array(RequestMethod.POST))
  def saveComment(@Valid b: Comment,
                  result: BindingResult, model: Model, locale: Locale): String = {
    if (logger.isDebugEnabled)
      logger.debug("adding comment, text sz: {}", if (b.text !=null) b.text.length else -1)
    // check for 'anonymous comments' feature
    if (!isCurrentUserLoggedIn && !allowAnonymousCommentsCreate)
      return MvcConstants.page403
    // retrieve paste record from database
    val p = manager().get(b.pasteId)
    if (p == null)
      return MvcConstants.page404
    if (p.thumbImage!=null) {
      val fid = resourceDao.getResource('t',p.thumbImage)
      if (fid.exists() && fid.isFile && !fid.delete()) {
        if (logger.isDebugEnabled)
          logger.debug("cannot delete previous file: {}", fid)
        return MvcConstants.page500
      }
    }
    if (result.hasErrors) {
      // dump errors, if debug enabled
      if (logger.isDebugEnabled) {
        logger.debug("form has {} errors", result.getErrorCount)
        for (e <- result.getAllErrors.asScala)
          logger.debug("error: {} code: {} msg: {}",
            e.getObjectName, e.getCode, e.getDefaultMessage)
      }
      // add same entity to page model again
      model.addAttribute("comment", b)
      fillEditModel(p, model, locale)
      // and go back
      return viewPage
    }
    if (isCurrentUserLoggedIn)
      b.author = getCurrentUser.getUsername()
    // persist comment
    commentDao.save(b)
    // update thumb image
    if (b.getThumbImage != null)
      p.thumbImage = resourceDao.saveResource('t', p.uuid, b.getThumbImage)
    p.touch()
    manager().save(p)
    model.asMap().clear()
    s"redirect:/main/paste/${b.pasteId}#${b.pasteId}_line_${b.lineNumber}"
  }

  /**
   * Saves paste
   * @param cancel
   *        cancel button
   * @param b
   *        updated paste
   * @param result
   *        binding result
   * @param model
   *        page model
   * @param locale
   *        request locale
   * @param redirectAttributes
   *          addtional attributes, used on page redirect
   * @return
   */
  @RequestMapping(value = Array("/save"), method = Array(RequestMethod.POST))
  override def save(@RequestParam(required = false) cancel: String,
                    @Valid @ModelAttribute(MvcConstants.MODEL_KEY) b: Paste,
                    result: BindingResult, model: Model, locale: Locale,
                    redirectAttributes: RedirectAttributes): String = {
    // check for 'allow anonymous to add pastas'
    if (!isCurrentUserLoggedIn && !allowAnonymousPasteCreate)
      return MvcConstants.page403

    /**
     * copy fields not filled in form
     */
    if (!b.isBlank) {
      val p = manager().get(b.id)
      if (p == null)
        return MvcConstants.page404
      if (p.thumbImage!=null) {
        val fid = resourceDao.getResource('t',p.thumbImage)
        if (fid.exists() && fid.isFile && !fid.delete()) {
          if (logger.isDebugEnabled)
            logger.debug("cannot delete previous preview file: {}", fid)
          return MvcConstants.page500
        }
      }
      // this is required when
      commentDao.deleteCommentsFor(b.id,null)
    }
    // check for selected channel
    if (b.channel == null || !channelDao.exist(b.channel))
      b.channel = channelDao.getDefault

    /**
     * concat all tags to one string
     */
    b.getTagsMap.clear()
    val allTags = tagDao.getTagsMap
    for (s <- b.tagsAsString.split(" "))
      if (!StringUtils.isBlank(s) && s.length >= 3 )
        if (allTags contains s)
          b.getTagsMap.put(s, allTags(s))
        else
          b.getTagsMap.put(s, new Tag(s))

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
            val n = mapper.readTree(b.text)
            b.text = mapper.writerWithDefaultPrettyPrinter.writeValueAsString(n)
          } catch {
            case _: Exception =>
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
    // another block, specific for new paste
    if (b.isBlank) {
      // set author
      if (isCurrentUserLoggedIn)
        b.author = getCurrentUser.getUsername()
      // build title
      b.title =
        if (b.text.length > Paste.TITLE_LENGTH) {
          val summary: String = b.text
          if (summary == null || summary.length < 3)
            b.text.substring(0, Paste.TITLE_LENGTH - 3) + "..."
          else if (summary.length > Paste.TITLE_LENGTH)
            summary.substring(0, Paste.TITLE_LENGTH - 3) + "..."
          else
            summary
        } else
          b.text
    }
    if (logger.isDebugEnabled)
      logger.debug("found thumbnail sz: {} comments {}",
        b.thumbImage.length, b.commentsCount)

    // update thumb image
    if (b.thumbImage != null)
      b.thumbImage = resourceDao.saveResource('t', b.uuid, b.thumbImage)
    // persist record
    val out = super.save(cancel, b, result, model, locale, redirectAttributes)
    // if there were no validation errors - redirect to list
    if (out.equals(listPage)) {
      model.asMap().clear()
      s"redirect:/main/paste/${b.id}"
    // otherwise stay on page
    } else
      out
  }
  @RequestMapping(value = Array("/raw/view"), method = Array(RequestMethod.GET))
  def getByPathRaw(@RequestParam(required = true) id: Integer,
                   model: Model, locale: Locale): String = {
    val r = getByPath(id, model, locale)
    if (!r.equals(viewPage))
      r
    else
      "/paste/raw/view"
  }
  @RequestMapping(value = Array("/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  override def getByPath(@PathVariable("id") id: Integer,
                         model: Model, locale: Locale): String = {
    val r = super.getByPath(id, model, locale)
    if (!r.equals(viewPage))
      return r
    val p = model.asMap().get(MvcConstants.MODEL_KEY).asInstanceOf[Paste]
    // set page title
    model.addAttribute("title", getResource("paste.view.title",
      Array(p.id, StringEscapeUtils.escapeHtml4(p.title)),
      locale))
    viewPage
  }
  /**
   * Respond available code types, used for syntax highlight
   * @return available code types
   */
  @RequestMapping(value = Array("/codetypes"),
    method = Array(RequestMethod.GET))
  @ResponseBody
  @JsonIgnore
  def getAvailableCodeTypes: util.Set[String] = codeTypeDao.getAvailableElements.asJava

  /**
   * Respond all tags as entities, used in dropdown
   * @return
   */
  @RequestMapping(value = Array("/tags/all"), method = Array(RequestMethod.GET))
  @ResponseBody
  @JsonIgnore
  def getAvailableTags: util.List[Tag] = tagDao.getTags.asJava

  /**
   * Respond all tags as strings, used for typing
   * @return
   */
  @RequestMapping(value = Array("/tags/names"), method = Array(RequestMethod.GET, RequestMethod.POST))
  @ResponseBody
  @JsonIgnore
  def getAvailableTagsNames: java.util.List[String] = {
    val out = new util.ArrayList[String]
    for (s <- tagDao.getAll.asScala)
      out.add(s.name)
    out
  }
  /**
   * return paste content as plain text
   *
   * @return
   */
  @RequestMapping(value = Array("/{id:[0-9]+}.txt"),
    method = Array(RequestMethod.GET),
    produces = Array("text/plain;charset=UTF-8"))
  @ResponseBody
  def getBodyPlain(@PathVariable("id") id: Integer): String = loadModel(id).text
  /**
   * Build new query DTO, used for search
   * @return new search query object
   */
  @ModelAttribute("query")
  def newQuery(): AuthorQuery = new AuthorQuery()

  /**
   * Build new Comment Dto
   * @param pp
   *        source Paste
   * @return
   */
  private def getNewCommentInstance(pp: Paste): Comment = {
    val p = new Comment()
    // set author to current user
    if (getCurrentUser != null)
      p.author = getCurrentUser.getUsername()
    // set related paste's id
    p.pasteId = pp.id
    p
  }
}

/**
 * Holds list of records ids, with additional functions.
 * Used from JSP EL
 * @param list
 */
class IdList(list: java.util.List[Integer]) {
  def getCount: Int = list.size()
  def getItems: util.List[Integer] = list
  def getItemsAsString: String = StringUtils.join(list, ",")
}
