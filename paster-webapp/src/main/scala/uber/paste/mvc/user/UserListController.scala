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

package uber.paste.mvc.user

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import uber.paste.model.User
import uber.paste.mvc.GenericListController
import uber.paste.dao.UserDaoImpl
import uber.paste.manager.UserManager
import org.springframework.beans.factory.annotation.Autowired


@Controller
@RequestMapping(Array("/user"))
class UserListController extends GenericListController[User]{

  @Autowired
  private val userManager:UserDaoImpl = null
  
  def listPage="redirect:/main/user/list"
  def editPage="/user/edit"
  def viewPage="/user/view"


  def manager() = userManager
 
}
