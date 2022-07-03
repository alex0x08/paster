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

import com.Ox08.paster.webapp.manager.ResourcePathHelper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, ResponseBody}
import java.io.{FileInputStream, IOException}

@Controller
@RequestMapping(Array("/resources"))
class ResourceController extends AbstractController {

  @Autowired
  private val resourcePathHelper: ResourcePathHelper = null

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


    ptype match {
      case "t" | "r" | "a" | "b" =>
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

    response.setHeader("Content-Length", fimg.length()+"")
    response.setHeader("Content-Disposition", "inline;filename='" + fimg.getName + "'")
    response.setDateHeader("Last-Modified", fimg.lastModified())
    response.setDateHeader("Expires", System.currentTimeMillis +
      31557600)
    response.setHeader("Cache-Control", "max-age=" + 31557600 + ", public")
    response.setHeader("Pragma", "cache")

    new InputStreamResource(new FileInputStream(fimg))

  }

  @throws(classOf[IOException])
  def writeError(response: HttpServletResponse, msg: String, status: Int) {
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
