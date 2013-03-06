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
import org.springframework.beans.factory.annotation.Autowired
import actors.threadpool.AtomicInteger

object PasteManager {
    object Stats {
      var totalPastas = new AtomicInteger
      var totalToday = new AtomicInteger

      def getTotal():Int = totalPastas.get()
    }

}

trait PasteManager extends GenericSearchManager[Paste]{

  def getByOwner(owner:User) : java.util.List[Paste]

  def getBySourceType(sourceType:PasteSource) : java.util.List[Paste]

  }

@Service("pasteManager")
class PasteManagerImpl extends GenericSearchManagerImpl[Paste] with PasteManager {

  @Autowired
  val pasteDao:PasteDao = null

  protected override def getDao:PasteDao = pasteDao

  def getByOwner(owner:User) : java.util.List[Paste]= {
    return pasteDao.getByOwner(owner)
  }
  def getBySourceType(sourceType:PasteSource) : java.util.List[Paste] = {
    return pasteDao.getBySourceType(sourceType)
  }

    override def save(obj:Paste):Paste = {
    val out = super.save(obj)

    PasteManager.Stats.totalPastas.addAndGet(1)
    return out
    }
  }
