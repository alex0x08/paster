package com.Ox08.paster.setup
import com.Ox08.paster.common.SystemManagementService
import com.Ox08.paster.webapp.base.Boot.BOOT
import com.Ox08.paster.webapp.base.{Boot, Logged, SystemError}
import com.Ox08.paster.webapp.mvc.MvcConstants
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.MessageSource
import org.springframework.stereotype.{Controller, Service}
import org.springframework.ui.Model
import org.springframework.util.Assert
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation._
import java.util.Locale
import scala.collection.mutable
import scala.jdk.CollectionConverters._
/**
 * This is 'all-in-one' configuration controller, used for setup stage
 */
@Controller
class SetupCtrl extends Logged {
  @Autowired
  private val setupService: PasterSetupService = null
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
  @ModelAttribute("availableSteps")
  def getAvailableSteps: java.util.Map[String, String] = setupService.getStepNames.asJava
  @ModelAttribute("availableLocales")
  def getAvailableLocales: Array[Locale] = Array(
    Locale.US,
    Locale.forLanguageTag("ru-RU")
  )
  protected def getResource(key: String, locale: Locale): String =
    messageSource.getMessage(key, new Array[java.lang.Object](0), locale)
  protected def getResource(key: String, args: Array[Any], locale: Locale): String =
    messageSource.getMessage(key, args.asInstanceOf[Array[java.lang.Object]], locale)
  @ExceptionHandler(Array(classOf[Throwable]))
  def handleAllExceptions(ex: Throwable): String = {
    logger.error(ex.getMessage, ex)
    MvcConstants.page500
  }
  @InitBinder
  def initBinder(webDataBinder: WebDataBinder, servletRequest: HttpServletRequest): Unit = {
    if (!"POST".equalsIgnoreCase(servletRequest.getMethod)
      && !servletRequest.getRequestURI.contains("/main/setup/"))
      return
    val nonCastedTarget = webDataBinder.getTarget
    if (nonCastedTarget == null || !nonCastedTarget.isInstanceOf[StepModel])
      return
    logger.debug(s"init binder: '${servletRequest.getRequestURI}' , method:  ${servletRequest.getMethod} , target: $nonCastedTarget ")
    val target = nonCastedTarget.asInstanceOf[StepModel]
    val url = servletRequest.getRequestURI.substring(servletRequest.getContextPath.length).toLowerCase
    val step = url.substring("/main/setup/".length)
    logger.debug("step: '{}'", step)
    if (!setupService.containsStep(step))
      throw SystemError.withCode(0x6001, s"Incorrect step type:$step")
    target.setStep(setupService.getStep(step))
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
  @RequestMapping(value = Array("/setup/{step}"), method = Array(RequestMethod.GET))
  def setupStep(model: Model, @PathVariable("step") step: String): String = {
    if (!setupService.containsStep(step)) {
      if (logger.isDebugEnabled)
        logger.debug("Cannot find step: {}", step)
      return MvcConstants.page500
    }
    model.addAttribute("step", setupService.getStep(step))
    model.addAttribute("updatedStep", new StepModel)

    if ("db".equalsIgnoreCase(step)) {

      val availableDrivers = Array(new DbType("H2 Embedded",
        "jdbc:h2:file:${paster.app.home}/db/pastedb;DB_CLOSE_ON_EXIT=TRUE;LOCK_TIMEOUT=10000",
        "org.h2.jdbcx.JdbcDataSource",current = true),
        new DbType("PostgreSQL",
        "jdbc:pgsql://127.0.0.1/test-db",
        "com.impossibl.postgres.jdbc.PGDataSource",current = false),
        new DbType("MySQL",
          "jdbc:mysql://localhost/testdb",
          "com.mysql.cj.jdbc.Driver",current = false))

      model.addAttribute("availableDrivers",availableDrivers)
    }

    s"/setup/$step"
  }
  @RequestMapping(value = Array("/setup/{stepName}"), method = Array(RequestMethod.POST))
  def updateStep(@Valid
                 @ModelAttribute("updatedStep")
                 updatedStep: StepModel,
                 result: BindingResult,
                 model: Model,
                 @PathVariable("stepName") step: String): String = {
    if (result.hasErrors) {
      // dump errors
      if (logger.isDebugEnabled) {
        logger.debug("form has {} errors", result.getErrorCount)
        for (e <- result.getAllErrors.asScala) {
          logger.debug("error: {} code: {} msg: {}",
            e.getObjectName, e.getCode, e.getDefaultMessage)
        }
      }
      model.addAttribute("step", setupService.getStep(step))
      return s"/setup/$step"
    }
    logger.debug("step: {}", updatedStep.getStep.getClass.getName)
    setupService.updateStep(updatedStep.getStep)
    val nextStep: SetupStep = setupService.getNextStep(updatedStep.getStep.getStepKey)
    // all steps done
    if (nextStep == null) {
      // but some are not completed yet
      val firstUncompleted: SetupStep = setupService.getFirstUncompleted
      if (firstUncompleted != null)
        s"/setup/${firstUncompleted.getStepKey}"
      else
        "/setup/finalizeInstall"
    } else {
      if (logger.isDebugEnabled)
        logger.debug(s"next step: ${nextStep.getStepName}")
      model.addAttribute("step", nextStep)
      s"/setup/${nextStep.getStepKey}"
    }
  }
  @RequestMapping(value = Array("/setup/finalizeInstall"), method = Array(RequestMethod.GET))
  def finalizeInstall: String = "/setup/finalizeInstall"
  @RequestMapping(value = Array("/setup/finalizeInstall"), method = Array(RequestMethod.POST))
  def doFinalizeInstall(model: Model): String = {
    if (!setupService.isSetupCompleted) {
      logger.warn("Setup is not completed!")
      return MvcConstants.page500
    }
    model.asMap().clear()
    Boot.BOOT.markInstalled()
    sysService.restartApplication()
    "redirect:/main/restarting"
  }
  @RequestMapping(Array("/restarting"))
  def restarting: String = "/restarting"
}
@Service
class PasterSetupService extends Logged {
  private val setupMap: mutable.Map[String, SetupStep] = mutable.LinkedHashMap()
  @PostConstruct
  def onInit(): Unit = {
    val steps: Array[SetupStep] = Array(new WelcomeStep, new SetupDbStep, new SetupUsersStep)
    for (s <- steps) {
      setupMap.put(s.getStepKey, s)
    }
  }
  def containsStep(stepName: String): Boolean = setupMap.contains(stepName)
  def getStep(stepName: String): SetupStep = setupMap(stepName)
  def getFirstUncompleted: SetupStep = {
    for (s <- setupMap.values) {
      if (!s.isCompleted)
        return s
    }
    null
  }
  def isSetupCompleted: Boolean = {
    for (s <- setupMap.values) {
      if (!s.isCompleted)
        return false
    }
    true
  }
  def getPreviousStep(stepName: String): SetupStep = {
    var prevStep: SetupStep = null
    for (k <- setupMap.keys) {
      if (k.equals(stepName))
        return prevStep
      prevStep = setupMap(k)
    }
    null
  }
  def getNextStep(stepName: String): SetupStep = {
    var found = false
    for (k <- setupMap.keys) {
      if (found)
        return setupMap(k)
      if (k.equals(stepName))
        found = true
    }
    null
  }
  def getStepNames: Map[String, String] = {
    val map: mutable.Map[String, String] = mutable.Map()
    for (s <- setupMap) {
      map.put(s._1, s._2.getStepName)
    }
    map.toMap
  }
  def getSteps: java.util.Collection[SetupStep] = setupMap.values.asJavaCollection
  def updateStep(step: SetupStep): Unit = {
    Assert.notNull(step, "Step should be non null!")
    logger.debug(s"updating step ${step.getClass.getName}")
    if (!setupMap.contains(step.getStepKey))
      throw SystemError.withCode(0x6001, s"Incorrect step type:${step.getClass.getName}")
    setupMap(step.getStepKey).update(step)
  }
}
class SetupUsersStep extends SetupStep("users", "Setup users") {
  override def update(dto: SetupStep): Unit = {
    val update: SetupUsersStep = dto.asInstanceOf[SetupUsersStep]
    markCompleted()
  }
}
class SetupDbStep extends SetupStep("db", "Setup database") {
  @NotNull(message = "{validator.not-null}")
  var dbUrl: String = _
  var dbUser: String = _
  var dbPassword: String = _
  @NotNull(message = "{validator.not-null}")
  var dbType: String = _
  def getDbUrl: String = dbUrl
  def getUser: String = dbUser
  def getPassword: String = dbPassword
  override def update(dto: SetupStep): Unit = {
    val update: SetupDbStep = dto.asInstanceOf[SetupDbStep]
    this.dbUrl = update.dbUrl
    this.dbUser = update.dbUser
    this.dbPassword = update.dbPassword
    markCompleted()
  }
}
class WelcomeStep extends SetupStep("welcome", "Setup language") {
  @NotNull(message = "{validator.not-null}")
  var defaultLang: String = "en"
  var switchToUserLocale: Boolean = true
  def getDefaultLang: String = defaultLang
  def isSwitchToUserLocale: Boolean = switchToUserLocale

  override def update(dto: SetupStep): Unit = {
    if (logger.isDebugEnabled)
      logger.debug(s"called update dto: ${dto.getClass.getName}")

    val update: WelcomeStep = dto.asInstanceOf[WelcomeStep]
    this.defaultLang = update.getDefaultLang
    this.switchToUserLocale = update.isSwitchToUserLocale
    if (logger.isDebugEnabled)
      logger.debug(s"defaultLang: ${this.defaultLang} switchToUserLocale: ${this.switchToUserLocale}")
    markCompleted()
  }
}
class StepModel {
  private var step: SetupStep = _
  def getStep: SetupStep = step
  def setStep(s: SetupStep): Unit = {
    step = s
  }
}
abstract class SetupStep(stepKey: String, stepName: String) extends Logged {
  // def this() = this(null)
  var completed: Boolean = false
  def isCompleted: Boolean = completed
  def markCompleted(): Unit = {
    completed = true
  }
  def getStepKey: String = stepKey
  def getStepName: String = stepName
  def update(dto: SetupStep): Unit
}

class DbType(name:String,url:String,driver:String,current:Boolean) {
  def getName: String = name
  def getUrl: String = url
  def getDriver: String = driver
  def isCurrent: Boolean = current
}