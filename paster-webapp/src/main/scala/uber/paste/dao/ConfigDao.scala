/**
 * Copyright (C) 2010 alex <me@alex.0x08.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.dao

import org.springframework.stereotype.Repository
import uber.paste.model.ConfigProperty
import org.springframework.transaction.annotation.Transactional



@Repository("configDao")
@Transactional(readOnly = true)
class ConfigDaoImpl extends KeyDaoImpl[ConfigProperty](classOf[ConfigProperty]) {

  def getProperty(code:String) : ConfigProperty = {
    return getByKey(code)
  }
  
   def getProperty(obj:ConfigProperty) : ConfigProperty = {
     return getByKey(obj.getCode)
  }

   def isPropertySet(code:String,value:String) : Boolean = {
    return em.createQuery("SELECT count(c) FROM ConfigProperty c WHERE c.code = :code and c.value = :value")
      .setParameter("code", code)
      .setParameter("value", value)
      .getSingleResult().asInstanceOf[Long].intValue() > 0
  }

}