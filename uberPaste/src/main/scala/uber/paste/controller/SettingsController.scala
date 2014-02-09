/*
 * Copyright 2014 Ubersoft, LLC.
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


import java.util.Locale
import org.apache.tools.ant.Project
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import uber.paste.manager.ProjectManager

@Controller
@RequestMapping(Array("/admin/settings"))
class SettingsController extends GenericEditController[Project]{

  @Autowired
  val projectManager:ProjectManager = null

  def editPage()="/admin/settings/edit"
  
  def manager():ProjectManager = return projectManager
 
  @RequestMapping(value = Array(GenericEditController.EDIT_ACTION), method = Array(RequestMethod.GET))
  def edit(model:Model,locale:Locale):String= {

    model.addAttribute(GenericController.MODEL_KEY, projectManager.getCurrentProject)

    return editPage
  }

  
}
