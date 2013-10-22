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
 * QAppVersion is a Querydsl query type for AppVersion
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QAppVersion extends BeanPath<AppVersion> {

    private static final long serialVersionUID = 806079249;

    public static final QAppVersion appVersion = new QAppVersion("appVersion");

    public final StringPath implBuildNum = createString("implBuildNum");

    public final StringPath implBuildTime = createString("implBuildTime");

    public final StringPath implVer = createString("implVer");

    public final StringPath implVersion = createString("implVersion");

    public final StringPath implVersionFull = createString("implVersionFull");

    public QAppVersion(String variable) {
        super(AppVersion.class, forVariable(variable));
    }

    public QAppVersion(Path<? extends AppVersion> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QAppVersion(PathMetadata<?> metadata) {
        super(AppVersion.class, metadata);
    }

}

