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
package uber.megashare.model.tree;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.support.index.IndexType;

/**
 *
 * @author <a href="mailto:aachernyshev@it.ru">Alex Chernyshev</a>
 */
public abstract class BaseTreeObject {

    @GraphId
    private Long id;
    
    private String name;
    
    private Long externalId;

    public Long getId() {
        return id;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    @Indexed(indexType = IndexType.FULLTEXT,indexName="obj_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
