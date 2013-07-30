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
import org.apache.lucene.queryParser.ParseException;
import uber.megashare.model.Struct;

/**
 * Generic DAO related to search
 * @author alex
 */
public interface GenericSearchableDao<T extends Struct> extends GenericVersioningDao<T> {

    /**
     * recreate index for T
     */
     public void indexAll();
     /**
      * search for T with query
      * @param query
      *         specified query
      * @return
      * @throws ParseException 
      */
    public List<T> search(String query) throws ParseException;
}
