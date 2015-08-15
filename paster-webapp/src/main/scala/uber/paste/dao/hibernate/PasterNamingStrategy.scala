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

package uber.paste.dao.hibernate

import org.hibernate.cfg.ImprovedNamingStrategy

object PasterNamingStrategy {
  
   private val TABLE_PREFIX="P_"
}

class PasterNamingStrategy  extends ImprovedNamingStrategy{

    /**
     * Transforms class names to table names by using the described naming conventions.
     * @param className
     * @return  The constructed table name.
     */
    override def classToTableName(className:String):String = {
        return transform(super.classToTableName(className))
    }
    
   override def collectionTableName(ownerEntity:String,
            ownerEntityTable:String, 
            associatedEntity:String,
            associatedEntityTable:String, 
            propertyName:String):String = 
         transform(super.collectionTableName(ownerEntity,
                ownerEntityTable, associatedEntity, associatedEntityTable,
                propertyName))
    

    override def logicalCollectionTableName(tableName:String,
            ownerEntityTable:String,
            associatedEntityTable:String,
            propertyName:String):String = 
        this.transform(super.logicalCollectionTableName(tableName,
                ownerEntityTable, associatedEntityTable, propertyName))
    

    def transform(tableName:String):String =
       PasterNamingStrategy.TABLE_PREFIX+tableName.toLowerCase
   
  
}
