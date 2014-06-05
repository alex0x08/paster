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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alex
 */
@XmlRootElement(name = "form")
public class XMLObject implements XmlCloneable,Serializable {

    @NotNull
    @Valid
    private Map<String,XMLField> fields = new HashMap<>();

    @XmlTransient
    private boolean signed;
    
    @XmlTransient
    private String signInfo;

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public String getSignInfo() {
        return signInfo;
    }

    public void setSignInfo(String signInfo) {
        this.signInfo = signInfo;
    }
    
    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    public Map<String,XMLField> getFields() {
        return fields;
    }

    public void rebuildFields() {
    
        Map<String,XMLField> out = new HashMap<>();
        for (XMLField f:fields.values()) {
            out.put(f.getUuid(), f);
        }
        this.fields=out;
    }
    
    public void setFields(Map<String,XMLField> fields) {
      //  this.fields.clear();
      //  this.fields.putAll(fields);
     
    }

    @Override
    public XMLObject clone() {

        XMLObject out = new XMLObject();

        for (Entry<String,XMLField> f : fields.entrySet()) {           
            out.fields.put(f.getKey(),f.getValue().clone());
        }
        return out;
    }
}
