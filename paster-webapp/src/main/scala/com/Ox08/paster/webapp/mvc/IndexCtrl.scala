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
import com.Ox08.paster.webapp.base.Boot
import com.Ox08.paster.webapp.model.GenericQuery
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
/**
 * Default Controller
 *
 * Starts from /main/
 *
 * @since 1.0
 * @author 0x08
 */
@Controller
class IndexCtrl extends AbstractCtrl {
  /**
   * Handles default url
   * @param model
   *        page model
   * @return
   *      redirect url
   */
  @RequestMapping(value = Array("/"))
  def index(model: Model): String = {
    model.asMap().clear()
    if (!Boot.BOOT.getSystemInfo.isInstalled)
      return "redirect:/main/setup/welcome"
    "redirect:/main/paste/list"
  }
  /**
   * Handles all error pages
   * @param response
   * @param errorCode
   * @return
   */
  @RequestMapping(Array("/error/{errorCode:[0-9_]+}"))
  def error(model:Model,response: HttpServletResponse,
            @PathVariable("errorCode") errorCode: Int): String = {
    model.addAttribute("appId", appId)
    model.addAttribute("systemInfo",systemInfo)
    errorCode match {
      case 403 | 404 | 500 =>
        response.setStatus(errorCode)
        s"/error/$errorCode"
      case _ =>
        "/error/500"
      }
  }
  /**
   * Login page
   * @param model
   * @return
   */
  @RequestMapping(Array("/login"))
  def login(model: Model): String = if (isCurrentUserLoggedIn) index(model) else {
    model.addAttribute("query", new GenericQuery())
    "/login"
  }
}
