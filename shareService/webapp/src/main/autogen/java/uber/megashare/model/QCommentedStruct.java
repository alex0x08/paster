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
package uber.megashare.model;


import static com.mysema.query.types.PathMetadataFactory.*;
import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QCommentedStruct is a Querydsl query type for CommentedStruct
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QCommentedStruct extends EntityPathBase<CommentedStruct> {

    private static final long serialVersionUID = 287185241;

    public static final QCommentedStruct commentedStruct = new QCommentedStruct("commentedStruct");

    public final QStruct _super = new QStruct(this);

    public final ListPath<Comment, QComment> comments = this.<Comment, QComment>createList("comments", Comment.class, QComment.class);

    public final NumberPath<Integer> commentsCount = createNumber("commentsCount", Integer.class);

    //inherited
    public final BooleanPath disabled = _super.disabled;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.util.Date> lastModified = _super.lastModified;

    //inherited
    public final StringPath name = _super.name;

    public QCommentedStruct(String variable) {
        super(CommentedStruct.class, forVariable(variable));
    }

    public QCommentedStruct(Path<? extends CommentedStruct> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QCommentedStruct(PathMetadata<?> metadata) {
        super(CommentedStruct.class, metadata);
    }

}

