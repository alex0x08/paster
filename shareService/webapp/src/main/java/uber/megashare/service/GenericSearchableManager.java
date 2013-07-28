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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uber.megashare.service;

import java.util.List;
import org.apache.lucene.queryParser.ParseException;
import org.springframework.transaction.annotation.Transactional;
import uber.megashare.model.SearchQuery;
import uber.megashare.model.Struct;

/**
 *
 * @author alex
 */
@Transactional(readOnly = true)
public interface GenericSearchableManager<T extends Struct,SQ extends SearchQuery> extends GenericVersioningManager<T> {

    
    public void indexAll();

    public List<T> search(SQ query) throws ParseException;

  
}
