package com.Ox08.paster.webapp.base

import org.slf4j.helpers.MessageFormatter
import org.springframework.util.Assert

import java.lang.reflect.InvocationTargetException
import scala.jdk.CollectionConverters._
import java.util
import java.util.{Locale, ResourceBundle}
import scala.collection.mutable
import scala.collection.mutable.WrappedArray


/**
 * Custom storage for i18n bundles
 * Constructor should be called from children class
 *
 * @author Alex Chernyshev <alex3.145@gmail.com>
 * @since 1.0
 */
abstract class AbstractI18nMessageStore protected( // default bundle name
                                                   val defaultBundleName: String) extends Loggered {
  //reloadMessages()
  // bundle locale
  protected var messageLocale: Locale = Locale.getDefault()
  // additional bundles, loaded from plugins
  final private val additionalBundles = new util.ArrayList[ResourceBundle]
  // default bundle
  protected var defaultBundle: ResourceBundle = null

  // reload default bundle
  final def reloadMessages(): Unit = {
    System.out.println("__:" +messageLocale)
    this.defaultBundle = ResourceBundle.getBundle(this.defaultBundleName, messageLocale)
  }

  // add & load additional bundle
  def addBundle(name: String): Unit = {
    this.addBundle(ResourceBundle.getBundle(name, messageLocale))
  }

  def addBundle(r: ResourceBundle): Unit = {
    Assert.notNull(r,"bundle should be non null")
    this.additionalBundles.add(r)
  }

  def getErrorLocale: Locale = messageLocale

  def setErrorLocale(locale: Locale): Unit = {
    Assert.notNull(locale,"locale should be non null")
    this.messageLocale = locale
    this.reloadMessages()
  }

  protected def formatMessage(raw: String, args: Any*): String =
    MessageFormatter.arrayFormat(raw, mutable.ArraySeq(args).toArray).getMessage

  /**
   * get formatted text by key, with lookup in additional bundles
   *
   * @param key
   * @return
   */
  protected def getMessage(key: String): String = {
    Assert.notNull(key,"key should be non null")
    if (this.defaultBundle.containsKey(key)) return this.defaultBundle.getString(key)
    for (r <- this.additionalBundles.asScala) {
      if (r.containsKey(key)) return r.getString(key)
    }
    null
  }
}

  class PasterRuntimeException(code: Int, // error code ( ex. 0x06001 )
                               message: String, parent: Exception) extends  RuntimeException(message, parent) {

    updateTrace(parent)

    def updateTrace(parent: Exception): Unit = {
      if (parent != null) setStackTrace(parent.getStackTrace)
    }

    def getCode: Int = code
  }



/**
 * class represents System Error with localized messages and exception handling
 *
 * @author Alex Chernyshev <alex3.145@gmail.com>
 * @since 1.0
 */
class SystemError extends AbstractI18nMessageStore("bundles/errorMessages") {

  private def createException[T <: PasterRuntimeException](clazz: Class[T], code: Int, params: Any*)
  = createExceptionImpl(clazz, code, null, null, params)

  private def createException[T <: PasterRuntimeException](clazz: Class[T], code: Int, message: String, params: Any*)
  = createExceptionImpl(clazz, code, message, null, params)

  /**
   * создать объект исключения со сформированным сообщением об ошибке
   *
   * @param <       T> тип возвращаемоего исключения
   * @param clazz   класс исключения
   * @param code    код ошибки
   * @param message доп. сообщение
   * @param parent  родительское исключение
   * @param params  параметры
   * @return сформированное исключение
   */
  private def createExceptionImpl[T <: PasterRuntimeException](clazz: Class[T], code: Int, message: String,
                                                               parent: Exception, params: Any*) = {
    val errorMsg = getErrorMessage(code, message, parent, true, params)
    try clazz.getConstructor(classOf[Int], classOf[String], classOf[Exception]).newInstance(code, errorMsg, parent)
    catch {
      case ex@(_: NoSuchMethodException | _: SecurityException | _: InstantiationException |
               _: IllegalAccessException |
               _: IllegalArgumentException |
               _: InvocationTargetException) =>
        throw new RuntimeException(ex)
    }
  }

