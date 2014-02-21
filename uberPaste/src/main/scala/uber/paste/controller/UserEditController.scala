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

import org.springframework.stereotype.Controller
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.RequestMapping
import uber.paste.model.User
import org.springframework.beans.factory.annotation.Autowired
import uber.paste.manager.UserManager
import org.springframework.ui.Model
import uber.paste.model.Role
import uber.paste.model.RoleEditor
import org.springframework.web.bind.annotation.InitBinder;
import java.util.Locale

@Controller
@RequestMapping(Array("/user"))
class UserEditController extends GenericEditController[User]{

  @Autowired
  private val userManager:UserManager = null
  
  def listPage()="redirect:/main/user/list"
  def editPage()="/user/edit"
  def viewPage()="/user/view"

  def manager():UserManager = return userManager
  
  @InitBinder
  def initBinder(binder:WebDataBinder):Unit = {
    binder.initDirectFieldAccess()
    binder.registerCustomEditor(classOf[Role], new RoleEditor())
  }

  override def fillEditModel(obj:User,model:Model,locale:Locale)  {
    super.fillEditModel(obj,model,locale)
   model.addAttribute("availableRoles", Role.list)
  }
  
  def getNewModelInstance():User = return new User

}
