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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uber.megashare.base.MimeSupport;
import uber.megashare.base.logging.LoggedCall;
import static uber.megashare.controller.EditConstants.VIEW_ACTION;
import static uber.megashare.controller.GenericEditController.DELETE_ACTION;
import static uber.megashare.controller.GenericEditController.SAVE_ACTION;
import static uber.megashare.controller.ListConstants.LIST_ACTION;
import static uber.megashare.controller.SharedFileConstants.FILE_PREFIX;
import uber.megashare.model.AccessLevel;
import uber.megashare.model.SharedFile;
import uber.megashare.model.SharedFileSearchQuery;
import uber.megashare.service.SharedFileManager;
import uber.megashare.service.image.ImageBuilder;
import uber.megashare.service.servlet.ServletUtils;


/**
 * Implementation of MVC Controller to manage single file (upload,download,edit)
 *
 * @author alex
 */
@Controller
@RequestMapping(FILE_PREFIX)
public class SharedFileEditController extends GenericCommentController<SharedFile> implements SharedFileConstants,ListConstants  {

    /**
     *
     */
    private static final long serialVersionUID = -7829803368219070244L;

    private SharedFileManager fileManager;
    
    @Autowired
    public SharedFileEditController(SharedFileManager fileManager) {
        super(fileManager);
        this.fileManager=fileManager;

        setListPage("redirect:list");
        /**
         * no redirect here due to field validation issues
         */
        setEditPage(FILE_PREFIX+EDIT_ACTION);
        setViewPage(FILE_PREFIX+VIEW_ACTION);

    }

    @RequestMapping(value = RAW_PREFIX+VIEW_ACTION, method = RequestMethod.GET)
    public String viewRaw(@RequestParam(required = true) Long id, Model model,Locale locale) {

        String sp = super.view(id, model,locale);

        return !sp.equals(viewPage)? sp : FILE_PREFIX +RAW_PREFIX+VIEW_ACTION;
    }

     @RequestMapping(value = RAW_PREFIX+"/pdfview", method = RequestMethod.GET)
    public String viewPdf(@RequestParam(required = true) Long id, Model model,Locale locale) {

        String sp = super.view(id, model,locale);

        return !sp.equals(viewPage)? sp : FILE_PREFIX +RAW_PREFIX+"/pdfview";
    }

    
    @RequestMapping(value = INTEGRATED_PREFIX+VIEW_ACTION, method = RequestMethod.GET)
    public String viewIntegrated(@RequestParam(required = true) Long id, Model model,Locale locale) {

        String sp = super.view(id, model,locale);
        return !sp.equals(viewPage) ? sp : FILE_PREFIX +INTEGRATED_PREFIX+"/view";
    }

    @Override
    protected boolean checkAccess(SharedFile obj, Model model) {

        /**
         * if file is viewable for all
         */
        if (obj.getAccessLevel().equals(AccessLevel.ALL)) {
            return true;
        }
        /**
         * if user is admin
         */
        if (isCurrentUserAdmin()) {
            return true;
        }

        /**
         * if current user is file owner
         */
        return (isCurrentUserLoggedIn() && getCurrentUser().equals(obj.getOwner()));
    }

