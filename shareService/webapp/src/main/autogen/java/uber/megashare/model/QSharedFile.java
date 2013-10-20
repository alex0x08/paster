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
 * QSharedFile is a Querydsl query type for SharedFile
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSharedFile extends EntityPathBase<SharedFile> {

    private static final long serialVersionUID = 1832215323;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QSharedFile sharedFile = new QSharedFile("sharedFile");

    public final QNode _super;

    //inherited
    public final EnumPath<AccessLevel> accessLevel;

    //inherited
    public final StringPath code;

    //inherited
    public final ListPath<Comment, QComment> comments;

    //inherited
    public final NumberPath<Integer> commentsCount;

    //inherited
    public final BooleanPath disabled;

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    //inherited
    public final NumberPath<Long> id;

    public final StringPath integrationCode = createString("integrationCode");

    //inherited
    public final DateTimePath<java.util.Date> lastModified;

    public final StringPath mime = createString("mime");

    //inherited
    public final StringPath name;

    public final StringPath nameContents = createString("nameContents");

    // inherited
    public final QUser owner;

    //inherited
    public final StringPath ownerName;

    public final NumberPath<Integer> previewHeight = createNumber("previewHeight", Integer.class);

    public final StringPath previewUrl = createString("previewUrl");

    public final NumberPath<Integer> previewWidth = createNumber("previewWidth", Integer.class);

    public final SetPath<Project, QProject> relatedProjects = this.<Project, QProject>createSet("relatedProjects", Project.class, QProject.class);

    public final DateTimePath<java.util.Date> removeAfter = createDateTime("removeAfter", java.util.Date.class);

    public final EnumPath<FileType> type = createEnum("type", FileType.class);

    public final StringPath url = createString("url");

    public final StringPath uuid = createString("uuid");

    public final SimplePath<uber.megashare.model.xml.XMLObject> xml = createSimple("xml", uber.megashare.model.xml.XMLObject.class);

    public QSharedFile(String variable) {
        this(SharedFile.class, forVariable(variable), INITS);
    }

    public QSharedFile(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSharedFile(PathMetadata<?> metadata, PathInits inits) {
        this(SharedFile.class, metadata, inits);
    }

    public QSharedFile(Class<? extends SharedFile> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QNode(type, metadata, inits);
        this.accessLevel = _super.accessLevel;
        this.code = _super.code;
        this.comments = _super.comments;
        this.commentsCount = _super.commentsCount;
        this.disabled = _super.disabled;
        this.id = _super.id;
        this.lastModified = _super.lastModified;
        this.name = _super.name;
        this.owner = _super.owner;
        this.ownerName = _super.ownerName;
    }

}

