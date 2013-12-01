/*
 * Copyright 2011 Ubersoft, LLC.
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


import java.util.Collection
import java.util.HashMap
import javax.persistence._
import org.hibernate.validator._
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length
import org.compass.annotations._
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle
import uber.paste.base.Loggered

/**
 * Key structure. The struct which has special uniqie text key
 */

class KeyObj[T <: Key] {

  val map = new HashMap[String,T]

   def add(c:T):Unit = {
    map.put(c.getCode,c)
  }

   def getList:Collection[T] = list 
  
  def list:Collection[T] = {
    return map.values
  }
  def valueOf(key:String):T = {
    return if (map.containsKey(key)) map.get(key) else null.asInstanceOf[T]
  }
}


@MappedSuperclass
class Key extends Struct with java.io.Serializable{
  
  @NotNull
  @Column(nullable = false, length = 50,unique=true)
  private var code: String = null  
  
  def this(code:String) = {
    this()
    this.code=code
  }

  def getCode() : String = code
  def setCode(f:String) : Unit = {code = f }

  override def hashCode():Int = {
    var hash:Int = 53*7;
        
    // if (id != null) 
    //   hash+=id.hashCode();
    if (code != null) 
      hash+=code.hashCode();
        
    return hash;
  }

  override def equals(from:Any):Boolean = {

    return if (from.isInstanceOf[Key]==false || getCode == null) {
      false
    } else {
      from.asInstanceOf[Key].getCode().equals(code)
    }
  }

  
   override def toString():String = {
     return Loggered.getNewProtocolBuilder(this)
                .append("code", code)
                .append("super",super.toString)
                 .toString()
  }
  
  
}
