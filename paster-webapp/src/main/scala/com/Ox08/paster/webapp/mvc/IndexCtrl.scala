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

import com.Ox08.paster.webapp.model.GenericQuery
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.stereotype.Controller
import org.springframework.ui.Model

@Controller
class IndexCtrl extends AbstractCtrl{
  
  
  @RequestMapping(value = Array("/"))
  def index(model:Model): String = {
    model.asMap().clear()
    "redirect:/main/paste/list"
  }

  @RequestMapping(Array("/error/{errorCode:[0-9_]+}"))
  def error(response:HttpServletResponse,
            @PathVariable("errorCode") errorCode:Int):String = errorCode match {
                case 403 |404 |500  =>
                  response.setStatus(errorCode)
                  s"/error/$errorCode"
                case _ =>
                  "/error/500"
  }

  @RequestMapping(Array("/login"))
  def login(model:Model):String = if (isCurrentUserLoggedIn) index(model) else {
  model.addAttribute("query", new GenericQuery())
  "/login"
  }

}
