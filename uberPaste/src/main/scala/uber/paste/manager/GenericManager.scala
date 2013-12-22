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

package uber.paste.manager

import uber.paste.base.Loggered
import uber.paste.dao.BaseDao


trait GenericManager[T <: java.io.Serializable, PK <:Long ] {

  /**
     * Generic method used to get all objects of a particular type. This
     * is the same as lookup up all rows in a table.
     * @return List of populated objects
     */
    def getList():java.util.List[T]

    /**
     * Generic method to get an object based on class and identifier. An
     * ObjectRetrievalFailureException Runtime Exception is thrown if
     * nothing is found.
     *
     * @param id the identifier (primary key) of the object to get
     * @return a populated object
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    def get(id:PK):T

    /**
     * Checks for existence of an object of type T using the id arg.
     * @param id the identifier (primary key) of the object to get
     * @return - true if it exists, false if it doesn't
     */
    def exists(id:PK):Boolean

    /**
     * Generic method to save an object - handles both update and insert.
     * @param object the object to save
     * @return the updated object
     */
    def save(obj:T):T

    /**
     * Generic method to delete an object based on class and id
     * @param id the identifier (primary key) of the object to remove
     */
    def remove(id:PK)
    
   
    def getIdList(from:PK):java.util.List[PK]
  
  
}

abstract class GenericManagerImpl[T <: java.io.Serializable, PK <:Long ] extends Loggered with GenericManager[T,PK]{

  protected def getDao:BaseDao[T, PK]


  /**
   * {@inheritDoc}
   */
  def getList():java.util.List[T] =  {
    return getDao.getList
  }

  /**
   * {@inheritDoc}
   */
  def get(id:PK):T = {
    return getDao.get(id)
  }

  /**
   * {@inheritDoc}
   */
  def exists(id:PK):Boolean = {
    return getDao.exists(id)
  }

  /**
   * {@inheritDoc}
   */
  def save(obj:T):T = {
    //logger.debug("___GenericManagerImpl.save obj="+obj)
    return getDao.save(obj)
  }

  /**
   * {@inheritDoc}
   */

  def remove(id:PK) {
    getDao.remove(id)
  }

   def getIdList(from:PK):java.util.List[PK] = {
      return getDao.getIdList(from)
   }
  


}
