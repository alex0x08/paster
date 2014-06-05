
package uber.megashare.plugin.cleanup.job;

import java.util.Calendar;
import org.quartz.JobExecutionContext;
import uber.megashare.model.SharedFile;
import uber.megashare.service.SharedFileManager;
import uber.megashare.service.job.AbstractJob;

/**
 * 
 * @author <a href="mailto:aachernyshev@it.ru">Alex Chernyshev</a>
 */
public class CleanupOldVersionsJob extends AbstractJob {

     private SharedFileManager fileManager;
    
    @Override
    protected void executeInternal(JobExecutionContext executionContext) {        
        
        log.debug("_YO!");
        
        
       /* for (SharedFile f:fileManager.getFilesToRemoval()) {
            log.debug("removing expired file "+f.getName()+" remove after "+f.getRemoveAfter()+" today "+Calendar.getInstance().getTime());
            fileManager.remove(f.getId());
        }*/
        
    }
   

    @Override
    protected void init() {
       fileManager = (SharedFileManager) applicationContext.getBean("shareManager");
    }
}
