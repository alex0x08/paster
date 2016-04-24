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
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils
import uber.paste.base.Loggered

/**
 * This is extended version of JDBC Database Console Servlet, 
 * packed with H2 Driver
 * 
 * It was created to automatically bind connection from local datasource 
 * to user's session and open console automatically.
 * 
 */
class H2ConsoleExtendedServlet extends WebServlet with Loggered{

  private var dataSource:DataSource = null
  
  private val server = new WebServer
  
   override def init() {         
        
    logger.debug("h2 servlet init..")
    
       val ctx:ApplicationContext = WebApplicationContextUtils
                    .getWebApplicationContext(getServletContext())
       
      dataSource = ctx.getBean("dataSource").asInstanceOf[DataSource]
       
         try {
                server.setAllowChunked(false)
                server.init()
         
           val  f =getClass().getSuperclass().getDeclaredField("server")
                    f.setAccessible(true)       
                    f.set(this, server)
               
           //    val session = createAndRegLocalSession()
                
            //   val ss = session.get("sessionId").asInstanceOf[String]
                
            //  logger.debug("h2 sessionId: {}",ss);
                
           //  getServletContext().setAttribute("h2console-session-id",ss);   
            
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
     
  @throws(classOf[java.io.IOException])
  override def doGet(req:HttpServletRequest,res:HttpServletResponse) {
  
    var session =  server.getSession(
      getServletContext().getAttribute("h2console-session-id").asInstanceOf[String])
  
    if (session==null) {
      session = createAndRegLocalSession()
    }
             
    
    /**
     * refresh connection if closed
     */
    var con = session.getConnection
    
    if (con==null ||con.isClosed) {
      con =dataSource.getConnection
      
      session.setConnection(con)
      session.put("url",con.getMetaData.getURL)
    }
    
    super.doGet(req,res)
  }
  
  private def createAndRegLocalSession():WebSession = {
    
     val conn = dataSource.getConnection()
                
               val session = server.createNewSession("local")
                   session.setShutdownServerOnDisconnect()
                   session.setConnection(conn)
                   session.put("url", conn.getMetaData().getURL())
        
               val ss = session.get("sessionId").asInstanceOf[String]
                
              logger.debug("h2 sessionId: {}",ss)
              
              getServletContext().setAttribute("h2console-session-id",ss)  
    
              return session
  }
}
