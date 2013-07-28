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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uber.megashare.model.Struct;
import uber.megashare.service.StructManager;

/**
 * Abstract list controller.
 *
 * @author alex
 */
public abstract class GenericListController<T extends Struct> extends GenericController<T> implements ListConstants {

    /**
     *
     */
    private static final long serialVersionUID = -4801670188232326655L;
    
  

    /**
     * Default constructor Must be called with an instance of service backend
     *
     * @param manager
     */
    protected GenericListController(StructManager<T> manager) {
        super(manager);
    }

    protected abstract List<T> getModels();

    protected abstract void putListModel(Model model, Locale locale);
    
  /*  protected final SourceCallback<T> defaultListCallback = new SourceCallback<T>() {

        @Override
        public PagedListHolder<T> invokeCreate() {
              session.removeAttribute("queryString");
            return new PagedListHolder<>(getModels());
        }
    };*/

    @RequestMapping(value = LIST_ACTION + "/{page:[0-9]+}", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listByPath(@PathVariable("page") Integer page,
            HttpServletRequest request,
            final HttpSession session,
            Model model,
            Locale locale) {
        return list(request,session, model, page, null, null, locale);
    }

    @RequestMapping(value = LIST_ACTION + "/limit/{pageSize:[0-9]+}", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listByPathSize(@PathVariable("pageSize") Integer pageSize,
            HttpServletRequest request,
            final HttpSession session,
            Model model,
            Locale locale) {
        return list(request,session, model, null, null, pageSize, locale);
    }

    @RequestMapping(value = LIST_ACTION + "/next", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listByPathNext(
            HttpServletRequest request,
            final HttpSession session,
            Model model,
            Locale locale) {
        return list(request,session, model, null, NEXT_PARAM, null, locale);
    }

    @RequestMapping(value = LIST_ACTION + "/prev", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listByPathPrev(
            HttpServletRequest request,
            final HttpSession session,
            Model model,
            Locale locale) {
        return list(request,session, model, null, "prev", null, locale);
    }

    @RequestMapping(value = LIST_ACTION, method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> list(HttpServletRequest request,
            final HttpSession session,
            Model model,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String NPpage,
            @RequestParam(required = false) Integer pageSize, Locale locale) {

    
        putListModel(model, locale);

        return processPagination(request, model, page, NPpage, pageSize,new SourceCallback<T>() {

        @Override
        public PagedListHolder<T> invokeCreate() {
            List<T> result = getModels();
            
                    session.removeAttribute("queryString");
                    return new PagedListHolder<>(result!=null ? result : Collections.EMPTY_LIST);
            
        }
    });
    }

    protected Collection<T> processPagination(HttpServletRequest request,
            Model model, Integer page, String NPpage, Integer pageSize,
            SourceCallback<T> createCall) {

        @SuppressWarnings("unchecked")
        PagedListHolder<T> pagedListHolder = (PagedListHolder<T>) request.getSession().getAttribute(NODE_LIST_MODEL_PAGE);

        if (pagedListHolder == null || (page == null && NPpage == null && pageSize == null)  ) {
            pagedListHolder = createCall.invokeCreate();
            getLogger().debug("pagedListHolder created ");
        } else {

            if (pageSize != null) {
                pagedListHolder.setPageSize(pageSize);
            } else if (NPpage != null) {

                if (NPpage.equals(NEXT_PARAM)) {
                    pagedListHolder.nextPage();
                } else {
                    pagedListHolder.previousPage();
                }

            } else if (page!=null) {

                if (page < 1) {
                    page = 1;
                }
                if (page > pagedListHolder.getPageCount()) {
                    page = pagedListHolder.getPageCount();
                }

                pagedListHolder.setPage(--page);
            }

        }

        

        request.getSession().setAttribute(NODE_LIST_MODEL_PAGE, pagedListHolder);

        model.addAttribute(NODE_LIST_MODEL_PAGE, pagedListHolder);

        return pagedListHolder.getPageList();

    }
}
