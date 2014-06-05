/**
 * Copyright (C) 2011 Alex <alex@0x08.tk>
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
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static uber.megashare.controller.EditConstants.MODEL_KEY;
import static uber.megashare.controller.EditConstants.SAVE_ACTION;
import uber.megashare.listener.SessionHelper;
import uber.megashare.model.Avatar;
import uber.megashare.model.Project;
import uber.megashare.model.User;
import uber.megashare.service.ProjectManager;

/**
 *
 * @author aachernyshev
 */
@Controller
@RequestMapping("/project")
public class ProjectEditController  extends AbstractEditController<Project>{
   
    
    @Autowired
    public ProjectEditController(ProjectManager projectManager) {
        super(projectManager);
        //   this.userManager = userManager;
        setListPage("redirect:list");
        setEditPage("project/edit");
        setViewPage("project/view");

    }

    @Override
    protected boolean checkAccess(Project obj, Model model) {
        return true;
    }
   

    @Override
    public Project getNewModelInstance() {
        return new Project();
    }
    
    /**
     *
     * @param cancel
     * @param b
     * @param result
     * @param model
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = SAVE_ACTION, method = RequestMethod.POST)
    @Override
    public String save(@RequestParam(required = false) String cancel,
            @Valid
            @ModelAttribute(MODEL_KEY) Project b,
            BindingResult result, Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
         
        if (!b.isBlank()) {
            Project current = manager.get(b.getId());
            current.setDescription(b.getDescription());
            current.setName(b.getName());
            current.setFile(b.getFile());
           b=current;
        }
        
         if (b.getFile()!=null && !b.getFile().isEmpty()) {
             try {
                 b.setAvatar(Avatar.fromStream(b.getFile().getInputStream(),false));
             } catch (IOException ex) {
                 getLogger().error(ex.getLocalizedMessage(),ex);
                 return page500;
             }
         } 
         
         String out = super.save(cancel, b, result, model, request, redirectAttributes);
         
         if (out.equals(listPage) && cancel==null) {
             /**
              * saved ok, update current users
              */
            for (User u:getUsersOnline()) {
                if (u.getRelatedProject().equals(b)) {
                    u.setRelatedProject(b);
                    SessionHelper.getInstance().updateUser(u);
                }
            }
         }
         
         return out;
     }
}
