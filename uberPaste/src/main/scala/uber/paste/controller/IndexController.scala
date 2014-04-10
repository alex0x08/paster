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

package uber.paste.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import uber.paste.openid.OpenIDServer

@Controller
class IndexController extends AbstractController{
  
  
  @RequestMapping(value = Array("/"))
  def index(model:Model) = {
    model.asMap().clear()
    "redirect:/main/paste/list"
  }
  
  
  @RequestMapping(Array("/login"))
  def login(model:Model):Unit = {
    model.addAttribute("availableServers",OpenIDServer.list)
  }
}
