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

import java.io.IOException
import org.h2.util.IOUtils
import org.h2.util.Utils

class ExtendedWebServer extends WebServer{

  @throws(classOf[IOException])
  override def getFile(file:String):Array[Byte] =   {
       
      trace("getFile <" + file + ">")
       
          val in =getClass().getResourceAsStream("/org/h2/server/web/res/" + file)
          
        if (in!=null) {
            trace("_found overrided resource "+file)
            return IOUtils.readBytesAndClose(in,-1)
          }
      
        
       val data = Utils.getResource("/org/h2/server/web/res/" + file)
        if (data == null) {
            trace(" null")
        } else {
            trace(" size=" + data.length)
        }
        return data
    }
}
