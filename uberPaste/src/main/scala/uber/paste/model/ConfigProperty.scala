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

object ConfigProperty {
  
  val IS_INSTALLED = new ConfigProperty("config.property.is-installed","IS_INSTALLED","1")
  val UPLOADS_DIR = new ConfigProperty("config.property.upload-dir","UPLOAD_DIR","upload")

}

@Entity
@Table(name = "CONFIG")
class ConfigProperty extends KeyValue with java.io.Serializable {

def this(name:String,code:String,value:String) = {
    this()
    setName(name)
    setCode(code)
    setValue(value)
  }
}
