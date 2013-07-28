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

import java.util.List;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uber.megashare.model.Struct;



/**
 * Implementation of versioning functions Based on Envers framework
 *
 * @author alex
 */
//@Transactional(readOnly = true,value= "transactionManager")
public abstract class GenericVersioningDaoImpl<T extends Struct> extends  StructDaoImpl<T> {

    public GenericVersioningDaoImpl(Class<T> clazz) {
        super(clazz);
    }
    
    @Transactional(readOnly = true, rollbackFor = Exception.class,value= "transactionManager")
    public Number getCurrentRevisionNumber(Long id) {
       List<Number> revs = getRevisions(id);

        return revs!=null && !revs.isEmpty() ? revs.get(revs.size()-1) : 0;
                /*(Number)getReader().createQuery()
                    .forRevisionsOfEntity(persistentClass, false, false)        
                    .addProjection(AuditEntity.revisionNumber().max()).getSingleResult()*/
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class,value= "transactionManager")
    public List<Number> getRevisions(Long id) {
        return getReader().getRevisions(persistentClass, id);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class,value= "transactionManager")
    public T getRevision(Long id, Number rev) {
        /**
         * fix stupid envers bug
         */
        if (rev instanceof Long) {
            rev = new Integer(rev.intValue());
        }

        T out = (T) getReader().find(persistentClass, id, rev);

        getLogger().debug("getRevision id=" + id + ",rev=" + rev + ",found=" + out);

        return getFull(out);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,value= "transactionManager",rollbackFor = Exception.class)
    public void revertToRevision(Long id, Number rev) {
        saveObject(getRevision(id, rev));

    }

    /**
     * returns AuditReader object, used for all versioning actions
     *
     * @return AuditReader assigned to current hibernate session
     */
    protected AuditReader getReader() {
        return AuditReaderFactory.get(getEntityManager());
    }
}
