/*
 * Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.mvc
import com.Ox08.paster.webapp.base.SystemMessage
import com.Ox08.paster.webapp.manager.{SettingType, SettingsDTO, SettingsManager}
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation._
import java.util
import scala.jdk.CollectionConverters._

@Controller
@RequestMapping(Array("/admin/settings"))
class SettingsCtl extends AbstractCtrl {
  @Autowired
  private val ss: SettingsManager = null

  // хранилище текущих настроек
  private val currentSettings = new util.LinkedHashMap[String, SettingsDTO]

  @RequestMapping(value=Array("/dbconsole"), method = Array(RequestMethod.GET))
  def dbconsole(): String = {
    "/admin/settings/dbconsole"
  }
  /**
   * обработка страницы 'settings'
   *
   */
  @GetMapping(Array("/settings"))
  def settings(model: Model): String = {
    val d = new UpdatedSettingsDTO // DTO для обновленных настроек
    if (currentSettings.isEmpty) {
      d.setSettings(ss.getConfigurableSettings)
      syncSettings(d.getSettings)
    }
    else d.setSettings(new util.ArrayList[SettingsDTO](currentSettings.values))
    model.addAttribute("configurableSettings", d)
   // model.addAttribute("restartRequired", rs.getProperty("restartRequired", classOf[Boolean]))
    "settings"
  }
  /**
   * Перечитать настройки из файла конфигурации
   *
   */
  @PostMapping(Array("/reloadSettings"))
  def reloadSettings(model: Model): String = {
    syncSettings(ss.getConfigurableSettings)
    "redirect:/settings"
  }
  /**
   * Удалить поле настройки Данный метод доступен только администратору
   * системы
   *
   * @param settingId uuid поля с настройкой
   */
  @PostMapping(Array("/removeSetting"))
  @Secured(Array("ROLE_ADMIN"))
  def removeSetting(model: Model, @RequestParam settingId: String): String = {
    if (currentSettings.containsKey(settingId)) {
      val setting = currentSettings.get(settingId)
      setting.setDeleted(true)
      synchronized {
        currentSettings.put(settingId, setting)
      }
      logger.debug(SystemMessage.of("paster.system.settings.fieldRemoved", setting.getKey))
    }
    "redirect:/settings"
  }
  /**
   * Добавить новое поле настройки Данный метод доступен только администратору
   *
   */
  @PostMapping(Array("/addSetting"))
  @Secured(Array("ROLE_ADMIN"))
  def addSetting(model: Model): String = {
    val newSetting = new SettingsDTO
    newSetting.setKey("new key")
    newSetting.setSaved(false)
    newSetting.setType(SettingType.STRING.toString)
    currentSettings.put(newSetting.getId, newSetting)
    "redirect:/settings"
  }
  @PostMapping(Array("/saveSettings"))
  @Secured(Array("ROLE_ADMIN"))
  def saveSettings(request: HttpServletRequest,
                   @Valid @ModelAttribute("configurableSettings") updatedSettings:UpdatedSettingsDTO,
                   result: BindingResult, model: Model): String = {
    // если есть ошибки валидации формы - не сохраняем, возвращаем на страницу
    if (result.hasErrors) {
      logger.warn(SystemMessage.of("paster.system.settings.formValidationErrorsFound",
        ""+result.getErrorCount))
      if (logger.isDebugEnabled)
        dumpErrors(result)
      return "settings"
    }
    logger.info(SystemMessage.of("paster.system.settings.totalSettings",
      ""+updatedSettings.getSettings.size))
    //сохраняем настройки
    ss.saveConfigurableSettings(updatedSettings.getSettings)
    // установка флага для сообщения об обязательной перезагрузки
   // rs.setProperty("restartRequired", true)
    syncSettings(ss.getConfigurableSettings)
    "redirect:/settings"
  }
  /**
   * Синхронизировать новые настройки с текущими
   *
   * @param newSettings DTO с новыми настройками
   */
  private def syncSettings(newSettings: util.List[SettingsDTO]): Unit = {
    synchronized {
      this.currentSettings.clear()
      for (s <- newSettings.asScala) {
        this.currentSettings.put(s.getId, s)
      }
    }
  }

  /**
   * DTO для передачи набора обновленных настроек
   */
  class UpdatedSettingsDTO {
    @Valid private var settings: util.List[SettingsDTO] = new util.ArrayList[SettingsDTO]
    def getSettings: util.List[SettingsDTO] = settings
    def setSettings(settings: util.List[SettingsDTO]): Unit = {
      this.settings = settings
    }
  }
}
