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

package uber.paste.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Lob
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlTransient
import org.codehaus.jackson.annotate.JsonIgnore
import org.compass.annotations.Searchable
import org.compass.annotations.SearchableProperty
import org.hibernate.envers.Audited
import org.compass.annotations._

@Entity
@Searchable
@XmlRootElement(name="project")
@Audited
class Project extends Struct with java.io.Serializable{

  @Lob
  @XmlTransient
  private var iconImage:String =null
  
  @SearchableProperty
  private var description:String = null
  
  @Lob
  @Column(length = Integer.MAX_VALUE)
  private var clientImage: String = null
  
  def getDescription() = description
  def setDescription(d:String) {description = d}
  
  @JsonIgnore
  def getIconImage() = iconImage
  def setIconImage(img:String) {iconImage = img}

  @JsonIgnore
  def getClientImage() = clientImage
  def setClientImage(img:String) {clientImage = img}

  
}
