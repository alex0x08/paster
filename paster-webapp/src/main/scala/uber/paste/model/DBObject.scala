/*
 * Copyright 2011 WorldWide Conferencing, LLC.
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
import java.io.Serializable
import javax.persistence._
import org.hibernate.annotations.GenericGenerator
import org.hibernate.envers.Audited
import uber.paste.base.Loggered


@MappedSuperclass
//@Audited
abstract class DBObject extends java.io.Serializable {

  @Id
  @GeneratedValue(generator = "AllSequenceStyleGenerator")
  @GenericGenerator(name = "AllSequenceStyleGenerator", 
                   strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator"
 )
  //@GeneratedValue(strategy = GenerationType.IDENTITY)
  //@SearchableId(name="id")
  //@Index(name="ddesc_index",columnNames = Array("id desc"))
  @XStreamAsAttribute
  private var id:java.lang.Long = null
    
  @XStreamAsAttribute
  private var disabled:Boolean = _

  
  def isDisabled() = disabled
  

  def setDisabled(disabled:Boolean) { this.disabled = disabled  }

  def isBlank() = (id == null)
 
  def getId() = id
 
  def setId(id:java.lang.Long) {
    this.id=id
  }

  override def hashCode():Int = {
    var hash:Int = 53*7
        
    if (id != null) 
      hash+=id.hashCode()
        
    return hash;
  }

  override def equals(from:Any) =
      (from.isInstanceOf[DBObject] && !isBlank() 
       && from.asInstanceOf[DBObject].getId().equals(id)) 
      
  

  override def toString():String =  Loggered.getNewProtocolBuilder(this)
                .append("id", id)
                .append("class", getClass().getCanonicalName()).toString()
  
  
}
