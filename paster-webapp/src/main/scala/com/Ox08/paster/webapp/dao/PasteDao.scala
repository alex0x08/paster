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

import com.Ox08.paster.webapp.model.{Paste, PasterUser}
import jakarta.persistence.criteria.{CriteriaQuery, Predicate}
import jakarta.persistence.{Query, Tuple}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.highlight.{Highlighter, InvalidTokenOffsetsException}
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

import java.io.IOException
import java.time.LocalDateTime
import java.util
import scala.jdk.CollectionConverters._

@Repository("pasteDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class PasteDao extends SearchableDaoImpl[Paste](classOf[Paste]) {

  def getByAuthor(author: PasterUser): util.List[Paste] = getListByKeyValue("author", author)

  def getByRemoteUrl(url: String): util.List[Paste] = getListByKeyValue("remoteUrl", url)

  def getNextPaste(paste: Paste): Paste = {
    val out: java.util.List[Paste] = getNextPreviousPaste(paste, direction = false, 1)
    if (out.isEmpty) null else out.get(0)
  }

  def getPreviousPaste(paste: Paste): Paste = {
    val out: java.util.List[Paste] = getNextPreviousPaste(paste, direction = true, 1)
    if (out.isEmpty) null else out.get(0)
  }

  def getPreviousPastas(paste: Paste): java.util.List[Paste] =
    getNextPreviousPaste(paste, direction = true, BaseDao.MAX_RESULTS)

  def getPreviousPastasIdList(paste: Paste): java.util.List[Integer] = {

    val out = new util.ArrayList[Integer]

    val cb = em.getCriteriaBuilder
    val cr = cb.createTupleQuery()
    val r = cr.from(getModel)

    cr.multiselect(r.get("id"))

    val select = new util.ArrayList[Predicate]
    select.add(cb.notEqual(r.get("id"), paste.id))

    if (paste.integrationCode != null) {
      select.add(cb.equal(r.get("integrationCode"), paste.integrationCode))
    }

    select.add(cb.equal(r.get("channel"), paste.channel))
    select.add(cb.lessThanOrEqualTo(r.get("lastModified")
      .as(classOf[LocalDateTime]), paste.lastModified))
    cr.where(select.toArray(new Array[Predicate](select.size)): _*)
      .orderBy(
        cb.desc(r.get("lastModified"))
      )
    val tupleResult: java.util.List[Tuple] = em.createQuery(cr)
      .setMaxResults(BaseDao.MAX_RESULTS).getResultList
    for (t <- tupleResult.asScala) {
      out.add(t.get(0).asInstanceOf[Integer])
    }
    out
  }


  private def getNextPreviousPaste(paste: Paste, direction: Boolean, maxResults: Int):
  java.util.List[Paste] = {

    val cr = new CriteriaSet()
    val select = new util.ArrayList[Predicate]

    select.add(cr.cb.notEqual(cr.r.get("id"), paste.id))
    if (paste.integrationCode != null) {
      select.add(cr.cb.equal(cr.r.get("integrationCode"), paste.integrationCode))
    }

    select.add(cr.cb.equal(cr.r.get("channel"), paste.channel))
    if (direction) {
      select.add(cr.cb.lessThanOrEqualTo(cr.r.get("lastModified").as(classOf[LocalDateTime]), paste.lastModified))
    } else {
      select.add(cr.cb.greaterThanOrEqualTo(cr.r.get("lastModified").as(classOf[LocalDateTime]), paste.lastModified))
    }

    val query = em.createQuery[Paste](
      cr.cr.where(select.toArray(new Array[Predicate](select.size)): _*)
        .orderBy(if (direction) {
          cr.cb.desc(cr.r.get("lastModified"))
        } else {
          cr.cb.asc(cr.r.get("lastModified"))
        }))
      .setMaxResults(maxResults)
    query.getResultList
  }

  def getByChannel(channel: String, sortAsc: Boolean): java.util.List[Paste] = {
    val cr = new CriteriaSet()
    val query: Query = em.createQuery(
      cr.cr.where(
        Array(
          cr.cb.equal(cr.r.get("channel"), channel)
        ): _*)
        .orderBy(cr.cb.desc(cr.r.get("stick"))
          , if (sortAsc) {
            cr.cb.asc(cr.r.get("lastModified"))
          } else {
            cr.cb.desc(cr.r.get("lastModified"))
          })
        .select(cr.r))
      .setMaxResults(BaseDao.MAX_RESULTS)
    query.getResultList.asInstanceOf[java.util.List[Paste]]
  }

  override def getList: java.util.List[Paste] = {
    val cr = new CriteriaSet()
    em.createQuery[Paste](cr.cr.orderBy(cr.cb.desc(cr.r.get("stick"))
      , cr.cb.desc(cr.r.get("lastModified"))))
      .setMaxResults(BaseDao.MAX_RESULTS).getResultList
  }

  def getListIntegrated(code: String): java.util.List[Paste] = {
    val cr = new CriteriaSet()
    val query = em.createQuery[Paste](
      cr.cr.where(Array(cr.cb.equal(cr.r.get("integrationCode"), code)): _*)
        .orderBy(cr.cb.desc(cr.r.get("stick"))
          , cr.cb.desc(cr.r.get("lastModified"))))
      .setMaxResults(BaseDao.MAX_RESULTS)
    query.getResultList
  }

  def countAll(p: String): java.lang.Long = {

    val cb = em.getCriteriaBuilder
    val cq: CriteriaQuery[java.lang.Long] = cb.createQuery(classOf[java.lang.Long])

    val r = cq.from(getModel)

    cq.select(cb.count(r))
    cq.where(Array(cb.equal(r.get("priority"), p)): _*)

    em.createQuery[java.lang.Long](cq)
      .getSingleResult
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
    em.createQuery[java.lang.Long](cq).getSingleResult
  }

  override def fillHighlighted(highlighter: Highlighter, pparser: QueryParser, model: Paste): Unit = {

    try {

      val hl = highlighter
        .getBestFragments(pparser.getAnalyzer
          .tokenStream("title", model.title),
          model.title, 3, " ...")

      if (hl != null && hl.trim().nonEmpty) {
        model.title = hl
      }

    } catch {
      case e@(_: IOException | _: InvalidTokenOffsetsException) =>
        logger.error(e.getLocalizedMessage, e)
    }

  }
}
