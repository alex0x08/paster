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
 * QSystemProperties is a Querydsl query type for SystemProperties
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSystemProperties extends EntityPathBase<SystemProperties> {

    private static final long serialVersionUID = 1921423580;

    public static final QSystemProperties systemProperties = new QSystemProperties("systemProperties");

    public final QBaseDBObject _super = new QBaseDBObject(this);

    public final StringPath baseUrl = createString("baseUrl");

    //inherited
    public final BooleanPath disabled = _super.disabled;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final DateTimePath<java.util.Date> installDate = createDateTime("installDate", java.util.Date.class);

    public final StringPath tmpDir = createString("tmpDir");

    public final StringPath uploadDir = createString("uploadDir");

    public final StringPath uploadUrl = createString("uploadUrl");

    public QSystemProperties(String variable) {
        super(SystemProperties.class, forVariable(variable));
    }

    public QSystemProperties(Path<? extends SystemProperties> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QSystemProperties(PathMetadata<?> metadata) {
        super(SystemProperties.class, metadata);
    }

}

