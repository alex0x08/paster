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

package uber.paste.manager

import uber.paste.dao.ConfigDaoImpl
import uber.paste.model.ConfigProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

@Service("configManager")
class ConfigManagerImpl {

  @Autowired
  @Qualifier("configDao")
  var configDao:ConfigDaoImpl = null

  protected def getDao:ConfigDaoImpl = configDao

  def getProperty(code:String) = configDao.getProperty(code)
 
  def isPropertySet(code:String,value:String) = configDao.isPropertySet(code, value)
 
  
}
