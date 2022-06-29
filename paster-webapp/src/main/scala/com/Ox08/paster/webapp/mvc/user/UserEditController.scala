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

package com.Ox08.paster.webapp.mvc.user

import com.Ox08.paster.webapp.dao.UserDaoImpl
import com.Ox08.paster.webapp.model.{Role, RoleEditor, User}
import com.Ox08.paster.webapp.mvc.{GenericController, GenericEditController}
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping

import javax.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.InitBinder

import java.util.Locale
import scala.jdk.CollectionConverters._

@Controller
@RequestMapping(Array("/user"))
class UserEditController extends GenericEditController[User] {

  @Autowired
  private val userManager: UserDaoImpl = null

  override def listPage = "redirect:/main/user/list"
  override def editPage = "/user/edit"
  override def viewPage = "/user/view"

  def manager(): UserDaoImpl = userManager

  @InitBinder
  def initBinder(binder: WebDataBinder): Unit = {
    binder.initDirectFieldAccess()
    binder.registerCustomEditor(classOf[Role], new RoleEditor())
  }

  override def fillEditModel(obj: User, model: Model, locale: Locale) {
    obj.setPassword(null)
    super.fillEditModel(obj, model, locale)
    model.addAttribute("availableRoles", Role.list)
  }

  @RequestMapping(value = Array(GenericEditController.SAVE_ACTION),
    method = Array(RequestMethod.POST))
  override def save(@RequestParam(required = false) cancel: String,
                    @Valid @ModelAttribute(GenericController.MODEL_KEY) b: User,
                    result: BindingResult, model: Model, locale: Locale,
                    redirectAttributes: RedirectAttributes): String = {

    if (!isCurrentUserLoggedIn() || !isCurrentUserAdmin()) {
      logger.warn("user {} is not allowed to save users", getCurrentUser())
      return page403
    }

    if (cancel != null) {
      redirectAttributes.addFlashAttribute("statusMessageKey", "action.cancelled")
      return listPage
    }

    if (result.hasErrors()) {
      for (f <- result.getFieldErrors().asScala) {
        if (!f.getField().equals("username") && !f.getField().equals("password")) {
          logger.debug("field={} ,rejected value= {} ,message= {} ",
            f.getField(),
            f.getRejectedValue(),
            f.getDefaultMessage())

          fillEditModel(b, model, locale)
          return editPage
        }
      }
    }

    if (b.isBlank && b.isPasswordEmpty) {
      result.rejectValue("password", "error.password.required")
      fillEditModel(b, model, locale)
      return editPage
    }

    if (!b.isPasswordEmpty() && !b.getPassword.equals(b.getPasswordRepeat)) {
      result.rejectValue("password", "error.password.missmatch")
      fillEditModel(b, model, locale)
      return editPage
    }

    if (!b.isBlank) {
      val old = manager.getFull(b.getId)
      if (!old.isRemoteUser) {
        old.setEmail(b.getEmail)
        old.setName(b.getName)
        old.setRoles(b.getRoles)

      }


      old.setDisabled(b.isDisabled)

      if (!b.isPasswordEmpty() && b.getPassword.equals(b.getPasswordRepeat)) {
        old.setPassword(b.getPassword)
      }

      manager.save(old)
    } else {
      manager.save(b)
    }

    redirectAttributes.addFlashAttribute("statusMessageKey", "action.success")

    listPage
  }


  @RequestMapping(value = Array(GenericEditController.DELETE_ACTION),
    method = Array(RequestMethod.GET, RequestMethod.POST))
  override def delete(@RequestParam(required = false) id: Long,
                      model: Model): String = {
    if (!isCurrentUserLoggedIn() || !isCurrentUserAdmin()) return page403
    super.delete(id, model)
  }

  def getNewModelInstance(): User = new User("Unnamed User")
}
