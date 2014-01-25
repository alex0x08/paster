
package uber.megashare.plugin.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uber.megashare.base.LoggedClass;
import uber.megashare.base.plugins.RunOnAppStart;
import uber.megashare.plugin.dao.YoDao;
import uber.megashare.plugin.model.YoObject;

/**
 * 
 * @author <a href="mailto:aachernyshev@it.ru">Alex Chernyshev</a>
 */
@Component
public class OnStartHandler extends LoggedClass implements RunOnAppStart{

    @Autowired
    private YoDao dao;
    
    public void onStart() {
  
        YoObject o = new YoObject();
        
        o.setName("yo!");
        
       o= dao.saveObject(o);
        
       getLogger().debug("saved yo: "+o);
        
    
    }

}
