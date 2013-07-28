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

import java.util.List;
import uber.megashare.model.Struct;



/**
 * Generic Database Access Level for objects type T
 * with versioning support
 * 
 * @author alex
 */
public interface GenericVersioningDao<T extends Struct> extends StructDao<T>{

    /**
     * get list of revisions for object with specified id
     * @param id object's id
     * @return  list of revisions
     */
    List<Number> getRevisions(Long id);
    /**
     * get specific revision of object
     * @param id object id
     * @param rev selected revision
     * @return 
     */
    T getRevision(Long id, Number rev);
    /**
     * revert object state to specified revision
     * @param id object id
     * @param rev selected revision number
     */
    void revertToRevision(Long id, Number rev);

    Number getCurrentRevisionNumber(Long id);
}
