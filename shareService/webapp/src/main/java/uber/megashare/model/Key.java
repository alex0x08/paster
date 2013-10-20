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

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import uber.megashare.base.LoggedClass;

/**
 *
 * @author achernyshev
 */
@MappedSuperclass
public abstract class Key extends Struct {

    @NotNull
    @Column(nullable = false, length = 50, unique = true)
    @XStreamAsAttribute
    private String code;

    public Key() {
    }

    public Key(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object from) {     
        return !(from instanceof Key) ? false : getCode() != null && ((Key) from).getCode().equals(code);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.code);
        return hash;
    }
    
      @Override
    public String toString() {
        return LoggedClass.getStaticInstance().getNewProtocolBuilder()
                .append("code", code)
                .append(super.toString()).toString();
    }
    
}
