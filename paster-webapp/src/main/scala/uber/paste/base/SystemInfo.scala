/*
 * Copyright 2013 Ubersoft, LLC.
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

package uber.paste.base

import java.util.Calendar
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uber.paste.model.AppVersion
import uber.paste.model.Project


@Component
class SystemInfo() {
  
    private var runtimeVersion:AppVersion = _
    
    private var dateStart:java.util.Date = null
    
    private var dateInstall:java.util.Date = null
  
    private var project:Project =null
  
   @Autowired
  def this(@Value("${build.version}") buildVersion:String,
                 @Value("${build.time}") buildTime:String) {
          this()                 
          runtimeVersion = new AppVersion().fillFromParams(buildVersion, buildTime)
   }
  
    def getRuntimeVersion() = runtimeVersion


    def getDateStart() =  dateStart
    def setDateStart(date:java.util.Date) {this.dateStart = date}
    
    def getDateInstall() =  dateInstall
    def setDateInstall(date:java.util.Date) {this.dateInstall = date}
    
  
    def getProject() = project
    def setProject(p:Project) {project = p}
  
}
