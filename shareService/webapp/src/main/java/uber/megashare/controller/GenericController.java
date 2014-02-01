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

import javax.servlet.http.HttpServletRequest;
import uber.megashare.model.Struct;
import uber.megashare.service.StructManager;

/**
 * Abstract Controller dedicated to specific generic object (T)
 *
 * @author alex
 */
public abstract class GenericController<T extends Struct> extends AbstractController {

    /**
     *
     */
    private static final long serialVersionUID = -1537801120061935523L;
    /**
     * name of model with list of T objects
     */
    protected static final String NODE_LIST_MODEL = "items",
            NODE_LIST_MODEL_PAGE = "pageItems";
    
    protected static final String INTEGRATED_PREFIX = "/integrated",
                                  RAW_PREFIX="/raw";
 
    
    protected String listPage, //default list page for T object
            editPage, //.. edit page ..
            viewPage;
    
    protected static final String QUERY_MODEL = "query";
    
    protected final StructManager<T> manager;

    protected GenericController(StructManager<T> manager) {
        this.manager = manager;
    }

    protected void resetPagingList(HttpServletRequest request) {
        request.getSession().removeAttribute(NODE_LIST_MODEL_PAGE);
    }

    public String getViewPage() {
        return viewPage;
    }

    public void setViewPage(String viewPage) {
        this.viewPage = viewPage;
    }

    public String getEditPage() {
        return editPage;
    }

    protected final void setEditPage(String editPage) {
        this.editPage = editPage;
    }

    public String getListPage() {
        return listPage;
    }

    protected final void setListPage(String listPage) {
        this.listPage = listPage;
    }
}
