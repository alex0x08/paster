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

package com.Ox08.paster.webapp.model

import com.Ox08.paster.webapp.base.SystemInfo
import javax.persistence._
import javax.validation.constraints.NotNull

object ConfigProperty extends KeyValueObj[ConfigProperty] {

  val EXTERNAL_SITE_URL = new ConfigProperty("config.property.external-site-url", 
                                             "EXTERNAL_SITE_URL", "http://localhost",false)
  val IS_INSTALLED = new ConfigProperty("config.property.is-installed", "IS_INSTALLED", "1",true)
  val APP_VERSION = new ConfigProperty("config.property.app-version", "APP_VERSION", "UNDEFINED",true)
  val INSTALL_DATE = new ConfigProperty("config.property.install-date", "INSTALL_DATE", null,true)

  add(EXTERNAL_SITE_URL)

  def createNew = new Builder(new ConfigProperty(null, null, null,false))

  class Builder(model: ConfigProperty) extends KeyValue.Builder[ConfigProperty](model) {

    def addReadOnly(v: Boolean): Builder = {
      get().setReadOnly(v); this
    }

  }
}

@Entity
class ConfigProperty(name: String, code: String, value: String, vreadOnly:Boolean)
  extends KeyValue(code, value, name) with java.io.Serializable {

  private var readOnly: Boolean = vreadOnly

  def this() = this(null, null, null,false)

  def isReadOnly() = readOnly
  def setReadOnly(b: Boolean) : Unit =  { this.readOnly = b }
  
override   def create(code:String) = new ConfigProperty(null,code,null,false)
}
