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
 * QBaseObject is a Querydsl query type for BaseObject
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QBaseObject extends BeanPath<BaseObject> {

    private static final long serialVersionUID = -1627161334;

    public static final QBaseObject baseObject = new QBaseObject("baseObject");

    public QBaseObject(String variable) {
        super(BaseObject.class, forVariable(variable));
    }

    public QBaseObject(Path<? extends BaseObject> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QBaseObject(PathMetadata<?> metadata) {
        super(BaseObject.class, metadata);
    }

}

