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

package uber.paste.mvc

import uber.paste.base.plugins.PluginUI
import uber.paste.base.{ Loggered, SystemInfo }
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.web.bind.annotation.ExceptionHandler
import uber.paste.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import javax.annotation.Resource
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.Locale
import scala.collection.JavaConversions._
import uber.paste.manager.UserManager

import org.springframework.web.bind.annotation._

object LocaleConstants {

  val availableLocales = Array(
    Locale.US,
    new Locale("ru", "RU")
  ).toList
}

abstract class AbstractController extends Loggered {

  protected val page404 = "/error/404"

  protected val page403 = "/error/403"

  protected val page500 = "/error/500"

  @Resource(name = "messageSource")
  protected val messageSource: MessageSource = null

  @Autowired
  protected val systemInfo: SystemInfo = null
  
  @Value("${config.comments.allow-anonymous.create}")
  val allowAnonymousCommentsCreate: Boolean = false

  @Value("${config.paste.allow-anonymous.create}")
  val allowAnonymousPasteCreate: Boolean = false

  @Value("${paste.app.id}")
  val appId:String = null
  
  @ModelAttribute("allowAnonymousCommentsCreate")
  @JsonIgnore
  def isAllowAnonymousCommentsCreate() = allowAnonymousCommentsCreate

  @ModelAttribute("allowAnonymousPasteCreate")
  @JsonIgnore
  def isAllowAnonymousPasteCreate() = allowAnonymousPasteCreate

  protected def getResource(key: String, locale: Locale): String =
    messageSource.getMessage(key, new Array[java.lang.Object](0), locale)

  protected def getResource(key: String, args: Array[Any], locale: Locale): String =
    messageSource.getMessage(key, args.asInstanceOf[Array[java.lang.Object]], locale)

  @ExceptionHandler(Array(classOf[Throwable]))
  protected def handleAllExceptions(ex: ObjectRetrievalFailureException): String = {
    logger.error(ex.getLocalizedMessage(), ex)
    return page500
  }

  @ModelAttribute("pluginUI")
  def getPluginUI() = PluginUI.getInstance

  @ModelAttribute("appId")
  def getAppId() = appId

  @ModelAttribute("systemInfo")
  def getSystemInfo() = systemInfo

  @ModelAttribute("availableLocales")
  def getAvailableLocales(): java.util.List[Locale] = LocaleConstants.availableLocales

  @JsonIgnore
  @ModelAttribute("currentUser")
  def getCurrentUser() = UserManager.getCurrentUser

  @JsonIgnore
  def isCurrentUserLoggedIn() = UserManager.getCurrentUser != null

  @JsonIgnore
  def isCurrentUserAdmin(): Boolean = {
    val u: User = UserManager.getCurrentUser
    return u != null && u.isAdmin()
  }

}
