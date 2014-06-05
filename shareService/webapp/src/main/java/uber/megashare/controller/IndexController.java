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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import uber.megashare.model.SharedFile;
import uber.megashare.model.SharedFileSearchQuery;
import uber.megashare.service.SharedFileManager;

/**
 *
 * @author alex
 */
@Controller
public class IndexController extends GenericSearchableController<SharedFile, SharedFileSearchQuery> {

    /**
     *
     */
    private static final long serialVersionUID = 1179212412041609338L;
    
    // private SharedFileManager fileManager;
    @Autowired
    public IndexController(SharedFileManager fileManager) {
        super(fileManager);
        
    }

   /* @RequestMapping(value = "/**\/*")
    public String defaultPage(Model model) {
        
        model.asMap().clear();
        return "redirect:/main/welcome";
    }*/

    @RequestMapping("/login")
    public void login() {
    }

    @RequestMapping("/profile")
    public String profile(Model model, Locale locale) {

        model.addAttribute("user", getCurrentUser());
        
        return "profile/edit";
    }

    @RequestMapping("/welcome")
    public void index(Model model, Locale locale) {
    }

    @ModelAttribute(QUERY_MODEL)
    @Override
    public SharedFileSearchQuery getNewQuery(final HttpSession session) {
        return  new SharedFileSearchQuery();
       // query.setQuery((String)session.getAttribute("queryString"));
      //  return query;
    }

    @Override
    protected void putListModel(Model model, Locale locale) {
    }

    /**
     * called at session start from defaultListCallback
     */
    @Override
    protected List<SharedFile> getModels() {
        return Collections.emptyList();
        //   throw new UnsupportedOperationException("Not supported yet.");
    }
}
