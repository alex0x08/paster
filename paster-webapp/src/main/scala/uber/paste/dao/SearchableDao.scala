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
import org.apache.lucene.util.Version
import org.hibernate.CacheMode

import org.hibernate.search.jpa.FullTextEntityManager
import org.hibernate.search.jpa.FullTextQuery
import org.hibernate.search.jpa.Search
import org.springframework.beans.factory.annotation.Autowired
import uber.paste.model.Struct

import uber.paste.model.Key
import uber.paste.model.Query

import org.apache.commons.lang3.StringUtils
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.queryParser.MultiFieldQueryParser
import org.apache.lucene.queryParser.ParseException
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.highlight.Highlighter
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException
import org.apache.lucene.search.highlight.QueryScorer
import org.apache.lucene.search.highlight.SimpleHTMLFormatter
import org.apache.lucene.search.highlight.SimpleSpanFragmenter

import java.io.IOException
import java.io.StringReader
import java.util.ArrayList
import scala.collection.JavaConversions._
import uber.paste.base.Loggered

object SearchableDaoImpl {
  
  val FORMATTER = new SimpleHTMLFormatter("[result]", "[/result]")

  val DEFAULT_START_FIELDS = Array[String]("name")
  
  val searchableDao = new ArrayList[SearchableDaoImpl[_]]()
}


@Transactional(readOnly = true)
abstract class SearchableDaoImpl[T <: Struct](model:Class[T])
  extends StructDaoImpl[T](model)  {
    
  /**
   * this is needed to be able to reindex all implementations
   * @see reindex 
   */
  SearchableDaoImpl.searchableDao.add(this)
  
  @throws(classOf[ParseException])
  protected class FSearch(query:String) extends Loggered{
        
      logger.debug("searching for {}",query)
    
    val fsession = getFullTextEntityManager()

        val pparser = new MultiFieldQueryParser(Version.LUCENE_36, 
                                                getDefaultStartFields(),
                                                new StandardAnalyzer(Version.LUCENE_36))
        
        
        val luceneQuery:org.apache.lucene.search.Query = pparser.parse(query)
        val scorer:QueryScorer = new QueryScorer(luceneQuery)
        val highlighter:Highlighter = new Highlighter(SearchableDaoImpl.FORMATTER, scorer)
        
            highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 100))
    
        val fquery:FullTextQuery = fsession.createFullTextQuery(luceneQuery, model)
               
        
        def getResults() = fillHighlighted(highlighter, pparser, fquery.getResultList())
        
    }
  
   def getFullTextEntityManager() = Search.getFullTextEntityManager(em)

   def getDefaultStartFields():Array[String] = {
        return SearchableDaoImpl.DEFAULT_START_FIELDS
    }
  
   def fillHighlighted(highlighter:Highlighter,
            pparser:QueryParser,
            results:java.util.List[_]):java.util.List[T] = {
        for (obj <- results) {
          fillHighlighted(highlighter, pparser, obj.asInstanceOf[T])
        }
        return results.asInstanceOf[java.util.List[T]]
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
      case e:InterruptedException =>
       {
         throw new RuntimeException(e)
        }             
    }    
   }
  
   def fillHighlighted(highlighter:Highlighter,
            pparser:QueryParser,
            model:T) {
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
  def search(query:String):java.util.List[T] = {

        /**
         * ignore empty queries
         */
        if (StringUtils.isBlank(query) || query.trim().equals("*")) {
          return getList()
        }
        
        /**
         * added for stupid users
         */
        return new FSearch(
          if (!query.contains(":") && !query.contains("*")) 
           (query+"*") else 
            query
          ).getResults()
  }
   

}

  