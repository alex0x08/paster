/*
 * Copyright 2011 WorldWide Conferencing, LLC.
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

import uber.paste.manager.GenericManager
import uber.paste.model.DBObject
import org.springframework.web.bind.annotation._

object GenericController {
  
   final val NODE_LIST_MODEL = "items"   
   final val NODE_LIST_MODEL_PAGE = "pageItems"
   final val MODEL_KEY= "model"
   final val NODE_COUNT_KEY = "count"
   
}

abstract class GenericController[T <: DBObject ] extends AbstractController {

    
  protected def listPage:String
 
  protected def editPage:String

  protected def viewPage:String


  def getListPage():String = {
    return listPage;
  } 
 
  def getEditPage():String = {
    return editPage;
  }

  def getViewPage():String = {
    return viewPage;
  }

  

  protected def manager():GenericManager[T,Long]
  

}
