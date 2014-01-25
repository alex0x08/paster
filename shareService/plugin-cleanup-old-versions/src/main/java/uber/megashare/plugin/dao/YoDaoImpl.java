
package uber.megashare.plugin.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uber.megashare.dao.StructDaoImpl;
import uber.megashare.plugin.model.YoObject;

/**
 * 
 * @author <a href="mailto:aachernyshev@it.ru">Alex Chernyshev</a>
 */
@Repository("yoDao")
@Transactional(readOnly = true, rollbackFor = Exception.class,value= "transactionManager")
public class YoDaoImpl extends StructDaoImpl<YoObject> implements YoDao{

    
    public YoDaoImpl() {
        super(YoObject.class);
    }
}
