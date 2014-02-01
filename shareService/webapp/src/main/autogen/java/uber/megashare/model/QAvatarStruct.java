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
 * QAvatarStruct is a Querydsl query type for AvatarStruct
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAvatarStruct extends EntityPathBase<AvatarStruct> {

    private static final long serialVersionUID = 332766120;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QAvatarStruct avatarStruct = new QAvatarStruct("avatarStruct");

    public final QStruct _super = new QStruct(this);

    public final QAvatar avatar;

    //inherited
    public final BooleanPath disabled = _super.disabled;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.util.Date> lastModified = _super.lastModified;

    //inherited
    public final StringPath name = _super.name;

    public QAvatarStruct(String variable) {
        this(AvatarStruct.class, forVariable(variable), INITS);
    }

    public QAvatarStruct(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAvatarStruct(PathMetadata<?> metadata, PathInits inits) {
        this(AvatarStruct.class, metadata, inits);
    }

    public QAvatarStruct(Class<? extends AvatarStruct> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.avatar = inits.isInitialized("avatar") ? new QAvatar(forProperty("avatar")) : null;
    }

}

