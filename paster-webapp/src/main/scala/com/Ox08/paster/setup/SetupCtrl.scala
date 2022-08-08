package com.Ox08.paster.setup

import com.Ox08.paster.common.SystemManagementService
import com.Ox08.paster.webapp.base.Boot.BOOT
import com.Ox08.paster.webapp.base.{Boot, Logged, SystemError}
import com.Ox08.paster.webapp.manager.UserManager
import com.Ox08.paster.webapp.model.{PasterUser, Role}
import com.Ox08.paster.webapp.mvc.MvcConstants
import com.Ox08.paster.webapp.web.PasterLocaleResolver
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import jakarta.validation.{ConstraintViolation, Valid, Validator}
import jakarta.validation.constraints.{NotEmpty, NotNull}
import org.apache.commons.beanutils.PropertyUtilsBean
import org.apache.commons.csv.CSVRecord
import org.apache.commons.io.{FileUtils, IOUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.commons.text.StringSubstitutor
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.MessageSource
import org.springframework.stereotype.{Controller, Service}
import org.springframework.ui.Model
import org.springframework.util.Assert
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation._

import java.io.File
import java.nio.charset.StandardCharsets
import java.util
import java.util.Locale
import scala.collection.mutable
import scala.jdk.CollectionConverters._

/**
 * This is 'all-in-one' configuration controller, used for setup stage
 */
private object SetupConstants {
  def getAvailableSecurityModes: Array[SecurityMode] = Array(new SecurityMode("Public", "public"),
    new SecurityMode("Private", "private"))

  def getAvailableDrivers: Array[DbType] = Array(new DbType("H2 Embedded Database (Default)",
    "jdbc:h2:file:${paster.app.home}/db/pastedb;DB_CLOSE_ON_EXIT=TRUE;LOCK_TIMEOUT=10000",
    "org.h2.jdbcx.JdbcDataSource", current = true, editable = false),
    new DbType("PostgreSQL",
      "jdbc:pgsql://127.0.0.1/test-db",
      "com.impossibl.postgres.jdbc.PGDataSource", current = false, editable = true),
    new DbType("MySQL",
      "jdbc:mysql://localhost/testdb",
      "com.mysql.cj.jdbc.Driver", current = false, editable = true))

}


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
  def getAvailableSteps: java.util.Collection[SetupStep] = setupService.getSteps

  @ModelAttribute("availableLocales")
  def getAvailableLocales: Array[Locale] = Boot.BOOT.getSystemInfo.getAvailableLocales
  def getAvailableSecurityModes: Array[SecurityMode] = SetupConstants.getAvailableSecurityModes
  def getAvailableDrivers: Array[DbType] = SetupConstants.getAvailableDrivers
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
    if (logger.isDebugEnabled)
      logger.debug(s"init binder: '${servletRequest.getRequestURI}' , method:  " +
        s"${servletRequest.getMethod} , target: $nonCastedTarget ")
    val target = nonCastedTarget.asInstanceOf[StepModel]
    if (target.getStep != null) {
      logger.debug("step already set - continue")
      return
    }
    val url = servletRequest.getRequestURI.substring(
            servletRequest.getContextPath.length).toLowerCase
    var step = url.substring("/main/setup/".length)
    step match {
      case "checkconnection" => step = "db"
      case "adduser" | "removeuser" => step = "users"
      case _ =>
    }
    if (logger.isDebugEnabled)
      logger.debug("step: '{}'", step)
    if (!setupService.containsStep(step))
      throw SystemError.withCode(0x6001, s"Incorrect step type: $step")
    target.setStep(setupService.getStep(step))
  }

  @RequestMapping(value = Array("/setup/removeUser"), method = Array(RequestMethod.POST))
  def removeUser(model: Model,
                 @RequestParam index: Int
                ): String = {
    val users = setupService.getStep[SetupUsersStep]("users").users
    if (!users.isEmpty && index < users.size())
      users.remove(index)
    fillModel("users", model)
    "/setup/users"
  }

  /**
   * Adds new user to the list of configured users
   *
   * @param updatedStep
   * DTO with submitted form values
   * @param result
   * form validatio result
   * @param model
   * page model
   * @return
   * step page
   */
  @RequestMapping(value = Array("/setup/addUser"), method = Array(RequestMethod.POST))
  def addUser(@ModelAttribute("updatedStep")
              updatedStep: StepModel,
              result: BindingResult,
              model: Model): String = {
    val uModel: SetupUsersStep = updatedStep.getStep[SetupUsersStep]

    for (u <- uModel.users.asScala) {
      logger.debug("user: {} - {} ", u.getName, u.getUsername)
    }

    val users = setupService.getStep[SetupUsersStep]("users").users
    val u = new PasterUser(s"user${users.size()}",
      s"Unnamed user${users.size()}",
      "user",
      java.util.Set.of(Role.ROLE_USER))
    users.add(new UserDTO().fromUser(u))
    fillModel("users", model)
    "/setup/users"
  }

  /**
   * Tries to check JDBC connection
   *
   * @param updatedStep
   * DTO with submitted values of current step
   * @param result
   * form validation result
   * @param model
   * page model
   * @return
   * step page
   */
  @RequestMapping(value = Array("/setup/checkConnection"), method = Array(RequestMethod.POST))
  def checkConnection(@ModelAttribute("updatedStep")
                      updatedStep: StepModel,
                      result: BindingResult,
                      model: Model): String = {
    val step: SetupDbStep = updatedStep.getStep.asInstanceOf[SetupDbStep]
    if (StringUtils.isBlank(step.dbType)) {
      val dbType = getAvailableDrivers.find(p => step.origName.equalsIgnoreCase(p.getName))
        .orElse(alternative = throw new RuntimeException(s"Cannot find type ${step.origName}")).get

      step.dbType = dbType.getDriver
      if (dbType.getName.contains("H2")) {
        step.dbUrl = dbType.getUrl
          .replace("${paster.app.home}",
            Boot.BOOT.getSystemInfo.getAppHome.getAbsolutePath)
      }
    }
    if (result.hasErrors) {
      // dump errors
      if (logger.isDebugEnabled) {
        logger.debug("form has {} errors", result.getErrorCount)
        for (e <- result.getAllErrors.asScala) {
          logger.debug("error: {} code: {} msg: {}",
            e.getObjectName, e.getCode, e.getDefaultMessage)
        }
      }
      model.addAttribute("step", updatedStep.getStep)
      return s"/setup/${step.getStepKey}"
    }

    //model.asMap().clear()
    var log: String = null
    try {
      logger.debug("checking driver: {} url: {}", step.dbType, step.dbUrl)
      //val clazz = Class.forName(step.dbType)
      //val ds:javax.sql.DataSource = clazz.getDeclaredConstructor().newInstance().asInstanceOf[javax.sql.DataSource]
      import org.springframework.jdbc.datasource.DriverManagerDataSource
      val ds2 = new DriverManagerDataSource
      ds2.setDriverClassName(step.getDbType)
      ds2.setUrl(step.dbUrl)
      ds2.setUsername(step.dbUser)
      ds2.setPassword(step.dbPassword)

      val con = ds2.getConnection
      con.close()
      log = "Connection established"
    } catch {
      case e@(_: Exception) =>
        logger.debug(e.getMessage, e)
        try log = ExceptionUtils.getStackTrace(e) catch {
          case _: Exception =>
          // ignore
        } finally
          if (StringUtils.isBlank(log))
            log = e.getMessage
    }
    fillModel(step.getStepKey, model)
    step.connectionLog = log
    "/setup/db"
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
   * error page
   * @param errorCode
   * http error code
   * @return
   * error page
   */
  @RequestMapping(Array("/error/{errorCode:[0-9_]+}"))
  def error(response: HttpServletResponse, model: Model,
            @PathVariable("errorCode") errorCode: Int): String = errorCode match {
    case 403 | 404 | 500 =>
      model.asMap().clear()
      model.addAttribute("appId", appId)
      model.addAttribute("systemInfo", systemInfo)
      response.setStatus(errorCode)
      "redirect:/main/setup/welcome"
    case _ =>
      "/error/500"
  }

  /**
   * Fills page model with attributes
   *
   * @param step
   * current step code
   * @param model
   * page model
   */
  private def fillModel(step: String, model: Model): Unit = {
    val cs = setupService.getStep[SetupStep](step)
    model.addAttribute("installInProgress",setupService.installInProgress)
    model.addAttribute("step", cs)
    model.addAttribute("updatedStep", new StepModel(cs))
    model.addAttribute("nextStep", setupService.getNextStep(step))
    model.addAttribute("previousStep", setupService.getPreviousStep(step))
    step match {
      case "users" =>
        val users: SetupUsersStep = cs.asInstanceOf[SetupUsersStep]
        if (users.users.isEmpty) {
          val availableUsers: util.ArrayList[UserDTO] = new java.util.ArrayList()
          val csv = new File(Boot.BOOT.getSystemInfo.getAppHome, "users.csv")
          UserManager.loadUsersFromCSV(csv, (record: CSVRecord) => {
            if (logger.isDebugEnabled)
              logger.debug("processing record : {}", record)
            val u = new PasterUser(record.get("NAME"),
              record.get("USERNAME"),
              record.get("PASSWORD"), util.Set.of(
                if (record.get("ISADMIN").toBoolean)
                  Role.ROLE_ADMIN
                else
                  Role.ROLE_USER))
            availableUsers.add(new UserDTO().fromUser(u))
          })
          model.addAttribute("availableUsers", availableUsers)
          users.getUsers.addAll(availableUsers)
        } else {
          model.addAttribute("availableUsers", users.users)
        }
        model.addAttribute("availableSecurityModes", getAvailableSecurityModes)

      case "db" =>
        val dbs: SetupDbStep = cs.asInstanceOf[SetupDbStep]
        dbs.connectionLog = null
        val availableDrivers = getAvailableDrivers
        if (!StringUtils.isBlank(dbs.origName))
          for (a <- availableDrivers) {
            a.setCurrent(dbs.origName.equals(a.getName))
          }
        model.addAttribute("availableDrivers", availableDrivers)
      case _ =>
    }
  }

  /**
   * special method to handle moving to previous page with POST request
   *
   * @param model
   * page model
   * @param step
   * current step key
   * @return
   * previous step page
   */
  @RequestMapping(value = Array("/setup/prev/{step}"), method = Array(RequestMethod.POST))
  def goPreviousStep(model: Model, @PathVariable("step") step: String): String = setupStep(model, step)

  @RequestMapping(value = Array("/setup/{step}"), method = Array(RequestMethod.GET))
  def setupStep(model: Model, @PathVariable("step") step: String): String = {
    if (!setupService.containsStep(step)) {
      if (logger.isDebugEnabled)
        logger.debug("Cannot find step: {}", step)
      return MvcConstants.page500
    }
    fillModel(step, model)
    s"/setup/$step"
  }

  /**
   *
   * @param updatedStep
   * submitted form values
   * @param result
   * form validation result
   * @param model
   * page model
   * @param step
   * current step key
   * @return
   * next/previous or current page
   */
  @RequestMapping(value = Array("/setup/{stepName}"), method = Array(RequestMethod.POST))
  def updateStep(@ModelAttribute("updatedStep")
                 @Valid
                 updatedStep: StepModel,
                 result: BindingResult,
                 model: Model,
                 @PathVariable("stepName") step: String): String = {
    if (logger.isDebugEnabled)
      logger.debug("updating step, class: {}", updatedStep.getStep.getClass.getName)
    setupService.updateStep(updatedStep.getStep, result)
    if (result.hasErrors) {
      //import org.springframework.validation.BindingResult
      model.asMap().put(classOf[BindingResult].getName + ".updatedStep", result)
     // model.asMap().put("errors",result)
      // dump errors
      if (logger.isDebugEnabled) {
        logger.debug("form has {} errors", result.getErrorCount)
        for (e <- result.getAllErrors.asScala) {
          logger.debug("error: {} code: {} msg: {}",
            e.getObjectName, e.getCode, e.getDefaultMessage)
        }
      }
      fillModel(step, model)
      return s"/setup/$step"
    }


    val nextStep: SetupStep = setupService.getNextStep(updatedStep.getStep.getStepKey)

    // all steps done
    if (nextStep == null) {
      // but some are not completed yet
      val firstUncompleted: SetupStep = setupService.getFirstUncompleted
      if (firstUncompleted != null) {
        fillModel(firstUncompleted.getStepKey, model)
        s"/setup/${firstUncompleted.getStepKey}"
      } else
        "/setup/finalizeInstall"
    } else {
      if (logger.isDebugEnabled)
        logger.debug(s"next step: ${nextStep.getStepName}")
      fillModel(nextStep.getStepKey, model)
      s"/setup/${nextStep.getStepKey}"
    }
  }

  @RequestMapping(value = Array("/setup/finalizeInstall"),
    method = Array(RequestMethod.GET, RequestMethod.POST))
  def finalizeInstall: String = "/setup/finalizeInstall"

  @RequestMapping(value = Array("/setup/doFinalizeInstall"), method = Array(RequestMethod.POST))
  def doFinalizeInstall(model: Model): String = {
    if (!setupService.isSetupCompleted) {
      logger.warn("Setup is not completed!")
      return MvcConstants.page500
    }

    if (setupService.installInProgress) {
      logger.warn("Installation already in progress!")
      return MvcConstants.page500
    }
    setupService.installInProgress = true
    model.asMap().clear()
    setupService.writeConfig()
    Boot.BOOT.markInstalled()
    //sysService.restartApplication()
    "redirect:/main/restarting"
  }

  @RequestMapping(Array("/restarting"))
  def restarting: String = "/restarting"
}

