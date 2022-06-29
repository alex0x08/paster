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

package com.Ox08.paster.webapp.manager

import com.Ox08.paster.webapp.base.Loggered
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.net.URLDecoder
import java.nio.file.FileSystems
import java.text.SimpleDateFormat
import java.util.{Base64, Calendar}

object ResourcePathHelper {
  val PATH_FORMAT = new SimpleDateFormat("YYYY/MM/dd/")
}

@Component("resourcePathHelper")
class ResourcePathHelper extends Loggered {

  @Value("${paste.app.home}")
  val pasteAppHome: String = null

  def getResource(ptype: String, fid: String): File = {

    //to avoid relative paths
    val id = URLDecoder.decode(fid, "UTF-8")
      .replaceAll("/", "x")
      .replaceAll("\\.", "x")
      .replaceAll(",", "/")

    if (logger.isDebugEnabled) logger.debug("file url {}", id)

    FileSystems.getDefault()
      .getPath(pasteAppHome, "resources", ptype, id + "."+getExtFor(ptype)).toFile
  }


  def saveResource(pt: String, objId :String,imgData:String): String = {
    val fname = new StringBuilder()
      .append(ResourcePathHelper.PATH_FORMAT.format(Calendar.getInstance().getTime()))
      .append(objId)
      .append('.')
      .append(getExtFor(pt))
      .toString

    val fimg = FileSystems.getDefault().getPath(pasteAppHome, "resources",
      pt,
      fname).toFile
    val imgData2 = imgData.substring(imgData.indexOf(',') + 1)
    FileUtils.writeByteArrayToFile(fimg, Base64.getDecoder().decode(imgData2.getBytes))

    fname.replaceAll("/", ",")

  }
  def getExtFor(ptype:String) = ptype match {
    case "r" =>
      "png"
    case _ =>
      "jpg"
  }

}
