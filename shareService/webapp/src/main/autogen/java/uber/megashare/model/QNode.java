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
package uber.megashare.model;


import static com.mysema.query.types.PathMetadataFactory.*;
import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QNode is a Querydsl query type for Node
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QNode extends EntityPathBase<Node> {

    private static final long serialVersionUID = -1536823012;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QNode node = new QNode("node");

    public final QCommentedStruct _super = new QCommentedStruct(this);

    public final EnumPath<AccessLevel> accessLevel = createEnum("accessLevel", AccessLevel.class);

    public final StringPath code = createString("code");

    //inherited
    public final ListPath<Comment, QComment> comments = _super.comments;

    //inherited
    public final BooleanPath disabled = _super.disabled;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.util.Date> lastModified = _super.lastModified;

    //inherited
    public final StringPath name = _super.name;

    public final QUser owner;

    public final StringPath ownerName = createString("ownerName");

    public QNode(String variable) {
        this(Node.class, forVariable(variable), INITS);
    }

    public QNode(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QNode(PathMetadata<?> metadata, PathInits inits) {
        this(Node.class, metadata, inits);
    }

    public QNode(Class<? extends Node> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.owner = inits.isInitialized("owner") ? new QUser(forProperty("owner")) : null;
    }

}

