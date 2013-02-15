/*
 * Copyright 2011 Ubersoft, LLC.
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

package uber.paste.tiles2


import java.util.Map;
import java.util.Map.Entry;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;
import uber.paste.base.Loggered

class ELViewPreparer extends Loggered with ViewPreparer {

  def execute(trc:TilesRequestContext, ac:AttributeContext) {

    logger.debug("_call preparer")

    val sc:Map[String, Object] = trc.getRequestScope()
	        
    if (!sc.containsKey("title")) {
                    
      sc.put("title", "FIXME: Blank page");
	
    }
             
      
  }
  
}
