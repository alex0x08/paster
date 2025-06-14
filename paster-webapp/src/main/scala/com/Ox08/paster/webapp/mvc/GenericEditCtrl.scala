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
import com.Ox08.paster.webapp.dao.StructDaoImpl
import com.Ox08.paster.webapp.model.Struct
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation._
import java.util.Locale
import org.springframework.web.servlet.mvc.support.RedirectAttributes
/**
 * Abstract generic controller for edit/view actions
 *
 * @tparam T
 *      model type
 */
abstract class GenericEditCtrl[T <: Struct] extends AbstractCtrl {
  /**
   * abstract method to instantiate new model object
   * @return
   */
  protected def getNewModelInstance: T
  /**
   * link to list page
   */
  protected def listPage: String
  /**
   * edit page
   */
  protected def editPage: String
  /**
   * link to view page for selected model
   * @return
   */
  protected def viewPage: String
  /**
   * assigned DAO service
   */
  protected def manager(): StructDaoImpl[T]
  /**
   * Fills page model, called before rendering view
   * @param obj
   *      model instance
   * @param model
   *     page model (Spring MVC)
   * @param locale
   *    request context locale
   */
  protected def fillEditModel(obj: T, model: Model, locale: Locale): Unit = {
    model.addAttribute(MvcConstants.MODEL_KEY, obj)
  }
  /**
   * loads model entity from DAO
   * @param id
   *    model id
   * @return
   *    loaded entity object
   */
  protected def loadModel(id: Integer): T = manager().getFull(id)
  /**
   * 'Create new' feature
   * Instantiates new model object and returns edit page, assigned to this model
   * in child controller
   * @param model
   *      page model
   * @param locale
   *      request context locale
   * @return
   */
  @RequestMapping(value = Array("/new"), method = Array(RequestMethod.GET))
  @ResponseStatus(HttpStatus.CREATED)
  def createNew(model: Model, locale: Locale): String = {
    fillEditModel(getNewModelInstance, model, locale)
    editPage
  }
  @RequestMapping(value = Array("/edit/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  def editWithId(model: Model, @PathVariable("id") id: Integer, locale: Locale): String = {
    if (id == 0)
      return MvcConstants.page404
    fillEditModel(loadModel(id), model, locale)
    editPage
  }
  @RequestMapping(value = Array("/save"), method = Array(RequestMethod.POST))
  def save(@RequestParam(required = false) cancel: String,
           @Valid @ModelAttribute(MvcConstants.MODEL_KEY) b: T,
           result: BindingResult, model: Model, locale: Locale,
           redirectAttributes: RedirectAttributes): String = {
    if (cancel != null) {
      redirectAttributes.addFlashAttribute("statusMessageKey", "action.cancelled")
      return listPage
    }
    if (result.hasErrors) {
      if (logger.isDebugEnabled)
        logger.debug("form has errors {}", result.getErrorCount)
      fillEditModel(b, model, locale)
      return editPage
    }
    val r: T = manager().save(b)
    // set id from create
    if (b.isBlank)
      b.setId(r.getId)

    // too noisy message
    //redirectAttributes.addFlashAttribute("statusMessageKey", "action.success")
    listPage
  }
  @RequestMapping(value = Array("/delete"),
    method = Array(RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE))
  def delete(@RequestParam(required = false) id: Integer): String = {
    manager().remove(id)
    listPage
  }
  /**
   * Find and load model entity by id,taken from path
   * @param id
   *      model id
   * @param model
   *      page model
   * @param locale
   *      request context locale
   * @return
   */
  @RequestMapping(value = Array("/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  def getByPath(@PathVariable("id") id: Integer, model: Model, locale: Locale): String = {
    val m = loadModel(id)
    if (m == null)
      return MvcConstants.page404
    model.addAttribute(MvcConstants.MODEL_KEY, m)
    fillEditModel(m, model, locale)
    viewPage
  }
}