  /**
   * сформировать сообщение об ошибке
   *
   * @param code    код ошибки
   * @param message дополнительное сообщение
   * @param parent  исключение
   * @param prefix  использовать ли префикс с кодом ошибки
   * @param params  дополнительные параметры ( для подстановки в шаблон )
   * @return
   */
  private def getErrorMessage(code: Int, message: String, parent: Exception, prefix: Boolean, params: Any*) = {
    // пытаемся найти текст ошибки по коду
    var errorMsg = getMessage("proxyman.system.error." + String.format("0x%x", code))
    var ccode = code
    // если не нашли - формируем 'неизвестную ошибку'
    if (errorMsg == null) {
      errorMsg = getMessage("proxyman.system.error.0x6000")
      ccode = 0x6000
    }
    // если нет доп. сообщения
    if (message == null) { // если нет исключения
      if (parent == null) { // формируем выходное сообщение, подставляем в шаблон переданные параметры
        errorMsg = formatMessage(errorMsg, params)
      }
      else { // если есть исключение - добавляем в набор параметров
        // сообщение об ошибке первым аргументом
        val pparams = prepareParams(getMessage(parent), params)
        // формируем выходное сообщение
        errorMsg = formatMessage(errorMsg, pparams)
      }
    }
    else { // если есть доп. сообщение
      if (parent == null) { // формируем выходное сообщение, используем доп. сообщение как шаблон
        errorMsg = formatMessage(message, params)
      }
      else {
        val pparams = prepareParams(getMessage(parent), params)
        errorMsg = formatMessage(message, pparams)
      }
    }
    // если используется префикс
    if (prefix) errorMsg = "[" + String.format("0x%x", ccode) + "] " + errorMsg
    errorMsg
  }

  /**
   * добавление указанного сообщения первым аргументом в указанном массиве
   * параметров. все остальные параметры сдвигаются влево
   *
   * @param params  массив параметров
   * @param message добаляемое сообщение
   * @return новый массив параметров
   */
  private def prepareParams(message: String, params: Any*) = {
    if (params.length > 0) {
      val pparams = new Array[AnyRef](params.length + 1)
      pparams(0) = message
      System.arraycopy(params, 0, pparams, 1, params.length)
      pparams
    }
    else
      Array[AnyRef](message)
  }

  /**
   * Get a detail message from an IOException. Most, but not all, instances of
   * IOException provide a non-null result for getLocalizedMessage(). But some
   * instances return null: in these cases, fallover to getMessage(), and if
   * even that is null, return the name of the exception itself.
   *
   * @param e an IOException
   * @return a string to include in a compiler diagnostic
   */
  private def getMessage(e: Exception): String = {
    var s = e.getLocalizedMessage
    if (s != null) return s
    s = e.getMessage
    if (s != null) return s
    e.toString
  }

}

object SystemError { // синглтон

    // синглтон класса
    private val INSTANCE = new SystemError

    def instance: SystemError = INSTANCE

  /**
   * получить сформированное сообщение об ошибке
   *
   * @param code   код ошибки
   * @param params параметры
   * @return сформированное сообщение
   */
  def messageFor(code: Int, params: Any*): String = INSTANCE.getErrorMessage(code, null, null, true, params)

  /**
   * получить сформированное сообщение об ошибке * с настройкой префикса
   *
   * @param code   код ошибки
   * @param prefix использовать ли префикс - true/false
   * @param params параметры
   * @return сформированное сообщение
   */
  def messageForPrefix(code: Int, prefix: Boolean, params: Any*): String = INSTANCE.getErrorMessage(code, null, null, prefix, params)

  /**
   * получить сформированное исключение для ошибки
   *
   * @param code   код ошибки
   * @param params параметры
   * @return сформированное исключение
   */
  def withCode(code: Int, params: Any*): PasterRuntimeException = INSTANCE.createException(classOf[PasterRuntimeException], code, params)

  def withMessage(code: Int, message: String, params: Any*): PasterRuntimeException = INSTANCE.createException(classOf[PasterRuntimeException], code, message, params)

  def withError(code: Int, parent: Exception, params: Any*): PasterRuntimeException = INSTANCE.createExceptionImpl(classOf[PasterRuntimeException], code, null, parent, params)

  def withError(code: Int, params: Any*): PasterRuntimeException = INSTANCE.createExceptionImpl(classOf[PasterRuntimeException], code, null, null, params)
}


/**
 * Системное сообщение шлюза
 *
 * @author Alex Chernyshev <alex3.145@gmail.com>
 * @since 3.0
 */
object SystemMessage { // синглтон
  private val INSTANCE = new SystemMessage

  def of(template: String, params: Any*): String = INSTANCE.createMessage(template, params)

  def instance: SystemMessage = INSTANCE
}

class SystemMessage private() // приватный конструктор
  extends AbstractI18nMessageStore("bundles/systemMessages") {
  /**
   * сформировать сообщение для ключа из бандла с подстановкой параметров
   *
   * @param key    ключ в бандле
   * @param params параметры
   * @return сформированное сообщние
   */
  private def createMessage(key: String, params: Any*) = {
    val raw = getMessage(key)
    if (raw == null) key
    else formatMessage(raw, params)
  }
}