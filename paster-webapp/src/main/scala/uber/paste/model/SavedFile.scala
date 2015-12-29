/*
 * Copyright 2015 Ubersoft, LLC.
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

package uber.paste.model

import java.io.File
import java.util.Date
import javax.jcr.Node
import javax.jcr.RepositoryException
import org.apache.jackrabbit.JcrConstants

class SavedFile(node:Node) {

  
   val content = node.getNode(JcrConstants.JCR_CONTENT)
     
     
  private val lastModified:Date = content.getProperty(JcrConstants.JCR_LASTMODIFIED).getDate().getTime
  
  private val id:String = node.getIdentifier.substring(1)
  
  private val mime:String = content.getProperty(JcrConstants.JCR_MIMETYPE).toString
 
  private val name:String = node.getName
  
  def getId() = id
  
  def getMime() = mime
 
  def getName() = name
  
  def getLastModified() = lastModified
  
}
