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

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.compass.annotations.{SearchableProperty, SearchableId}
;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle
import uber.paste.base.Loggered

@MappedSuperclass
abstract class DBObject extends java.io.Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @SearchableId(name="id")
  private var id:java.lang.Long = null
    
  private var disabled:Boolean = _

  
  def isDisabled():Boolean = {
    return disabled;
  }

  def setDisabled(disabled:Boolean) {
    this.disabled = disabled;
  }

  def isBlank():Boolean = {
    return id == null
  }

  def getId():java.lang.Long = {
    return id;
  }

  def setId(id:java.lang.Long) {
    this.id=id;
  }

  override def hashCode():Int = {
    var hash:Int = 53*7;
        
    if (id != null) 
      hash+=id.hashCode();
        
    return hash;
  }

  override def equals(from:Any):Boolean = {

    return if (from.isInstanceOf[DBObject]==false || isBlank()) {
      false;
    } else {
      from.asInstanceOf[DBObject].getId()==id;
    }
  }

  override def toString():String = {
     return  Loggered.getNewProtocolBuilder(this)
                .append("id", id)
                .append("class", getClass().getCanonicalName()).toString();
  }
  
}
