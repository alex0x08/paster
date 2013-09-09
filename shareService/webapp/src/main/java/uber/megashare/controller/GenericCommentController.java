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

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uber.kaba.markup.parser.AppMode;
import uber.kaba.markup.parser.KabaMarkupParser;
import uber.megashare.model.Comment;
import uber.megashare.model.CommentedStruct;
import uber.megashare.service.CommentManager;
import uber.megashare.service.GenericVersioningManager;

/**
 *
 * @author achernyshev
 */
public abstract class GenericCommentController<T extends CommentedStruct> extends GenericVersioningController<T> {

    protected static final String ADD_COMMENT_ACTION = "/addComment",REMOVE_COMMENT_ACTION = "/deleteComment",
            COMMENT_MODEL_KEY = "newComment";

    @Autowired
    private CommentManager commentManager;
    
    
    public GenericCommentController(GenericVersioningManager<T> manager) {
        super(manager);
    }
    
    @ModelAttribute(COMMENT_MODEL_KEY)
    public Comment getNewCommentModel() {
        Comment c= new Comment();
        c.setAuthor(getCurrentUser());
        return c;
    }
    
    @RequestMapping(value = REMOVE_COMMENT_ACTION, method = {RequestMethod.POST,RequestMethod.GET})
    public String deleteComment(
            @RequestParam(required = true) Long modelId,
            @RequestParam(required = true) Long commentId, 
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        if (!isCurrentUserLoggedIn()) {
            addMessageDenied(redirectAttributes);
            return viewPage;
        }
        
         T b = manager.getFull(modelId);
        
        if (b==null) {
            return page404;
        }

        Comment toRemove = new Comment();
        toRemove.setId(commentId);
        
        b.removeComment(toRemove);

        T r = manager.save(b);
        

        addMessageSuccess(redirectAttributes);
     
      return  "redirect:/main"+viewPage+"?id="+r.getId();
    }    

    @RequestMapping(value = ADD_COMMENT_ACTION, method = RequestMethod.POST)
    public String addComment(@RequestParam(required = true) Long modelId,
            @Valid
            @ModelAttribute(COMMENT_MODEL_KEY) Comment newComment,
            BindingResult result, Model model, 
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) throws IOException {


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

        newComment.setMessage(
        KabaMarkupParser.getInstance().setSource(newComment.getMessage()).setMode(AppMode.SHARE)
                .setPasteUrl(pasterUrl).setShareUrl(externalUrl).parseAll().get());
    
        
        b.addComment(newComment);
        
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
