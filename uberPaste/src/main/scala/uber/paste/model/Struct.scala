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

import java.util.Calendar
import javax.persistence._
import org.hibernate.validator._
import javax.validation.constraints.{Size, NotNull, Pattern}
import org.hibernate.validator.constraints.Length
import org.compass.annotations._
import org.codehaus.jackson.annotate.JsonIgnore
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle
import org.compass.core.CompassHighlighter
import java.text.SimpleDateFormat
import uber.paste.base.Loggered

object Struct {
  
  val terms = List[String]("id","name")

  final val DB_DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss"

  final val SD_FULL = new SimpleDateFormat(DB_DATE_FORMAT_FULL)
}

@MappedSuperclass
abstract class Struct extends DBObject with SearchObject with  java.io.Serializable{

  @NotNull
  @SearchableProperty
  @Column(length=256)
  //@Pattern(regexp = "(.+)", message = "{struct.name.validator}")
  @Size(min=3, message = "{struct.name.validator}")
  private var name: String = null

  @Column(name = "last_modified", columnDefinition = "timestamp")
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  @SearchableProperty(format = Struct.DB_DATE_FORMAT_FULL)
  private var lastModified:java.util.Date = null

  @PreUpdate
  @PrePersist
  def updateTimeStamps() {
    lastModified = Calendar.getInstance().getTime();
  }
  
  def getLastModified():Date = {
    return lastModified;
  }

  def terms():List[String] = Struct.terms
  
  def fillFromHits(ch:CompassHighlighter)  {
    
    val f = ch.fragment("name")
    if (f!=null) {
      setName(f)
    }

    val l = ch.fragment("lastModified")
    if (l!=null)
      lastModified = Struct.SD_FULL.parse(l)
  }
  
  def loadFull() {}
  
  def getName() : String = name
  def setName(f:String) : Unit = {name = f }

  override def toString():String = {
     return  Loggered.getNewProtocolBuilder(this)
                .append("name", name)
                .append("super",super.toString)
                .toString()
  }
  
  
  
}
