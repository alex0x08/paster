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

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uber.megashare.model.Comment;

/**
 *
 * @author alex
 */
@Repository("commentDao")
@Transactional(readOnly = true, rollbackFor = Exception.class,value= "transactionManager")
public class CommentDaoImpl extends GenericDaoImpl<Comment, Long> implements CommentDao {

    /**
     *
     */
    private static final long serialVersionUID = 3947946208075599466L;

    public CommentDaoImpl() {
        super(Comment.class);
    }

    
}
