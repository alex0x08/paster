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

package uber.paste.base

import org.hibernate.Session
import org.hibernate.envers.configuration.AuditConfiguration
import org.hibernate.envers.configuration.GlobalConfiguration
import org.hibernate.envers.entities.mapper.PersistentCollectionChangeData
import org.hibernate.envers.entities.mapper.relation.MiddleIdData
import org.hibernate.envers.strategy.DefaultAuditStrategy
import org.hibernate.envers.tools.query.QueryBuilder
import scala.collection.JavaConversions._

class PasterAuditStrategy extends DefaultAuditStrategy{

  override def perform(session:Session, entityName:String, 
                       auditCfg:AuditConfiguration,  id:java.io.Serializable, data:Any,
                         revision:Any) {
        
    
      System.out.println("_perform "+entityName+" id="+id)
     super.perform(session, entityName, auditCfg, id, data, revision)
                        }
                        
  override def performCollectionChange(session:Session, auditCfg:AuditConfiguration,
                                        persistentCollectionChangeData:PersistentCollectionChangeData, 
                                        revision:Object) {
     
                                        System.out.println("_collection change "+revision+" entity "+persistentCollectionChangeData.getEntityName)
     
    for (e<-persistentCollectionChangeData.getData.entrySet) {
      System.out.println("k="+e.getKey+" v="+e.getValue)
    }
     
    super.performCollectionChange(session, auditCfg, persistentCollectionChangeData, revision)
                                        }
                         
  
  override def addEntityAtRevisionRestriction(globalCfg:GlobalConfiguration, 
                                              rootQueryBuilder:QueryBuilder, 
                                              revisionProperty:String,
			 revisionEndProperty:String,  
                         addAlias:Boolean, 
                         idData:MiddleIdData, 
                         revisionPropertyPath:String, 
			 originalIdPropertyName:String,  
                         alias1:String, alias2:String) {

    System.out.println("__add e="+idData.getEntityName+" rest "+revisionProperty+" end="+revisionEndProperty+" path="+revisionPropertyPath+" alias1="+alias1+" alias2="+alias2)
    super.addEntityAtRevisionRestriction(globalCfg, rootQueryBuilder, revisionProperty, revisionEndProperty, addAlias, idData, revisionPropertyPath, originalIdPropertyName, alias1, alias2)
  }
}