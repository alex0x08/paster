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
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod}
import java.util.Locale
@Controller
@RequestMapping(Array("/admin/settings"))
class SettingsCtrl extends AbstractCtrl {
  def editPage = "/admin/settings/edit"
  def manager(): Null = null
  @RequestMapping(value = Array("/dbconsole"), method = Array(RequestMethod.GET))
  def dbconsole(): String = {
    "/admin/settings/dbconsole"
  }
}
