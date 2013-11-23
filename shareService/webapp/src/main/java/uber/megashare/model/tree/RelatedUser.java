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

package uber.megashare.model.tree;

import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.support.index.IndexType;
import uber.megashare.model.Project;
import uber.megashare.model.User;

/**
 * 
 * @author <a href="mailto:aachernyshev@it.ru">Alex Chernyshev</a>
 */
@NodeEntity
public class RelatedUser extends BaseTreeObject{

    @Indexed(indexType = IndexType.FULLTEXT,indexName="user_name")
    @Override
    public String getName() {
        return super.getName();
    }
    
      public RelatedUser fromUser(User u) {
        
        setName(u.getName());
        setExternalId(u.getId());
        return this;
    }
    
    public User toUser() {
        User out = new User();
        out.setId(getExternalId());
        out.setName(getName());        
        return out;
    }
}
