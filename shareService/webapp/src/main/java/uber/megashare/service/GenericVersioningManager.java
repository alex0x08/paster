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
package uber.megashare.service;

import java.util.List;
import uber.megashare.model.Struct;


/**
 * Generic manager for objects that supports versioning
 * @author alex
 */
public interface GenericVersioningManager<T extends Struct> extends StructManager<T> {

    
    
    /**
     * get specified revision of versioned object
     * @param id object id
     * @param rev revision number
     * @return object instance
     */
    T getRevision(Long id, Number rev);

    /**
     * get list of available revisions for selected object
     * @param id id of selected object
     * @return list of revisions
     */
    List<Number> getRevisions(Long id);

   /**
     * revert object state to specified revision
     * @param id object's id
     * @param rev revision number
     */
    void revertToRevision(Long id, Number rev);
    
    Number getCurrentRevisionNumber(Long id);
}
