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

import org.apache.commons.lang3.StringUtils
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.queryparser.classic.{MultiFieldQueryParser, ParseException, QueryParser}
import org.apache.lucene.search.highlight.{Highlighter, QueryScorer, SimpleHTMLFormatter, SimpleSpanFragmenter}
import org.hibernate.CacheMode
import org.hibernate.search.jpa.{FullTextEntityManager, FullTextQuery, Search}
import org.springframework.transaction.annotation.Transactional
import uber.paste.base.Loggered
import uber.paste.model.Struct
import java.util.ArrayList
import scala.jdk.CollectionConverters._

object SearchableDaoImpl {

  val FORMATTER = new SimpleHTMLFormatter("[result]", "[/result]")
  val DEFAULT_START_FIELDS = Array[String]("name")
  val searchableDao = new ArrayList[SearchableDaoImpl[_]]()
}

@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
abstract class SearchableDaoImpl[T <: Struct](model: Class[T])
  extends StructDaoImpl[T](model) {

  /**
   * this is needed to be able to reindex all implementations
   *
   * @see reindex 
   */
  SearchableDaoImpl.searchableDao.add(this)

  protected class FSearch(query: String) extends Loggered {
    logger.debug("searching for {}", query)

    val fsession: FullTextEntityManager = getFullTextEntityManager()
    val pparser = new MultiFieldQueryParser(getDefaultStartFields(),
      new StandardAnalyzer())

    val luceneQuery: org.apache.lucene.search.Query = pparser.parse(query)
    val scorer: QueryScorer = new QueryScorer(luceneQuery)
    val highlighter: Highlighter = new Highlighter(SearchableDaoImpl.FORMATTER, scorer)
    highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 100))
    val fquery: FullTextQuery = fsession.createFullTextQuery(luceneQuery, model)

    def getResults() = fillHighlighted(highlighter, pparser, fquery.getResultList())

  }

  def getFullTextEntityManager() = Search.getFullTextEntityManager(em)

  def getDefaultStartFields(): Array[String] = SearchableDaoImpl.DEFAULT_START_FIELDS

  def fillHighlighted(highlighter: Highlighter,
                      pparser: QueryParser,
                      results: java.util.List[_]): java.util.List[T] = {
    for (obj <- results.asScala) {
      fillHighlighted(highlighter, pparser, obj.asInstanceOf[T])
    }
    results.asInstanceOf[java.util.List[T]]
  }

  def indexAll() {

    val fsession = getFullTextEntityManager()
    try {

      fsession.createIndexer(model)
        .batchSizeToLoadObjects(25)
        .cacheMode(CacheMode.NORMAL)
        .threadsToLoadObjects(1)
        .threadsForSubsequentFetching(2)
        .startAndWait()

    } catch {
      case e: InterruptedException => {
        throw new RuntimeException(e)
      }
    }
  }

  def fillHighlighted(highlighter: Highlighter,
                      pparser: QueryParser,
                      model: T) {
    /* try {
         val hl = highlighter
                 .getBestFragments(pparser.getAnalyzer()
                         .tokenStream("code", new StringReader(model.getCode())),
                         model.getName(), 3, " ...");

         if (hl != null && hl.trim().length() > 0) {
             model.setCode(hl);
         }


 } catch {
   case e @ (_ : IOException  | _ : InvalidTokenOffsetsException) => {
       logger.error(e.getLocalizedMessage,e)
     }
 }*/

  }


  @throws(classOf[ParseException])
  def search(query: String): java.util.List[T] = {

    /**
     * ignore empty queries
     */
    if (StringUtils.isBlank(query) || query.trim().equals("*")) {
      return getList()
    }

    /**
     * added for stupid users
     */
    new FSearch(
      if (!query.contains(":") && !query.contains("*"))
        query + "*" else
        query
    ).getResults()
  }


}

  