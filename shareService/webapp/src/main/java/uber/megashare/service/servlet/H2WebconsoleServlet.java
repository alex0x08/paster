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

package uber.megashare.service.servlet;

import java.lang.Boolean;
import java.lang.Class;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.h2.server.web.WebServer;

import org.h2.server.web.WebServlet;
import org.slf4j.Logger;
import uber.megashare.base.LoggedClass;

/**
 * 
 * @author <a href="mailto:aachernyshev@it.ru">Alex Chernyshev</a>
 */
public class H2WebconsoleServlet extends WebServlet{

    private Logger log = LoggedClass.newInstance(this.getClass()).getLogger();
    
        @Override
    public void init() {
            try {
                System.out.println("_h2console props="+System.getProperty("share.app.h2.home"));
                
                Field fieldServer = getClass().getSuperclass().getDeclaredField("server");

                fieldServer.setAccessible(true);
                
                
            //WebServer superServer = (WebServer) fieldServer.get(this);
                
                  String[] args = new String[]{"-properties",  System.getProperty("share.app.h2.home")};
        
         WebServer sserver = new WebServer();
        
        // Method am =sserver.getClass().getMethod("setAllowChunked",Boolean.class);
         
        // am.invoke(sserver, false);
         
         //sserver.setAllowChunked(false);
        
         
         
        sserver.init(args);
  
        
        fieldServer.set(this, sserver);
                
          //  getServletConfig().getServletContext().setInitParameter("-properties",  System.getProperty("share.app.h2.home"));
          //  getServletConfig().getServletContext().setInitParameter("-url", "jdbc:h2:tcp://localhost:6666/filesdb;DB_CLOSE_ON_EXIT=TRUE");
           // super.init();
            } catch (    NoSuchFieldException | 
                    SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                log.error(ex.getLocalizedMessage(),ex);
          }
       
    }

    
}
