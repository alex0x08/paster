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
 * QSavedSession is a Querydsl query type for SavedSession
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSavedSession extends EntityPathBase<SavedSession> {

    private static final long serialVersionUID = -1342169303;

    public static final QSavedSession savedSession = new QSavedSession("savedSession");

    public final QKey _super = new QKey(this);

    //inherited
    public final StringPath code = _super.code;

    //inherited
    public final BooleanPath disabled = _super.disabled;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.util.Date> lastModified = _super.lastModified;

    //inherited
    public final StringPath name = _super.name;

    public QSavedSession(String variable) {
        super(SavedSession.class, forVariable(variable));
    }

    public QSavedSession(Path<? extends SavedSession> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QSavedSession(PathMetadata<?> metadata) {
        super(SavedSession.class, metadata);
    }

}
