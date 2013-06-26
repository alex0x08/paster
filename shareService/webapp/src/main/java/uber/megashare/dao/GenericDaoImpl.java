/**
 * Copyright (C) 2011 aachernyshev <alex@0x08.tk>
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
package uber.megashare.dao;

import org.springframework.orm.ObjectRetrievalFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uber.megashare.base.LoggedClass;
import uber.megashare.model.BaseDBObject;

/**
 * This class serves as the Base class for all other DAOs - namely to hold
 * common CRUD methods that they might all use. You should only need to extend
 * this class when your require custom CRUD logic.
 * <p/>
 * <p>To register this class in your Spring context file, use the following XML.
 * <pre>
 *      &lt;bean id="fooDao" class="uber.tracecontrol.dao.hibernate.GenericDaoHibernate"&gt;
 *          &lt;constructor-arg value="uber.tracecontrol.model.Foo"/&gt;
 *      &lt;/bean&gt;
 * </pre>
 *
 * @author <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 * @param <T> a type variable
 * @param <PK> the primary key for that type
 */
@Transactional(readOnly = true, rollbackFor = Exception.class)
public abstract class GenericDaoImpl<T extends BaseDBObject, PK extends Serializable> extends LoggedClass implements GenericDao<T, PK> {
   
    /**
	 * 
	 */
	private static final long serialVersionUID = -7455984245668865869L;

	protected final Class<T> persistentClass;
    
    private EntityManager em;

    public static final int MAX_RESULTS = 10000; //не тянуть все, нужно больше 10к записей - юзай фильтры и поиск

    
    protected class CriteriaSet {
    	
    	final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	final CriteriaQuery<T> cr = cb.createQuery(persistentClass);
    	final Root<T> r = cr.from(persistentClass);
    	
    }
    
    
    /**
     * Sets the entity manager.
     */
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    public EntityManager getEntityManager() {
        return this.em;
    }   
 

    /**
     * Constructor that takes in a class to see which type of entity to persist.
     * Use this constructor when subclassing.
     *
     * @param persistentClass the class type you'd like to persist
     */
    protected GenericDaoImpl(final Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> getAll() {
    	
    	CriteriaSet cr = new CriteriaSet();
    	
        Query query = getEntityManager().createQuery(cr.cr.orderBy(cr.cb.desc(cr.r.get("lastModified"))));
        return (List<T>) query.getResultList();
 
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(PK id) {        
        T entity = em.find(this.persistentClass, id);
        if (entity == null) {
            getLogger().warn("Uh oh, '" + this.persistentClass + "' object with id '" + id + "' not found...");
            throw new ObjectRetrievalFailureException(this.persistentClass, id);
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(PK id) {
        T entity = em.find(this.persistentClass, id);
        return entity != null;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public T saveObject(T object) {
        T out = em.merge(object);
        em.flush();        
        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
        @Override
    public void remove(PK id) {
        em.remove(get(id));
    }

    /**
     * {@inheritDoc}
     */
    public void fill(T obj) {
    
        getLogger().debug("filling object="+obj);
        
        if (obj==null) {
            return;
        }
        
        obj.loadFull();
        
    //    fillImpl(obj);
       
    }
    
   
      
    /**
     * {@inheritDoc}
     */
    public T getFull(T u) {
       fill(u);
       return u;
    }

    /**
     * {@inheritDoc}
     */
    public T getFull(PK id) {
        T u = get(id);
        if (u == null) {
            return null;
        }
        fill(u);
        return u;
    }
    
   
   
}
