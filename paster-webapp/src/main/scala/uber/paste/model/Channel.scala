/*
 * Copyright 2016 Ubersoft, LLC.
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

import javax.persistence.Column
import javax.persistence.Entity
import javax.xml.bind.annotation.XmlRootElement

@Entity
@XmlRootElement(name = "channel")
class Channel(code: String, desc: String, p_default: Boolean) extends KeyValue(code, null, desc) {

  @Column(name = "c_default")
  private var default: Boolean = p_default

  def this() = this(null, null, false)

  def getCodeLowerCase() = super.getCode().toLowerCase

  def isDefault() = default

  def setDefault(v: Boolean) { default = v }

override def create(code:String) = new Channel(code,null,false)

}
