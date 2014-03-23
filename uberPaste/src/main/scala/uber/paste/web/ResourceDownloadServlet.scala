/*
 * Copyright 2014 Ubersoft, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.web

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URLDecoder
import java.nio.ByteBuffer
import java.nio.file.FileSystems
import java.util.Arrays
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils
import uber.paste.base.Loggered
import uber.paste.manager.PasteManager
import uber.paste.manager.ResourcePathHelper

class ResourceDownloadServlet extends HttpServlet with Loggered {

  private var appContext:ApplicationContext = null

  private var resourcePathHelper:ResourcePathHelper = null
  
  override def init() {
    appContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext())
    resourcePathHelper = appContext.getBean(classOf[ResourcePathHelper])
  } 
  
  @throws(classOf[ServletException])
  @throws(classOf[IOException])
  override protected def doGet(request:HttpServletRequest,response:HttpServletResponse) = processRequest(request, response)

  @throws(classOf[ServletException])
  @throws(classOf[IOException])
  override protected def doPost(request:HttpServletRequest,response:HttpServletResponse) = processRequest(request, response)

  @throws(classOf[ServletException])
  @throws(classOf[IOException])
  protected def processRequest( request:HttpServletRequest, response:HttpServletResponse)
  {
    
    var id = request.getParameter("uuid");

    if (id == null) {
      writeError(response, "file id not set",500)
      return
    }
   
    val fimg = resourcePathHelper.getThumb(id)
    if (!fimg.exists || !fimg.isFile) {
      writeError(response, "file not found",404)
      return
    }
   
             
    response.setContentType("image/jpeg");
    response.setHeader("Content-Length", String.valueOf(fimg.length()))
    response.setHeader("Content-Disposition", "inline;filename='" + id + ".jpg'")
   
        readData(fimg,response)
          
  }
    

  
def readData(fimg:File,response:HttpServletResponse) {
  
  var input:FileInputStream = null
  var byteBuffer:ByteBuffer = null
    try {
    input =  new FileInputStream(fimg)
    byteBuffer = ByteBuffer.wrap(new Array[Byte](256 * 1024))
    
    var length:Int = 0
    while( {length = input.getChannel().read(byteBuffer); length != -1 }  ) { 
        
      response.getOutputStream.write(byteBuffer.array, 0, length)
      byteBuffer.clear()
    }
    
  } finally {
    
      try {input.close() }
      try {byteBuffer.clear() }
      
  }
}
  

    
  
  
@throws(classOf[IOException])
def writeError(response:HttpServletResponse, msg:String, status:Int)
{
  response.setContentType("text/html;charset=UTF-8");
  response.setStatus(status);

  try  {
            
    val out = response.getWriter()
            
    out.println(new StringBuilder()
                .append("<html><head><title>ERROR: ")
                .append(msg)
                .append("</title></head><body><h1>ERROR: ")
                .append(msg)
                .append("</h1></body></html>")
                .toString)
    out.flush()
  } finally {
          
    try { response.getWriter.close}
  }
}
}
