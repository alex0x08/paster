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
import com.Ox08.paster.webapp.model.{Paste, PasterUser}
import jakarta.persistence.criteria.{CriteriaQuery, Predicate, Selection}
import jakarta.persistence.{Query, Tuple}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.highlight.{Highlighter, InvalidTokenOffsetsException}
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.time.LocalDateTime
import scala.collection.mutable
import scala.jdk.CollectionConverters._

/**
 * Custom DAO level for paste entity
 * @since 1.0
 * @author 0x08
 */
@Repository("pasteDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class PasteDao extends SearchableDaoImpl[Paste](classOf[Paste]) {
  override def getDefaultStartFields: Array[String] = Array("title", "text")

  /**
   * Query paste entities by author.
   * Response limited by BaseDao.MAX_RESULTS
   * @param author
   *        selected author (an user)
   * @return
   */
  def getByAuthor(author: PasterUser): java.util.List[Paste] =
                getListByKeyValue("author", author)
  def getByRemoteUrl(url: String): java.util.List[Paste] =
                getListByKeyValue("remoteUrl", url)
  /**
   * Find paste entity, which is next to selected
   *
   * @param paste
   *        selected paste entity
   * @return
   *        next paste entity
   */
  def getNextPaste(paste: Paste): Paste = {
    val out: java.util.List[Paste] = getNextOrPreviousPaste(paste, direction = false, 1)
    if (out.isEmpty) null else out.get(0)
  }

  /**
   * Find paste entity, which is previous to selected one
   * @param paste
   *        selected paste entity
   * @return
   */
  def getPreviousPaste(paste: Paste): Paste = {
    val out: java.util.List[Paste] = getNextOrPreviousPaste(paste, direction = true, 1)
    if (out.isEmpty) null else out.get(0)
  }

  /**
   * Find list of paste entities which are previous to selected
   * @param paste
   *        selected entity
   * @return
   *      list of paste entities
   */
  def getPreviousPastas(paste: Paste): java.util.List[Paste] =
    getNextOrPreviousPaste(paste, direction = true, BaseDao.MAX_RESULTS)

  /**
   * Find and return list of IDs of paste entities, which are previous to selected
   * @param paste
   *        selected entity
   * @return
   *        list of IDs
   */
  def getPreviousPastasIdList(paste: Paste): java.util.List[Integer] = {
    val out = new java.util.ArrayList[Integer]
    val cb = em.getCriteriaBuilder
    val cr = cb.createTupleQuery()
    val r = cr.from(getModel)
    cr.select(r.get("id"))
    val select = new java.util.ArrayList[Predicate]
    select.add(cb.notEqual(r.get("id"), paste.id))
    if (paste.integrationCode != null)
      select.add(cb.equal(r.get("integrationCode"), paste.integrationCode))
    select.add(cb.equal(cb.lower(r.get("channel")), paste.channel.toLowerCase))
    select.add(cb.lessThanOrEqualTo(r.get("created")
      .as(classOf[LocalDateTime]), paste.created))
    cr.where(select.toArray(new Array[Predicate](select.size)): _*)
      .orderBy(
        cb.desc(r.get("created"))
      )
    val tupleResult: java.util.List[Tuple] = em.createQuery(cr)
      .setMaxResults(BaseDao.MAX_RESULTS).getResultList
    for (t <- tupleResult.asScala)
      out.add(t.get(0).asInstanceOf[Integer])
    out
  }
  /**
   * Seeks for previous or next paste , related to specified
   *
   * @param paste
   * selected paste
   * @param direction
   * false  -previous
   * true - next
   * @param maxResults
   * maximum results in list
   * @return
   */
  private def getNextOrPreviousPaste(paste: Paste,
                                     direction: Boolean, maxResults: Int):
  java.util.List[Paste] = {
    val cr = new CriteriaSet()
    val select = new java.util.ArrayList[Predicate]
    select.add(cr.cb.notEqual(cr.r.get("id"), paste.id))
    if (paste.integrationCode != null)
      select.add(cr.cb.equal(cr.r.get("integrationCode"), paste.integrationCode))
    select.add(cr.cb.equal(cr.cb.lower(cr.r.get("channel")), paste.channel.toLowerCase))
    if (direction)
      select.add(cr.cb.lessThanOrEqualTo(cr.r.get("created")
        .as(classOf[LocalDateTime]), paste.created))
    else
      select.add(cr.cb.greaterThanOrEqualTo(cr.r.get("created")
        .as(classOf[LocalDateTime]), paste.created))
    val query = em.createQuery[Paste](
      cr.cr.where(select.toArray(new Array[Predicate](select.size)): _*)
        .orderBy(if (direction)
          cr.cb.desc(cr.r.get("created"))
        else
          cr.cb.asc(cr.r.get("created"))))
      .setMaxResults(if (maxResults > BaseDao.MAX_RESULTS)
        BaseDao.MAX_RESULTS else maxResults)
    query.getResultList
  }
  def getByChannel(channel: String, sortAsc: Boolean): java.util.List[Paste] = {
    val cr = new CriteriaSet()
    val query: Query = em.createQuery(
      cr.cr.where(
        Array(
          cr.cb.equal(cr.cb.lower(cr.r.get("channel")), channel.toLowerCase)
        ): _*)
        .orderBy(cr.cb.desc(cr.r.get("stick"))
          , if (sortAsc)
            cr.cb.asc(cr.r.get("lastModified"))
          else
            cr.cb.desc(cr.r.get("lastModified")))
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
  /**
   *
   *
   * SELECT COUNT(p.*) AS TOTAL,
   * sum(case when p."P_CHANNEL" = 'Main' then 1 else 0 end) AS Main,
   * sum(case when p."P_CHANNEL" = 'Tech' then 1 else 0 end) AS Tech FROM P_PASTAS p;
   *
   */
  def countStats(channels: Array[String]): Map[String, Long] = {
    /*val cb = em.getCriteriaBuilder
    val cq: CriteriaQuery[Tuple] = cb.createTupleQuery()
    val r = cq.from(getModel)
    val paths = new java.util.ArrayList[Selection[_]]()
    paths.add(cb.count(r).alias("total"))
    for (c <- channels) {
      paths.add(cb.sum(
        cb.selectCase()
          .when(cb.equal(cb.lower(r.get("channel")), c.toLowerCase), 1)
          .otherwise(0).as(classOf[Integer]))
        .alias(c))
    }
    // migrate from deprecated multiselect
    cq.select(cb.tuple(paths))
    val results = em.createQuery[Tuple](cq).getResultList
    */
    val out = mutable.Map[String, Long]()

    out.put("total", 0) // results.get(0).get("total").asInstanceOf[Long])
    /*for (c <- channels) {
      out.put(c,
        if (results.get(0).get(c) == null)
          0
            else
        results.get(0).get(c).asInstanceOf[Integer].longValue())
    }*/
    out.toMap
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
      cb.equal(cb.lower(r.get("channel")), channel.toLowerCase)): _*)
    em.createQuery[java.lang.Long](cq).getSingleResult
  }
  override def fillHighlighted(highlighter: Highlighter,
                               queryParser: QueryParser, model: Paste): Unit = {
    try {
      val hl = highlighter
        .getBestFragments(queryParser.getAnalyzer
          .tokenStream("title", model.title),
          model.title, 3, " ...")
      if (hl != null && hl.trim().nonEmpty)
        model.title = hl
    } catch {
      case e@(_: IOException | _: InvalidTokenOffsetsException) =>
        logger.error(e.getMessage, e)
    }
  }
}
