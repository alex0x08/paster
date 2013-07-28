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
 * QFolder is a Querydsl query type for Folder
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QFolder extends EntityPathBase<Folder> {

    private static final long serialVersionUID = 353042696;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QFolder folder = new QFolder("folder");

    public final QNode _super;

    //inherited
    public final EnumPath<AccessLevel> accessLevel;

    //inherited
    public final StringPath code;

    //inherited
    public final ListPath<Comment, QComment> comments;

    //inherited
    public final BooleanPath disabled;

    public final ListPath<SharedFile, QSharedFile> files = this.<SharedFile, QSharedFile>createList("files", SharedFile.class, QSharedFile.class);

    public final ListPath<Folder, QFolder> folders = this.<Folder, QFolder>createList("folders", Folder.class, QFolder.class);

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final DateTimePath<java.util.Date> lastModified;

    //inherited
    public final StringPath name;

    // inherited
    public final QUser owner;

    //inherited
    public final StringPath ownerName;

    public QFolder(String variable) {
        this(Folder.class, forVariable(variable), INITS);
    }

    public QFolder(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QFolder(PathMetadata<?> metadata, PathInits inits) {
        this(Folder.class, metadata, inits);
    }

    public QFolder(Class<? extends Folder> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QNode(type, metadata, inits);
        this.accessLevel = _super.accessLevel;
        this.code = _super.code;
        this.comments = _super.comments;
        this.disabled = _super.disabled;
        this.id = _super.id;
        this.lastModified = _super.lastModified;
        this.name = _super.name;
        this.owner = _super.owner;
        this.ownerName = _super.ownerName;
    }

}