    /**
     * actual uploaded file processing is here
     *
     * @param mfile uploaded file
     * @param input a set of properties inside SharedFile instance
     */
    private SharedFile uploadSave(final MultipartFile mfile, final SharedFile input) {


        return new LoggedCall<SharedFile>() {
            @Override
            public SharedFile callImpl(ToStringBuilder log) throws Exception {

                log.append("uploadSave starting ,currentUser=" + getCurrentUser() + " file=" + input);

                SharedFile old = null;

                if (!input.isBlank()) {
                    old = manager.getFull(input.getId());
                    input.setComments(old.getComments());
                    input.setOwner(old.getOwner());
                    input.setFileSize(old.getFileSize());
                    input.setMime(old.getMime());
                    input.setPreviewUrl(old.getPreviewUrl());
                    input.setType(old.getType());
                    input.setUrl(old.getUrl());
                    input.setUuid(old.getUuid());
                    input.setName(old.getName());
                    input.setIntegrationCode(old.getIntegrationCode());
                    
                    if (input.getIntegrationCode()!=null) {
                        input.setAccessLevel(AccessLevel.ALL);
                    }
                } else {
                    /**
                     * cos we allow upload only for authorized users - there
                     * always will be an user
                     */
                    input.setOwner(getCurrentUser());
                }

                //SharedFile out = new SharedFile();

                if (mfile != null && !mfile.isEmpty()) {

                    input.resetContent();

                    input.setName(mfile.getOriginalFilename());

                    /**
                     * user can specify only access level for now
                     */
                    /* if (input != null) {
                     out.setAccessLevel(input.getAccessLevel());
                     }/*

               
                     /**
                     * this is initial mime, specified by user's browser for uploaded file
                     */
                   // input.setMime(mfile.getContentType());
                    /**
                     * ..and size
                     */
                    input.setFileSize(mfile.getSize());

                    
                       /**
                     * calc file extension from file name
                     */
                    String ext= FilenameUtils.getExtension(input.getFile().getOriginalFilename());
                    if (ext==null) {
                        ext = "data";
                    }
                    String mime = fileManager.getMimeExt(ext);
                   
                    //System.out.println("__ext "+ext+" mime "+mime);
                
                    if (mime==null) {
                        mime = input.getFile().getContentType();
                    }
                    input.setMime(mime);

                    /**
                     * and there we set abstract file type (for icons and
                     * download attachment/inline properties)
                     */
                    input.setType(fileManager.lookupType(mime));
                    
                    /**
                     * this is actual local file name
                     *
                     */
                    
                    String calcPath = settingsManager.getCalculatedFileDir(input.getLastModified(), 
                            input.isBlank() ? 0 : 
                            smanager.getCurrentRevisionNumber(input.getId()).longValue()+1);
                    
                    File fullUploadPath = new File(settingsManager.getCurrentSettings()
                            .getUploadDir(),calcPath);
                    fullUploadPath.mkdirs();
                    
                    input.setUrl(calcPath +File.separator+System.currentTimeMillis() +"."+ ext);

                
                    File fout = new File(settingsManager.getCurrentSettings()
                            .getUploadDir(), input.getUrl());


                    /**
                     * save attachment to file
                     */
                    mfile.transferTo(fout);

                    /**
                     * create preview if applicable
                     */
                    ImageBuilder builder = ImageBuilder.createInstance().setSource(
                            new FileInputStream(fout));


                    if (!builder.isUnsupported()) {
                        byte[] img = builder.scaleToProfile().getScaledAsBytes();
                        
                        input.setPreviewHeight(builder.getImageInfo().getHeight());
                        input.setPreviewWidth(builder.getImageInfo().getWidth());
                        
                        
                        input.setPreviewUrl(calcPath +File.separator+System.currentTimeMillis() + "_preview.png");
                        
                        FileUtils.writeByteArrayToFile(
                                new File(settingsManager.getCurrentSettings().getUploadDir(), input.getPreviewUrl()), img);
                    } else {

                        log.append("image preview generation is not supported for file " + fout.getName());
                    }

                }
                SharedFile out = manager.save(input);

                log.append("saved file " + out);

                return out;

            }
        }.call();


    }

    /**
     * this function designed to be called from javascript via ajax or from
     * external clients
     *
     * @param inputFile
     * @param response
     * @return json with result code
     */
    @ResponseBody
    @RequestMapping("/upload")
    public String save(
            @RequestParam(value = "file", required = true) MultipartFile inputFile,
            HttpServletResponse response, HttpServletRequest request) {

        if (!isCurrentUserLoggedIn()) {
            return "{result:access-denied}";
        }

        getLogger().debug("upload mime=" + inputFile.getContentType());

        uploadSave(inputFile, null);
        resetPagingList(request);
        return "{result:ok}";
    }

    @ResponseBody
    @RequestMapping(value = "/upload-xdr", method = RequestMethod.POST, produces = "application/json")
    public Object uploadAjax(@Valid
            @ModelAttribute(MODEL_KEY) SharedFile b,
            BindingResult result, Model model,
            HttpServletResponse response, HttpServletRequest request)  {



        if (!isCurrentUserLoggedIn()) {
            return createJsonError("access-denied");
        }

        /**
         * check if file was ever selected
         */
        if (b.isBlank() && b.getFile().isEmpty()) {
            return createJsonError("no-file-selected");
        }
        
      //  System.out.println("_input mime "+b.getFile().getContentType());
        

        /*if (b.isBlank()) {*/

        /**
         * perform actual file save & thumbnail generation
         */
        
        SharedFile out =uploadSave(b.getFile(), b);

        getLogger().debug(
                "saved new file id=" + out.getId());
        
        resetPagingList(request);
         
        return out;
        /*
         } else {
            
         // SharedFile out = uploadSave(b.getFile(), b);
         return createJsonError("not supported");

         }
         */
    }

