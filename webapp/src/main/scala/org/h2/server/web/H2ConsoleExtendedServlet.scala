/*
 * Copyright 2014 Ubersoft, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.h2.server.web

import java.sql.SQLException
import javax.sql.DataSource
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils
import uber.paste.base.Loggered

class H2ConsoleExtendedServlet extends WebServlet with Loggered{

   override def init() {
         
        
       val ctx:ApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext())
       val ds = ctx.getBean("dataSource").asInstanceOf[DataSource]
       
         try {
           
        //     val  config = getServletConfig()
            val server = new ExtendedWebServer()
                server.setAllowChunked(false)
                server.init()
         
           val  f =getClass().getSuperclass().getDeclaredField("server")
                    f.setAccessible(true)       
                    f.set(this, server)
               
                val conn = ds.getConnection()
                
               val session = server.createNewSession("local")
                   session.setShutdownServerOnDisconnect()
                   session.setConnection(ds.getConnection())
                   session.put("url", conn.getMetaData().getURL())
        
               val ss = session.get("sessionId").asInstanceOf[String]
                
              logger.debug("result="+ss);
                
             getServletContext().setAttribute("h2console-session-id",ss);   
            
       } catch {
           case e @ (_ : NoSuchFieldException 
                     | _ : IllegalAccessException
                     | _ : SQLException
                     | _ : SecurityException                    
                     | _ : IllegalArgumentException) => {
    
               logger.error(e.getLocalizedMessage,e)

                }
          }  
      
       
       
     }
  
}
