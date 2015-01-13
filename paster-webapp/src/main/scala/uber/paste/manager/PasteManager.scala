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

package uber.paste.manager

import uber.paste.model._
import uber.paste.dao.PasteDao
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import scala.collection.JavaConversions._

object PasteManager {
    object Stats extends KeyObj[PriorStat]{
      
      
    for (s<-Priority.list) {
      add(new PriorStat(s))
    }
    
    }

}

class PriorStat(p:Priority) extends Key(p.getCode){

     private val counter = new AtomicInteger

     def getPriority() = p
  
     def getCounter():Int = counter.get
     def increment() = counter.incrementAndGet
     def decrease() = counter.decrementAndGet
     def increment(i:Int) = counter.addAndGet(i)
 
}

trait PasteManager extends GenericSearchManager[Paste]{

  def getByOwner(owner:User) : java.util.List[Paste]

  def getBySourceType(sourceType:PasteSource,desc:Boolean) : java.util.List[Paste]

  def getListIntegrated(code:String):java.util.List[Paste]

  def getByRemoteUrl(url:String) : Paste

  def countAllSince(source:PasteSource,dateFrom:java.lang.Long):java.lang.Long

  def getPreviousPastasIdList(paste:Paste):java.util.List[Long]
  
  def getNextPaste(paste:Paste): Paste
  
  def getPreviousPaste(paste:Paste): Paste
  
  }

@Service("pasteManager")
class PasteManagerImpl extends GenericSearchManagerImpl[Paste] with PasteManager {

  @Autowired
  val pasteDao:PasteDao = null

  protected override def getDao:PasteDao = pasteDao

  def getByRemoteUrl(url:String) : Paste= {
    return pasteDao.getByRemoteUrl(url)
  }

  def getByOwner(owner:User) : java.util.List[Paste]= {
    return pasteDao.getByOwner(owner)
  }
  def getBySourceType(sourceType:PasteSource,desc:Boolean) : java.util.List[Paste] = {
    return pasteDao.getBySourceType(sourceType,desc)
  }

  def getNextPaste(paste:Paste): Paste = pasteDao.getNextPaste(paste)
  
  def getPreviousPaste(paste:Paste): Paste = pasteDao.getPreviousPaste(paste)
  
  def getPreviousPastasIdList(paste:Paste):java.util.List[Long] = pasteDao.getPreviousPastasIdList(paste)
   
  
  @Secured(Array("ROLE_ADMIN"))
  override def remove(id:Long) = {
    
    val obj = get(id)
    if (obj!=null) {
      super.remove(id)
      PasteManager.Stats.valueOf(obj.getPriority.getCode)
      }
    }
  def getListIntegrated(code:String):java.util.List[Paste] = pasteDao.getListIntegrated(code)

  def countAllSince(source:PasteSource,dateFrom:java.lang.Long):java.lang.Long = pasteDao.countAllSince(source,dateFrom)


  override def save(obj:Paste):Paste = {

      val wasNew = obj.isBlank()

      try {

      return super.save(obj)

      } finally {
        if (wasNew) {
          PasteManager.Stats.valueOf(obj.getPriority.getCode).increment
        }
      }

    }
  }