    private String createJsonError(String msg) {
        return "{\"error\":\"+msg\"}";
    }

    @RequestMapping(value = {SAVE_ACTION,SAVE_ACTION+INTEGRATED_PREFIX}, method = RequestMethod.POST)
    @Override
    public String save(@RequestParam(required = false) String cancel,
            @Valid
            @ModelAttribute(MODEL_KEY) SharedFile b,
            BindingResult result, Model model,
            HttpServletRequest request) {

        /**
         * allow upload only for authorized users
         */
        if (!isCurrentUserLoggedIn()) {
            addMessageDenied(model);
            return b.getIntegrationCode()!=null ? FILE_PREFIX+INTEGRATED_PREFIX+LIST_ACTION : editPage;
        }

        /**
         * process cancel button
         */
        if (cancel != null) {
            addMessageCancelled(model);
            
            model.asMap().clear();
   
            return b.getIntegrationCode()!=null ? FILE_PREFIX+INTEGRATED_PREFIX+LIST_ACTION: listPage;
        }

        /**
         * check if file was ever selected
         */
        if (b.isBlank() && b.getFile().isEmpty()) {

            model.addAttribute("statusMessageKey", "no-file-selected");
            return b.getIntegrationCode()!=null ? FILE_PREFIX+INTEGRATED_PREFIX+LIST_ACTION : editPage;
        }

        /**
         * return back to edit form if there are any errors
         */
        if (result.hasErrors()) {
            getLogger().debug("form has errors " + result.getErrorCount());
            for (FieldError f : result.getFieldErrors()) {
                /**
                 * skip fields that will be filled after file upload
                 */
                if (f.getField().equals("name") || f.getField().equals("url")
                        || f.getField().equals("uuid")) {
                    continue;
                }
                getLogger().debug(
                        "field=" + f.getField() + ",rejected value="
                        + f.getRejectedValue());
                return  b.getIntegrationCode()!=null ? FILE_PREFIX+INTEGRATED_PREFIX+LIST_ACTION :  editPage;
            }

        }

        /*if (b.isBlank()) {

         getLogger().debug(
         "save new file mime=" + b.getFile().getContentType());
         uploadSave(b.getFile(), b);
         } else {
         */
        getLogger().debug("update file=" + b);

        SharedFile f = uploadSave(b.getFile(), b);
        //manager.save(b);

        if (b.isBlank()) {
            b.setId(f.getId());
        }
        //      }



        resetPagingList(request);

        addMessageSuccess(model);

        model.asMap().clear();
   
        
        if (f.getIntegrationCode()!=null) {
            return "redirect:/main"+FILE_PREFIX+INTEGRATED_PREFIX+LIST_ACTION+"/"+f.getIntegrationCode();
        }
        
        return listPage;
    }

    @Override
    public SharedFile getNewModelInstance() {
        return new SharedFile();
    }

    
     @RequestMapping(value = DELETE_ACTION, method = {RequestMethod.GET, RequestMethod.POST})
    @Override
    public String delete(@RequestParam(required = true) Long id,
            Model model, HttpServletRequest request) {
        
         SharedFile file = get(id);
         if (file!=null) {
            manager.remove(id);
            resetPagingList(request);
         }
             
         model.asMap().clear();
             
        return file.getIntegrationCode()!=null ? "redirect:/main" + FILE_PREFIX +INTEGRATED_PREFIX+LIST_ACTION+"/"+file.getIntegrationCode() : listPage;
    }
    
    /**
     * instantiate the new search query object
     */
    @ModelAttribute(QUERY_MODEL)
    public SharedFileSearchQuery getNewQuery() {

        SharedFileSearchQuery query = new SharedFileSearchQuery();

        if (isCurrentUserLoggedIn()) {

            if (query.getUserId() == null) {
                query.setUserId(getCurrentUser().getId());
            }

            query.setLevels(Arrays.asList(AccessLevel.values()));
        } else {

            query.setLevels(Arrays.asList(AccessLevel.ALL));
        }
        return query;
    }
}
