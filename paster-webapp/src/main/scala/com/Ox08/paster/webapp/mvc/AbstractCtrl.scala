/*
 * Copyright 2011 Ubersoft, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.mvc
import com.Ox08.paster.webapp.base.Boot.BOOT
import com.Ox08.paster.webapp.base.{Boot, Logged}
import com.Ox08.paster.webapp.manager.UserManager
import com.Ox08.paster.webapp.model.PasterUser
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.MessageSource
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.web.bind.annotation._
import java.util.Locale
object MvcConstants {
  val page404 = "/error/404"
  val page403 = "/error/403"
  val page500 = "/error/500"
  final val NODE_LIST_MODEL = "items"
  final val NODE_LIST_MODEL_PAGE = "pageItems"
  final val MODEL_KEY = "model"
}
abstract class AbstractCtrl extends Logged {
  @Autowired
  protected val messageSource: MessageSource = null
  protected val systemInfo: BOOT.SystemInfo = Boot.BOOT.getSystemInfo
  @Value("${paster.security.comments.allow-anonymous.create}")
  val allowAnonymousCommentsCreate: Boolean = false
  @Value("${paster.security.pastas.allow-anonymous.create}")
  val allowAnonymousPasteCreate: Boolean = false
  @Value("${paste.app.id}")
  val appId: String = null
  @ModelAttribute("allowAnonymousCommentsCreate")
  @JsonIgnore
  def isAllowAnonymousCommentsCreate: Boolean = allowAnonymousCommentsCreate
  @ModelAttribute("allowAnonymousPasteCreate")
  @JsonIgnore
  def isAllowAnonymousPasteCreate: Boolean = allowAnonymousPasteCreate
  protected def getResource(key: String, locale: Locale): String =
    messageSource.getMessage(key, new Array[java.lang.Object](0), locale)
  protected def getResource(key: String, args: Array[Any], locale: Locale): String =
    messageSource.getMessage(key, args.asInstanceOf[Array[java.lang.Object]], locale)
  @ExceptionHandler(Array(classOf[Throwable]))
  protected def handleAllExceptions(ex: ObjectRetrievalFailureException): String = {
    logger.error(ex.getLocalizedMessage, ex)
    MvcConstants.page500
  }
  @ModelAttribute("appId")
  def getAppId: String = appId
  @ModelAttribute("systemInfo")
  def getSystemInfo: BOOT.SystemInfo = systemInfo
  @ModelAttribute("availableLocales")
  def getAvailableLocales: Array[Locale] = Array(
    Locale.US,
    Locale.forLanguageTag("ru_RU")
  )
  @JsonIgnore
  @ModelAttribute("currentUser")
  def getCurrentUser: PasterUser = UserManager.getCurrentUser
  @JsonIgnore
  def isCurrentUserLoggedIn: Boolean = UserManager.getCurrentUser != null
  @JsonIgnore
  def isCurrentUserAdmin: Boolean = {
    val u: PasterUser = UserManager.getCurrentUser
    u != null && u.isAdmin
  }
}