@Service
class PasterSetupService extends Logged {

  import org.springframework.beans.factory.annotation.Value
  import org.springframework.core.io.ClassPathResource

  @Value("${classpath*:config.template}")
  private val configTemplate: ClassPathResource = null
  private val setupMap: mutable.Map[String, SetupStep] = mutable.LinkedHashMap()

  @Autowired
  val localeResolver: PasterLocaleResolver = null

  @Autowired
  val validator: Validator = null

  private[setup] var installInProgress: Boolean = false

  @PostConstruct
  def onInit(): Unit = {
    val steps: Array[SetupStep] = Array(new WelcomeStep, new SetupDbStep, new SetupUsersStep)
    for (s <- steps) {
      setupMap.put(s.getStepKey, s)
    }
  }

  def containsStep(stepName: String): Boolean = setupMap.contains(stepName)

  def getStep[T <: SetupStep](stepName: String): T = setupMap(stepName).asInstanceOf[T]

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

  def updateStep(step: SetupStep, result: BindingResult): Unit = {
    Assert.isTrue(!installInProgress, "Install in progress!")
    Assert.notNull(step, "Step should be non null!")
    val violations: util.Set[ConstraintViolation[SetupStep]] = validator.validate(step)
    logger.debug(s"updating step ${step.getClass.getName} ,step has ${violations.size()} violations")

    for (v <- violations.asScala) {
      result.reject("step."+v.getPropertyPath.toString, v.getMessage)
    }

    if (!setupMap.contains(step.getStepKey))
      throw SystemError.withCode(0x6001, s"Incorrect step type:${step.getClass.getName}")
    // update configuration options
    setupMap(step.getStepKey).update(step)

    // switch current locale to selected
    if ("welcome".equalsIgnoreCase(step.getStepKey) && step.completed) {
      val wstep: WelcomeStep = getStep[WelcomeStep](step.getStepKey)
      Boot.BOOT.getSystemInfo.setSystemLocale(wstep.defaultLang)
      localeResolver.setDefaultLocale(Boot.BOOT.getSystemInfo.getSystemLocale)
      localeResolver.switchToUserLocale = false
    }
  }

