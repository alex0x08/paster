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
import org.hibernate.envers.Audited
import org.hibernate.envers.NotAudited
import org.hibernate.validator._
import javax.validation.constraints.{Size, NotNull}
import org.compass.annotations._
import java.util.{Calendar,Date}
import org.compass.core.CompassHighlighter
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import java.text.SimpleDateFormat
import uber.paste.base.Loggered

object Struct {
  
  val terms = List[String]("id")

  final val DB_DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss"

  final val SD_FULL = new SimpleDateFormat(DB_DATE_FORMAT_FULL)
}

@MappedSuperclass
@Audited
abstract class Struct extends DBObject with SearchObject with  java.io.Serializable{

  @Column(name = "last_modified") //, columnDefinition = "datetime"
  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  @SearchableProperty(format = Struct.DB_DATE_FORMAT_FULL)
  @XStreamAsAttribute
  @NotAudited
  private var lastModified:java.util.Date = null

  @PreUpdate
  @PrePersist
  def updateTimeStamps() {
    lastModified = Calendar.getInstance().getTime();
   // System.out.println("id="+getId+"_lastModified update "+lastModified);
  }
  
  def getLastModified():Date = {
    return lastModified;
  }

  def terms():List[String] = Struct.terms
  
  def fillFromHits(ch:CompassHighlighter)  {

    val l = ch.fragment("lastModified")
    if (l!=null)
      lastModified = Struct.SD_FULL.parse(l)
  }
  
  
  def loadFull() {}
  

  override def toString():String = {
     return  Loggered.getNewProtocolBuilder(this)
                .append("lastModified", lastModified)
                .append("super",super.toString)
                .toString()
  }
  
  
  
}
