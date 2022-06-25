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
import org.hibernate.search.annotations.{DateBridge, Field, Index, Resolution}
import uber.paste.base.Loggered
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.persistence._

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

@MappedSuperclass
abstract class Struct extends DBObject with SearchObject with  java.io.Serializable{

  @Column(name = "last_modified") //, columnDefinition = "datetime"
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  @Field(index = Index.YES)
  @DateBridge(resolution = Resolution.DAY)    
  @XStreamAsAttribute
  private var lastModified:java.util.Date = _
  
  @Column(name = "created") //, columnDefinition = "datetime"
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  @Field(index = Index.YES)
  @DateBridge(resolution = Resolution.DAY)    
  @XStreamAsAttribute
  private var created:java.util.Date = Calendar.getInstance().getTime()

  @PrePersist
  @PreUpdate
  def touch() {
    lastModified = Calendar.getInstance.getTime
  }
  
  def getLastModified()= lastModified
  
  def getCreated() = created
      
  def terms():List[String] = Struct.terms
      
  def loadFull() {}  

  
}
