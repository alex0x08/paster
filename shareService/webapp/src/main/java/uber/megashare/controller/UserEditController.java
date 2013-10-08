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

import java.beans.PropertyEditorSupport;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uber.megashare.model.Project;
import uber.megashare.model.Role;
import uber.megashare.model.User;
import uber.megashare.service.ProjectManager;
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

    private ProjectManager projectManager;
    
    @Autowired
    public UserEditController(UserManager userManager,ProjectManager projectManager) {
        super(userManager);
        //   this.userManager = userManager;
        this.projectManager = projectManager;
        setListPage("redirect:list");
        setEditPage("user/edit");
        setViewPage("user/view");

    }

    @Override
    protected boolean checkAccess(User obj, Model model) {
        return true;
    }
   

    @InitBinder
    protected void initBinder(WebDataBinder binder,Locale locale) {
    
         binder.registerCustomEditor(Project.class,"relatedProject", new PropertyEditorSupport() {
        @Override
        public void setAsText(String text) {
            System.out.println("set project from "+text);
            setValue(new Project(Long.valueOf(text)));
        
        }
        
             @Override
             public String getAsText() {
                 System.out.println("_get as text "+getValue());
                 if (getValue() == null) {
                     return "";
                 } else {
                     return ((Project) getValue()).getId().toString();
                 }
             }    
        
    });
    }
    
    @Override
    public User getNewModelInstance() {
        return new User();
    }
    
    @ModelAttribute("availableRoles")
    public Role[] getAvailableRoles() {
        return Role.values();
    }
    
    @ModelAttribute("availableProjects")
    public List<Project> getAvailableProjects() {
        return projectManager.getAll();
    }
    
    @RequestMapping(value = {SAVE_ACTION}, method = RequestMethod.POST)
    @Override
    public String save(@RequestParam(required = false) String cancel,
            @Valid
            @ModelAttribute(MODEL_KEY) User b,
            BindingResult result, Model model,
            HttpServletRequest request, RedirectAttributes redirectAttributes) {
        
        /**
         * allow upload only for authorized users
         */
        if (!isCurrentUserLoggedIn()) {
            addMessageDenied(redirectAttributes);
            return editPage;
        }

        if (cancel != null) {
            addMessageCancelled(redirectAttributes);
            return listPage;
        }

        if (result.hasErrors() && !result.getFieldError().getField().equals("password")) {
            
            if (getLogger().isDebugEnabled()) {
                debugPrintRejected(result);
            }
            return editPage;
        }
        
        
        if (b.getRoles() == null || b.getRoles().isEmpty()) {
            result.addError(new ObjectError("user.roles", "Roles not set."));
            return editPage;
        }

        if (!b.isBlank()) {
            
            User current = userManager.getFull(b.getId());
            current.setEmail(b.getEmail());
            current.setDefaultFileAccessLevel(b.getDefaultFileAccessLevel());
            current.setName(b.getName());
            
            Project p  =projectManager.get(b.getRelatedProject().getId());
            
            if (p==null) {
                    result.addError(new ObjectError("relatedProject","Project does not exist."));
                    return editPage; 
            }
            
            current.setRelatedProject(p);
            current.setRoles(b.getRoles());
            
            if (!StringUtils.isBlank(b.getNewPassword())) {
                
               if (!b.getNewPassword().equals(b.getRepeatPassword())) {
           
                    result.addError(new ObjectError("newPassword","Password must match."));
                    return editPage; 
                }
            
                current = userManager.changePassword(current, b.getNewPassword());
            }
            
            b = current;
            
            System.out.println("_saving pass "+b.getPassword());
            
        } else {

           if (!StringUtils.isBlank(b.getNewPassword()) && !b.getNewPassword().equals(b.getRepeatPassword())) {
                    result.addError(new ObjectError("newPassword","Password must match."));
                    return editPage; 
                }
           
                b= userManager.changePassword(b, b.getNewPassword());
       }
        
        User r = manager.save(b);


        /**
         * set id from create this is needed to correct model's id postback when
         * validation fails
         */
        if (b.isBlank()) {
            b.setId(r.getId());
        }

        resetPagingList(request);

        addMessageSuccess(redirectAttributes);
        return listPage;

        }

}
