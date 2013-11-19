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
 * QUser is a Querydsl query type for User
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1536610587;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QUser user = new QUser("user");

    public final QAvatarStruct _super;

    // inherited
    public final QAvatar avatar;

    public final EnumPath<AvatarType> avatarType = createEnum("avatarType", AvatarType.class);

    public final EnumPath<AccessLevel> defaultFileAccessLevel = createEnum("defaultFileAccessLevel", AccessLevel.class);

    //inherited
    public final BooleanPath disabled;

    public final StringPath email = createString("email");

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final DateTimePath<java.util.Date> lastModified;

    public final StringPath login = createString("login");

    //inherited
    public final StringPath name;

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final StringPath prefferedLocaleCode = createString("prefferedLocaleCode");

    public final QProject relatedProject;

    public final SetPath<Role, EnumPath<Role>> roles = this.<Role, EnumPath<Role>>createSet("roles", Role.class, EnumPath.class);

    public final ListPath<SavedSession, QSavedSession> savedSessions = this.<SavedSession, QSavedSession>createList("savedSessions", SavedSession.class, QSavedSession.class);

    public final StringPath skype = createString("skype");

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUser(PathMetadata<?> metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAvatarStruct(type, metadata, inits);
        this.avatar = _super.avatar;
        this.disabled = _super.disabled;
        this.id = _super.id;
        this.lastModified = _super.lastModified;
        this.name = _super.name;
        this.relatedProject = inits.isInitialized("relatedProject") ? new QProject(forProperty("relatedProject"), inits.get("relatedProject")) : null;
    }

}

