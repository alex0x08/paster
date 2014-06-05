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
package uber.megashare.model.xml;

import java.io.Serializable;
import java.util.UUID;
import javax.validation.constraints.Pattern;
import uber.megashare.base.LoggedClass;

/**
 *
 * @author alex
 */
public class XMLField implements Serializable,Cloneable{
    
    private String id="";
    
    @Pattern(regexp = "^[a-zA-Z_\\-.0-9]{1,30}$", message = "{struct.name.validator}")
    private String name;
    
    @Pattern(regexp = "(.+)", message = "{struct.name.validator}")
    private String value;
    
    private String uid = UUID.randomUUID().toString();

    private boolean secure;

    public boolean isEmpty() {
        return name==null || value==null;
    }
    
    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }
    
    public String getUuid() {
        return uid;
    }
    
    public void setUuid(String uid) {
        this.uid=uid;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public XMLField clone() {
    
        XMLField out = new XMLField();
        out.id=id.toString();
        out.name=name.toString();
        out.value=value.toString();
        
        return out;
    }
    
    @Override
    public String toString() {
    
         return LoggedClass.getStaticInstance()
                .getNewProtocolBuilder()
                .append("uuid", uid)
                .append("id", id)
                .append("name", name)
                .append("value", value)                
                .toString() ;
    }
}
