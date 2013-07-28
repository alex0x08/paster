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
 * QAttachedFile is a Querydsl query type for AttachedFile
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAttachedFile extends EntityPathBase<AttachedFile> {

    private static final long serialVersionUID = -420413862;

    public static final QAttachedFile attachedFile = new QAttachedFile("attachedFile");

    public final QStruct _super = new QStruct(this);

    public final ArrayPath<Byte> data = createArray("data", Byte[].class);

    //inherited
    public final BooleanPath disabled = _super.disabled;

    public final ArrayPath<Byte> icon = createArray("icon", Byte[].class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.util.Date> lastModified = _super.lastModified;

    public final StringPath mime = createString("mime");

    //inherited
    public final StringPath name = _super.name;

    public QAttachedFile(String variable) {
        super(AttachedFile.class, forVariable(variable));
    }

    public QAttachedFile(Path<? extends AttachedFile> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QAttachedFile(PathMetadata<?> metadata) {
        super(AttachedFile.class, metadata);
    }

}

