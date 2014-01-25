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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.lucene.queryParser.ParseException;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uber.megashare.model.SearchQuery;
import uber.megashare.model.Struct;
import uber.megashare.service.GenericSearchableManager;

/**
 * 
 * @author alex
 */
public abstract class GenericSearchableController<T extends Struct, SQ extends SearchQuery> extends AbstractListController<T> {

    /**
     *
     */
    private static final long serialVersionUID = -1951673387953448584L;
        
    protected static final String SEARCH_ACTION = "/list/search";
    
    /**
     * associated manager
     */
    protected final GenericSearchableManager<T, SQ> smanager;

    protected GenericSearchableController(GenericSearchableManager<T, SQ> manager) {
        super(manager);
        this.smanager = manager;
    }

    //@ModelAttribute("query")
    public abstract SearchQuery getNewQuery(final HttpSession session);

    @RequestMapping(value = SEARCH_ACTION, method = {RequestMethod.POST,RequestMethod.GET})
    public @ModelAttribute(NODE_LIST_MODEL)
    Collection<T> search(HttpServletRequest request,
            final HttpSession session,
            Model model, Locale locale,
            final @ModelAttribute(QUERY_MODEL) SQ query,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String NPpage,
            @RequestParam(required = false) Integer pageSize
            ) throws ParseException {
     
        putListModel(model, locale);

        return processPagination(request, model, page, NPpage, pageSize,null, false,new SourceCallback<T>() {

            @Override
            public PagedListHolder<T> invokeCreate() {
                try {
                    List<T> result = smanager.search(query);
                    if (result!=null) {
                        session.setAttribute("queryString", query.getQuery());
                    } else {
                        session.removeAttribute("queryString");
                    }
                    return new PagedListHolder<>(result!=null ? result : Collections.EMPTY_LIST);
                } catch (ParseException e) {
                    //getLogger().error(e.getLocalizedMessage(), e);
                    return new PagedListHolder<>(Collections.EMPTY_LIST);
                }
            }
        });



    }
}
