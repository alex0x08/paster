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
package com.Ox08.paster.webapp.manager
import com.Ox08.paster.webapp.base.{Boot, Logged}
import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Component
import java.io.File
import java.net.URLDecoder
import java.nio.file.FileSystems
import java.text.SimpleDateFormat
import java.util.{Base64, Calendar}
import scala.collection.mutable
object ResourceManager {
  private val PATH_FORMAT = new SimpleDateFormat("YYYY/MM/dd/")
}
/**
 * Resource Manager to persist/retrieve binaries attached to entities
 */
@Component("resourcePathHelper")
class ResourceManager extends Logged {
  /**
   * Get file from object id and resource type
   *
   * @param resourceType
   * single char resource type
   * @param fid
   * object id
   * @return
   */
  def getResource(resourceType: Char, fid: String): File = {
    //to avoid relative paths
    val id = URLDecoder.decode(fid, "UTF-8")
      .replaceAll("/", "x")
      .replaceAll("\\.", "x")
      .replaceAll(",", "/")
    if (logger.isDebugEnabled)
      logger.debug("getting file from url {}", id)
    FileSystems.getDefault
      .getPath(Boot.BOOT.getSystemInfo.getAppHome.getAbsolutePath,
        "resources", resourceType.toString,
        s"$id.${getExtFor(resourceType)}").toFile
  }
  /**
   * Saves image to file
   *
   * @param pt
   * single char resource type
   * @param objId
   * object id
   * @param imgData
   * binary image data
   * @return
   */
  def saveResource(pt: Char, objId: String, imgData: String): String = {
    val fileName = new mutable.StringBuilder()
      .append(ResourceManager.PATH_FORMAT.format(Calendar.getInstance().getTime))
      .append(objId)
      .append('.')
      .append(getExtFor(pt))
      .toString
    val fileImg = FileSystems.getDefault
      .getPath(Boot.BOOT.getSystemInfo.getAppHome.getAbsolutePath, "resources",
        pt.toString,
        fileName).toFile
    val imgData2 = imgData.substring(imgData.indexOf(',') + 1)
    FileUtils.writeByteArrayToFile(fileImg, Base64.getDecoder.decode(imgData2.getBytes))
    fileName.replaceAll("/", ",")
  }
  /**
   * Get extension for resource type
   *
   * @param resourceType
   * single char resource type
   * @return
   */
  def getExtFor(resourceType: Char): String = resourceType match {
    case 'r' =>
      "png"
    case _ =>
      "jpg"
  }
}
