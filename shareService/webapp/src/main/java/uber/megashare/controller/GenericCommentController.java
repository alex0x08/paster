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
import javax.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uber.megashare.model.Comment;
import uber.megashare.model.CommentedStruct;
import uber.megashare.service.GenericVersioningManager;

/**
 *
 * @author achernyshev
 */
public abstract class GenericCommentController<T extends CommentedStruct> extends GenericVersioningController<T> {

    protected static final String ADD_COMMENT_ACTION = "/addComment",
            COMMENT_MODEL_KEY = "newComment";

    public GenericCommentController(GenericVersioningManager<T> manager) {
        super(manager);
    }
    
    @ModelAttribute(COMMENT_MODEL_KEY)
    public Comment getNewCommentModel() {
        Comment c= new Comment();
        c.setAuthor(getCurrentUser());
        return c;
    }

    @RequestMapping(value = ADD_COMMENT_ACTION, method = RequestMethod.POST)
    public String addComment(@RequestParam(required = true) Long modelId,
            @Valid
            @ModelAttribute(COMMENT_MODEL_KEY) Comment newComment,
            BindingResult result, Model model, 
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {


        if (!isCurrentUserLoggedIn()) {
            addMessageDenied(redirectAttributes);
            return viewPage;
        }

        if (result.hasErrors()) {
            
            if (getLogger().isDebugEnabled()) {
                debugPrintRejected(result);
            }
            return viewPage;
        }

        T b = manager.getFull(modelId);
        
        if (b==null) {
            return page404;
        }

        b.getComments().add(newComment);

        T r = manager.save(b);


        /**
         * set id from create this is needed to correct model's id postback when
         * validation fails
         */
        if (b.isBlank()) {
            b.setId(r.getId());
        }

        //resetPagingList(request);

        addMessageSuccess(redirectAttributes);
        
        /*  model.addAttribute(MODEL_KEY,r);

          model.addAttribute(COMMENT_MODEL_KEY, getNewCommentModel());
          
        putModel(r, model);
        */
   
        model.asMap().clear();
        
      return  "redirect:/main"+viewPage+"?id="+r.getId();
    }
}
