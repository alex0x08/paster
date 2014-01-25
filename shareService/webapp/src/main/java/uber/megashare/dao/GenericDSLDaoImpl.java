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
package uber.megashare.dao;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.*;
import com.mysema.query.types.path.PathBuilder;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.transaction.annotation.Transactional;
import static uber.megashare.dao.GenericDaoImpl.MAX_RESULTS;
import uber.megashare.model.BaseDBObject;

/**
 * Имплементация базового DAO для интеграции с QueryDSL
 * Все потомки получают +100 к пафосу и все функции по работе с предикатами
 * <p/>
 * Взято из реализации JPARepository и творчески переделано
 *
 * @author achernyshev
 * @since 1.0
 */
@Transactional(readOnly = true,value= "transactionManager", rollbackFor = Exception.class)
public abstract class GenericDSLDaoImpl<T extends BaseDBObject, PK extends Serializable> 
            extends GenericDaoImpl<T, PK> implements GenericDSLDao<T, PK> {

    protected final EntityPath<T> path;
    
    protected final PathBuilder<T> builder;
  
    
    protected GenericDSLDaoImpl(final Class<T> persistentClass) {
        super(persistentClass);
        path = SimpleEntityPathResolver.INSTANCE.createPath(persistentClass);
        this.builder = new PathBuilder<>(path.getType(), path.getMetadata());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQuery createQuery() {
        return new JPAQuery(getEntityManager()).limit(MAX_RESULTS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQuery createQuery(Predicate... predicate) {
        return new JPAQuery(getEntityManager()).from(path).where(predicate).limit(MAX_RESULTS);
    }

    /**
     * {@inheritDoc}
     */
    protected JPQLQuery applyPagination(JPQLQuery query, Pageable pageable) {
        
        return pageable == null ? query : applySorting(
                query.offset(pageable.getOffset())
                .limit(pageable.getPageSize() > MAX_RESULTS ? MAX_RESULTS : pageable.getPageSize()),
                pageable.getSort());
    }

    /**
     * {@inheritDoc}
     */
    protected JPQLQuery applySorting(JPQLQuery query, Sort sort) {

        if (sort == null) {
            return query;
        }
        for (Sort.Order order : sort) {
            query.orderBy(toOrder(order));
        }

        return query;
    }

    /**
     * {@inheritDoc}
     */
    protected OrderSpecifier<?> toOrder(Sort.Order order) {
     return new OrderSpecifier(
                order.isAscending() ? com.mysema.query.types.Order.ASC
                        : com.mysema.query.types.Order.DESC, builder.get(order.getProperty()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findOne(Predicate predicate) {
        return createQuery(predicate).limit(MAX_RESULTS).uniqueResult(path);
    }
    
     @Override
    public T findOne(Predicate prdct, OrderSpecifier<?>... oss) {
        return createQuery(prdct).limit(MAX_RESULTS).orderBy(oss).uniqueResult(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll(Predicate predicate) {
        return createQuery(predicate).limit(MAX_RESULTS).list(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll(Predicate prdct, OrderSpecifier<?>... oss) {
        return createQuery(prdct).limit(MAX_RESULTS).orderBy(oss).list(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> findAll(Predicate prdct, Pageable pgbl) {
        return new PageImpl<>(applyPagination(createQuery(prdct), pgbl).list(path),
                pgbl, applyPagination(createQuery(prdct), pgbl).count());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count(Predicate prdct) {
        return createQuery(prdct).count();
    }

    protected EntityPath<T> getPath() {
        return path;
    }
}
