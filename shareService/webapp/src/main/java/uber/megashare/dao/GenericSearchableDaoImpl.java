/**
 * Copyright (C) 2011 Alex <alex@0x08.tk>
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
package uber.megashare.dao;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.springframework.transaction.annotation.Transactional;
import uber.megashare.model.Struct;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.hibernate.CacheMode;
import org.hibernate.search.jpa.Search;
import uber.megashare.base.LoggedClass;

/**
 *
 * @author alex
 */
@Transactional(readOnly = true, rollbackFor = Exception.class,value= "transactionManager")
public abstract class GenericSearchableDaoImpl<T extends Struct> extends GenericVersioningDaoImpl<T> {

    protected class FSearch extends LoggedClass{
        
         FullTextEntityManager fsession = getFullTextEntityManager();

         QueryParser pparser = new MultiFieldQueryParser(Version.LUCENE_35, getDefaultStartFields(), an);
        
        
        Query luceneQuery;
        QueryScorer scorer;
        Highlighter highlighter;
        FullTextQuery fquery;
        
        public FSearch(String query) throws ParseException {
        
            getLogger().debug("searching for "+query);
            luceneQuery = pparser.parse(query);
            scorer=new QueryScorer(luceneQuery);
            highlighter = new Highlighter(formatter, scorer);
            highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 100));
            fquery = fsession.createFullTextQuery(luceneQuery, persistentClass);
      
        }
        
        public List<T> getResults() {        
            return fillHighlighted(highlighter, pparser, fquery.getResultList());
        }
    }
    
    
    /**
     *
     */
    private static final long serialVersionUID = -4658396754985952199L;

    protected SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("[result]", "[/result]");
   
    
    protected GenericSearchableDaoImpl(Class<T> clazz) {
        super(clazz);
    }
    
    /**
     * Set of default model fields to search
     */
    protected static final String DEFAULT_START_FIELDS[] = {"name"};


    public String[] getDefaultStartFields() {
        return DEFAULT_START_FIELDS;
    }

    /**
     * get hibernate search manager
     *
     * @return
     */
    protected FullTextEntityManager getFullTextEntityManager() {
        return Search.getFullTextEntityManager(getEntityManager());
    }

    
    /**
     * {@inheritDoc}
     */
    public void indexAll()  {
        FullTextEntityManager fsession = getFullTextEntityManager();
        try {
            fsession.createIndexer(persistentClass)
                    .batchSizeToLoadObjects(25)
                    .cacheMode(CacheMode.NORMAL)
                    .threadsToLoadObjects(1)
                    .threadsForSubsequentFetching(2)
                    .startAndWait();

            /**
             * not a best ever re-index implementation, but enough for demo
             */
            /*for (T t : getAll()) {
                fsession.index(t);
            }*/
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> search(String query) throws ParseException{

        /**
         * ignore empty queries
         */
        if (StringUtils.isBlank(query) || query.trim().equals("*")) {
            return getAll();
        }
        
        /**
         * added for stupid users
         */
        if (!query.contains(":") && !query.contains("*")) {
            query+="*";
        }

            return new FSearch(query).getResults();

    }
   
     protected List<T> fillHighlighted(Highlighter highlighter,
            QueryParser pparser,
            List<T> results) {
        for (T obj : results) {
            fillHighlighted(highlighter, pparser, obj);
        }
        return results;
     }
    
    protected void fillHighlighted(Highlighter highlighter,
            QueryParser pparser,
            T model) {
        try {
            String hl = highlighter
                    .getBestFragments(pparser.getAnalyzer()
                            .tokenStream("name", new StringReader(model.getName())),
                            model.getName(), 3, " ...");

            if (hl != null && hl.trim().length() > 0) {
                model.setName(hl);
            }
        } catch (IOException | InvalidTokenOffsetsException ex) {
            getLogger().error(ex.getLocalizedMessage(), ex);
        }

    }


    
    protected static RussianAnalyzer an;
    
    static {
        try {
            an= new RussianAnalyzer();
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
    }
}
