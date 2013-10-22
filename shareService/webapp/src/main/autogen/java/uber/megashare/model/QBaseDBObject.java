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
 * QBaseDBObject is a Querydsl query type for BaseDBObject
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QBaseDBObject extends EntityPathBase<BaseDBObject> {

    private static final long serialVersionUID = -1435897784;

    public static final QBaseDBObject baseDBObject = new QBaseDBObject("baseDBObject");

    public final QBaseObject _super = new QBaseObject(this);

    public final BooleanPath disabled = createBoolean("disabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBaseDBObject(String variable) {
        super(BaseDBObject.class, forVariable(variable));
    }

    public QBaseDBObject(Path<? extends BaseDBObject> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QBaseDBObject(PathMetadata<?> metadata) {
        super(BaseDBObject.class, metadata);
    }

}

