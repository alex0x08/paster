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

package uber.paste.dao

import javax.persistence.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import uber.paste.model.Project

trait ProjectDao extends SearchableDao[Project]{

  def getLast():Project
}

@Repository("projectDao")
@Transactional(readOnly = true)
class ProjectDaoImpl extends SearchableDaoImpl[Project](classOf[Project]) with ProjectDao{


 def getLast():Project = {

    val cr = new CriteriaSet

    val query:Query = em.createQuery(cr.cr.orderBy(cr.cb.desc(cr.r.get("lastModified"))))
      .setMaxResults(1)
      return query.getSingleResult.asInstanceOf[Project]
  }
}