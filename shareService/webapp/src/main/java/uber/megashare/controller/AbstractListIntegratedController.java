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

import java.util.Collection;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import static uber.megashare.controller.GenericController.INTEGRATED_PREFIX;
import static uber.megashare.controller.ListConstants.LIST_ACTION;
import uber.megashare.model.SearchQuery;
import uber.megashare.model.Struct;
import uber.megashare.service.GenericSearchableManager;

/**
 * Abstract parent controller for list with integrated view
 * @author aachernyshev
 */
public abstract class AbstractListIntegratedController<T extends Struct, SQ extends SearchQuery> 
extends GenericSearchableController<T, SQ> implements ListConstants
    {
    
     protected AbstractListIntegratedController(GenericSearchableManager<T, SQ> manager) {
        super(manager);
     }
    
    /**
     * 
     * @param integrationCode unique integration code
     * @param sortColumn sort column    
     * @param request http request object
     * @param session http session
     * @param model generic model
     * @param locale http useragent locale
     * @return collection of model objects 
     */ 
    @RequestMapping(value = {INTEGRATED_PREFIX +LIST_ACTION + "/{integrationCode:[a-z0-9_]+}/sort/{sortColumn:[a-zA-Z0-9]+}",
                             INTEGRATED_PREFIX +LIST_ACTION + "/{integrationCode:[a-z0-9_]+}/sort/{sortColumn:[a-zA-Z0-9]+}/up"}, 
                             method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listWithSortIntegrated(
            @PathVariable("integrationCode") String integrationCode,
            @PathVariable("sortColumn") String sortColumn,
            HttpServletRequest request,
            final HttpSession session,
            Model model,
            Locale locale) {
        return listIntegrated(integrationCode, request,session, model, null, null,null, sortColumn,false, locale);
    }
    
    
    @RequestMapping(value = INTEGRATED_PREFIX +LIST_ACTION + "/{integrationCode:[a-z0-9_]+}/sort/{sortColumn:[a-zA-Z0-9]+}/down", 
                    method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listWithSortDownIntegrated(
            @PathVariable("integrationCode") String integrationCode,
            @PathVariable("sortColumn") String sortColumn,
            HttpServletRequest request,
            final HttpSession session,
            Model model,
            Locale locale) {
        return listIntegrated(integrationCode,request,session, model, null, null,null, sortColumn,true, locale);
    } 
     
    @RequestMapping(value = INTEGRATED_PREFIX + LIST_ACTION + "/{integrationCode:[a-z0-9_]+}/{page:[0-9]+}", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listByPathIntegrated(
            @PathVariable("integrationCode") String integrationCode,
            @PathVariable("page") Integer page,
            HttpServletRequest request,
            final HttpSession session,
            Model model,
            Locale locale) {
        return listIntegrated(integrationCode, request, session, model, page, null, null,null,false, locale);
    }

    @RequestMapping(value =INTEGRATED_PREFIX+LIST_ACTION+"/{integrationCode:[a-z0-9_]+}/limit/{pageSize:[0-9]+}", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listByPathSizeIntegrated(
             @PathVariable("integrationCode") String integrationCode,
             @PathVariable("pageSize") Integer pageSize,
            HttpServletRequest request,
             final HttpSession session,
            Model model,
            Locale locale) {
        return listIntegrated(integrationCode,request, session, model, null, null, pageSize,null,false, locale);
    }

    @RequestMapping(value = INTEGRATED_PREFIX+LIST_ACTION+"/{integrationCode:[a-z0-9_]+}/next", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listByPathNextIntegrated(
             @PathVariable("integrationCode") String integrationCode,
            HttpServletRequest request,
             final HttpSession session,
            Model model,
            Locale locale) {
        return listIntegrated(integrationCode,request, session, model, null, NEXT_PARAM, null,null,false, locale);
    }

    @RequestMapping(value = INTEGRATED_PREFIX+LIST_ACTION+"/{integrationCode:[a-z0-9_]+}/prev", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listByPathPrevIntegrated(
             @PathVariable("integrationCode") String integrationCode,
             HttpServletRequest request,
              final HttpSession session,
            Model model,
            Locale locale) {
        return listIntegrated(integrationCode,request, session, model, null, "prev", null,null,false, locale);
    }

    /**
     * Returns list of objects T related to integration code
     * @param integrationCode provided integration code
     * @param request http request object
     * @param session http session object
     * @param model model of type T
     * @param page selected page
     * @param NPpage scroll to next page
     * @param pageSize objects per page
     * @param sortColumn selected sort column
     * @param sortAsc need to sort asc ?
     * @param locale http useragent locale
     * @return 
     */
    @RequestMapping(value = INTEGRATED_PREFIX+LIST_ACTION+"/{integrationCode:[a-z0-9_]+}", method = RequestMethod.GET)
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> listIntegrated(
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
      
        putListModel(model, locale);

        return processPagination(request, model, page, NPpage, pageSize, sortColumn, sortAsc, new SourceCallback<T>() {
            @Override
            public PagedListHolder<T> invokeCreate() {
                return new ExtendedPagedListHolder<>(manager.getObjectsForIntegration(integrationCode),"list_integrated");
            }
        });
    }

     
}