  def writeConfig(): Unit = {
    val values: java.util.Map[String, Object] = new util.HashMap()
    val pu = new PropertyUtilsBean
    for (e <- setupMap) {
      val props: util.Map[_, _] = pu.describe(e._2)
      for (p <- props.asScala) {
        values.put(s"${e._1}.${p._1.toString}", p._2.asInstanceOf[Object])
      }
    }
    val tpl: String = new String(IOUtils.toByteArray(configTemplate.getInputStream), StandardCharsets.UTF_8)
    val sub = new StringSubstitutor(values)
    sub.setEnableSubstitutionInVariables(false)
    val filledTemplate: String = sub.replace(tpl)
    FileUtils.writeStringToFile(BOOT.getSystemInfo.getConfigFile, filledTemplate, "UTF-8")
  }
}

class SetupUsersStep extends SetupStep("users", "Setup users") {
  val users: java.util.List[UserDTO] = new util.ArrayList[UserDTO]()
  @NotNull(message = "{validator.not-null}")
  var securityMode: String = _
  var allowAnonymousCommentsCreate: Boolean = _
  var allowAnonymousPastasCreate: Boolean = _

  def getUsers: java.util.List[UserDTO] = users

  def isAllowAnonymousCommentsCreate: Boolean = allowAnonymousCommentsCreate

