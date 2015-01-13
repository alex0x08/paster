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

import javax.persistence._
import org.hibernate.validator._
import uber.paste.base.Loggered
import java.beans.PropertyEditorSupport

/**
 *  The structure with key and value
 * 
 */


class KeyValueEditor[T <: KeyValue](vobj:KeyValueObj[T]) extends PropertyEditorSupport{

  override def setAsText(text:String):Unit = {
    setValue(vobj.valueOf(text.toLowerCase));
  }

  override def getAsText():String = {
    val s = getValue().asInstanceOf[T]
    return if (s == null) {
      null
    } else {
      s.getCode().toString()
    }
  }
}


class KeyValueObj[T <: KeyValue] extends KeyObj[T]{ }
  

@MappedSuperclass
class KeyValue extends Key with java.io.Serializable{
  
  @Column(name="pvalue")
  private var value: String = null
  
  def this(code:String) = {
    this()
    setCode(code)
  }

  def this(code:String,value:String) = {
    this()
    setCode(code)
    this.value=value
  }
   
  def getValue() : String = value
  def setValue(f:String) : Unit = {value = f }

   override def toString():String = Loggered.getNewProtocolBuilder(this)
                .append("value", value)
                 .toString()+super.toString
  
  
  
}
