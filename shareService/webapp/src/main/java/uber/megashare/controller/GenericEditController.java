/**
 * Copyright (C) 2011 alex <alex@0x08.tk>
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

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import static uber.megashare.controller.EditConstants.MODEL_KEY;
import uber.megashare.model.Struct;
import uber.megashare.service.StructManager;

/**
 *
 * @author alex
 */
public abstract class GenericEditController<T extends Struct> extends GenericController<T> implements EditConstants {

    /**
     *
     */
    private static final long serialVersionUID = 3844308254206235808L;

    protected GenericEditController(StructManager<T> manager) {
        super(manager);
    }

    /**
     * get new model instance associated with this controller see implementation
     * in child controllers
     *
     * @return
     */
    protected abstract T getNewModelInstance();

    protected void putModel(T obj, Model model) {

        if (!obj.isBlank()) {
            if (manager.exists(obj.getId() + 1)) {
                model.addAttribute("availableNext", 1);
            }

            if (manager.exists(obj.getId() - 1)) {
                model.addAttribute("availablePrev", 1);
            }
        }
    }

    protected abstract boolean checkAccess(T obj, Model model);

    @RequestMapping(value = "/{id:[0-9]+}", method = RequestMethod.GET)
    public String getByPath(@PathVariable("id") Long id, Model model, Locale locale) {
      return view(id,model,locale);
    }

    @RequestMapping(value = EDIT_ACTION + "/{id:[0-9]+}", method = RequestMethod.GET)
    public String editByPath(@PathVariable("id") Long id, Model model, Locale locale) {
        return edit(model, id, locale);
    }

    @RequestMapping(value = VIEW_ACTION, method = RequestMethod.GET)
    public String view(@RequestParam(required = false) Long id, Model model, Locale locale) {

        String result = edit(model, id, locale);
        return result.equals(editPage) ? viewPage : result;
    }

    @RequestMapping(value = EDIT_ACTION, method = RequestMethod.GET)
    public String edit(Model model, @RequestParam(required = false) Long id, Locale locale) {

        if (id == null) {
            return listPage;
        }

        T omodel = manager.getFull(id);

        if (omodel == null) {
            return page404;
        }

        if (!checkAccess(omodel, model)) {
            return page403;
        }

        model.addAttribute(MODEL_KEY, omodel);

        putModel(omodel, model);

        return editPage;
    }

    @RequestMapping(value = NEW_ACTION, method = RequestMethod.GET)
    public String createNew(Model model) {

        /**
         * allow create only for authorized users
         */
        if (!isCurrentUserLoggedIn()) {
            addMessageDenied(model);
            return listPage;
        }
        model.addAttribute(MODEL_KEY, getNewModelInstance());

        return editPage;
    }

    @RequestMapping(value = SAVE_ACTION, method = RequestMethod.POST)
    public String save(@RequestParam(required = false) String cancel,
            @Valid
            @ModelAttribute(MODEL_KEY) T b,
            BindingResult result, Model model, HttpServletRequest request) {

        /**
         * allow upload only for authorized users
         */
        if (!isCurrentUserLoggedIn()) {
            addMessageDenied(model);
            return editPage;
        }

        if (cancel != null) {
            addMessageCancelled(model);
            return listPage;
        }

        if (result.hasErrors()) {
            if (getLogger().isDebugEnabled()) {
                debugPrintRejected(result);
            }
            return editPage;
        }

        T r = manager.save(b);


        /**
         * set id from create this is needed to correct model's id postback when
         * validation fails
         */
        if (b.isBlank()) {
            b.setId(r.getId());
        }

        resetPagingList(request);

        addMessageSuccess(model);
        return listPage;
    }

    @RequestMapping(value = DELETE_ACTION, method = {RequestMethod.GET, RequestMethod.POST})
    public String delete(@RequestParam(required = true) Long id,
            Model model, HttpServletRequest request) {
        manager.remove(id);
        resetPagingList(request);
        return listPage;
    }

    protected T get(Long id) {
        getLogger().debug("get by id=" + id);
        return manager.getFull(id);
    }

    protected void debugPrintRejected(BindingResult result) {
        getLogger().debug("form has errors " + result.getErrorCount());
        for (FieldError f : result.getFieldErrors()) {
            getLogger().debug("field=" + f.getField() + ",rejected value=" + f.getRejectedValue() + ",message=" + f.getDefaultMessage());
        }
    }
}
