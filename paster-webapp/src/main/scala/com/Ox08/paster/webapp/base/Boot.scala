package com.Ox08.paster.webapp.base

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.springframework.util.Assert
import java.io.{File, FileInputStream, IOException}
import java.nio.file.Paths
import java.text.{ParseException, SimpleDateFormat}
import java.util.{Calendar, Date, Properties}

object Boot {

  val BOOT = new Boot

}

class Boot private()  extends Loggered {

  private val UNDEFINED = "UNDEFINED"

  private val MAVEN_TS_FORMAT: SimpleDateFormat = new SimpleDateFormat("yyy-MM-dd_HHmm")


  private val wasBoot = false // a mark that system has been booted already
  private var initialConfig = false

  def getSystemInfo = SystemInfo.SYSTEM_INFO

  /**
   * Start booot sequence
   *
   * @param appCode application codename
   *
   */
  def doBootSequence(appCode: String): Unit = {
    // system already booted?
    if (wasBoot) throw SystemError.withCode(0x6004, appCode)
    // codename is blank?
    if (StringUtils.isBlank(appCode)) throw SystemError.withCode(0x6003)

    val system = SystemInfo.SYSTEM_INFO
    // system state locked?
    if (system.isLocked) throw SystemError.withCode(0x6002)

    // set codename
    system.setAppCode(appCode)
    // check 'appDebug' flag
    system.setDebug(java.lang.Boolean.valueOf(System.getProperty("appDebug", "false")))
    logger.info(SystemMessage.of("proxyman.system.message.debugMode", system.isDebug))
    // try to detect app's home folder
    var app_home: File = null
    val appVar = appCode + ".app.home"
    // if there is no system property with apps home
    if (!System.getProperties.containsKey(appVar)) { // get current user's home folder
      val user_home = System.getProperty("user.home")
      // generate full path like: ~/.apps/codename
      app_home = Paths.get(user_home, ".apps", appCode).toFile()
      if (!app_home.exists || !app_home.isDirectory) { // try to create all folders in path, if fail - throw exception and quit
        if (!app_home.mkdirs) throw SystemError.withCode(0x6005, app_home.getAbsolutePath)
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
    logger.info(SystemMessage.of("proxyman.system.message.appHome", app_home.getAbsolutePath))
    // try to kill previous JVM instance (if present)
    killPreviousInstance(app_home)
    // create temp folder
    val temp_store = createAppFolder(app_home, "temp")
    system.setTempDir(temp_store)
    /*// create folder for binary files
    val files_dir = createAppFolder(app_home, "pfiles")
    logger.info(SystemMessage.of("proxyman.system.message.filesDir", files_dir.getAbsolutePath))
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
    val mf_version = new AppVersion().fillFromProperties(build_info)
    mf_version.fillGitState(git_info)
    // set env variables ( used in EL from Spring )
    system.setRuntimeVersion(mf_version)
    System.setProperty("app.version", mf_version.getImplBuildNum)
    System.setProperty("app.build.time", mf_version.getImplBuildTime)
    System.setProperty("app.build.number", mf_version.getImplBuildNum)
    System.setProperty("app.build.version", mf_version.getImplVersionFull)
    logger.info(SystemMessage.of("proxyman.system.message.appRelease", mf_version.getImplVersionFull))
    logger.info(SystemMessage.of("proxyman.system.message.appGitBranch", mf_version.getGitState.getBranch))
    // load customized config ( 'config.properties' file)
    createLoadAppConfig(system, app_home)
    if (system.getExternalUrlPrefix != null) {
      System.setProperty("app.externalUrlPrefix", system.getExternalUrlPrefix)
      logger.info(SystemMessage.of("proxyman.system.message.appExternalUrl", system.getExternalUrlPrefix))
    }
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
        val fpid = FileUtils.readFileToString(pid, "UTF-8")
        if (StringUtils.isBlank(fpid)) {
          logger.warn("pid file is empty!")
          return
        }
        // parse PID
        val npid = NumberUtils.toInt(fpid, -1)
        if (npid <= 0) {
          logger.warn("wrong pid : {}", npid)
          return
        }
        // lookup process by PID
        ProcessHandle.of(npid).ifPresent((processHandle: ProcessHandle) => {
          logger.info("killing old instance: {}", npid)
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
  private def doResourceCheck(js_store: File, name: String) : File= {
    Assert.notNull(name, SystemError.messageFor(0x6006))
    val rScript: File = new File(js_store, name)
    if (!rScript.exists() || !rScript.isFile() || (rScript.length() == 0))
      try FileUtils.copyURLToFile(getClass().getResource("/default/" + name), rScript)
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
   * @param app_home домашний каталог
   * @param name     имя создаваемой папки
   * @return
   */
  private def createAppFolder(app_home: File, name: String) = {
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
      logger.warn(SystemMessage.of("proxyman.system.message.appConfigNotFound", config.getAbsolutePath))
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
        logger.info(SystemMessage.of("proxyman.system.message.loadedLinesFromAppConf", system.getConfig.size))
      } catch {
        case e: IOException =>
          throw SystemError.withError(0x6001, e)
      } finally if (input != null) input.close()

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
    private var runtimeVersion: AppVersion = null
    // дата и время запуска системы
    final private val dateStart = Calendar.getInstance.getTime
    // дата установки
    private var dateInstalled = null
    private var appHome: File = null
    private var tempDir: File = null
    private var configFile: File = null
    // для предотвращения дальнейших изменений
    private var locked = false
    private var debug = true
    private var config = new Properties() // загруженная конфигурация
    private var externalUrlPrefix: String = null // полный внешний префикс для ссылок

    // ex. : http://some.site.com/app
    // используется когда система стоит за
    // прокси-сервером и не знает о
    // своем внешнем имени
    private var appCode: String = null // кодовое обозначение системы

    def getAppCode = appCode

    def setAppCode(appCode: String): Unit = {
      checkLock()
      this.appCode = appCode
    }

    def getExternalUrlPrefix = externalUrlPrefix

    def setExternalUrlPrefix(prefix: String): Unit = {
      checkLock()
      this.externalUrlPrefix = prefix
    }

    def isDebug = debug

    def setDebug(debug: Boolean): Unit = {
      checkLock()
      this.debug = debug
    }

    def getSetting(setting: String, defaultValue: String): String = {
      if (!(config.containsKey(setting))) {
        return defaultValue
      }
      config.getProperty(setting)
    }

    def getSettingAsBoolean(setting: String, defaultValue: Boolean): Boolean = {
      if (!(config.containsKey(setting))) {
        return defaultValue
      }
      java.lang.Boolean.valueOf(config.getProperty(setting))
    }

    def getSettingAsInt(setting: String, defaultValue: Int): Int = {
      if (!(config.containsKey(setting))) {
        return defaultValue
      }
      Integer.valueOf(config.getProperty(setting))
    }

    def getConfig = config

    def setConfig(config: Nothing): Unit = {
      checkLock()
      this.config = config
    }

    def getConfigFile = configFile

    def setConfigFile(configFile: File): Unit = {
      checkLock()
      this.configFile = configFile
    }

    def getAppHome = appHome

    def setAppHome(appHome: File): SystemInfo = {
      checkLock()
      this.appHome = appHome
      this
    }

    def getTempDir = tempDir

    def setTempDir(tempDir: File): SystemInfo = {
      checkLock()
      this.tempDir = tempDir
      this
    }

    def getRuntimeVersion = runtimeVersion

    def setRuntimeVersion(runtimeVersion: AppVersion): SystemInfo = {
      checkLock()
      this.runtimeVersion = runtimeVersion
      this
    }

    def getDateStart = dateStart

    def isLocked = locked

    def lock(): Unit = {
      this.locked = true
    }

    private def checkLock(): Unit = {
      if (locked) {
        //  throw SystemError.withCode(0x6002)
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
  class AppVersion extends Serializable {
    private var implVer: String = null
    private var implBuildNum: String = null
    private var implBuildTime: String = null
    private var implVersionFull: String = null // full version string
    private var buildDate: Date = null
    private var gitState: GitRepositoryState = null

    def isGitState: Boolean = gitState != null

    def getGitState: GitRepositoryState = gitState

    def fillGitState(p: Properties): AppVersion = {
      gitState = new GitRepositoryState(p)
      this
    }

    def fillFromProperties(p: Properties): AppVersion = {
      implVer = p.getProperty("build.version", UNDEFINED)
      implBuildTime = p.getProperty("build.time", UNDEFINED)
      implBuildNum = p.getProperty("build.number", UNDEFINED)
      try buildDate = MAVEN_TS_FORMAT.parse(implBuildTime)
      catch {
        case ex: ParseException =>
        // implBuildNum = UNDEFINED;
        //throw new RuntimeException(ex);
      }
      implVersionFull = implVer + "." + implBuildNum
      this
    }

    def getBuildDate = buildDate

    def getImplVersion = implVer

    def getImplBuildNum = implBuildNum

    def getImplBuildTime = implBuildTime

    def getImplVersionFull = implVersionFull

    def getFull = implVersionFull
  }

  /**
   * Класс содержит поля детальной информации о git-репозитории
   */
  class GitRepositoryState(val properties: Properties) {
    val tags = properties.get("git.tags")
    val branch = properties.get("git.branch")
    val remoteOriginUrl = properties.get("git.remote.origin.url")
    val commitId = properties.get("git.commit.id")

    val commitIdAbbrev = properties.get("git.commit.id.abbrev")
    val describe = properties.get("git.commit.id.describe")
    val describeShort = properties.get("git.commit.id.describe-short")
    val commitUserName = properties.get("git.commit.user.name")
    val commitUserEmail = properties.get("git.commit.user.email")
    val commitMessageFull = properties.get("git.commit.message.full")
    val commitMessageShort = properties.get("git.commit.message.short")
    val commitTime = properties.get("git.commit.time")
    val buildUserName = properties.get("git.build.user.name")
    val buildUserEmail = properties.get("git.build.user.email")
    val buildTime = properties.get("git.build.time")


    def getTags = tags

    def getBranch = branch

    def getRemoteOriginUrl = remoteOriginUrl

    def getCommitId = commitId

    def getCommitIdAbbrev = commitIdAbbrev

    def getDescribe = describe

    def getDescribeShort = describeShort

    def getCommitUserName = commitUserName

    def getCommitUserEmail = commitUserEmail

    def getCommitMessageFull = commitMessageFull

    def getCommitMessageShort = commitMessageShort

    def getCommitTime = commitTime

    def getBuildUserName = buildUserName

    def getBuildUserEmail = buildUserEmail

    def getBuildTime = buildTime
  }


}


