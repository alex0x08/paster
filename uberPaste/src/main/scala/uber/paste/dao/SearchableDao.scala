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

package uber.paste.dao

import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import uber.paste.model.Struct
import uber.paste.model.Query
import org.compass.core.CompassDetachedHits
import org.compass.core.CompassHits
import org.compass.core.CompassTemplate
import org.compass.core.CompassCallback
import org.compass.core.CompassSession
import org.compass.core.CompassException
import java.util.ArrayList

abstract trait SearchableDao[T <: Struct] extends StructDao[T] {

  def search(query:Query):java.util.List[T]
}

@Transactional(readOnly = true)
abstract class SearchableDaoImpl[T <: Struct](model:Class[T])
  extends StructDaoImpl[T](model) with SearchableDao[T] {

  @Autowired
  private val compassTemplate:CompassTemplate = null


  def search(callback:CompassCallback[CompassDetachedHits]):java.util.List[T] = {
    val out = new ArrayList[T]

    val hits:CompassDetachedHits = compassTemplate.execute(callback)

    val ch = hits.highlightedText(0)

    val fragment = if (ch!=null) { ch.getHighlightedText } else { logger.debug("highlighter is null"); null }

    logger.debug("highlight="+fragment)

    for (d <- hits.getDatas) {
      out.add(d.asInstanceOf[T])
    }
    return out
  }
  def search(query:Query):java.util.List[T] = {

    if (query.isEmpty) {
      return getList
    }
   val out = new ArrayList[T]
    compassTemplate.execute(new CompassCallback[CompassDetachedHits]() {

      @throws(classOf[CompassException])
      def doInCompass(session:CompassSession):CompassDetachedHits =  {
        val hits:CompassHits = query.fillQuery(session)
          .setTypes(model).hits

        var maxlength = hits.length-1
        if (maxlength>50) {
          maxlength =50
        }

        val mterms = model.newInstance.terms

        for (i <- 0 to maxlength) {

          val data = hits.data(i).asInstanceOf[T]

          try {

            if (logger.isDebugEnabled()) {
              for (term <- mterms) {
                val f = hits.highlighter(i).fragment(term)
                logger.debug("fragment "+f)
              }
            }

            data.fillFromHits(hits.highlighter(i))
          } catch {
            case e:org.compass.core.engine.SearchEngineException => {
              logger.error(e.getLocalizedMessage,e)
            }
          }
          out.add(data)

        }

        return null
        //hits.detach(0,50)
      }
    })
    return out
  }
}