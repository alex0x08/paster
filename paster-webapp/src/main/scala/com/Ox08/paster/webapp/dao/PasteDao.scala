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

package com.Ox08.paster.webapp.dao

import com.Ox08.paster.webapp.model.{Paste, Priority, User}
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

import java.util.ArrayList
import javax.persistence.{Query, Tuple}
import javax.persistence.criteria.{CriteriaQuery, Predicate}
import scala.jdk.CollectionConverters._

@Repository("pasteDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class PasteDaoImpl extends SearchableDaoImpl[Paste](classOf[Paste]) {

  def getByOwner(owner: User) = getListByKeyValue("owner", owner)

  def getByRemoteUrl(url: String) = getListByKeyValue("remoteUrl", url)

  def getNextPaste(paste: Paste): Paste = {
    val out: java.util.List[Paste] = getNextPreviousPaste(paste, false, 1)
    if (out.isEmpty) null else out.get(0)
  }

  def getPreviousPaste(paste: Paste): Paste = {
    val out: java.util.List[Paste] = getNextPreviousPaste(paste, true, 1)
    if (out.isEmpty) null else out.get(0)
  }

  def getPreviousPastas(paste: Paste): java.util.List[Paste] =
    getNextPreviousPaste(paste, true, BaseDaoImpl.MAX_RESULTS)

  def getPreviousPastasIdList(paste: Paste): java.util.List[Long] = {

    val out = new ArrayList[Long]

    val cb = em.getCriteriaBuilder
    val cr = cb.createTupleQuery()
    val r = cr.from(getModel())

    cr.multiselect(r.get("id"))

    val select = new ArrayList[Predicate]
    select.add(cb.notEqual(r.get("id"), paste.getId()))

    if (paste.getIntegrationCode() != null) {
      select.add(cb.equal(r.get("integrationCode"), paste.getIntegrationCode()))
    }

    select.add(cb.equal(r.get("channel"), paste.getChannel()))
    select.add(cb.lessThanOrEqualTo(r.get("lastModified")
      .as(classOf[java.util.Date]), paste.getLastModified()))
    cr.where(select.toArray(new Array[Predicate](select.size)): _*)
      .orderBy(
        cb.desc(r.get("lastModified"))
      )
    val tupleResult: java.util.List[Tuple] = em.createQuery(cr)
      .setMaxResults(BaseDaoImpl.MAX_RESULTS).getResultList()
    for (t <- tupleResult.asScala) {
      out.add(t.get(0).asInstanceOf[Long])
    }
    out
  }


  private def getNextPreviousPaste(paste: Paste, direction: Boolean, maxResults: Int):
  java.util.List[Paste] = {

    val cr = new CriteriaSet()
    val select = new ArrayList[Predicate]

    select.add(cr.cb.notEqual(cr.r.get("id"), paste.getId))
    if (paste.getIntegrationCode != null) {
      select.add(cr.cb.equal(cr.r.get("integrationCode"), paste.getIntegrationCode()))
    }

    select.add(cr.cb.equal(cr.r.get("channel"), paste.getChannel()))
    if (direction) {
      select.add(cr.cb.lessThanOrEqualTo(cr.r.get("lastModified").as(classOf[java.util.Date]), paste.getLastModified()))
    } else {
      select.add(cr.cb.greaterThanOrEqualTo(cr.r.get("lastModified").as(classOf[java.util.Date]), paste.getLastModified()))
    }

    val query = em.createQuery[Paste](
      cr.cr.where(select.toArray(new Array[Predicate](select.size)): _*)
        .orderBy(if (direction) {
          cr.cb.desc(cr.r.get("lastModified"))
        } else {
          cr.cb.asc(cr.r.get("lastModified"))
        }))
      .setMaxResults(maxResults)
    query.getResultList()
  }

  def getByChannel(channel: String, sortAsc: Boolean): java.util.List[Paste] = {
    val cr = new CriteriaSet()
    val query: Query = em.createQuery(
      cr.cr.where(
        Array(
          cr.cb.equal(cr.r.get("channel"), channel)
        ): _*)
        .orderBy(cr.cb.desc(cr.r.get("sticked"))
          , if (sortAsc) {
            cr.cb.asc(cr.r.get("lastModified"))
          } else {
            cr.cb.desc(cr.r.get("lastModified"))
          })
        .select(cr.r))
      .setMaxResults(BaseDaoImpl.MAX_RESULTS)
    query.getResultList().asInstanceOf[java.util.List[Paste]]
  }

  override def getList(): java.util.List[Paste] = {
    val cr = new CriteriaSet()
    em.createQuery[Paste](cr.cr.orderBy(cr.cb.desc(cr.r.get("sticked"))
      , cr.cb.desc(cr.r.get("lastModified"))))
      .setMaxResults(BaseDaoImpl.MAX_RESULTS).getResultList()
  }

  def getListIntegrated(code: String): java.util.List[Paste] = {
    val cr = new CriteriaSet()
    val query = em.createQuery[Paste](
      cr.cr.where(Array(cr.cb.equal(cr.r.get("integrationCode"), code)): _*)
        .orderBy(cr.cb.desc(cr.r.get("sticked"))
          , cr.cb.desc(cr.r.get("lastModified"))))
      .setMaxResults(BaseDaoImpl.MAX_RESULTS)
    query.getResultList()
  }

  def countAll(p: Priority): java.lang.Long = {

    val cb = em.getCriteriaBuilder()
    val cq: CriteriaQuery[java.lang.Long] = cb.createQuery(classOf[java.lang.Long])

    val r = cq.from(getModel())

    cq.select(cb.count(r))
    cq.where(Array(cb.equal(r.get("priority"), p.getCode())): _*)

    em.createQuery[java.lang.Long](cq)
      .getSingleResult()
  }

  /**
   * count all pastas created/modified from specified date
   *
   * @param dateFrom
   * dateTime from start to search
   * @return number of modified or created pastas         
   */
  def countAllSince(channel: String, dateFrom: java.lang.Long): java.lang.Long = {

    val cb = em.getCriteriaBuilder
    val cq: CriteriaQuery[java.lang.Long] = cb.createQuery(classOf[java.lang.Long])

    val r = cq.from(getModel)

    cq.select(cb.count(r))

    cq.where(Array(
      cb.greaterThan(r.get("lastModified")
        .as(classOf[java.util.Date]), new java.util.Date(dateFrom)),
      cb.equal(r.get("channel"), channel)): _*)
    em.createQuery[java.lang.Long](cq).getSingleResult()
  }

}
