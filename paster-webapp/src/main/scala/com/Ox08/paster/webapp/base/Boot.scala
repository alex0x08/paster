package com.Ox08.paster.webapp.base
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.springframework.util.Assert
import java.io.{File, FileInputStream, IOException, StringReader, StringWriter}
import java.nio.file.Paths
import java.text.{ParseException, SimpleDateFormat}
import java.util.{Calendar, Date, Properties}
object Boot {
  private val INSTALLED_TS_FORMAT: SimpleDateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm")
  val BOOT = new Boot
}
class Boot private() extends Logged {
  private val wasBoot = false // a mark that system has been booted already
  private var initialConfig = false
  def getSystemInfo: SystemInfo = SystemInfo.SYSTEM_INFO
  /**
   * Start boot sequence
   *
   * @param appCode application codename
   *
   */
  def doBootSequence(appCode: String): Unit = {
    // system already booted?
    if (wasBoot)
      throw SystemError.withCode(0x6004, appCode)
    // codename is blank?
    if (StringUtils.isBlank(appCode))
      throw SystemError.withCode(0x6003)
    val system = SystemInfo.SYSTEM_INFO
    // system state locked?
    if (system.isLocked)
      throw SystemError.withCode(0x6002)

    // set codename
    system.setAppCode(appCode)
    // check 'appDebug' flag
    system.setDebug(java.lang.Boolean.valueOf(System.getProperty("appDebug", "false")))
    logger.info(SystemMessage.of("paster.system.message.debugMode", "" + system.isDebug))
    // try to detect app's home folder
    var app_home: File = null
    val appVar = appCode + ".app.home"
    // if there is no system property with apps home
    if (!System.getProperties.containsKey(appVar)) { // get current user's home folder
      val user_home = System.getProperty("user.home")
      // generate full path like: ~/.apps/codename
      app_home = Paths.get(user_home, ".apps", appCode).toFile
      if (!app_home.exists || !app_home.isDirectory) {
        // try to create all folders in path, if fail - throw exception and quit
        if (!app_home.mkdirs)
          throw SystemError.withCode(0x6005, app_home.getAbsolutePath)
      }
      // set app's home folder as system variable, it will be used in EL expressions
      System.setProperty(appVar, app_home.getAbsolutePath)
    }
    else { // if app's home variable was found in environment
      // ex. as JVM argument:  -Dcodename.app.home=...
      app_home = new File(System.getProperty(appVar))
    }
    // put app's home to systemInfo object
    system.setAppHome(app_home)
    logger.info(SystemMessage.of("paster.system.message.appHome", app_home.getAbsolutePath))
    // try to kill previous JVM instance (if present)
    killPreviousInstance(app_home)
    writePid(app_home)
    // create temp folder
    val temp_store = createAppFolder(app_home, "temp")
    system.setTempDir(temp_store)
    /*// create folder for binary files
    val files_dir = createAppFolder(app_home, "pfiles")
    logger.info(SystemMessage.of("paster.system.message.filesDir", files_dir.getAbsolutePath))
    system.setFilesDir(files_dir)*/
    val config = new File(app_home, "config.properties")
    if (!config.exists || !config.isFile) initialConfig = true
    doResourceCheck(app_home, "users.csv")
    doResourceCheck(app_home, "config.properties")
    // load release details
    // if these files missing - the release is broken!
    val build_info = new Properties()
    val git_info = new Properties()
    try { // load build details
      build_info.load(getClass.getResourceAsStream("/release.properties"))
      // load git commit details
      git_info.load(getClass.getResourceAsStream("/git.properties"))
    } catch {
      case ex: IOException =>
        // its required to throw exception here and exit - don't run on broken builds!
        throw SystemError.withError(0x6001, ex)
    }
    // parse build info
    val mf_version = new AppVersion(build_info, git_info)
    // set env variables ( used in EL from Spring )
    system.setRuntimeVersion(mf_version)
    System.setProperty("app.version", mf_version.implBuildNum)
    System.setProperty("app.build.time", mf_version.implBuildTime)
    System.setProperty("app.build.number", mf_version.implBuildNum)
    System.setProperty("app.build.version", mf_version.implVersionFull)
    logger.info(SystemMessage.of("paster.system.message.appRelease", mf_version.implVersionFull))
    logger.info(SystemMessage.of("paster.system.message.appGitBranch",
      mf_version.getGitState.getProperty("git.branch", null)))
    // load customized config ( 'config.properties' file)
    createLoadAppConfig(system, app_home)
    if (system.getExternalUrlPrefix != null) {
      System.setProperty("app.externalUrlPrefix", system.getExternalUrlPrefix)
      logger.info(SystemMessage.of("paster.system.message.appExternalUrl", system.getExternalUrlPrefix))
    }
    checkInstalled(system, app_home)
  }
  def markInstalled(): Unit = {
    val system: SystemInfo = getSystemInfo
    val iFile = new File(system.getAppHome, ".installed")
    val dateInstalled = new Date()
    val p: Properties = new Properties()
    p.setProperty("dateInstalled", Boot.INSTALLED_TS_FORMAT.format(dateInstalled))
    val out = new StringWriter()
    p.store(out, "Install information")
    FileUtils.writeStringToFile(iFile, out.toString, "UTF-8")
    system.setInstalled(installed = true, dateInstalled)
  }
  private def checkInstalled(system: SystemInfo, appHome: File): Unit = {
    val iFile = new File(appHome, ".installed")
    if (iFile.exists() && iFile.isFile) {
      if (iFile.length() == 0 || iFile.length() > 2048)
        throw SystemError.withError(0x6001, "Incorrect or broken '.installed' file!")
      val data: String = FileUtils.readFileToString(iFile, "UTF-8")
      val p = new Properties()
      p.load(new StringReader(data))
      val dateInstalledStr = p.getProperty("dateInstalled", null)
      if (dateInstalledStr == null)
        throw SystemError.withError(0x6001, "Incorrect or broken '.installed' file!")
      val dateInstalled: Date = Boot.INSTALLED_TS_FORMAT.parse(dateInstalledStr)
      if (logger.isDebugEnabled)
        logger.debug("found install date: {}", dateInstalled)
      system.setInstalled(installed = true, dateInstalled)
    } else {
      /*val dateInstalled = new Date()
      val p: Properties = new Properties()
      p.setProperty("dateInstalled", INSTALLED_TS_FORMAT.format(dateInstalled))
      val out = new StringWriter()
      p.store(out, "Install information")
      FileUtils.writeStringToFile(iFile, out.toString, "UTF-8")
      system.setInstalled(installed = true, dateInstalled)*/
    }
  }
  private def writePid(appHome: File): Unit = {
    val pidFile = new File(appHome, "app.pid")
    val pid = ProcessHandle.current.pid()
    FileUtils.writeStringToFile(pidFile, pid.toString, "UTF-8")
  }
  /**
   * Tries to kill previous JVM instance, lookup by PID from 'app.pid' file
   *
   * @param appHome application codename
   */
  private def killPreviousInstance(appHome: File): Unit = {
    try { // lookup PID file
      val pid = new File(appHome, "app.pid")
      if (pid.exists && pid.isFile) { // read PID
        val previousPidStr = FileUtils.readFileToString(pid, "UTF-8")
        if (StringUtils.isBlank(previousPidStr)) {
          logger.warn("pid file is empty!")
          return
        }
        // parse PID
        val previousPid = NumberUtils.toInt(previousPidStr, -1)
        if (previousPid <= 0) {
          logger.warn("wrong pid : {}", previousPid)
          return
        }
        // lookup process by PID
        ProcessHandle.of(previousPid).ifPresent((processHandle: ProcessHandle) => {
          logger.info("killing old instance: {}", previousPid)
          // try to kill
          processHandle.destroy()
        })
      }
    } catch {
      case e: Exception =>
        throw SystemError.withError(0x6001, e)
    }
  }
  /**
   * Проверка на существование файла в домашней папке системы и его загрузка
   * из jar
   *
   * @param js_store каталог в домашней папке, в котором происходит проверка
   * @param name     имя файла, должно совпадать с именем в jar
   * @return
   */
  private def doResourceCheck(js_store: File, name: String) = {
    Assert.notNull(name, SystemError.messageFor(0x6006))
    val rScript = new File(js_store, name)
    if (!rScript.exists() || !rScript.isFile || (rScript.length() == 0))
      try FileUtils.copyURLToFile(getClass.getResource("/default/" + name), rScript)
      catch {
        case ex: IOException =>
          throw SystemError.withError(0x6001, ex)
      }
    rScript
  }
  /**
   * Создание внутренней папки в домашнем каталоге со всеми необходимыми
   * проверками
   *
   * @param app_home
   * домашний каталог
   * @param name
   * имя создаваемой папки
   * @return
   */
  private def createAppFolder(app_home: File, name: String): File = {
    val folder = if (name != null) new File(app_home, name)
    else app_home
    if ((!folder.exists || !folder.isDirectory) && !folder.mkdirs)
      throw SystemError.withCode(0x6005, folder.getAbsolutePath)
    folder
  }
  /**
   * загрузка конфига
   *
   * @param system   инстанс инф-ии о системе
   * @param app_home домашняя папка
   * @return
   */
  private def createLoadAppConfig(system: SystemInfo, app_home: File): File = {
    val config = new File(app_home, "config.properties")
    if (!config.exists || !config.isFile) {
      logger.warn(SystemMessage.of("paster.system.message.appConfigNotFound", config.getAbsolutePath))
      //  throw new IllegalStateException("app config file not found!");
      return null
    }
    system.setConfigFile(config)
    if (initialConfig) {
      //  loadPluginsConfiguration(system)
      initialConfig = false
    }
    val input = new FileInputStream(config)
    try {
      system.getConfig.load(input)
      system.setExternalUrlPrefix(system.getConfig.getProperty("externalUrlPrefix", null))
      logger.info(SystemMessage.of("paster.system.message.loadedLinesFromAppConf", "" + system.getConfig.size))
    } catch {
      case e: IOException =>
        throw SystemError.withError(0x6001, e)
    } finally
      if (input != null)
        input.close()
    config
  }
  object SystemInfo {
    val SYSTEM_INFO = new SystemInfo
  }
  /**
   * Класс базовой информации о системе
   *
   * @since 1.0
   * @author Alex Chernyshev <alex3.145@gmail.com>
   */
  final class SystemInfo private() {
    // версия сборки
    private var runtimeVersion: AppVersion = _
    // дата и время запуска системы
    final private val dateStart = Calendar.getInstance.getTime
    private var appHome: File = _
    private var tempDir: File = _
    private var configFile: File = _
    // для предотвращения дальнейших изменений
    private var locked = false
    private var debug = true
    private var installed = false
    private var config = new Properties() // загруженная конфигурация
    private var externalUrlPrefix: String = _ // полный внешний префикс для ссылок
    private var installedDate: Date = _
    // ex. : http://some.site.com/app
    // используется когда система стоит за
    // прокси-сервером и не знает о
    // своем внешнем имени
    private var appCode: String = _ // кодовое обозначение системы
    def getAppCode: String = appCode
    private[base] def setAppCode(appCode: String): Unit = {
      checkLock()
      this.appCode = appCode
    }
    def getExternalUrlPrefix: String = externalUrlPrefix
    def setExternalUrlPrefix(prefix: String): Unit = {
      checkLock()
      this.externalUrlPrefix = prefix
    }
    def isDebug: Boolean = debug
    private[base] def setDebug(debug: Boolean): Unit = {
      checkLock()
      this.debug = debug
    }
    def isInstalled: Boolean = installed
    private[base] def setInstalled(installed: Boolean, installedDate: Date): Unit = {
      checkLock()
      this.installed = installed
      this.installedDate = installedDate
    }
    def getSetting(setting: String, defaultValue: String): String = {
      if (!config.containsKey(setting)) return defaultValue
      config.getProperty(setting)
    }
    def getSettingAsBoolean(setting: String, defaultValue: Boolean): Boolean = {
      if (!config.containsKey(setting)) {
        return defaultValue
      }
      java.lang.Boolean.valueOf(config.getProperty(setting))
    }
    def getSettingAsInt(setting: String, defaultValue: Int): Int = {
      if (!config.containsKey(setting))
        return defaultValue
      Integer.valueOf(config.getProperty(setting))
    }
    def getConfig: Properties = config
    def setConfig(config: Properties): Unit = {
      checkLock()
      this.config = config
    }
    def getConfigFile: File = configFile
    def setConfigFile(configFile: File): Unit = {
      checkLock()
      this.configFile = configFile
    }
    def getAppHome: File = appHome
    def setAppHome(appHome: File): SystemInfo = {
      checkLock()
      this.appHome = appHome
      this
    }
    def getTempDir: File = tempDir
    def setTempDir(tempDir: File): SystemInfo = {
      checkLock()
      this.tempDir = tempDir
      this
    }
    def getRuntimeVersion: AppVersion = runtimeVersion
    def setRuntimeVersion(runtimeVersion: AppVersion): SystemInfo = {
      checkLock()
      this.runtimeVersion = runtimeVersion
      this
    }
    def getDateStart: Date = dateStart
    def isLocked: Boolean = locked
    def lock(): Unit = {
      this.locked = true
    }
    private def checkLock(): Unit = {
      if (locked) {
          throw SystemError.withCode(0x6002)
      }
    }
  }
  /**
   * Информация о версии системы
   *
   * @since 1.0
   * @author Alex Chernyshev <alex3.145@gmail.com>
   */
  @SerialVersionUID(1L)
  class AppVersion(p: Properties, git: Properties) extends Serializable {
    private val UNDEFINED = "UNDEFINED"
    private val MAVEN_TS_FORMAT: SimpleDateFormat = new SimpleDateFormat("yyy-MM-dd_HHmm")
    val implVer: String = p.getProperty("build.version", UNDEFINED)
    val implBuildNum: String = p.getProperty("build.number", UNDEFINED)
    val implBuildTime: String = p.getProperty("build.time", UNDEFINED)
    val buildDate: Date = try {
      MAVEN_TS_FORMAT.parse(implBuildTime)
    } catch {
      case _: ParseException => null
    }
    val implVersionFull: String = implVer + "." + implBuildNum
    def getFull: String = implVersionFull // for EL
    def getGitState: Properties = git
  }
}


