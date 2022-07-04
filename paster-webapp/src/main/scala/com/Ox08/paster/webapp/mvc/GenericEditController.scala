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

import com.Ox08.paster.webapp.model.Struct
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation._

import java.util.Locale
import org.springframework.web.servlet.mvc.support.RedirectAttributes

object GenericEditController {

  final val EDIT_ACTION = "/edit"
  final val VIEW_ACTION = "/view"
  final val SAVE_ACTION = "/save"
  final val NEW_ACTION = "/new"
  final val DELETE_ACTION = "/delete"

}

abstract class GenericEditController[T <: Struct] extends GenericController[T] {


  protected def getNewModelInstance(): T

  protected def fillEditModel(obj: T, model: Model, locale: Locale): Unit = {
    model.addAttribute(GenericController.MODEL_KEY, obj)
  }

  def loadModel(id: java.lang.Long) = manager().getFull(id)


  @RequestMapping(value = Array(GenericEditController.NEW_ACTION), method = Array(RequestMethod.GET))
  @ResponseStatus(HttpStatus.CREATED)
  def createNew(model: Model, locale: Locale): String = {

    fillEditModel(getNewModelInstance(), model, locale)

    editPage
  }


  @RequestMapping(value = Array("/edit/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  def editWithId(model: Model, @PathVariable("id") id: Long, locale: Locale): String = {

    if (id == 0) {
      return page404
    }

    fillEditModel(loadModel(id), model, locale)

    editPage
  }


  @RequestMapping(value = Array(GenericEditController.SAVE_ACTION), method = Array(RequestMethod.POST))
  def save(@RequestParam(required = false) cancel: String,
           @Valid @ModelAttribute(GenericController.MODEL_KEY) b: T,
           result: BindingResult, model: Model, locale: Locale,
           redirectAttributes: RedirectAttributes): String = {

    if (cancel != null) {
      redirectAttributes.addFlashAttribute("statusMessageKey", "action.cancelled")
      return listPage
    }

    if (result.hasErrors()) {
      logger.debug("form has errors {}", result.getErrorCount())

      fillEditModel(b, model, locale)
      return editPage
    }


    val r: T = manager().save(b)

    // set id from create
    if (b.isBlank()) {
      b.setId(r.getId())
    }

    redirectAttributes.addFlashAttribute("statusMessageKey", "action.success")

    listPage
  }


  @RequestMapping(value = Array(GenericEditController.DELETE_ACTION),
    method = Array(RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE))
  def delete(@RequestParam(required = false) id: Long,
             model: Model): String = {
    manager().remove(id);
    listPage
  }


  @RequestMapping(value = Array("/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  def getByPath(@PathVariable("id") id: java.lang.Long, model: Model, locale: Locale): String = {

    val m = loadModel(id)

    if (m == null) {
      return page404
    }

    model.addAttribute(GenericController.MODEL_KEY, m)

    fillEditModel(m, model, locale)

    viewPage
  }


}
