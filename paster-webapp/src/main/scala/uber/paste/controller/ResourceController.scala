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

package uber.paste.controller

import java.io.IOException
import java.net.URLDecoder
import javax.jcr.Node
import javax.jcr.Session
import javax.servlet.http.HttpServletResponse
import org.apache.jackrabbit.JcrConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import uber.paste.manager.RepositoryManager
import uber.paste.manager.ResourcePathHelper

@Controller
@RequestMapping(Array("/resources"))
class ResourceController extends AbstractController {

  @Autowired
  private var resourcePathHelper: ResourcePathHelper = null

  @Autowired
  private var repositoryManager:RepositoryManager = null
  
  @RequestMapping(
    value = Array("/{version:[a-zA-Z0-9]+}/{type:[a-z]}/{lastModified:[0-9]+}/paste_content/{path}"),
    method = Array(RequestMethod.GET)
  )
  @ResponseBody
  def getResource(
    model: Model,
    @PathVariable("lastModified") lastModified: Long,
    @PathVariable("path") path: String,
    @PathVariable("type") ptype: String,
    response: HttpServletResponse
  ): InputStreamResource = {

    //model.asMap.clear()
    ptype match {
      case "t" | "r" | "a" | "b" => {
          //allow
      }
      case _ => {
        writeError(response, "uknown type", 404)
        return null

      }
    }

   /*val id =  URLDecoder.decode(path, "UTF-8")
      .replaceAll("/", "x")
      .replaceAll("\\.", "x")
      .replaceAll(",", "/")
    */
    
     var  s:Session = repositoryManager.createAdministrativeSession()
            
       val node:Node =     s.getNodeByIdentifier("/paste_content/"+path+".png")

      
               
    
    
   // val node:Node = repositoryManager.getNode(path)
    
    
    
   // val fimg = resourcePathHelper.getResource(ptype, path)

    if (node==null) {
      writeError(response, "file not found", 404)
      return null
    }

   
        response.setContentType("image/png")
   
    val content_node = node.getNode(JcrConstants.JCR_CONTENT)
    
    val content = content_node.getProperty(JcrConstants.JCR_DATA)
    
    response.setHeader("Content-Length", String.valueOf(content.getLength))
    response.setHeader("Content-Disposition", "inline;filename='" + node.getName + "'")
    response.setDateHeader("Last-Modified", content_node.getProperty(JcrConstants.JCR_LASTMODIFIED).getLong)
    response.setDateHeader("Expires", System.currentTimeMillis +
      31557600)
    response.setHeader("Cache-Control", "max-age=" + 31557600 + ", public")
    response.setHeader("Pragma", "cache")
    try {
     
    return new InputStreamResource(content.getBinary.getStream)
    } finally {
      
       s.logout()
          
    }
  }
@throws(classOf[IOException])
def writeError(response:HttpServletResponse, msg:String, status:Int)
{
  response.setContentType("text/html;charset=UTF-8");
  response.setStatus(status);

    val out = response.getWriter()

    out.println(new StringBuilder()
      .append("<html><head><title>ERROR: ")
      .append(msg)
      .append("</title></head><body><h1>ERROR: ")
      .append(msg)
      .append("</h1></body></html>")
      .toString)
    out.flush()

  }

}
