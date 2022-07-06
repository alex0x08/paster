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

package com.Ox08.paster.webapp.model

import com.Ox08.paster.webapp.base.Loggered
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import jakarta.persistence.{Column, GeneratedValue, Id, MappedSuperclass, PrePersist, PreUpdate, Temporal, TemporalType}
import org.hibernate.annotations.GenericGenerator
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.{FullTextField, GenericField}

import java.text.SimpleDateFormat
import java.util.{Calendar, Objects}

object Struct {
  
  val terms = List[String]("id","name")

  final val DB_DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss"
  final val SD_FULL = new SimpleDateFormat(DB_DATE_FORMAT_FULL)
  
    abstract class Builder[T <: java.io.Serializable](obj:T) extends Loggered{
        def get():T = obj
        }
}
/**
 * Struct model, have lastModified  field
 */


trait SearchObject {
  def terms():List[String]
}


@MappedSuperclass
abstract class Struct extends DBObject with SearchObject with  java.io.Serializable{

  @Column(name = "last_modified") //, columnDefinition = "datetime"
  @Temporal(TemporalType.TIMESTAMP)
  @GenericField
 // @DateBridge(resolution = Resolution.DAY)
  @XStreamAsAttribute
  private var lastModified:java.util.Date = _

  @Column(name = "created") //, columnDefinition = "datetime"
  @Temporal(TemporalType.TIMESTAMP)
  @GenericField
  //(index = Index.YES)
  //@DateBridge(resolution = Resolution.DAY)
  @XStreamAsAttribute
  private val created: java.util.Date = Calendar.getInstance().getTime()

  @PrePersist
  @PreUpdate
  def touch(): Unit= {
    lastModified = Calendar.getInstance().getTime()
  }
  
  def getLastModified()= lastModified
  
  def getCreated() = created
      
  def terms():List[String] = Struct.terms
      
  def loadFull() : Unit= {}

  
}



@MappedSuperclass
abstract class DBObject extends java.io.Serializable {

  @Id
  @GeneratedValue(generator = "AllSequenceStyleGenerator")
  @GenericGenerator(name = "AllSequenceStyleGenerator",
    strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator"
  )
  @XStreamAsAttribute
  private var id:java.lang.Long = null

  @XStreamAsAttribute
  private var disabled:Boolean = _

  def isDisabled() = disabled

  def setDisabled(disabled:Boolean): Unit =  { this.disabled = disabled  }

  def isBlank() = (id == null)

  def getId() = id

  def setId(id:java.lang.Long): Unit =  {
    this.id=id
  }

  override def hashCode():Int = {
    var hash:Int = 53*7

    if (id != null)
      hash+=Objects.hashCode(id)

    hash
  }

  override def equals(from:Any) =
    (from.isInstanceOf[DBObject] && !isBlank()
      && from.asInstanceOf[DBObject].getId().equals(id))


  override def toString():String =  Loggered.toStringSkip(this, null)


}

