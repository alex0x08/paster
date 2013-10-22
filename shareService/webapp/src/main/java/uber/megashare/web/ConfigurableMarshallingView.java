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
package uber.megashare.web;

import java.util.Map;
import javax.servlet.ServletException;
import org.springframework.oxm.Marshaller;

import org.springframework.web.servlet.view.xml.MarshallingView;

/**
 *
 * @author aachernyshev
 */
public class ConfigurableMarshallingView extends MarshallingView{
    
    private String[] modelKeys;

     private Marshaller marshaller;
    
     public ConfigurableMarshallingView(Marshaller marshaller) {
         super(marshaller);
         this.marshaller=marshaller;
     }
    
    
    public String[] getModelKeys() {
        return modelKeys;
    }

    public void setModelKeys(String[] modelKeys) {
        this.modelKeys = modelKeys;
    }
    
    @Override
    protected Object locateToBeMarshalled(Map<String, Object> model) throws ServletException {
        
        
        if (modelKeys == null) {
            return super.locateToBeMarshalled(model);
        }

        for (String modelKey : modelKeys) {

            Object o = model.get(modelKey);
            if (o != null && marshaller.supports(o.getClass())) {
                return o;
            }
        }

        return null;
    }

}
