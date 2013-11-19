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

import java.io.IOException;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uber.megashare.model.Avatar;
import uber.megashare.model.AvatarType;
import uber.megashare.model.User;

/**
 * Standalone controller for user's profile was needed for more security
 *
 * @author alex
 */
@Controller
@RequestMapping("/profile")
public class ProfileController extends AbstractController {

    /**
     *
     */
    private static final long serialVersionUID = 384908865174501039L;
   
    protected String listPage = "redirect:list", //default list page for T object
            editPage = "profile/edit", //.. edit page ..
            redirectEditPage = "redirect:edit",
            viewPage = "profile/view";

    @ModelAttribute("user")
    protected User newRequest(@RequestParam(required = false) Long id, Model model) {
        return (id != null ? userManager.getFull(id) : null);
    }
  
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String view(@RequestParam(required = false) Long id, Model model) {
        return edit(id, model);
    }

    /**
     * main profile operations endpoint
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Long id, Model model) {

        /**
         * anonymous users cannot operate with profiles at all
         */
        if (!isCurrentUserLoggedIn()) {
            return page403;
        }

        /**
         * if no user id specified - assume current logged user
         */
        User user;
        if (id == null) {
            user = getCurrentUser();
        } else {
            user = userManager.getFull(id);

            if (user == null) {
                return page404;
            }

            if (!isCurrentUserAdmin() && user.equals(getCurrentUser())) {
                return page403;
            }
        }

        model.addAttribute("user", user);        
        
        return editPage;
    }

    /**
     * save profile
     *
     * @param cancel
     * @param b
     * @param result
     * @param model
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@RequestParam(required = false) String cancel,
            @Valid User b,
            BindingResult result, Model model) {

        /**
         * process cancel button
         */
        if (cancel != null) {
            model.addAttribute("statusMessageKey", "action.cancelled");
            
            model.asMap().clear();
   
            return listPage;
        }

        if (result.hasErrors()) {
            getLogger().debug("form has errors " + result.getErrorCount());


            /**
             * skip non-used fields
             */
            for (FieldError f : result.getFieldErrors()) {
                if (f.getField().equals("login") || f.getField().equals("password")) {
                    continue;
                }
                getLogger().debug("field=" + f.getField() + ",rejected value=" + f.getRejectedValue() + ",message=" + f.getDefaultMessage());

                return editPage;
            }

        }


        /**
         * disallow change fields like password or roles
         */
        User current = userManager.getFull(b.getId());
        current.setEmail(b.getEmail());
        current.setDefaultFileAccessLevel(b.getDefaultFileAccessLevel());
        current.setName(b.getName());
        current.setSkype(b.getSkype());
        current.setPhone(b.getPhone());
        
        if (current.getAvatarType()!=b.getAvatarType()) {
            current.setAvatar(null);
        }
        
        current.setAvatarType(b.getAvatarType());
     
        current.setPrefferedLocaleCode(b.getPrefferedLocaleCode());
            
        
        if (!StringUtils.isBlank(b.getNewPassword())) {
            
            if (!b.getNewPassword().equals(b.getRepeatPassword())) {
           
                result.addError(new ObjectError("newPassword","Password must match."));
                return editPage; 
            } else {
                
                current= userManager.changePassword(current, b.getNewPassword());
            }
        }
        
          if (b.getAvatarType() == AvatarType.FILE && b.getFile()!=null && !b.getFile().isEmpty()) {
             try {
                 current.setAvatar(Avatar.fromStream(b.getFile().getInputStream(),true));
             } catch (IOException ex) {
                 getLogger().error(ex.getLocalizedMessage(),ex);
                 return page500;
             }
         } 
        
        
        current=userManager.save(current);

        getLogger().debug("user " + current.getId() + " saved roles="+current.getRoles().size());

        model.addAttribute("statusMessageKey", "action.success");

        model.asMap().clear();
   
        return redirectEditPage;
    }
}