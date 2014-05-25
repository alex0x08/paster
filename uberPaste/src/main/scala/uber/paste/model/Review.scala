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

import javax.persistence._
import javax.validation.constraints.{Size,NotNull}
import javax.xml.bind.annotation.XmlTransient
import org.codehaus.jackson.annotate.JsonIgnore
import org.compass.annotations.Searchable
import org.compass.annotations.SearchableProperty

@Entity
@Searchable
class Review extends Struct{

  
  @Lob
  //@NotNull
  @SearchableProperty
  //(analyzer = )
  @Size(min=3, message = "{struct.name.validator}")
  @Column(length = Integer.MAX_VALUE)
  private var verdict: String = null

  
  @ManyToOne(fetch = FetchType.EAGER,cascade= Array(CascadeType.PERSIST,CascadeType.MERGE))
  @JoinColumn(name = "owner_id")
  //@NotAudited
  private var owner:User = null
 
  @XmlTransient
  @JsonIgnore
  def getOwner(): User = owner
  def setOwner(u:User) {owner = u}

  
}
