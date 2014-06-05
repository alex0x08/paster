/*
 * Copyright 2011 Ubersoft, LLC.
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

import org.compass.core.CompassQuery
import org.compass.core.CompassSession

/**
 * Query trait
 */
trait Query {

  /**
   * if this query empty
   */
  def isEmpty():Boolean
  /**
   * @return query string
   */
  def getQuery():String
  def setQuery(query:String)

  /**
   * @return current page
   */
  def getPage():Int
  def setPage(page:Int)

  /**
   * build CompassQuery from CompassSession
   */
  def fillQuery(session:CompassSession):CompassQuery

}


class GenericQuery extends Query {

  protected var query:String =null
  
  protected var page:Int = 1
  
  def setPage(page:Int)  { this.page = page }
  def getPage() = page
  
  def isEmpty():Boolean = {
   return query == null || query.equals("*") 
  }
  
  def getQuery():String = query
  def setQuery(query:String) { this.query = query }
  
  def fillQuery(session:CompassSession):CompassQuery = {
    
    if (query == null || query.trim.length == 0) {
        query = "*"
    }
    
    session.queryBuilder
         .queryString(query).toQuery
  }
}
