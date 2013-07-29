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

    public static final QUser user = new QUser("user");

    public final QStruct _super = new QStruct(this);

    public final EnumPath<AccessLevel> defaultFileAccessLevel = createEnum("defaultFileAccessLevel", AccessLevel.class);

    //inherited
    public final BooleanPath disabled = _super.disabled;

    public final StringPath email = createString("email");

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.util.Date> lastModified = _super.lastModified;

    public final StringPath login = createString("login");

    //inherited
    public final StringPath name = _super.name;

    public final StringPath password = createString("password");

    public final ListPath<Role, EnumPath<Role>> roles = this.<Role, EnumPath<Role>>createList("roles", Role.class, EnumPath.class);

    public final ListPath<SavedSession, QSavedSession> savedSessions = this.<SavedSession, QSavedSession>createList("savedSessions", SavedSession.class, QSavedSession.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QUser(PathMetadata<?> metadata) {
        super(User.class, metadata);
    }

}

