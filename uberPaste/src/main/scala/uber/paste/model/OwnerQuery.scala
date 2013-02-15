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
import org.compass.core.CompassQueryBuilder
import org.compass.core.CompassSession


class OwnerQuery extends GenericQuery with Query {

  protected var ownerId:java.lang.Long = null
  
  def getOwnerId():java.lang.Long = ownerId
  
  def setOwnerId(ownerId:java.lang.Long) { this.ownerId = ownerId}
  
  override def fillQuery(session:CompassSession):CompassQuery = {
    
    val qb = session.queryBuilder
    
    val bb:CompassQueryBuilder.CompassBooleanQueryBuilder = qb.bool
    .addMust(super.fillQuery(session))
    
    if (ownerId!=null) {
      bb.addMust(qb.term("ownerId", ownerId))
    }
    return bb.toQuery
  }
}
