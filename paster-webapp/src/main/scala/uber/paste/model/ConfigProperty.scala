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

package uber.paste.model

import javax.persistence._
import javax.validation.constraints.NotNull
import uber.paste.base.SystemInfo


object ConfigProperty extends KeyValueObj[ConfigProperty]{
  
  val IS_INSTALLED = new ConfigProperty("config.property.is-installed","IS_INSTALLED","1")
  val UPLOADS_DIR = new ConfigProperty("config.property.upload-dir","UPLOAD_DIR","upload")
  val EXTERNAL_SITE_URL = new ConfigProperty("config.property.external-site-url","EXTERNAL_SITE_URL","http://localhost")
  val APP_VERSION = new ConfigProperty("config.property.app-version","APP_VERSION","UNDEFINED")
  val INSTALL_DATE = new ConfigProperty("config.property.install-date","INSTALL_DATE",null)
 
  add(EXTERNAL_SITE_URL)
  
  
  def createNew = new Builder(new ConfigProperty)
  

  class Builder(model:ConfigProperty) extends Named.NamedBuilder[ConfigProperty](model){
  
    def addValue(value:String): Builder  = {
      get.setValue(value); return this
    }

  }
}

@Entity
class ConfigProperty extends KeyValue with java.io.Serializable {

    
def this(name:String,code:String,value:String) = {
    this(); setName(name)
    setCode(code); setValue(value)
  }
  
  
}