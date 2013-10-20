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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uber.megashare.base.LoggedClass;
import uber.megashare.dao.SettingsDao;
import uber.megashare.model.SystemProperties;

/**
 *
 * @author alex
 */
@Controller
@RequestMapping("/settings")
public class SettingsController extends LoggedClass {

    /**
     *
     */
    private static final long serialVersionUID = 1241118631588884180L;
    
    private static final String viewPage = "redirect:view.html";
    
    protected SettingsDao settingsDao;

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public SystemProperties view() {
        return settingsDao.getCurrentSettings();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public SystemProperties edit() {
        return settingsDao.getCurrentSettings();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@RequestParam(required = false) String cancel, SystemProperties b, Model model) {

        if (cancel != null) {
            model.addAttribute("statusMessageKey", "action.cancelled");
            return viewPage;
        }


        SystemProperties result = settingsDao.saveObject(b);

        // set id from create
        if (b.getId() == null) {
            b.setId(result.getId());
        }

        model.addAttribute("statusMessageKey", "action.success");
        return viewPage;
    }
}
