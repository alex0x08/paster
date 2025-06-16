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
import org.apache.commons.lang3.StringUtils
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.queryparser.classic.{MultiFieldQueryParser, ParseException, QueryParser}
import org.apache.lucene.search.highlight.{Highlighter, QueryScorer, SimpleHTMLFormatter, SimpleSpanFragmenter}
import org.hibernate.CacheMode
import org.hibernate.search.backend.lucene.LuceneExtension
import org.hibernate.search.backend.lucene.search.query.LuceneSearchQuery
import org.hibernate.search.engine.search.predicate.SearchPredicate
import org.hibernate.search.mapper.orm.Search
import org.hibernate.search.mapper.orm.session.SearchSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util
import scala.jdk.CollectionConverters._

object SearchableDaoImpl {
  val FORMATTER = new SimpleHTMLFormatter("[result]", "[/result]")
  private val DEFAULT_START_FIELDS: Array[String] = Array[String]("name")
}

/**
 * This service used to configure full-text indexes
 */
@Service
class SetupIndexes extends Logged with ApplicationListener[ContextRefreshedEvent] {
  @Value("${paster.reindexOnBoot:false}")
  private val reindexOnBoot: Boolean = false // if we allow re-indexing on boot

  /**
   * Triggers on Paster start and does re-indexing
   * @param event
   */
  @Transactional
  def onApplicationEvent(event: ContextRefreshedEvent): Unit = {
    // if we allow re-index on boot
    if (reindexOnBoot) {
      val allSearchableDao = event.getApplicationContext
                      .getBeansOfType(classOf[SearchableDaoImpl[_]])
      for (d <- allSearchableDao.entrySet().asScala)
        d.getValue.indexAll()
      logger.info("reindex completed.")
    } else
      logger.debug("reindex disabled. skipping..")
  }
}

/**
 * Abstract Searchable service, dedicated for single entity
 * @param model
 *      target entity class
 * @tparam T
 *        target entity
 */
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
abstract class SearchableDaoImpl[T <: Struct](model: Class[T])
  extends StructDaoImpl[T](model) {

  /**
   * This class is responsible for full-text searching, new session will be opened on each
   * request
   * @param query
   *        text query
   */
  private class FSearch(query: String) extends Logged {
    if (logger.isDebugEnabled)
      logger.debug("searching for {}", query)

    // get search session from EntityManager
    private val searchSession: SearchSession = getFullTextEntityManager
    // build query parser
    private val queryParser = new MultiFieldQueryParser(getDefaultStartFields,
      new StandardAnalyzer())
  //  val sort: org.apache.lucene.search.Sort = new org.apache.lucene.search.Sort(
  //    new org.apache.lucene.search.SortField("id",
   //     org.apache.lucene.search.SortField.Type.LONG))
    // parse query
    private val luceneQuery: org.apache.lucene.search.Query = queryParser.parse(query)

    private val scorer: QueryScorer = new QueryScorer(luceneQuery)
    private val highlighter: Highlighter = new Highlighter(SearchableDaoImpl.FORMATTER, scorer)
    highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 100))
    // build predicate
    private val predicate: SearchPredicate = searchSession
      .scope(model).predicate.extension(LuceneExtension.get)
      .fromLuceneQuery(luceneQuery).toPredicate
    // do search
    private val searchQuery: LuceneSearchQuery[T] = searchSession.search(model)
      .extension(LuceneExtension.get())
      .where(predicate).toQuery

    /**
     * Return found results
     * @return
     */
    def getResults: util.List[T] = fillHighlighted(
      highlighter,
      queryParser,
      searchQuery.fetchAll().hits())
  }
  private def getFullTextEntityManager: SearchSession = Search.session(em)
  def getDefaultStartFields: Array[String] = SearchableDaoImpl.DEFAULT_START_FIELDS

  /**
   * Highlight found results
   * @param highlighter
   * @param queryParser
   * @param results
   * @return
   */
  private def fillHighlighted(highlighter: Highlighter,
                              queryParser: QueryParser,
                              results: java.util.List[_]): java.util.List[T] = {
    if (logger.isDebugEnabled)
      logger.debug("found {} results", results.size())
    for (obj <- results.asScala)
      fillHighlighted(highlighter, queryParser, obj.asInstanceOf[T])
    results.asInstanceOf[java.util.List[T]]
  }

  /**
   * re-index current model
   */
  def indexAll(): Unit = {
    val searchSession = getFullTextEntityManager
    try {
      searchSession.massIndexer(model)
        .batchSizeToLoadObjects(25)
        .cacheMode(CacheMode.NORMAL)
        .threadsToLoadObjects(1)
        // .threadsForSubsequentFetching(2)
        .startAndWait()
    } catch {
      case e: InterruptedException =>
        throw new RuntimeException(e)
    }
  }
  def fillHighlighted(highlighter: Highlighter,
                      pparser: QueryParser,
                      model: T): Unit
  @throws(classOf[ParseException])
  def search(query: String): java.util.List[T] = {
    /**
     * ignore empty queries
     */
    if (StringUtils.isBlank(query) || query.trim().equals("*"))
      return getList
     new FSearch(
      if (!query.contains(":") && !query.contains("*"))
        query + "*"
      else
        query
    ).getResults
  }
}
