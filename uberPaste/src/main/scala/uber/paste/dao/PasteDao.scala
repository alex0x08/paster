/*
 * Copyright 2011 WorldWide Conferencing, LLC.
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

import uber.paste.model.{PasteSource, Paste, User}
import org.springframework.stereotype.Repository
import javax.persistence.Query
import org.springframework.transaction.annotation.Transactional

trait PasteDao extends SearchableDao[Paste]{

  def getByOwner(owner:User) : java.util.List[Paste]

  def getBySourceType(sourceType:PasteSource,desc:Boolean) : java.util.List[Paste]

  def getListIntegrated(code:String):java.util.List[Paste]


  }

@Repository("pasteDao")
@Transactional(readOnly = true)
class PasteDaoImpl extends SearchableDaoImpl[Paste](classOf[Paste]) with PasteDao{

  def getByOwner(owner:User) : java.util.List[Paste] = {

    val cr = new CriteriaSet

    val query:Query = em.createQuery(cr.cr.where(Array(cr.cb.equal(cr.r.get("owner"), owner)):_*)
      .select(cr.r))
      .setMaxResults(BaseDaoImpl.MAX_RESULTS)
    return query.getResultList().asInstanceOf[java.util.List[Paste]]
  }


  def getBySourceType(sourceType:PasteSource,desc:Boolean) : java.util.List[Paste] = {

    val cr = new CriteriaSet

    val query:Query = em.createQuery(
      cr.cr.where(
        Array(
          cr.cb.equal(cr.r.get("pasteSource"), sourceType.getCode())
        ):_*)
        .orderBy(cr.cb.desc(cr.r.get("sticked"))
      ,if (desc) {cr.cb.desc(cr.r.get("lastModified"))} else {cr.cb.asc(cr.r.get("lastModified"))})
      .select(cr.r))
      .setMaxResults(BaseDaoImpl.MAX_RESULTS)
    return query.getResultList().asInstanceOf[java.util.List[Paste]]
  }

  override def getList():java.util.List[Paste] = {

    val cr = new CriteriaSet

    val query:Query = em.createQuery(cr.cr.orderBy(cr.cb.desc(cr.r.get("sticked"))
      ,cr.cb.desc(cr.r.get("lastModified"))))
      .setMaxResults(BaseDaoImpl.MAX_RESULTS)
    return query.getResultList().asInstanceOf[java.util.List[Paste]]
  }

  def getListIntegrated(code:String):java.util.List[Paste] = {

    val cr = new CriteriaSet

    val query:Query = em.createQuery(
      cr.cr.where(Array(cr.cb.equal(cr.r.get("integrationCode"), code)):_*)
        .orderBy(cr.cb.desc(cr.r.get("sticked"))
        ,cr.cb.desc(cr.r.get("lastModified"))))
        .setMaxResults(BaseDaoImpl.MAX_RESULTS)
    return query.getResultList().asInstanceOf[java.util.List[Paste]]
  }


}
