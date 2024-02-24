/*
 * Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.mvc
import com.Ox08.paster.webapp.manager.ResourceManager
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, ResponseBody}
import java.io.{FileInputStream, IOException}
import scala.collection.mutable
/**
 * Responds binaries generated at runtime.
 * Currently, its only used for Paste's previews
 */
@Controller
@RequestMapping(Array("/paste-resources"))
class ResourceCtrl extends AbstractCtrl {
  @Value("${paster.resources.cacheMillis:31557600}")
  private val MAX_AGE = 0
  @Autowired
  private val resourcePathHelper: ResourceManager = null
  /**
   * Respond static resource
   * @param lastModified
   *        file's 'last modified' timestamp, used to bypass caching
   * @param path
   *        relative path
   * @param pType
   *        resource type
   * @param response
   *        http servlet response object
   * @return
   *      streamed resource
   */
  @RequestMapping(
    value = Array("/{version:[a-zA-Z0-9]+}/{type:[a-z]}/{lastModified:[0-9]+}/paste_content/{path}"),
    method = Array(RequestMethod.GET))
  @ResponseBody
  def getResource(
                   @PathVariable("lastModified") lastModified: Long,
                   @PathVariable("path") path: String,
                   @PathVariable("type") pType: Char,
                   response: HttpServletResponse
                 ): InputStreamResource = {
    if (logger.isDebugEnabled) logger.debug("get {} type: {} lm: {}", path, pType, lastModified)
    // check the resource type
    pType match {
      case 't' | 'r' | 'a' | 'b' => //allow
      case _ => writeError(response, "unknown type"); return null
    }
    val fImg = resourcePathHelper.getResource(pType, path)
    // respond error if resource not found
    if (fImg == null) {  writeError(response, "file not found"); return null }
    response.setHeader("Content-Length", fImg.length().toString)
    // we use this method only for images, so its ok to inline MIME and content-disposition
    response.setContentType("image/png")
    response.setHeader("Content-Disposition", s"inline;filename='${fImg.getName}'")
    response.setDateHeader("Last-Modified", fImg.lastModified())
    response.setDateHeader("Expires", System.currentTimeMillis + MAX_AGE)
    response.setHeader("Cache-Control", s"max-age=$MAX_AGE, public")
    response.setHeader("Pragma", "cache")
    new InputStreamResource(new FileInputStream(fImg))
  }
  /**
   * Generates minimal HTML page with error response.
   * Suppose to be 'last chance' on error reporting.
   * @param response
   * @param msg
   * @throws java.io.IOException
   */
  @throws(classOf[IOException])
  private def writeError(response: HttpServletResponse, msg: String): Unit = {
    response.setContentType("text/html;charset=UTF-8")
    response.setStatus(404)
    val out = response.getWriter
    out.println(new mutable.StringBuilder()
      .append("<html><head><title>ERROR: ")
      .append(msg)
      .append("</title></head><body><h1>ERROR: ")
      .append(msg)
      .append("</h1></body></html>")
      .toString)
    out.flush()
  }
}
