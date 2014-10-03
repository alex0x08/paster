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
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.highlight.Highlighter
import org.hibernate.envers.Audited
import org.hibernate.envers.NotAudited
import org.hibernate.search.annotations.DateBridge
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Index
import org.hibernate.search.annotations.Resolution
import org.hibernate.validator._
import javax.validation.constraints.{Size, NotNull}
import java.util.{Calendar,Date}
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
  @Field(index = Index.YES)
  @DateBridge(resolution = Resolution.DAY)    
  @XStreamAsAttribute
  @NotAudited
  private var lastModified:java.util.Date = null

  @PreUpdate
  @PrePersist
  def updateTimeStamps() {
    lastModified = Calendar.getInstance().getTime();
    // System.out.println("id="+getId+"_lastModified update "+lastModified);
  }
  
  def getLastModified():Date =  lastModified
  

  def setLastModified(from:Date) = { lastModified = from }
    
  
  
  def terms():List[String] = Struct.terms
  
  
  
  def loadFull() {}
  

  override def toString():String =  Loggered.getNewProtocolBuilder(this)
    .append("lastModified", lastModified)
    .append("super",super.toString)
    .toString()
  
  
  
  
}
