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
package uber.megashare.service;

import java.util.List;
import uber.megashare.dao.GenericVersioningDao;
import uber.megashare.model.Struct;



/**
 *
 * @author alex
 */
public class GenericVersioningManagerImpl<T extends Struct> extends StructManagerImpl<T> {

    protected GenericVersioningDao<T> vdao;

    public GenericVersioningManagerImpl(GenericVersioningDao<T> genericDao) {
        super(genericDao);
        this.vdao = genericDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<Number> getRevisions(Long id) {
        return vdao.getRevisions(id);
    }

    /**
     * {@inheritDoc}
     */
    public void revertToRevision(Long id, Number rev) {
        vdao.revertToRevision(id, rev);
    }

    /**
     * {@inheritDoc}
     */
    public T getRevision(Long id, Number rev) {
        return vdao.getRevision(id, rev);
    }
    
     public Number getCurrentRevisionNumber(Long id) {
         return vdao.getCurrentRevisionNumber(id);
     }
}
