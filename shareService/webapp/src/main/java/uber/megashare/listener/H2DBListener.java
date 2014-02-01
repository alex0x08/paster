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
package uber.megashare.listener;

import java.io.File;
import java.sql.SQLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.h2.tools.Server;
import uber.megashare.base.LoggedClass;

/**
 * 
 * @author <a href="mailto:aachernyshev@it.ru">Alex Chernyshev</a>
 */
public class H2DBListener extends LoggedClass implements ServletContextListener{

    private Server h2Server;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        
        String appHome = System.getProperty("share.app.home");
        
        File h2Home = new File(appHome,"h2");
        
        h2Home.mkdirs();
        
        System.setProperty("share.app.h2.home",h2Home.getAbsolutePath());
        
        
        try {
            h2Server=Server.createTcpServer("-tcp",
                    "-tcpDaemon",
                    "-tcpShutdownForce",
                    "-tcpPort","6666",
                    
                    "-baseDir",h2Home.getAbsolutePath()).start();
        } catch (SQLException ex) {
           getLogger().error(ex.getLocalizedMessage(),ex);
        }
         
     }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
        h2Server.stop();
    }

}
