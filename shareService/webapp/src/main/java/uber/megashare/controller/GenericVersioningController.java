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

import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import static uber.megashare.controller.EditConstants.EDIT_ACTION;
import uber.megashare.model.Struct;
import uber.megashare.service.GenericVersioningManager;

/**
 *
 * @author achernyshev
 */
public abstract class GenericVersioningController<T extends Struct> extends AbstractEditController<T> {

    protected static final String REVERT_ACTION = "/revert";
    /**
     * associated manager
     */
    protected final GenericVersioningManager<T> smanager;

    protected GenericVersioningController(GenericVersioningManager<T> manager) {
        super(manager);
        this.smanager = manager;
    }

    @Override
    protected void putModel(T obj, Model model) {

        super.putModel(obj, model);
        
        getLogger().debug("__putModel obj id=" + obj.getId());
        
        if (!obj.isBlank()) {
            
            List<Number> revs = smanager.getRevisions(obj.getId());
            /**
             * one element = current revision
             */
            model.addAttribute("availableRevisions",revs!=null && revs.size()>1 ? revs : null  );
            model.addAttribute("lastRevision", smanager.getCurrentRevisionNumber(obj.getId()));
        }
    }

    
    @RequestMapping(value = VIEW_ACTION, method = RequestMethod.GET)
    public String view(@RequestParam(required = true) Long id,
                       @RequestParam(required = false) Long revision, 
                       Model model, Locale locale) {
       
          String result = edit(model, id, revision, locale);
          return result.equals(editPage) ? viewPage : result;
      }
    
     @RequestMapping(value = EDIT_ACTION, method = RequestMethod.GET)
    public String edit(Model model,
                        @RequestParam(required = true) Long id,
                        @RequestParam(required = false) Long revision, Locale locale) {
        
         T omodel = revision!=null ? smanager.getRevision(id, revision) : manager.getFull(id);     
        
         if (omodel==null) {
             return page404;
         }        
         
        if (!checkAccess(omodel,model)) {
            return page403;
        }
        
        model.addAttribute(MODEL_KEY, omodel);

         putModel(omodel, model);
        
        return editPage;
    }
     
    @RequestMapping(value = EDIT_ACTION+"-no-revision", method = RequestMethod.GET)
    @Override
    public String edit(Model model,@RequestParam(required = true) Long id, Locale locale) {
          return edit(model, id,null, locale);
    }

    @RequestMapping(value = VIEW_ACTION+"-no-revision", method = RequestMethod.GET)
    @Override
    public String view(@RequestParam(required = true) Long id, Model model,Locale locale) {
          return view( id, null,model,locale);
    }

    
    @RequestMapping(value = REVERT_ACTION, method = {RequestMethod.POST, RequestMethod.GET})
    public String revert(@RequestParam(required = false) String cancel,
            @RequestParam(value = "revision", required = true) Long revision,
            @RequestParam(value = "id", required = true) Long id,
            Model model, HttpServletRequest request,Locale locale) {

        smanager.revertToRevision(id, revision);

         return super.edit(model, id, locale);
       
    }
}
