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
package uber.megashare.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import uber.megashare.dao.ProjectDao;
import uber.megashare.model.Project;
import uber.megashare.model.ProjectQuery;

/**
 *
 * @author aachernyshev
 */
@Service("projectManager")
@Secured(value="ROLE_ADMIN")
public class ProjectManagerImpl extends GenericSearchableManagerImpl<Project,ProjectQuery> implements ProjectManager{
    
    private ProjectDao projectDao;
    
    @Autowired
    public ProjectManagerImpl(ProjectDao projectRepo) {
        super(projectRepo);
        this.projectDao = projectRepo;        
    }
 
    @Secured(value={"ROLE_ADMIN","ROLE_USER"})
    @Override
     public List<Project> getAll() {
        return dao.getAll();
    }
}
