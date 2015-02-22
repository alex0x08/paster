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

package uber.paste.dao

import uber.paste.model.Key
import javax.persistence.Query
import org.springframework.transaction.annotation.Transactional


@Transactional(readOnly = true)
abstract class KeyDaoImpl[T <: Key](model:Class[T]) extends StructDaoImpl[T](model) {

  def getByKey(code:String) : T = {

    val cr = new CriteriaSet

    val query= em.createQuery[T](cr.cr.where(Array(cr.cb.equal(cr.r.get("code"), code)):_*).select(cr.r))
    val results = query.getResultList()

    return if (results.isEmpty()) null.asInstanceOf[T] else results.get(0)

  }

}
