/*
 * Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.dao
import com.Ox08.paster.webapp.base.Logged
import com.Ox08.paster.webapp.model.Struct
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Root}
import jakarta.persistence.{EntityManager, PersistenceContext, Tuple}
import org.springframework.transaction.annotation.{Propagation, Transactional}
import java.util
import scala.jdk.CollectionConverters._
object BaseDao {
  val MAX_RESULTS = 2000
}
/**
 * Basic DAO operations
 *
 * @param model model class (generics is not enough)
 */
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
abstract class BaseDao[T <: java.io.Serializable, PK <: java.io.Serializable](model: Class[T])
  extends Logged {
  /**
   * a wrapper to help work with Criteria API
   */
  protected class CriteriaSet {
    val cb: CriteriaBuilder = em.getCriteriaBuilder // criteria builder instance
    val cr: CriteriaQuery[T] = cb.createQuery(model)
    val r: Root[T] = cr.from(model) // query and root instances
    val ct: CriteriaQuery[Tuple] = cb.createTupleQuery() // tuple query
  }
  // famous JPA entity manager instance
  @PersistenceContext
  protected val em: EntityManager = null
  /**
   * @return model class
   */
  protected def getModel: Class[T] = model
  /**
   * save object in database, nothing new here
   *
   * yes, we need flush because of 2nd level cache usage
   *
   * @param obj
   * object to save
   * @return
   * persisted object
   */
  @Transactional(readOnly = false,
    rollbackFor = Array(classOf[Exception]), propagation = Propagation.REQUIRED)
  def save(obj: T): T = {
    if (logger.isDebugEnabled)
        logger.debug("saving obj {}", obj)
    val out: T = em.merge(obj)
    em.flush()
    out
  }
  /**
   * persist new object in database
   *
   * @param obj
   * new (not saved) object
   */
  @Transactional(readOnly = false)
  def persist(obj: T): Unit = em.persist(obj)
  /**
   * check if object exists in database
   *
   * @param id
   * object's unique id (PK type)
   * @return true/false         
   */
  def exists(id: PK): Boolean = em.find(model, id) != null
  /**
   * remove object from database
   *
   * @param id
   * object's unique identifier
   */
  @Transactional
  def remove(id: PK): Unit = {
    val obj: T = get(id)
    if (obj != null) {
      em.remove(obj)
      em.flush()
    } else
        logger.error("tried to remove non-exist object {}", id)
  }
  /**
   * retrieves object by id
   *
   * @param id
   * object's unique id
   * @return object of type T
   */
  def get(id: PK): T = em.find(model, id)
  /**
   * count all objects of type T
   *
   * @return objects count
   */
  def countAll(): java.lang.Long = {
    val cr = new CriteriaSet
    val cq: CriteriaQuery[java.lang.Long] =
      cr.cb.createQuery(classOf[java.lang.Long])
    cq.select(cr.cb.count(cq.from(model)))
    em.createQuery[java.lang.Long](cq).getSingleResult
  }
  /**
   * get single object
   */
  def getSingleByKeyValue(key: String, value: Any,
                          order: Option[String] = None,
                          asc: Option[Boolean] = None): T = {
    val results = getListByKeyValue(key, value, order, asc)
    if (results.isEmpty)
      null.asInstanceOf[T]
    else
      results.get(0)
  }
  /**
   * get list of objects by criteria (key-value)
   *
   * @param key
   * object's field name
   * @param value
   * object's field value
   * @param order
   * sort field
   * @param asc
   * use asc or desc sorting
   * @return list of objects with type T                                         
   */
  def getListByKeyValue(key: String, value: Any,
                        order: Option[String] = None,
                        asc: Option[Boolean] = None): java.util.List[T] = {
    val cr: CriteriaSet = new CriteriaSet
    em.createQuery[T](cr.cr.where(Array(cr.cb.equal(cr.r.get(key), value)): _*)
      .select(cr.r)
      .orderBy(
          if (order.isDefined)
              if (asc.getOrElse(false))
                    cr.cb.asc(cr.r.get(order.get))
                  else
                    cr.cb.desc(cr.r.get(order.get))
          else
            cr.cb.desc(cr.r.get(key))
      )).setMaxResults(BaseDao.MAX_RESULTS).getResultList
  }
  /**
   * get list of objects T
   *
   */
  def getList: java.util.List[T] = {
    val cr = new CriteriaSet
    em.createQuery[T](cr.cr.orderBy(cr.cb.desc(cr.r.get("id"))))
      .setMaxResults(BaseDao.MAX_RESULTS)
      .getResultList
  }
  def getAll: java.util.List[T] = {
    val cr = new CriteriaSet
    em.createQuery[T](cr.cr)
      .setMaxResults(BaseDao.MAX_RESULTS)
      .getResultList
  }
  /**
   * get list of objects ids, starting from FROM argument
   */
  def getIdList(from: PK): java.util.List[PK] = {
    val out = new util.ArrayList[PK]
    val cr = new CriteriaSet
    cr.ct.multiselect(cr.r.get("id"))
    // do not replace this with pattern matching
    if (from.isInstanceOf[Long] && from.asInstanceOf[Long] > 0)
      cr.ct.where(Array(cr.cb.lt(cr.r.get("id"), from.asInstanceOf[Long])): _*)
    val tupleResult: java.util.List[Tuple] = em.createQuery(cr.ct)
      .setMaxResults(BaseDao.MAX_RESULTS).getResultList
    for (t <- tupleResult.asScala) {
      out.add(t.get(0).asInstanceOf[PK])
    }
    out
  }
}
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
abstract class StructDaoImpl[T <: Struct](model: Class[T])
  extends BaseDao[T, Integer](model) {
  @Transactional
  def getFull(id: Integer): T = {
    val out: T = get(id)
    if (out == null) null.asInstanceOf[T] else {
      out.loadFull()
      out
    }
  }
}