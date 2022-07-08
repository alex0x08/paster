/**
 * Copyright (C) 2010 alex <me@alex.0x08.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.base
import ch.qos.logback.classic
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggerContextListener
import ch.qos.logback.core.spi.{ContextAwareBase, LifeCycle}
import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.commons.lang3.builder.{ReflectionToStringBuilder, StandardToStringStyle, ToStringBuilder}
import org.slf4j.{Logger, LoggerFactory}
import java.lang.reflect.Field
/**
 *  A common logger class, with shared logic related to logging and loggers
 *
 *  @author Alex Chernyshev <alex3.145@gmail.com>
 *  @since 1.0
 */
object Logged {
  /**
   * predefined logging style for key-value pairs
   */
  val style: StandardToStringStyle = new StandardToStringStyle() {
    setFieldSeparator(", ")
    setUseClassName(false)
    setUseIdentityHashCode(false)
    setArraySeparator(":")
    setArrayEnd(" ")
    setArrayStart(" ")
    setContentStart(" ")
    setContentEnd(" ")
    setFieldNameValueSeparator(": ")
    override def append(buffer: java.lang.StringBuffer,
                        fieldName: String,
                        value: Any,
                        fullDetail: java.lang.Boolean): Unit = {
      if (value != null) {
        super.append(buffer, fieldName, value, fullDetail)
      }
    }
  }
  def toStringSkip(x: Any, fields: Array[String]): String = {
    new ReflectionToStringBuilder(x, style) {
      override def accept(f: Field): Boolean = {
        if (!super.accept(f)) {
          return false
        }
        if (fields == null) {
          return true
        }
        for (field <- fields) {
          if (f.getName.equals(field)) return false
        }
        true
      }
    }.toString
  }
  def getNewProtocolBuilder(clazz: AnyRef): ToStringBuilder =
    new ToStringBuilder(clazz.getClass.getName, Logged.style)
  def getLogger(clazz: AnyRef): Logger = LoggerFactory.getLogger(clazz.getClass)
}
trait Logged {
  @transient
  // @WebMethod(exclude = true)
  @JsonIgnore
  def logger: Logger = LoggerFactory.getLogger(getClass.getName)
  @transient
  // @WebMethod(exclude = true)
  @JsonIgnore
  def getNewProtocolBuilder: ToStringBuilder = new ToStringBuilder(this, Logged.style)
}
/**
 * An Logback listener, used to pass 'appDebug' variable from environment to logback configs
 */
class LoggerStartupListener extends ContextAwareBase with
  LoggerContextListener with LifeCycle {
  import ch.qos.logback.classic.LoggerContext
  import org.springframework.util.Assert
  private var started = false
  def start(): Unit = {
    if (started) return
    val isDebug = System.getProperty("appDebug", "false")
    Assert.notNull(isDebug, "appDebug is empty!")
    // проверка на значение
    val debug = java.lang.Boolean.valueOf(isDebug)
    val c = getContext
    c.putProperty("appDebug", debug.toString)
    started = true
  }
  def stop(): Unit = {
  }
  def isStarted: Boolean = started
  def isResetResistant = true
  def onStart(context: LoggerContext): Unit = {
  }
  def onReset(context: LoggerContext): Unit = {
  }
  def onStop(context: LoggerContext): Unit = {
  }
  override def onLevelChange(logger: classic.Logger, level: Level): Unit = {
  }
}