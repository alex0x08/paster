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
package uber.megashare.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = -306930427;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QComment comment = new QComment("comment");

    public final QBaseDBObject _super = new QBaseDBObject(this);

    public final QUser author;

    public final DateTimePath<java.util.Date> created = createDateTime("created", java.util.Date.class);

    //inherited
    public final BooleanPath disabled = _super.disabled;

    public final QAttachedFile file;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath message = createString("message");

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QComment(PathMetadata<?> metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QUser(forProperty("author")) : null;
        this.file = inits.isInitialized("file") ? new QAttachedFile(forProperty("file")) : null;
    }

}

