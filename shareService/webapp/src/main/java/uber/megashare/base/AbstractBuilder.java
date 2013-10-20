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
package uber.megashare.base;

import java.io.Serializable;

/**
 * Абстрактный билдер
 *
 * Отюда наследуются все остальные билдеры кроме CriteriaQueryBuilder
 * @author alex
 *
 * @since 3.0
 */
public abstract class AbstractBuilder<T extends Serializable> extends LoggedClass{

    /**
	 * 
	 */
	private static final long serialVersionUID = 7656767395926120686L;
	
	protected T obj; // объект над которым работает билдер


    /**
     * коструктор по-умолчанию
     */
    protected AbstractBuilder() {
    }

    /**
     * Конструктор для указанного объекта
     * @param s объект для изменения через билдер
     */
    protected AbstractBuilder(T s) {
        this.obj = s;
    }

    /**
     * Получить объект над которым работает билдер
     * @return T
     */
    public T get() {
        return this.obj;
    }

    
}
