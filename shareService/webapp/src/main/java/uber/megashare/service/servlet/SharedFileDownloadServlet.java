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
package uber.megashare.service.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import uber.megashare.model.AccessLevel;
import uber.megashare.model.FileType;
import uber.megashare.model.SharedFile;
import uber.megashare.service.SettingsManager;
import uber.megashare.service.SharedFileManager;
import static uber.megashare.service.servlet.AbstractBaseServlet.writeError;

/**
 *
 * @author alex
 */
public class SharedFileDownloadServlet extends AbstractBaseServlet {

    /**
     *
     */
    private static final long serialVersionUID = 2735894619181978770L;
    private static final int CACHE_DURATION_IN_SECOND = 60 * 60 * 24 * 2; // 2 days
    private static final long CACHE_DURATION_IN_MS = CACHE_DURATION_IN_SECOND * 1000;
    
    private SharedFileManager manager;
    
    private SettingsManager settingsManager;

    @Override
    public void initImpl() {

        manager = appContext.getBean(SharedFileManager.class);
        settingsManager = appContext.getBean(SettingsManager.class);

        log.debug("download servlet initialized");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-f

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        String id = request.getParameter("id");

        if (id == null) {
            writeError(response, "file id not set");
            return;
        }
        
        Long revision = NumberUtils.toLong(request.getParameter("revision"));
       

        SharedFile f =  revision>0 ? manager.getRevision(NumberUtils.toLong(id), revision) : manager.getFileFromUUID(id);
        if (f == null) {
            
            f = manager.getFull(NumberUtils.toLong(id));
            
            if (f==null ){
                writeError(response, "No such file id=" + id);
                return;
            }
        }


        if (!f.getAccessLevel().equals(AccessLevel.ALL) ) {
          
            if (getCurrentUser(request)==null || (!getCurrentUser(request).isAdmin()
                    && !f.getOwner().equals(getCurrentUser(request)))) {

                writeError(response, "Access denied on file " + id);
                return;
            }

        }

        boolean preview = request.getParameter("preview") != null;


        if (preview) {
            response.setContentType("image/png");

            File fpreview = new File(settingsManager.getCurrentSettings().getUploadDir(), f.getPreviewUrl());
            
            log.debug("loading file "+fpreview.getAbsolutePath());
            
            
            if (!fpreview.exists() || !fpreview.isFile()) {
                writeError(response, "Preview file not found " + id);
                return;
            }

            try (FileInputStream in = new FileInputStream(fpreview)) {
                IOUtils.copy(in, response.getOutputStream());
            }
            return;
        }

        setContentDispositionHeader(request, response,
                f.getType() != FileType.IMAGE && f.getType() != FileType.TEXT,
                f.getName());

        response.setContentType(f.getMime());

        long now = System.currentTimeMillis();
        
        response.addHeader("Cache-Control", "max-age=" + CACHE_DURATION_IN_SECOND);
        response.addHeader("Cache-Control", "must-revalidate");
        response.setDateHeader("Last-Modified", now);
        response.setDateHeader("Expires", now + CACHE_DURATION_IN_MS);


        File ff = new File(settingsManager.getCurrentSettings().getUploadDir(), f.getUrl());

        log.debug("loading file "+ff.getAbsolutePath());
            
        
        if (!ff.exists() || !ff.isFile()) {
            writeError(response, "The file was not found " + id);
            return;
        }

        try (FileInputStream stream = new FileInputStream(ff)) {
            IOUtils.copyLarge(stream, response.getOutputStream());
        }
    }

    private void setContentDispositionHeader(HttpServletRequest request, 
            HttpServletResponse response, boolean isAttachment, 
            String fileName) {


        StringBuilder b = new StringBuilder(isAttachment ? "attachment; " : "inline; ");
        //if (isIE(request)) {
            b.append("filename=");
       // } else {
        //    b.append("filename*=UTF-8''");
       // }
        b.append(fileName);

        response.setHeader("Content-Disposition", b.toString());
    }

    private boolean isIE(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent == null ? false : userAgent.contains("MSIE");
    }
}
