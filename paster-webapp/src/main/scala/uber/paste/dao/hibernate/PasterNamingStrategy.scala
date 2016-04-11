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

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategy
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
import org.hibernate.cfg.ImprovedNamingStrategy
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment

object PasterNamingStrategy {
  
   private val TABLE_PREFIX="P_"
}

class PasterNamingStrategy  extends PhysicalNamingStrategyStandardImpl with PhysicalNamingStrategy{

  
        override def toPhysicalColumnName(name:Identifier, context:JdbcEnvironment):Identifier = {
		return super.toPhysicalColumnName(transform(name), context)
	}

	override def toPhysicalCatalogName(name:Identifier,context:JdbcEnvironment):Identifier = {
		return super.toPhysicalCatalogName(transform(name), context)
	}

	override def toPhysicalSchemaName(name:Identifier, context:JdbcEnvironment):Identifier = {
		return super.toPhysicalSchemaName(transform(name), context)
	}

	override def toPhysicalSequenceName(name:Identifier, context:JdbcEnvironment):Identifier = {
		return super.toPhysicalSequenceName(transform(name), context)
	}

	override def toPhysicalTableName(name:Identifier, context:JdbcEnvironment):Identifier = {
		return super.toPhysicalTableName(transform(name), context)
	}
  
  
    /**
     *  append default prefix to all paster's tables and columns to avoid 
     *  issues with reserved keywords
     */    
    def transform(i:Identifier):Identifier =
         return if (i==null )  null
          else
            Identifier.toIdentifier(
              (PasterNamingStrategy.TABLE_PREFIX+i.render()).toLowerCase,i.isQuoted())
   
  
}
