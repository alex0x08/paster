/**
 * Copyright (C) 2011 aachernyshev <alex@0x08.tk>
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uber.megashare.model.AccessLevel;
import uber.megashare.model.Folder;
import uber.megashare.model.SharedFile;


@Repository("folderDao")
//@Transactional(readOnly = true,value= "transactionManager")
public class FolderDaoImpl extends GenericSearchableDaoImpl<Folder>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 691848440760481546L;

	public FolderDaoImpl() {
		super(Folder.class);
	}

	
	
    /**
     * {@inheritDoc}
     */
    public List<Folder> search(String query, Long userId, List<AccessLevel> levels) throws ParseException {

        getLogger().debug("search for {" + query + "} with userId=" + userId + " and levels=" + StringUtils.collectionToCommaDelimitedString(levels));

        if (query == null || query.trim().length() == 0 || query.trim().equals("*")) {
            getLogger().debug("empty query, return all files for user");
            return userId != null ? getFoldersForUser(userId,Folder.ROOT_CODE, AccessLevel.values()) : getFolders(Folder.ROOT_CODE,new AccessLevel[]{AccessLevel.ALL});
        }

        List<Folder> out = new ArrayList<Folder>();
        FullTextEntityManager fsession = getFullTextEntityManager();

        QueryParser pparser = new QueryParser(Version.LUCENE_31, "name", new StandardAnalyzer(Version.LUCENE_31));
        Query lucenceQuery = pparser.parse(query);

        FullTextQuery fquery = fsession.createFullTextQuery(lucenceQuery, persistentClass);

        getLogger().debug("found " + fquery.getResultSize() + " results");


        for (Object o : fquery.getResultList()) {
            Folder file = (Folder) o;

            getLogger().debug("file id=" + file.getId() + ",owner=" + file.getOwner() + ",level=" + file.getAccessLevel());

            if (levels.contains(file.getAccessLevel())) {

                if (userId == null || file.getOwner().getId().equals(userId)) {

                    out.add(file);
                    getLogger().debug("added " + file.getId() + " to output");
                }
            }

        }

        return out;
    }

    /**
     * {@inheritDoc}
     */
    public List<Folder> getFoldersForUser(Long id, String code,AccessLevel[] levels) {
        return getEntityManager()
        		.createQuery("select distinct f from Folder f where f.code=:code and f.accessLevel in (:levels) and f.owner.id = :id order by f.lastModified desc",persistentClass)
        		.setParameter("id", id)
        		.setParameter("code", code)
        		.setParameter("levels", Arrays.asList(levels))
        		.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public List<Folder> getFolders(String code,AccessLevel[] levels) {
        return getEntityManager()
        		.createQuery("select distinct f from Folder f where f.code=:code and f.accessLevel in (:levels) order by f.lastModified desc",persistentClass)
        		.setParameter("levels", Arrays.asList(levels))
        		.setParameter("code", code)
        		.getResultList();
    }


}
