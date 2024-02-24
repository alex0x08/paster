package com.Ox08.paster.webapp.manager
import com.Ox08.paster.webapp.base.Boot.BOOT
import com.Ox08.paster.webapp.base.{Logged, SystemError, SystemMessage}
import com.Ox08.paster.webapp.manager.SettingsManager.KEY_SECTION
import jakarta.validation.constraints.NotNull
import org.apache.commons.configuration2.ex.ConfigurationException
import org.apache.commons.configuration2.{PropertiesConfiguration, PropertiesConfigurationLayout}
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import java.io._
import java.nio.file.{Files, StandardCopyOption}
import java.util
import java.util.UUID
import scala.jdk.CollectionConverters._
object SettingsManager {
  val KEY_SECTION = "--section--"
}
@Service
class SettingsManager extends Logged {
  /**
   * Сохранить настройки
   *
   * @param settings набор обновленных настроек
   */
  def saveConfigurableSettings(settings: util.List[_ <: SettingsDTO]): Unit = {
    val config = new PropertiesConfiguration
    val layout = new PropertiesConfigurationLayout
    // загрузка текущих настроек
    var input:InputStream = null
    try {
      input = new FileInputStream(BOOT.getSystemInfo.getConfigFile)
      layout.load(config, new InputStreamReader(input))
      }catch {
        case e@(_: IOException | _: ConfigurationException) =>
          throw SystemError.withError(0x7010, e)
      } finally if (input != null) input.close()
    
    for (s <- settings.asScala) {
      logger.debug(SystemMessage.of("paster.system.settings.updatingKey", s.getKey, s.getValue))
      if (s.isDeleted) {
        config.clearProperty(s.getKey)
        logger.debug(SystemMessage.of("paster.system.settings.deletedKey", s.getKey, s.getValue))
      } else {
        if (syncDTO(s)) {
          if (s.isSaved) {
            config.setProperty(s.getKey, s.getValue)
            logger.debug(SystemMessage.of("paster.system.settings.updatedKey", s.getKey, s.getValue))
          }
          else {
            config.addProperty(s.getKey, s.getValue)
            layout.setComment(s.getKey, null)
            logger.debug(SystemMessage.of("paster.system.settings.addedKey", s.getKey, s.getValue))
          }
        }
      }
    }
    // 3х шаговая запись обновленных настроек
    // временный файл
    val fnew = new File(BOOT.getSystemInfo.getAppHome,
      "config.properties" + "_" + System.currentTimeMillis)
    // основной файл
    val fconf = BOOT.getSystemInfo.getConfigFile
    var w: Writer = null
    try {
        w = new FileWriter(fnew)
        // шаг 1. запись во временный файл
        layout.save(config, w)
        // шаг 2. создание бекапа текущей конфигурации
        if (fconf.exists) {
          val backup = new File(BOOT.getSystemInfo.getAppHome, fconf.getName + ".backup")
          Files.copy(fconf.toPath, backup.toPath, StandardCopyOption.REPLACE_EXISTING)
        }
        // шаг 3. копирование с заменой временного файла в основной
        Files.copy(fnew.toPath, fconf.toPath, StandardCopyOption.REPLACE_EXISTING)
      } catch {
        case e@(_: IOException | _: ConfigurationException) =>
          throw SystemError.withError(0x7009, e)
      } finally if (w != null) w.close()    
  }
  /**
   * получение текущих настроек
   *
   * @return список dto с текущими настройками
   */
  def getConfigurableSettings: util.List[SettingsDTO] = {
    val config: PropertiesConfiguration = new PropertiesConfiguration
    val layout: PropertiesConfigurationLayout = new PropertiesConfigurationLayout
    // чтение текущих настроек из файла
    var input: InputStream = null
    try {
      input = new FileInputStream(BOOT.getSystemInfo.getConfigFile)
      layout.load(config, new InputStreamReader(input))
      val out: util.List[SettingsDTO] = new util.ArrayList[SettingsDTO]
      // чтение конфигурационного файла и формирование списка полей настроек
      for (key <- layout.getKeys.asScala) {
        val comment: String = StringEscapeUtils.unescapeJava(layout.getCanonicalComment(key, false))
        parseAndSetValueFromConfig(out, key, config.getString(key), comment)
      }
      out
    } catch {
      case e@(_: IOException | _: ConfigurationException) =>
        throw SystemError.withError(0x7010, e)
    } finally if (input != null) input.close()
  }
  /**
   * необходимо ли синхронизировать данное поле
   *
   * @param s dto с настройками
   * @return true - необходимо, false - нет
   */
  private def syncDTO(s: SettingsDTO): Boolean = {
    // если нет ключа - не сохраняем, пропускаем
    if (StringUtils.isBlank(s.getKey)) return false
    // если поле является секцией
    if (s.getKey.startsWith(KEY_SECTION)) return false
    // если поле булевое
    if (SettingType.BOOLEAN.toString eq s.getType) s.setValue(String.valueOf(s.getBoolValue))
    true
  }
  /**
   * разбор строки из конфигурационного файла
   *
   */
  private def parseAndSetValueFromConfig(out: util.List[SettingsDTO],
                                         key: String, value: String, comment: String): Unit = {
    var ccomment = comment
    // определение наличия комментария
    if (!StringUtils.isBlank(comment) && comment.startsWith(KEY_SECTION)) {
      val sectionComment = StringUtils.substringBetween(comment, KEY_SECTION, "\n")
      val s = new SettingsDTO
      s.setSaved(true)
      s.setKey(KEY_SECTION)
      s.setComment(sectionComment)
      s.setType(SettingType.SECTION.toString)
      out.add(s)
      ccomment = StringUtils.substringAfter(comment, sectionComment)
    }
    val s = new SettingsDTO
    s.setSaved(true)
    s.setKey(key)
    s.setValue(value)
    s.setType(SettingType.STRING.toString)
    s.setComment(ccomment)
    // если есть значение
    if (!StringUtils.isBlank(s.getValue)) {
      // проверки для булевого типа
      if ("true".equalsIgnoreCase(s.getValue)) {
        s.setBoolValue(true)
        s.setType(SettingType.BOOLEAN.toString)
      }
      else if ("false".equalsIgnoreCase(s.getValue)) {
        s.setBoolValue(false)
        s.setType(SettingType.BOOLEAN.toString)
      } else {
        // остальные типы
        val keyLow = s.getKey.toLowerCase
        if (keyLow.endsWith("secret") || keyLow.endsWith("pass"))
          s.setType(SettingType.PASSWORD.toString)
      }
    }
    logger.debug(SystemMessage.of("paster.system.settings.loadedKey",
      key, s.getValue, s.getComment, s.getType))
    out.add(s)
  }
}
  /**
   * тип секции
   */
  object SettingType extends Enumeration {
    type SettingType = Value
    val STRING, // строка
    BOOLEAN, //  DATE, // дата
    NUMBER, // число
    // булевый
    PASSWORD, // пароль
    SECTION // секция
    = Value
  }
  /**
   * DTO для хранения одного поля настройки
   */
  class SettingsDTO {
    final private val id = UUID.randomUUID.toString
    @NotNull private var key: String = _
    private var value: String = _
    private var comment: String = _
    private var boolValue = false
    // булевое значение
    private var saved = false
    // признак сохранено или нет
    private var deleted = false // признак что поле удалено
    private var ftype: String = _
    def getId: String = id
    def getBoolValue: Boolean = boolValue
    def isDeleted: Boolean = deleted
    def setDeleted(deleted: Boolean): Unit = {
      this.deleted = deleted
    }
    def isSaved: Boolean = saved
    def setSaved(saved: Boolean): Unit = {
      this.saved = saved
    }
    def getType: String = ftype
    def setType(ftype: String): Unit = {
      this.ftype = ftype
    }
    def getKey: String = key
    def setKey(key: String): Unit = {
      this.key = key
    }
    def getValue: String = value
    def setValue(value: String): Unit = {
      this.value = value
    }
    def isBoolValue: Boolean = boolValue
    def setBoolValue(boolValue: Boolean): Unit = {
      this.boolValue = boolValue
    }
    def getComment: String = comment
    def setComment(comment: String): Unit = {
      this.comment = comment
    }
  }

