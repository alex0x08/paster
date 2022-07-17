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
  val DEFAULT_START_FIELDS: Array[String] = Array[String]("name")
}
@Service
class SetupIndexes extends Logged with ApplicationListener[ContextRefreshedEvent] {
  @Value("${paster.reindexOnBoot:false}")
  val reindexOnBoot: Boolean = false
  @Transactional
  def onApplicationEvent(event: ContextRefreshedEvent): Unit = {
    if (reindexOnBoot) {
      val allSearchableDao = event.getApplicationContext.getBeansOfType(classOf[SearchableDaoImpl[_]])
      for (d <- allSearchableDao.entrySet().asScala) {
        d.getValue.indexAll()
      }
      logger.info("reindex completed.")
    } else logger.info("reindex was disabled. skipping it.")
  }
}
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
abstract class SearchableDaoImpl[T <: Struct](model: Class[T])
  extends StructDaoImpl[T](model) {
  protected class FSearch(query: String) extends Logged {
    logger.debug("searching for {}", query)
    val searchSession: SearchSession = getFullTextEntityManager
    val queryParser = new MultiFieldQueryParser(getDefaultStartFields,
      new StandardAnalyzer())
    val luceneQuery: org.apache.lucene.search.Query = queryParser.parse(query)
    val scorer: QueryScorer = new QueryScorer(luceneQuery)
    val highlighter: Highlighter = new Highlighter(SearchableDaoImpl.FORMATTER, scorer)
    highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 100))
    val predicate2: SearchPredicate = searchSession.scope(model).predicate.extension(LuceneExtension.get)
      .fromLuceneQuery(luceneQuery).toPredicate
    val searchQuery: LuceneSearchQuery[T] = searchSession.search(model)
      .extension(LuceneExtension.get())
      .where(predicate2).toQuery
    def getResults: util.List[T] = fillHighlighted(highlighter, queryParser, searchQuery.fetchAll().hits())
  }
  def getFullTextEntityManager: SearchSession = Search.session(em)
  def getDefaultStartFields: Array[String] = SearchableDaoImpl.DEFAULT_START_FIELDS
  def fillHighlighted(highlighter: Highlighter,
                      queryParser: QueryParser,
                      results: java.util.List[_]): java.util.List[T] = {
    logger.debug("found {} results", results.size())
    for (obj <- results.asScala) {
      fillHighlighted(highlighter, queryParser, obj.asInstanceOf[T])
    }
    results.asInstanceOf[java.util.List[T]]
  }
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
    if (StringUtils.isBlank(query) || query.trim().equals("*")) {
      return getList
    }

    /**
     * added for stupid users
     */
    new FSearch(
      /*if (!query.contains(":") && !query.contains("*"))
        query + "*" else*/
      query
    ).getResults
  }
}

