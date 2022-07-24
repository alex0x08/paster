package com.Ox08.paster.setup
import com.Ox08.paster.common.SystemManagementService
import com.Ox08.paster.webapp.base.Boot.BOOT
import com.Ox08.paster.webapp.base.{Boot, Logged}
import com.Ox08.paster.webapp.mvc.MvcConstants
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.MessageSource
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._
import java.util.Locale
/**
 * This is 'all-in-one' configuration controller, used for setup stage
 */
@Controller
class SetupCtrl extends Logged {
  @Autowired
  private val sysService: SystemManagementService = null
  @Autowired
  protected val messageSource: MessageSource = null
  protected val systemInfo: BOOT.SystemInfo = Boot.BOOT.getSystemInfo
  @Value("${paste.app.id}")
  val appId: String = null
  @ModelAttribute("appId")
  def getAppId: String = appId
  @ModelAttribute("systemInfo")
  def getSystemInfo: BOOT.SystemInfo = systemInfo
  @ModelAttribute("availableLocales")
  def getAvailableLocales: Array[Locale] = Array(
    Locale.US,
    Locale.forLanguageTag("ru_RU")
  )
  protected def getResource(key: String, locale: Locale): String =
    messageSource.getMessage(key, new Array[java.lang.Object](0), locale)
  protected def getResource(key: String, args: Array[Any], locale: Locale): String =
    messageSource.getMessage(key, args.asInstanceOf[Array[java.lang.Object]], locale)
  @ExceptionHandler(Array(classOf[Throwable]))
  protected def handleAllExceptions(ex: Throwable): String = {
    logger.error(ex.getMessage, ex)
    MvcConstants.page500
  }
  @RequestMapping(value = Array("/"))
  def index(model: Model): String = {
    model.asMap().clear()
    "redirect:/main/setup/welcome"
  }
  /**
   * Handles error pages
   *
   * @param response
   * @param errorCode
   * @return
   */
  @RequestMapping(Array("/error/{errorCode:[0-9_]+}"))
  def error(response: HttpServletResponse, model: Model,
            @PathVariable("errorCode") errorCode: Int): String = errorCode match {
    case 403 | 404 | 500 =>
      model.asMap().clear()
      response.setStatus(errorCode)
      "redirect:/main/setup/welcome"
    case _ =>
      "/error/500"
  }
  @RequestMapping(Array("/setup/welcome"))
  def setupWelcome(model: Model): String = "/setup/welcome"


  @RequestMapping(Array("/setup/db"))
  def setupDatabase(model: Model): String = {



    "/setup/db"
  }

  @RequestMapping(value = Array("/setup/finalizeInstall"), method = Array(RequestMethod.POST))
  def finalizeInstall(
                       model: Model, locale: Locale): String = {
    model.asMap().clear()
    Boot.BOOT.markInstalled()
    sysService.restartApplication()
    "redirect:/main/restarting"
  }
  @RequestMapping(Array("/restarting"))
  def restarting(model: Model): String = "/restarting"
}
