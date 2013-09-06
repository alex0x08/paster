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

import java.util.Collections;
import java.util.List;
import javax.persistence.Query;
import uber.megashare.model.Struct;

/**
 *
 * @author alex
 */
//@Transactional(readOnly = true,value= "transactionManager")
public abstract class StructDaoImpl<T extends Struct> extends GenericDSLDaoImpl<T, Long> implements StructDao<T> {

    /**
     *
     */
    private static final long serialVersionUID = -6377133748447697686L;

    protected StructDaoImpl(Class<T> clazz) {
        super(clazz);
    }
    
    @Override
    public List<T> getObjectsForIntegration(String integrationCode) {

        CriteriaSet cr = new CriteriaSet();

        Query query = getEntityManager().createQuery(cr.cr.where(cr.cb.equal(cr.r.get("integrationCode"), integrationCode))
                .orderBy(cr.cb.desc(cr.r.get("lastModified"))));
        return query.getResultList().isEmpty() ? Collections.EMPTY_LIST : query.getResultList();

    }

}
