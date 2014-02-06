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

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uber.megashare.base.logging.LoggedCall;
import static uber.megashare.controller.EditConstants.VIEW_ACTION;
import static uber.megashare.controller.AbstractEditController.DELETE_ACTION;
import static uber.megashare.controller.AbstractEditController.SAVE_ACTION;
import static uber.megashare.controller.ListConstants.LIST_ACTION;
import static uber.megashare.controller.SharedFileConstants.FILE_PREFIX;
import uber.megashare.model.AccessLevel;
import uber.megashare.model.Project;
import uber.megashare.model.SharedFile;
import uber.megashare.model.SharedFileSearchQuery;
import uber.megashare.model.Struct;
import uber.megashare.model.User;
import uber.megashare.model.xml.XMLField;
import uber.megashare.model.xml.XMLObject;
import uber.megashare.service.SharedFileManager;
import uber.megashare.service.image.ImageBuilder;


/**
 * Implementation of MVC Controller to manage single file (upload,download,edit)
 *
 * @author alex
 */
@Controller
@RequestMapping(FILE_PREFIX)
public class SharedFileEditController extends AbstractCommentController<SharedFile> implements SharedFileConstants,ListConstants  {

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
    
    @InitBinder
    protected void initBinder(WebDataBinder binder,Locale locale) {
       
        
        //The date format to parse or output your dates
        SimpleDateFormat dateFormat = new SimpleDateFormat(messageSource.getMessage("date.format", null, locale));
        //Create a new CustomDateEditor
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        //Register it as custom editor for the Date type
        binder.registerCustomEditor(Date.class, editor);
     
       binder.registerCustomEditor(Set.class,"relatedProjects", new ProjectCollectionEditor(Set.class));
       binder.registerCustomEditor(Set.class,"relatedUsers", new UserCollectionEditor(Set.class));
     // binder.registerCustomEditor(Set.class, new CustomCollectionEditor(Set.class));  
    }
    
    
   
    @RequestMapping(value = RAW_PREFIX+"/comments", method = RequestMethod.GET)
    public String viewRawComments(@RequestParam(required = true) Long id, Model model,Locale locale) {

        String sp = super.view(id, model,locale);
        return !sp.equals(viewPage)? sp : FILE_PREFIX +RAW_PREFIX+"/comments";
    }

    
    @RequestMapping(value = RAW_PREFIX+VIEW_ACTION, method = RequestMethod.GET)
    public String viewRaw(@RequestParam(required = true) Long id, Model model,Locale locale) {

        String sp = super.view(id, model,locale);

        return !sp.equals(viewPage)? sp : FILE_PREFIX +RAW_PREFIX+VIEW_ACTION;
    }

     @RequestMapping(value = RAW_PREFIX+"/pdfview", method = RequestMethod.GET)
    public String viewPdf(@RequestParam(required = true) Long id,
    @RequestParam(required = false) Long revision, Model model,Locale locale) {

        String sp = super.view(id,revision, model,locale);

        return !sp.equals(viewPage)? sp : FILE_PREFIX +RAW_PREFIX+"/pdfview";
    }

    
    @RequestMapping(value = INTEGRATED_PREFIX+VIEW_ACTION, method = RequestMethod.GET)
    public String viewIntegrated(@RequestParam(required = true) Long id,
                                 @RequestParam(required = false) Long revision, Model model,Locale locale) {

        String sp = super.view(id,revision, model,locale);
        return !sp.equals(viewPage) ? sp : FILE_PREFIX +INTEGRATED_PREFIX+"/view";
    }

