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
package uber.megashare.service.job;

import java.util.Calendar;
import org.quartz.JobExecutionContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import uber.megashare.model.Role;
import uber.megashare.model.SharedFile;
import uber.megashare.model.User;
import uber.megashare.service.SharedFileManager;


/**
 * Job-task дергающий рассылку уведомлений
 * @author alex
 */
public class CheckRemoveJob extends AbstractJob {

     private SharedFileManager fileManager;
    
    @Override
    protected void executeInternal(JobExecutionContext executionContext) {
        
        
        
        for (SharedFile f:fileManager.getFilesToRemoval()) {
            log.debug("removing expired file "+f.getName()+" remove after "+f.getRemoveAfter()+" today "+Calendar.getInstance().getTime());
            fileManager.remove(f.getId());
        }
        
    }
   

    @Override
    protected void init() {
       fileManager = (SharedFileManager) applicationContext.getBean("shareManager");
    }
}