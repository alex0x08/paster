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

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import javax.validation.constraints.{Size, NotNull}
import javax.persistence.Column
import javax.persistence.MappedSuperclass
import org.hibernate.envers.Audited
import org.hibernate.search.annotations.Field
import uber.paste.base.Loggered

object Named extends Struct {
  
  /**
   *  a set of properties to search by default (without field prefix)
   */
  override val terms = super.terms ::: List[String]("name")
}

@MappedSuperclass
@Audited
class Named extends Struct {

  @NotNull
  @Field
  @Column(length=256)
  //@Pattern(regexp = "(.+)", message = "{struct.name.validator}")
  @Size(min=3, message = "{struct.name.validator}")
  @XStreamAsAttribute
  private var name: String = null

  override def terms():List[String] = Named.terms
  
  /*override def fillFromHits(ch:CompassHighlighter)  {
    super.fillFromHits(ch)
    
    val f = ch.fragment("name")
    if (f!=null) {
      setName(f)
    }
  }*/

 def fillFromDTO(dto:Named) {
   setName(dto.name)
  }
   
  
  def getName() : String = name
  def setName(f:String) : Unit = {name = f }

  override def toString():String =  Loggered.getNewProtocolBuilder(this)
                .append("name", name)
                .append("super",super.toString)
                .toString()
  
  

  
}
