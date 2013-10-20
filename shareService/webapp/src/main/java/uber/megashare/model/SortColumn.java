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

import java.util.Objects;

/**
 *
 * @author aachernyshev
 */
public class SortColumn {

    private String property,name;

    public SortColumn(String property,String name) {
        this.property=property;
        this.name=name;
    }
    
    public String getName() {
        return name;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object from) {
        return from instanceof SortColumn && property != null && property.equals(((SortColumn) from).property);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.property);
        return hash;
    }
    
}
