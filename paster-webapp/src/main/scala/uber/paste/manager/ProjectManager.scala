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

package uber.paste.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import uber.paste.base.SystemInfo
import uber.paste.dao.ProjectDao
import uber.paste.model.Project

trait ProjectManager extends GenericSearchManager[Project]{

 def getCurrentProject():Project

}

@Service("projectManager")
class ProjectManagerImpl extends GenericSearchManagerImpl[Project] with ProjectManager {

  @Autowired
  val projectDao:ProjectDao = null

  def getCurrentProject() = SystemInfo.instance.getProject
  
   protected override def getDao:ProjectDao = {
    return projectDao
  }
  
  @Secured(Array("ROLE_ADMIN"))
  override def save(obj:Project):Project = {
    val pr =projectDao.save(obj)
    SystemInfo.instance.setProject(pr)
    return pr
  }
  
  @Secured(Array("ROLE_ADMIN"))
  override def remove(id:Long) = {
    projectDao.remove(id)
  }
  
}