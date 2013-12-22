/**
 * Copyright (C) 2010 alex <me@alex.0x08.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.dao

import org.springframework.transaction.annotation.Transactional
import uber.paste.base.Loggered
import javax.persistence.{Query, EntityManager, PersistenceContext, Tuple}
import java.util.ArrayList
import javax.persistence.criteria.CriteriaQuery
import scala.collection.JavaConversions._


abstract trait BaseDao[T <: java.io.Serializable,PK <:Long] {

  def save(obj:T):T

  def persist(obj:T)

  def remove(id:PK):Unit

  def get(id:PK):T

  def getFull(id:PK):T
  
  def getList():java.util.List[T]

  def countAll():java.lang.Long

  def exists(id:PK):Boolean

  def getIdList(from:PK):java.util.List[PK]
  
}

object BaseDaoImpl {
   val MAX_RESULTS  = 2000
}

@Transactional(readOnly = true)
abstract class BaseDaoImpl[T <: java.io.Serializable,PK <:Long ](model:Class[T]) extends Loggered with BaseDao[T,PK]{


  protected class CriteriaSet {
    val cb = em.getCriteriaBuilder
    val cr = cb.createQuery(model)
    val r = cr.from(model)
  }

  @PersistenceContext
  protected val em:EntityManager = null

  protected def getModel() = model

  @Transactional(readOnly = false)
  def save(obj:T):T = {
    logger.debug("saving obj "+obj)
    val out:T = em.merge(obj)
    em.flush()
    return out
  }

  @Transactional(readOnly = false)
  def persist(obj:T):Unit = {
    em.persist(obj)
  }

  def exists(id:PK):Boolean = {
    return em.find(model, id)!=null
  }

  @Transactional
  def remove(id:PK):Unit = {
    val obj:T = get(id)

    if (obj!=null) {
      em.remove(obj)
      em.flush()
    }
  }

  def get(id:PK):T = {
    return em.find(model,id)
  }

  def countAll():java.lang.Long = {

    val cr = new CriteriaSet

    val cq:CriteriaQuery[java.lang.Long] = cr.cb.createQuery(classOf[java.lang.Long])
    cq.select(cr.cb.count(cq.from(model)))

    return em.createQuery(cq)
      .getSingleResult().asInstanceOf[java.lang.Long]

  }

  def getList():java.util.List[T] = {

    val cr = new CriteriaSet

    val query:Query = em.createQuery(cr.cr.orderBy(cr.cb.desc(cr.r.get("id")))).setMaxResults(BaseDaoImpl.MAX_RESULTS)
    return query.getResultList().asInstanceOf[java.util.List[T]]
  }
  
  
  def getIdList(from:PK):java.util.List[PK] = {
    
    val out = new ArrayList[PK]
    
   // val cr = new CriteriaSet

    
     val cb = em.getCriteriaBuilder
    
     val cr = cb.createTupleQuery()
 
    val r = cr.from(model)
 
    
    cr.multiselect(r.get("id"))
    
    if (from >0) {
      cr.where(Array(cb.lt(r.get("id"), from)):_*)
    }
    
    val tupleResult:java.util.List[Tuple] = em.createQuery(cr)
          .setMaxResults(BaseDaoImpl.MAX_RESULTS).getResultList()
    
    for (t<-tupleResult) {
      out.add(t.get(0).asInstanceOf[PK])
    }
    return out
  }
  
}