  def isAllowAnonymousPastasCreate: Boolean = allowAnonymousPastasCreate

  def getSecurityMode: String = securityMode

  def setAllowAnonymousCommentsCreate(v: Boolean): Unit = {
    this.allowAnonymousCommentsCreate = v
  }

  def setAllowAnonymousPastasCreate(v: Boolean): Unit = {
    this.allowAnonymousPastasCreate = v
  }

  def setSecurityMode(mode: String): Unit = {
    this.securityMode = mode
  }

  override def update(dto: SetupStep): Unit = {
    val update: SetupUsersStep = dto.asInstanceOf[SetupUsersStep]
    this.securityMode = update.securityMode
    markCompleted()
  }
}

/**
 * Database configuration step
 */
class SetupDbStep extends SetupStep("db", "Setup database") {
  @NotEmpty
  //(message = "{validator.not-null}")
  var dbUrl: String = _
  var dbUser: String = "paster"
  var dbPassword: String = "paster"
  // link to dbtype below
  //@NotNull(message = "{validator.not-null}")
  @NotEmpty
  var origName: String = _
  // driver
  //@NotNull(message = "{validator.not-null}")
  @NotEmpty
  var dbType: String = _
  var connectionLog: String = _

  def getConnectionLog: String = connectionLog

  def getDbType: String = dbType

