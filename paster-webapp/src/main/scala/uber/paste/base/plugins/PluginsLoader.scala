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

package uber.paste.base.plugins

import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import uber.paste.base.Loggered

//@Component
class PluginsLoader extends InitializingBean with Loggered  {

  //@Autowired
 // @Qualifier("ui-definitions")
  private var uiDefs:Resource  = null
  

  //@Autowired
  //@Qualifier("plugins-ui-definitions")
  private var pluginsUiDefs:Array[Resource]  = null
  
  
  def getUiDefs() = uiDefs
  def setUiDefs(defs:Resource) {this.uiDefs = defs}
  
  def getPluginsUiDefs() = pluginsUiDefs
  def setPluginsUiDefs(defs:Array[Resource]) {this.pluginsUiDefs = defs}
  
  @throws(classOf[Exception])
  override def afterPropertiesSet() {
    PluginUI.load(uiDefs.getInputStream)  
    
    if (pluginsUiDefs!=null && pluginsUiDefs.length>0) {
        logger.info(String.format("Found %s plugin definitions",pluginsUiDefs.length+""))
         for (d<-pluginsUiDefs) {
           logger.info("loading "+d.getURL)
           PluginUI.append(d.getURL)
         }
      }
      
      if (logger.isDebugEnabled) {
        logger.debug(PluginUI.getXml)
      }
  }
  
  
}
