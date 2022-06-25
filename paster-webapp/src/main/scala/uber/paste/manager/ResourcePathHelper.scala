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

package uber.paste.manager

import java.io.File
import java.net.URLDecoder
import java.nio.file.FileSystems
import java.text.SimpleDateFormat
import java.util.Calendar
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.codec.Base64
import org.springframework.stereotype.Component
import uber.paste.base.Loggered
import uber.paste.model.{Comment, Paste, Project}

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

    if (logger.isDebugEnabled) {
      logger.debug("file url {}", id)
    }

    val ext = 
     ptype match {
      case "r" => {
          "png"
      }
      case _ => {
        "jpg"
      }
    }
    
    return FileSystems.getDefault()
      .getPath(pasteAppHome, "resources", ptype, id + "."+ext).toFile
  }

  def saveProjectImages(b: Project): String = {

    /* val fname = "customer/"+ResourcePathHelper.PATH_FORMAT.format(Calendar.getInstance().getTime())
    +b.getId
      
        val fimg = FileSystems.getDefault().getPath(System.getProperty("paste.app.home"),
                                                    "images",
                                                    fname+".png").toFile
      
        FileUtils.writeByteArrayToFile(fimg,Base64.decode(imgData.getBytes))
      
     return fname.replaceAll("/", ",")*/
    return null
  }


  def saveResource(pt: String, b: Comment,p:Paste): String = {

    val fname = new StringBuilder()
      //.append("thumbnails/")
      .append(ResourcePathHelper.PATH_FORMAT.format(Calendar.getInstance().getTime()))
      .append(p.getUuid)
      .toString

    val fimg = FileSystems.getDefault().getPath(pasteAppHome, "resources",
      pt,
      fname + ".jpg").toFile

    val imgData = b.getThumbImage.substring(b.getThumbImage.indexOf(',') + 1)
    FileUtils.writeByteArrayToFile(fimg, Base64.decode(imgData.getBytes))
    fname.replaceAll("/", ",")

  }
  def saveResource(pt: String, b: Paste): String = {

    val fname = new StringBuilder()
      //.append("thumbnails/")
      .append(ResourcePathHelper.PATH_FORMAT.format(Calendar.getInstance().getTime()))
      .append(b.getUuid)
      .toString

    pt match {
      case "t" => {
        val fimg = FileSystems.getDefault().getPath(pasteAppHome, "resources",
          pt,
          fname + ".jpg").toFile

        val imgData = b.getThumbImage.substring(b.getThumbImage.indexOf(',') + 1)
        FileUtils.writeByteArrayToFile(fimg, Base64.decode(imgData.getBytes))
      
      }
      case "r" => {
        val fimg = FileSystems.getDefault().getPath(pasteAppHome, "resources",
          pt,
          fname + ".png").toFile
        val imgData = b.getReviewImgData.substring(b.getReviewImgData.indexOf(',') + 1)
        FileUtils.writeByteArrayToFile(fimg, Base64.decode(imgData.getBytes))
      }
      case _ => {
        null
      }

    }

    fname.replaceAll("/", ",")
  }
}
