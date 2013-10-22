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
 * QStruct is a Querydsl query type for Struct
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QStruct extends EntityPathBase<Struct> {

    private static final long serialVersionUID = 730034287;

    public static final QStruct struct = new QStruct("struct");

    public final QBaseDBObject _super = new QBaseDBObject(this);

    //inherited
    public final BooleanPath disabled = _super.disabled;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final DateTimePath<java.util.Date> lastModified = createDateTime("lastModified", java.util.Date.class);

    public final StringPath name = createString("name");

    public QStruct(String variable) {
        super(Struct.class, forVariable(variable));
    }

    public QStruct(Path<? extends Struct> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QStruct(PathMetadata<?> metadata) {
        super(Struct.class, metadata);
    }

}

