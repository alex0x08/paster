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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.math.NumberUtils;
import uber.megashare.model.AttachedFile;
import uber.megashare.model.Comment;
import uber.megashare.service.CommentManager;

/**
 *
 * @author alex
 */
public class AttachDownloadServlet extends AbstractBaseServlet{
   
    /**
	 * 
	 */
	private static final long serialVersionUID = 3864128267062152463L;
	
	private CommentManager commentsDao;

  
    @Override
    public void initImpl() {     

         commentsDao = appContext.getBean(CommentManager.class);
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {


        Long id  = NumberUtils.toLong(request.getParameter("id"),-1);
        
       
        if (id<=0) {
            writeError(response,"Comment id not set");
            return;
        }

        Comment c = commentsDao.getFull(id);
        if (c==null) {
            writeError(response,"No such Comment id="+id);
            return;

        }

        AttachedFile a = c.getFile();

         if (a==null) {
            writeError(response,"Selected comment does not countain attachment, id="+id);
            return;

        }

        boolean isIcon  =request.getParameter("icon") != null;

           

        if (isIcon) {
                 response.setContentType(AttachedFile.ICON_TYPE);
                 response.getOutputStream().write(a.getIcon());
                 return;
        }

       response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + a.getName());

       response.setContentType(a.getMime());
       response.getOutputStream().write(a.getData());

    }

   

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>


}