  def getDbUrl: String = dbUrl

  def getDbUser: String = dbUser

  def getDbPassword: String = dbPassword

  def getOrigName: String = origName

  def setDbType(t: String): Unit = {
    this.dbType = t
  }

  def setOrigName(name: String): Unit = {
    this.origName = name
  }

  def setDbUrl(url: String): Unit = {
    this.dbUrl = url
  }

  def setDbUser(user: String): Unit = {
    this.dbUser = user
  }

  def setDbPassword(pwd: String): Unit = {
    this.dbPassword = pwd
  }

  override def update(dto: SetupStep): Unit = {
    val update: SetupDbStep = dto.asInstanceOf[SetupDbStep]

    // support for default selection
    if (StringUtils.isBlank(update.dbType)) {
      val dbType = SetupConstants.getAvailableDrivers.find(p => update.origName
        .equalsIgnoreCase(p.getName))
        .orElse(throw new RuntimeException("Cannot find type " + update.origName)).get
      this.dbType = dbType.getDriver
      this.dbUrl = dbType.getUrl
    } else {
      this.dbType = update.dbType
      this.dbUrl = update.dbUrl
      this.dbUser = update.dbUser
      this.dbPassword = update.dbPassword
      this.origName = update.origName
    }
    markCompleted()
  }
}

/**
 * Welcome step
 */
class WelcomeStep extends SetupStep("welcome", "Setup language") {
  @NotNull(message = "{validator.not-null}")
  var defaultLang: String = "en"
  var switchToUserLocale: Boolean = true

  def getDefaultLang: String = defaultLang

  def isSwitchToUserLocale: Boolean = switchToUserLocale

  def setDefaultLang(lang: String): Unit = {
    this.defaultLang = lang
  }

  def setSwitchToUserLocale(switch: Boolean): Unit = {
    this.switchToUserLocale = switch
  }

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

class StepModel(step: SetupStep) {
  private var _step: SetupStep = step

  def getStep[T <: SetupStep]: T = _step.asInstanceOf[T]

  def setStep(s: SetupStep): Unit = {
    _step = s
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

class DbType(name: String, url: String, driver: String, current: Boolean, editable: Boolean) {
  private var _current: Boolean = current

  def getName: String = name

  def getUrl: String = url

  def getDriver: String = driver

  def isCurrent: Boolean = _current

  def isEditable: Boolean = editable

  def setCurrent(v: Boolean): Unit = {
    _current = v
  }
}

class SecurityMode(name: String, key: String) {
  def getName: String = name

  def getKey: String = key
}

class UserDTO {
  @NotNull
  private var _username: String = _
  @NotNull
  private var _name: String = _
  @NotNull
  private var _password: String = _
  private var _isadmin: Boolean = false

  def fromUser(pasterUser: PasterUser): UserDTO = {
    this._isadmin = pasterUser.isAdmin
    this._name = pasterUser.getName
    this._username = pasterUser.getUsername
    this._password = pasterUser.getPassword
    this
  }

  def toUser: PasterUser = new PasterUser(this._name, this._username, this._password, util.Set.of(
    if (_isadmin)
      Role.ROLE_ADMIN
    else
      Role.ROLE_USER
  ))

  def getUsername: String = _username

  def getName: String = _name

  def getPassword: String = _password

  def isAdmin: Boolean = _isadmin

  def setUsername(username: String): Unit = {
    this._username = username
  }

  def setName(name: String): Unit = {
    this._name = name
  }

  def setPassword(pwd: String): Unit = {
    this._password = pwd
  }

  def setAdmin(value: Boolean): Unit = {
    this._isadmin = value
  }
}