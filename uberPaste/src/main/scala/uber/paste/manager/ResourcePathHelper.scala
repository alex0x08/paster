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
import uber.paste.model.Paste
import uber.paste.model.Project

object ResourcePathHelper {
   val PATH_FORMAT = new SimpleDateFormat("YYYY/MM/dd/")
      
}

@Component("resourcePathHelper")
class ResourcePathHelper extends Loggered {

  @Value("${paste.app.home}")
  val pasteAppHome:String = null

  def getThumb(fid:String): File = {
    
       //to avoid relative paths
    val id = URLDecoder.decode(fid,"UTF-8")
    .replaceAll("/", "x")
    .replaceAll("\\.", "x")
    .replaceAll(",", "/")
    
    if (logger.isDebugEnabled) {
      logger.debug("file url "+id)
    }
    
    return FileSystems.getDefault()
      .getPath(System.getProperty("paste.app.home"),"images",id+".jpg").toFile
  }
 
   def saveProjectImages(b:Project):String = {
    
   /* val fname = "customer/"+ResourcePathHelper.PATH_FORMAT.format(Calendar.getInstance().getTime())
    +b.getId
      
        val fimg = FileSystems.getDefault().getPath(System.getProperty("paste.app.home"),
                                                    "images",
                                                    fname+".png").toFile
      
     // val imgData = b.getThumbImage.substring(b.getThumbImage.indexOf(',')+1)
      //logger.debug(" base64="+imgData)
      
        FileUtils.writeByteArrayToFile(fimg,Base64.decode(imgData.getBytes))
      
     return fname.replaceAll("/", ",")*/
    return null
   }
  
  def saveThumb(b:Paste):String = {
    
     val fname = "thumbnails/"+ResourcePathHelper.PATH_FORMAT.format(Calendar.getInstance().getTime())+b.getUuid
      
        val fimg = FileSystems.getDefault().getPath(System.getProperty("paste.app.home"),
                                                    "images",
                                                    fname+".jpg").toFile
      
      val imgData = b.getThumbImage.substring(b.getThumbImage.indexOf(',')+1)
      //logger.debug(" base64="+imgData)
      
        FileUtils.writeByteArrayToFile(fimg,Base64.decode(imgData.getBytes))
      
     return fname.replaceAll("/", ",")
  }
}
