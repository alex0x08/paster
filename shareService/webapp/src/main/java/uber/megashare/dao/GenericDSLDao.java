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
package uber.megashare.dao;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uber.megashare.model.BaseDBObject;

/**
 * Базовый DAO для интеграции с QueryDSL
 *
 * @author achernyshev
 */
public interface GenericDSLDao<T extends BaseDBObject, PK extends Serializable> extends GenericDao<T, PK> {

    /**
     * найти сущность с указанным предикатом
     *
     * @param prdct предикат
     * @return сущнось
     */
    T findOne(Predicate prdct);
    
    T findOne(Predicate prdct, OrderSpecifier<?>... oss);
    

    /**
     * Найти все сущности, удовлетворяющие заданному предикату
     *
     * @param prdct предикат
     * @return список сущностей
     */
    List<T> findAll(Predicate prdct);

    /**
     * Найти все сущности по данному выражению и с указанной сортировкой
     *
     * @param prdct выражение
     * @param oss   указатель на поля для сортировки
     * @return список сущностей
     */
    List<T> findAll(Predicate prdct, OrderSpecifier<?>[] oss);

    /**
     * Найти все сущности по данному выражению из указанной выборки страниц
     *
     * @param prdct выражение
     * @param pgbl  выборка с сущностями
     * @return страница сущностей
     */
    Page<T> findAll(Predicate prdct, Pageable pgbl);

    /**
     * Получить количество записей, удовлетворяющих данному выражению
     *
     * @param prdct предикат
     * @return число
     */
    long count(Predicate prdct);

    JPQLQuery createQuery();

    /**
     * Получить базовое JPQL-выражение для дальнейших преобразований
     *
     * @param predicate предикат
     * @return объект выражения
     */
    JPQLQuery createQuery(Predicate... predicate);


}
