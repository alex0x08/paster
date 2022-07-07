package com.Ox08.paster.webapp.mvc.paste

import com.Ox08.paster.webapp.base.Logged
import com.Ox08.paster.webapp.dao.{CommentDao, PasteDao}
import com.Ox08.paster.webapp.manager.{ResourceManager, UserManager}
import com.Ox08.paster.webapp.manager.UserManager.getCurrentUser
import com.Ox08.paster.webapp.model.{Comment, Paste, Struct}
import com.Ox08.paster.webapp.mvc.MvcConstants
import com.fasterxml.jackson.annotation.JsonIgnore
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import jakarta.persistence.{Column, Lob, Transient}
import jakarta.validation.Valid
import jakarta.validation.constraints.{NotNull, Size}
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.{ModelAttribute, RequestMapping, RequestMethod, RequestParam, RestController}

import java.util.Locale

@RestController
@RequestMapping(Array("/paste-api"))
class PasteCommentsCtrl extends Logged{

  @Value("${config.comments.allow-anonymous.create}")
  private val allowAnonymousCommentsCreate: Boolean = false

  @Autowired
  private val commentDao: CommentDao = null

  @Autowired
  private val pasteDao: PasteDao = null

  @Autowired
  private val resourcePathHelper: ResourceManager = null

  def isCurrentUserLoggedIn: Boolean = UserManager.getCurrentUser != null


  /**
   * add new comment
   *
   * @return
  @RequestMapping(value = Array("/saveComment"), method = Array(RequestMethod.POST))
  def saveComment(@Valid b: NewCommentDTO): String = {

    logger.debug("adding comment {}", b)

    if (!isCurrentUserLoggedIn && !allowAnonymousCommentsCreate) return MvcConstants.page403

    val p:Paste = pasteDao.get(b.pasteId)

    if (p == null) return MvcConstants.page404

    if (result.hasErrors) {
      logger.debug("form has errors {}", result.getErrorCount)
      model.addAttribute("comment", b)
      //fillEditModel(p, model, locale)
      return ""
    }

    if (isCurrentUserLoggedIn) {
      b.author = getCurrentUser.getUsername()
      // b.getOwner().increaseTotalComments()
    }
    commentDao.save(b)

    if (b.getThumbImage != null)
      p.thumbImage =resourcePathHelper.saveResource("t", p.uuid,b.getThumbImage)
    p.touch()
    pasteDao.save(p)
    model.asMap().clear()
    s"redirect:/main/paste/${b.pasteId}#line_${b.lineNumber}"
  }
   */


  @RequestMapping(value = Array("/removeComment"),
    method = Array(RequestMethod.POST, RequestMethod.GET))
  def removeComment(@RequestParam(required = true) commentId: Integer,
                    @RequestParam(required = true) pasteId: Integer,
                    @RequestParam(required = true) lineNumber: Long,
                    model: Model): String = {

    if (logger.isDebugEnabled)
      logger.debug("_removeComment commentId={} , lineNumber ={} {}", commentId, lineNumber, "")

    commentDao.remove(commentId)
    model.asMap().clear()
    s"redirect:/main/paste/$pasteId#line_$lineNumber"
  }


  class NewCommentDTO  {

    var pasteId: Long = _

    var text: String = _

    var author: String = _

    var lineNumber: Int = _

    var parentId: Integer = _

    var thumbImage: String = _

  }


}
