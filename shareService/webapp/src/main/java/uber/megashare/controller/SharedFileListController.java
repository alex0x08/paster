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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import static uber.megashare.controller.AbstractListController.LIST_ACTION;
import uber.megashare.model.AccessLevel;
import uber.megashare.model.SharedFile;
import uber.megashare.model.SharedFileSearchQuery;
import uber.megashare.service.SharedFileManager;
import static uber.megashare.controller.SharedFileConstants.FILE_PREFIX;
import uber.megashare.service.FolderManager;


/**
 * Implementation of MVC Controller for file list operations
 *
 * @author alex
 */
@Controller
@RequestMapping(FILE_PREFIX)
public class SharedFileListController 
    extends AbstractListIntegratedController<SharedFile, SharedFileSearchQuery> 
    implements SharedFileConstants,EditConstants {

    /**
     *
     */
    private static final long serialVersionUID = 2212858269285459262L;

      
    private final SharedFileManager fileManager;
   
    private FolderManager folderManager;

    @Autowired
    public SharedFileListController(
            SharedFileManager fileManager,
            FolderManager folderManager
           ) {
        super(fileManager);
        this.fileManager = fileManager;
        this.folderManager = folderManager;

        setListPage("redirect:"+FILE_PREFIX+LIST_ACTION);
        /**
         * no redirect here due to field validation issues
         */
        setEditPage(FILE_PREFIX+EDIT_ACTION);
    }

    
    
    /**
     * get list of shared files
     * @return 
     */
    @Override
    protected List<SharedFile> getModels() {
        /**
         * if the method was called by authorized user - show only his own
         * files, else - only public files
         */
      //  Collections.addAll(this, elements);
      //  folderManager.getChildren(folderManager.getParentFolder())
        
 //         List<SharedFile> out = new ArrayList<>();
          /*for(FolderNode f:folderManager.getChildren(folderManager.getParentFolder())) {
              out.add(f.toSharedFile());
          }*/
          
           return !isCurrentUserLoggedIn()? fileManager.getFiles(null,new AccessLevel[]{AccessLevel.ALL}):
                    fileManager.getFilesForUser(getCurrentUser().getId(),getCurrentUser().getRelatedProject().getId(),
                    AccessLevel.values(),null);
          
    }
   
    
    @RequestMapping(value = LIST_ACTION + "/{mode:[a-zA-Z]+}", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<SharedFile> listByMode(final @PathVariable("mode") String mode,
            HttpServletRequest request,
            final HttpSession session,
            final Model model,
            Locale locale) {
        
        
                putListModel(model, locale);

        return processPagination(request, model, null, null, null,null,false,new SourceCallback<SharedFile>() {

        @Override
        public PagedListHolder<SharedFile> invokeCreate() {
            
            List<SharedFile> result;
            
            AccessLevel l = AccessLevel.valueOf(mode.toUpperCase());
           
            model.addAttribute("currentViewMode",l);
            
            switch(AccessLevel.valueOf(mode.toUpperCase())) {
                case ALL: result = getModels(); break;
                case PROJECT: result = fileManager.getFiles(
                        getCurrentUser().getRelatedProject().getId(), 
                        new AccessLevel[]{AccessLevel.PROJECT}); break;
                case OWNER: result = fileManager.getFilesForUser(
                        getCurrentUser().getId(),null,
                        new AccessLevel[]{AccessLevel.OWNER,AccessLevel.USERS}, null); break;
                default: result = null;
            }
                     model.addAttribute("currentResultSize",result!=null ? result.size():0);
         
                    session.removeAttribute("queryString");
                    return new PagedListHolder<>(result!=null ? result : Collections.EMPTY_LIST);
            }
        });

        
    }


    @RequestMapping(value = LIST_ACTION, method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    @Override
    Collection<SharedFile> list(HttpServletRequest request,
            final HttpSession session,
            final Model model,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String NPpage,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String sortColumn,
            @RequestParam(required = false) boolean sortAsc,
            Locale locale) {

          model.addAttribute("currentViewMode",AccessLevel.ALL);
       
    
        putListModel(model, locale);

        return processPagination(request, model, page, NPpage, pageSize,sortColumn,sortAsc,new SourceCallback<SharedFile>() {

        @Override
        public PagedListHolder<SharedFile> invokeCreate() {
            List<SharedFile> result = getModels();
                model.addAttribute("currentResultSize",result.size());
     
                    session.removeAttribute("queryString");
                    return new PagedListHolder<>(result!=null ? result : Collections.EMPTY_LIST);
            }
        });
    }
    

    @RequestMapping(value = INTEGRATED_PREFIX+LIST_ACTION+"/{integrationCode:[a-z0-9_]+}", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    @Override
    Collection<SharedFile> listIntegrated(
            final @PathVariable("integrationCode") String integrationCode,
            HttpServletRequest request,
             final HttpSession session,
             Model model,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String NPpage,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String sortColumn,
            @RequestParam(required = false) boolean sortAsc,            
            Locale locale) {

        
        SharedFile smodel = new SharedFile();
        smodel.setIntegrationCode(integrationCode);
        smodel.setAccessLevel(AccessLevel.ALL);
        
         model.addAttribute(AbstractEditController.MODEL_KEY, smodel);
        Collection<SharedFile> out= super.listIntegrated(integrationCode, request, session, model, page, NPpage, pageSize,sortColumn,sortAsc, locale);
            
        return out;
    }

    

    @Override
    protected void putListModel(Model model, Locale locale) {
        
        model.addAttribute("availableModes", new AccessLevel[]{
            AccessLevel.ALL,
            AccessLevel.OWNER,
            AccessLevel.PROJECT});
        /**
         * add list of users in systems to view from dropdown selectbox
         */
        /*if (isCurrentUserAdmin()) {
            model.addAttribute("availableUsers", userManager.getAll());
        }*/
     
    }

    /**
     * instantiate the new search query object
     */
    @ModelAttribute(QUERY_MODEL)
    @Override
    public SharedFileSearchQuery getNewQuery(final HttpSession session) {

        SharedFileSearchQuery query = new SharedFileSearchQuery();

        query.setQuery((String)session.getAttribute("queryString"));
        
        if (isCurrentUserLoggedIn()) {

            if (query.getUserId() == null) {
                query.setUserId(getCurrentUser().getId());
            }

            query.setLevels(Arrays.asList(AccessLevel.values()));
        } else {

            query.setLevels(Arrays.asList(AccessLevel.ALL));
        }
        return query;
    }
    
    
}
