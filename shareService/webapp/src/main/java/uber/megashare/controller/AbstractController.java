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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uber.megashare.base.LoggedClass;
import uber.megashare.listener.SessionHelper;
import uber.megashare.model.AvatarType;
import uber.megashare.model.Project;
import uber.megashare.model.SystemProperties;
import uber.megashare.model.User;
import uber.megashare.service.ProjectManager;
import uber.megashare.service.SettingsManager;
import uber.megashare.service.UserManager;
import uber.megashare.service.security.SecurityHelper;

/**
 *
 * @author alex
 */
public abstract class AbstractController extends LoggedClass {

    interface ActionListener {

        void invokeAction(Model model, 
                HttpServletRequest request, 
                HttpServletResponse response, 
                Locale locale);
    }
    /**
     *
     */
    private static final long serialVersionUID = -7713053747053956403L;
    
    protected static final String STATUS_MESSAGE_KEY = "statusMessageKey",
            MSG_ACCESS_DENIED = "access-denied",
            MSG_ACTION_CANCELLED = "action.cancelled",
            MSG_ACTION_SUCCESS = "action.success";
    
    /**
     * Various system pages
     */
    protected final String page404 = "404",
            page500 = "500",
            page403 = "403";
    
    @Autowired
    protected UserManager userManager;
    
    @Autowired
    protected ProjectManager projectManager;    
    
    @Autowired
    protected SettingsManager settingsManager;
    
    @Value("${external.url}")
    protected String externalUrl;
  
    @Value("${paster.url}")
    protected String pasterUrl;

    @Value("${resources.cdn.enabled}")
    protected boolean cdnEnabled;

    @Value("${google.docs.integration.enabled}")
    protected boolean googleDocsIntegrationEnabled;
  
    @Value("${paste.integration.enabled}")
    protected boolean pasteIntegrationEnabled;

    @Resource(name = "messageSource")
    protected MessageSource messageSource;

    @Value("${resources.gavatar.enabled}")
    protected boolean gavatarEnabled;

    @Value("${gavatar.url}")
    protected String gavatarUrl;

    private final static List<Locale> availableLocales = new ArrayList<>();

    static {
        availableLocales.add(Locale.US);
        availableLocales.add(new Locale("ru", "RU"));
    }
    
    @ModelAttribute("currentSettings")
    public SystemProperties getCurrentSettings() {
        return settingsManager.getCurrentSettings();
    }
    
    @ModelAttribute("appVersion")
    public String getAppVersion() {
        return settingsManager.getCurrentSettings().getAppVersion().getImplBuildNum();
    }
    
    @ModelAttribute("pasteIntegrationEnabled")
    public boolean isPasteIntegrationEnabled() {
        return pasteIntegrationEnabled;
    }
    
    @ModelAttribute("googleDocsEnabled")
    public boolean isGoogleDocsEnabled() {
        return googleDocsIntegrationEnabled;
    }

    @ModelAttribute("cdnEnabled")
    public boolean isCDNEnabled() {
        return cdnEnabled;
    }
    
    @ModelAttribute("gavatarEnabled")
    public boolean isGavatarEnabled() {
        return gavatarEnabled;
    }

    @ModelAttribute("gavatarlUrl")
    public String getGavatarUrl() {
        return gavatarUrl;
    }
    
    @ModelAttribute("externalUrl")
    public String getExternalUrl() {
        return externalUrl;
    }
    
    @ModelAttribute("pasteUrl")
    public String getPasterUrl() {
        return pasterUrl;
    }
    
    

    /**
     * handles error throwed from spring dao level
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ObjectRetrievalFailureException.class)
    public String handleException(ObjectRetrievalFailureException ex) {
        getLogger().error("Object not found: " + ex.getLocalizedMessage(), ex);
        return page500;
    }
    

    @ModelAttribute("availableLocales")
    public List<Locale> getAvailableLocales() {
        return availableLocales;
    }
    
    @ModelAttribute("availableProjects")
    public List<Project> getAvailableProjects() {
        return  isCurrentUserLoggedIn() ? projectManager.getAll(): Collections.EMPTY_LIST;
    }

    @ModelAttribute("availableAvatarTypes")
    public AvatarType[] getAvailableAvatarTypes() {
        return AvatarType.values();
    }
    
    @ModelAttribute("availableProjectsWithUsers")
    public Collection<ProjectUsers> getAvailableProjectsWithUsers() {
        
        if (!isCurrentUserLoggedIn()) {
            return Collections.EMPTY_LIST;
        }
        
        Map<Project,ProjectUsers> out = new HashMap<>();
        
        for (User user:userManager.getAll()) {
          
            ProjectUsers pu = out.containsKey(user.getRelatedProject()) ? 
                    out.get(user.getRelatedProject()) : 
                    new ProjectUsers(user.getRelatedProject());
          
                pu.getUsers().add(user);
                out.put(pu.getProject(), pu);
        }
        
        return out.values();
        
    }

    @ModelAttribute("availableUsers")
    public List<User> availableUsers() {        
        return isCurrentUserLoggedIn() ? userManager.getAll() :Collections.EMPTY_LIST;
    }

    public boolean isCurrentUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public boolean isCurrentUserAdmin() {
        User u = getCurrentUser();
        return u != null && u.isAdmin();
    }

    public void addMessageDenied(RedirectAttributes redirect) {
        redirect.addFlashAttribute(STATUS_MESSAGE_KEY, MSG_ACCESS_DENIED);
    }

    public void addMessageCancelled(RedirectAttributes redirect) {
        redirect.addFlashAttribute(STATUS_MESSAGE_KEY, MSG_ACTION_CANCELLED);
    }

    public void addMessageSuccess(RedirectAttributes redirect) {
        redirect.addFlashAttribute(STATUS_MESSAGE_KEY, MSG_ACTION_SUCCESS);
    }

    /**
     * 
     * @return list all users online 
     */
    @ModelAttribute("usersOnline")
    public List<User> getUsersOnline() {
        return getCurrentUser() != null
                ? SessionHelper.getInstance().getSessions(getCurrentUser().getUsername())
                : SessionHelper.getInstance().getSessions();
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        return SecurityHelper.getInstance().getCurrentUser();
    }
    
   public static class ProjectUsers {
    
        private Project project;
        
        private Set<User> users = new LinkedHashSet<>();

        public ProjectUsers(Project p) {
            this.project=p;
        }
        
        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        public Set<User> getUsers() {
            return users;
        }    
        
        
    }
}
