/**
 * Copyright (C) 2011 alex <alex@0x08.tk>
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

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import uber.megashare.base.LoggedClass;
import uber.megashare.model.Role;
import uber.megashare.model.User;

/**
 *
 * @author alex
 */
public abstract class AbstractJob extends QuartzJobBean {
   
    protected final Logger log = LoggedClass.newInstance(getClass()).getLogger();

    protected ApplicationContext applicationContext;
 
    public void setApplicationContext(ApplicationContext appContext) {
        applicationContext = appContext;
       
            User startup = new User();
            startup.setName("Scheduler User");
            startup.setLogin("job");
            startup.setPassword("jobstartup");
            startup.getRoles().add(Role.ROLE_ADMIN);
         // log user in automatically
      
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    "job", "job", startup.getAuthorities());
            auth.setDetails(startup);
            SecurityContextHolder.getContext().setAuthentication(auth);
        
        init();
    }

   protected abstract void init();
    
}
