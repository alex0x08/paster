/**
 * Copyright (C) 2011 aachernyshev <alex@0x08.tk>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uber.megashare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import uber.megashare.model.Role;
import uber.megashare.model.User;
import uber.megashare.service.UserManager;

/**
 *
 * @author alex
 */
@Controller
@RequestMapping("/user")
public class UserEditController extends GenericEditController<User> {

    // private UserManager userManager;
    /**
     *
     */
    private static final long serialVersionUID = 9012983327522416777L;

    @Autowired
    public UserEditController(UserManager userManager) {
        super(userManager);
        //   this.userManager = userManager;
        setListPage("redirect:list");
        setEditPage("user/edit");
        setViewPage("user/view");

    }

    @Override
    protected boolean checkAccess(User obj, Model model) {
        return true;
    }
   

    @Override
    public User getNewModelInstance() {
        return new User();
    }
    
    @ModelAttribute("availableRoles")
    public Role[] getAvailableRoles() {
        return Role.values();
    }

}
