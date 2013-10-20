/**
 * Copyright (C) 2011 alex <alex@0x08.tk>
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

import com.mysema.query.types.expr.BooleanExpression;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uber.megashare.base.logging.LoggedCall;
import uber.megashare.model.AccessLevel;
import uber.megashare.model.Project;
import uber.megashare.model.QSharedFile;
import uber.megashare.model.SharedFile;

/**
 *
 * @author alex
 */
@Repository("shareDao")
@Transactional(readOnly = true,value= "transactionManager", rollbackFor = Exception.class)
public class SharedFileDaoImpl extends GenericSearchableDaoImpl<SharedFile> implements SharedFileDao {

    /**
     *
     */
    private static final long serialVersionUID = -4874510100464951022L;
    
    protected static final String SHARE_START_FIELDS[] = {"name", "nameContents", "comments_message"};

    public SharedFileDaoImpl() {
        super(SharedFile.class);
    }

    /**
     * {@inheritDoc}
     */
    public SharedFile getFileFromUUID(String uuid) {
        return findOne(QSharedFile.sharedFile.uuid.eq(uuid));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED,value= "transactionManager",rollbackFor = Exception.class)
    @Override
    public List<SharedFile> search(final String query,
            final Long userId,final Long projectId,
            final List<AccessLevel> levels) throws ParseException {

        return new LoggedCall<List<SharedFile>>() {
            public List<SharedFile> callImpl(ToStringBuilder log) throws Exception {

                log.append("search for {" + query + "} with userId=" + userId + " and levels=" + StringUtils.collectionToCommaDelimitedString(levels));

                if (org.apache.commons.lang.StringUtils.isBlank(query) || query.trim().equals("*")) {
                    log.append("empty query, return all files for user");
                    return userId != null
                            ? getFilesForUser(userId,projectId, AccessLevel.values())
                            : getFiles(projectId,new AccessLevel[]{AccessLevel.ALL});
                }

                String qquery = query;
                /**
                 * added for stupid users
                 */
                /*if (!query.contains(":") && !query.contains("*")) {
                 qquery += "*";
                 }*/
                List<SharedFile> out = new ArrayList<>();
                FullTextEntityManager fsession = getFullTextEntityManager();

                   QueryParser pparser = new MultiFieldQueryParser(Version.LUCENE_35, SHARE_START_FIELDS, an);
     
                Query lucenceQuery = pparser.parse(qquery);

                FullTextQuery fquery = fsession.createFullTextQuery(lucenceQuery, persistentClass);

                log.append("found " + fquery.getResultSize() + " results ,an=" + an.getClass().getName());


                for (Object o : fquery.getResultList()) {
                    SharedFile file = (SharedFile) o;

                    log.append("file id=" + file.getId() + ",owner=" + file.getOwner() + ",level=" + file.getAccessLevel());

                    if (!levels.contains(file.getAccessLevel())) {
                        continue;
                    }

                    if (userId == null || file.getOwner().getId().equals(userId) || file.getAccessLevel() == AccessLevel.ALL) {

                        out.add(file);
                        log.append("added " + file.getId() + " to output");
                    }

                }

                return out;
            }
        }.call();

    }

   
    /**
     * {@inheritDoc}
     */
    @Override
    public List<SharedFile> getFilesForUser(Long userId, Long projectId, AccessLevel[] levels) {
       // System.out.println("_getFiles for user "+userId+" proj="+projectId);
        List<BooleanExpression> out = new ArrayList<>();
        
        if (projectId!=null) {        
            out.add(QSharedFile.sharedFile.relatedProjects.contains(new Project(projectId)));
        
            if (userId!=null) {            
                 out.add(BooleanExpression.anyOf(
                         QSharedFile.sharedFile.owner.id.eq(userId),
                         QSharedFile.sharedFile.accessLevel.in(AccessLevel.PROJECT)
                ));
            }
        
        } else {
                out.add(userId!=null ? QSharedFile.sharedFile.owner.id.eq(userId) :
                        QSharedFile.sharedFile.accessLevel.in(AccessLevel.ALL));                
        }
        return findAll(BooleanExpression.allOf(out.toArray(new BooleanExpression[out.size()])), 
                QSharedFile.sharedFile.lastModified.desc());
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
     public List<SharedFile> getFilesToRemoval() {
        return findAll(
                QSharedFile.sharedFile.removeAfter.isNotNull()
                .and(QSharedFile.sharedFile.removeAfter.lt(Calendar.getInstance().getTime())));

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<SharedFile> getFiles(Long projectId,AccessLevel[] levels) {
        
          List<BooleanExpression> out = new ArrayList<>();
        
          out.add(projectId!=null ? 
                QSharedFile.sharedFile.relatedProjects.contains(new Project(projectId)):
                QSharedFile.sharedFile.accessLevel.in(AccessLevel.ALL));
        
        out.add(QSharedFile.sharedFile.accessLevel.in(levels));
        
        return findAll(BooleanExpression.allOf(out.toArray(new BooleanExpression[out.size()])), 
                QSharedFile.sharedFile.lastModified.desc());
    }

    @Override
    public List<SharedFile> getObjectsForIntegration(String intergationCode) {
        return findAll(QSharedFile.sharedFile.accessLevel.eq(AccessLevel.ALL)
                .and(QSharedFile.sharedFile.integrationCode.eq(intergationCode)),
                QSharedFile.sharedFile.lastModified.desc());

    }
}