    @Override
    protected boolean checkAccess(SharedFile obj, Model model) {

        /**
         * if file is viewable for all or  if user is admin
         */
        if (obj.getAccessLevel().equals(AccessLevel.ALL) ||isCurrentUserAdmin()) {
            return true;
        }
      
        
        if (obj.getAccessLevel().equals(AccessLevel.PROJECT) ) {
            return !isCurrentUserLoggedIn()? false : obj.getRelatedProjects().contains(getCurrentUser().getRelatedProject());
        }
        
          
        if (obj.getAccessLevel().equals(AccessLevel.USERS) ) {
            return !isCurrentUserLoggedIn()? false : obj.getRelatedUsers().contains(getCurrentUser()) 
                    || getCurrentUser().equals(obj.getOwner());
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
                
                 for(XMLField f:input.getXml().getFields().values()) {
            
                       System.out.println("field "+f);
                    }
                
                input.getXml().rebuildFields();
                
                  
     
                
                SharedFile old;

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
                    
                     if (input.getRelatedProjects().isEmpty()) {
                        input.setRelatedProjects(old.getRelatedProjects());
                       // input.getRelatedProjects().add(getCurrentUser().getRelatedProject());
                    }
                    
                    /**
                     * allow upload and replace exist pulic file for all logged in users
                     */
                    if (old.getAccessLevel()== AccessLevel.ALL && !input.getOwner().equals(getCurrentUser())) {
                        input.setAccessLevel(AccessLevel.ALL);
                        input.setOwner(getCurrentUser());
                    }
                    
                    /**
                     *  if file is integrated - it can be only public
                     */
                    if (input.getIntegrationCode()!=null) {
                        input.setAccessLevel(AccessLevel.ALL);
                    }
                } else {
                    /**
                     * cos we allow upload only for authorized users - there
                     * always will be an user
                     */
                    input.setOwner(getCurrentUser());

                    if (input.getRelatedProjects().isEmpty()) {
                        input.getRelatedProjects().add(getCurrentUser().getRelatedProject());
                    }

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
                    
                    String calcPath = settingsManager.getCalculatedFileDir(input.getCreated(),
                            input.getUuid(),
                            input.isBlank() ? 0 : 
                            smanager.getCurrentRevisionNumber(input.getId()).longValue()+1);
                    
                    File fullUploadPath = new File(settingsManager.getCurrentSettings()
                            .getUploadDir(),calcPath);
                    fullUploadPath.mkdirs();
                    
                    input.setUrl(calcPath +File.separator+input.getUuid() +"."+ ext);

                
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
                        
                        
                        input.setPreviewUrl(calcPath +File.separator+input.getUuid()+ "_preview.png");
                        
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
     
        /**
         * perform actual file save & thumbnail generation
         */
        
        
        SharedFile out =uploadSave(b.getFile(), b);

        getLogger().debug(
                "saved new file id=" + out.getId());
        
        resetPagingList(request);
         
        return out;
       
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
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        /**
         * allow upload only for authorized users
         */
        if (!isCurrentUserLoggedIn()) {
            addMessageDenied(redirectAttributes);
            return b.getIntegrationCode()!=null ? FILE_PREFIX+INTEGRATED_PREFIX+LIST_ACTION : editPage;
        }

        /**
         * process cancel button
         */
        if (cancel != null) {
            addMessageCancelled(redirectAttributes);
            
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
                
                
                /**
                 * push back full object
                 */
                SharedFile old = manager.getFull(b.getId());
                    
                XMLObject ox = b.getXml();
               
                ox.rebuildFields();
                
                b.fillFrom(old);
                b.setXml(ox);
                
                
                //model.addAttribute(MODEL_KEY, old);
                return  b.getIntegrationCode()!=null ? FILE_PREFIX+INTEGRATED_PREFIX+LIST_ACTION :  editPage;
            }

        }

        
        getLogger().debug("update file=" + b);

        SharedFile f = uploadSave(b.getFile(), b);
        //manager.save(b);

        if (b.isBlank()) {
            b.setId(f.getId());
        }
     
        resetPagingList(request);

        addMessageSuccess(redirectAttributes);

        model.asMap().clear();   
        
        if (f.getIntegrationCode()!=null) {
            return "redirect:/main"+FILE_PREFIX+INTEGRATED_PREFIX+LIST_ACTION+"/"+f.getIntegrationCode();
        }
        
        return listPage;
    }

    @Override
    public SharedFile getNewModelInstance() {
        SharedFile out = new SharedFile();
        out.getRelatedProjects().add(getCurrentUser().getRelatedProject());
        
        XMLField f = new XMLField();
        
        f.setName("xxxKey");
        f.setValue("Test value");
        
        out.getXml().getFields().put(f.getUuid(),f);
        return out;
    }

    
     @RequestMapping(value = DELETE_ACTION, method = {RequestMethod.GET, RequestMethod.POST})
    @Override
    public String delete(@RequestParam(required = true) Long id,
            Model model, HttpServletRequest request) {
        
         SharedFile file = get(id);
         if (file==null) {
             return page404;
         }
         
            manager.remove(id);
            resetPagingList(request);            
             
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
    
    
    class UserCollectionEditor extends AbstractCollectionCustomEditor<User> {
    
        public UserCollectionEditor(Class collectionType) {
            super(User.class,collectionType);
        }
    }
    
    class ProjectCollectionEditor extends AbstractCollectionCustomEditor<Project> {
    
        public ProjectCollectionEditor(Class collectionType) {
            super(Project.class,collectionType);
        }
    }
    
    abstract class AbstractCollectionCustomEditor<T extends Struct> extends CustomCollectionEditor {
        
        private Class<T> modelClass;
        
        public  AbstractCollectionCustomEditor(Class<T> modelType, Class collectionType) {
            super(collectionType);
            this.modelClass=modelType;
        }
        
    @Override
    protected Object convertElement(Object element) {
        System.out.println("getSting : " + element.toString()+" element class="+element.getClass().getName());
        if (element.getClass().isAssignableFrom(modelClass)) {
            return element;
        }
        
        
            try {
            
                T p = modelClass.newInstance();
                 p.setId(Long.valueOf(element.toString()));
                return p;
            
            } catch (InstantiationException |IllegalAccessException ex) {
               throw new IllegalStateException(ex);
            }
    }
    
    }
}
