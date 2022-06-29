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

package com.Ox08.paster.webapp.dao

import com.Ox08.paster.webapp.model.Key
import org.springframework.transaction.annotation.Transactional

/**
 *  Dao for objects contain key name (not PK)
 */
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
abstract class KeyDaoImpl[T <: Key](model:Class[T]) extends StructDaoImpl[T](model) {

  def getByKey(code:String) = getSingleByKeyValue("code",code.toUpperCase)

}
