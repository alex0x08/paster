package uber.megashare.plugin.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QYoObject is a Querydsl query type for YoObject
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QYoObject extends EntityPathBase<YoObject> {

    private static final long serialVersionUID = -2060069664;

    public static final QYoObject yoObject = new QYoObject("yoObject");

    public final uber.megashare.model.QStruct _super = new uber.megashare.model.QStruct(this);

    //inherited
    public final DateTimePath<java.util.Date> lastModified = _super.lastModified;

    //inherited
    public final StringPath name = _super.name;

    public QYoObject(String variable) {
        super(YoObject.class, forVariable(variable));
    }

    public QYoObject(Path<? extends YoObject> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QYoObject(PathMetadata<?> metadata) {
        super(YoObject.class, metadata);
    }

}

