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
 * QAvatar is a Querydsl query type for Avatar
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAvatar extends EntityPathBase<Avatar> {

    private static final long serialVersionUID = 216049139;

    public static final QAvatar avatar = new QAvatar("avatar");

    public final ArrayPath<Byte> icon = createArray("icon", Byte[].class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ArrayPath<Byte> picture = createArray("picture", Byte[].class);

    public QAvatar(String variable) {
        super(Avatar.class, forVariable(variable));
    }

    public QAvatar(Path<? extends Avatar> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QAvatar(PathMetadata<?> metadata) {
        super(Avatar.class, metadata);
    }

}

