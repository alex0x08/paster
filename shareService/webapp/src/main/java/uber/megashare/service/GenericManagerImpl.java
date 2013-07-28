/**
 * Copyright (C) 2011 alex <alex@0x08.tk>
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

import java.io.Serializable;
import java.util.List;
import uber.megashare.base.LoggedClass;
import uber.megashare.dao.GenericDao;
import uber.megashare.model.BaseDBObject;


/**
 * This class serves as the Base class for all other Managers - namely to hold
 * common CRUD methods that they might all use. You should only need to extend
 * this class when your require custom CRUD logic.
 *
 * <p>To register this class in your Spring context file, use the following XML.
 * <pre>
 *     &lt;bean id="userManager" class="uber.tracecontrol.service.impl.GenericManagerImpl"&gt;
 *         &lt;constructor-arg&gt;
 *             &lt;bean class="uber.tracecontrol.dao.hibernate.GenericDaoHibernate"&gt;
 *                 &lt;constructor-arg value="uber.tracecontrol.model.User"/&gt;
 *                 &lt;property name="sessionFactory" ref="sessionFactory"/&gt;
 *             &lt;/bean&gt;
 *         &lt;/constructor-arg&gt;
 *     &lt;/bean&gt;
 * </pre>
 *
 * <p>If you're using iBATIS instead of Hibernate, use:
 * <pre>
 *     &lt;bean id="userManager" class="uber.tracecontrol.service.impl.GenericManagerImpl"&gt;
 *         &lt;constructor-arg&gt;
 *             &lt;bean class="uber.tracecontrol.dao.ibatis.GenericDaoiBatis"&gt;
 *                 &lt;constructor-arg value="uber.tracecontrol.model.User"/&gt;
 *                 &lt;property name="dataSource" ref="dataSource"/&gt;
 *                 &lt;property name="sqlMapClient" ref="sqlMapClient"/&gt;
 *             &lt;/bean&gt;
 *         &lt;/constructor-arg&gt;
 *     &lt;/bean&gt;
 * </pre>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 * @param <T> a type variable
 * @param <PK> the primary key for that type
 */
public abstract class GenericManagerImpl<T extends BaseDBObject, PK extends Serializable> extends LoggedClass implements GenericManager<T, PK> {
  

    /**
	 * 
	 */
	private static final long serialVersionUID = 4852856653776616598L;
	/**
     * GenericDao instance, set by constructor of child classes
     */
    protected final GenericDao<T, PK> dao;

    public GenericManagerImpl(GenericDao<T, PK> genericDao) {
        this.dao = genericDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<T> getAll() {
        return dao.getAll();
    }

    /**
     * {@inheritDoc}
     */
    public T get(PK id) {
        return dao.get(id);
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(PK id) {
        return dao.exists(id);
    }

    /**
     * {@inheritDoc}
     */
    public T save(T object) {
        getLogger().debug("___GenericManagerImpl.save obj="+object);
        return dao.saveObject(object);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(PK id) {
        dao.remove(id);
    }
    
    public T getFull(PK id) {
        return dao.getFull(id);
    }
}
