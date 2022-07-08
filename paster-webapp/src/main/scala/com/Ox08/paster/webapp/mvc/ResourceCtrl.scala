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
package com.Ox08.paster.webapp.mvc
import com.Ox08.paster.webapp.manager.ResourceManager
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, ResponseBody}
import java.io.{FileInputStream, IOException}
import scala.collection.mutable
@Controller
@RequestMapping(Array("/paste-resources"))
class ResourceCtrl extends AbstractCtrl {
  @Value("${paster.resources.cacheMillis:31557600}")
  private val MAX_AGE = 0
  @Autowired
  private val resourcePathHelper: ResourceManager = null
  @RequestMapping(
    value = Array("/{version:[a-zA-Z0-9]+}/{type:[a-z]}/{lastModified:[0-9]+}/paste_content/{path}"),
    method = Array(RequestMethod.GET)
  )
  @ResponseBody
  def getResource(
                   @PathVariable("lastModified") lastModified: Long,
                   @PathVariable("path") path: String,
                   @PathVariable("type") ptype: Char,
                   response: HttpServletResponse
                 ): InputStreamResource = {
    logger.debug("get {} type: {} lm: {}", path, ptype, lastModified)
    ptype match {
      case 't' | 'r' | 'a' | 'b' =>
      //allow
      case _ =>
        writeError(response, "uknown type", 404)
        return null
    }
    val fimg = resourcePathHelper.getResource(ptype, path)
    if (fimg == null) {
      writeError(response, "file not found", 404)
      return null
    }
    response.setContentType("image/png")
    response.setHeader("Content-Length", fimg.length().toString)
    response.setHeader("Content-Disposition", s"inline;filename='${fimg.getName}'")
    response.setDateHeader("Last-Modified", fimg.lastModified())
    response.setDateHeader("Expires", System.currentTimeMillis + MAX_AGE)
    response.setHeader("Cache-Control", s"max-age=$MAX_AGE, public")
    response.setHeader("Pragma", "cache")
    new InputStreamResource(new FileInputStream(fimg))
  }
  @throws(classOf[IOException])
  def writeError(response: HttpServletResponse, msg: String, status: Int): Unit = {
    response.setContentType("text/html;charset=UTF-8")
    response.setStatus(status)
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
