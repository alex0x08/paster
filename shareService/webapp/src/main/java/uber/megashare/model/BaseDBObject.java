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
package uber.megashare.model;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import uber.megashare.base.LoggedClass;

/**
 * Базовый объект, сохраняемый в базе
 * @author alex
 */
@MappedSuperclass
public abstract class BaseDBObject extends BaseObject implements Serializable, IdentifiedObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7254989743482726486L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private boolean disabled;

    /**
     * является ли объект отключенным
     *
     * @return
     *  true - да
     *  false -нет
     */
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * пустой ли объект (сохранен ли в базе)
     *
     * @return
     *  true - да
     *  false - нет
     */
    public boolean isBlank() {
        return id == null;
    }

    /**
     * Получить уникальный ключ объекта
     * @return
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * Сравнение с другими объектами
     * происходит сторого только по id
     * @param from 
     * @return
     */
    @Override
    public boolean equals(Object from) {

        if (!(from instanceof BaseDBObject)) {
            return false;
        }
        return getId() != null && ((BaseDBObject) from).getId().equals(id);
    }

    @Override
    public String toString() {
        return LoggedClass.getStaticInstance().getNewProtocolBuilder()
                .append("id", id)
                .append("class", getClass().getCanonicalName()).toString();
    }
    
    public abstract void loadFull();
}
