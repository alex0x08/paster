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
package uber.megashare.model;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import scala.actors.threadpool.Arrays;

/**
 *
 * @author aachernyshev
 */
public class EnumListConverter implements Converter {

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        
        System.out.println("_marshall class="+source.getClass().getCanonicalName());
        Set<Enum> list = (Set<Enum>) source;
        
    //    writer.addAttribute("roles", String.valueOf(StringUtils.join(list, ",")));
        writer.setValue(String.valueOf(StringUtils.join(list, ",")));
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        return Arrays.asList(StringUtils.split(reader.getValue(), ","));
     }

    @Override
    public boolean canConvert(Class type) {      
        return true;
        //return type.isAssignableFrom(List.class) 
         //       && type.getGenericInterfaces().length>0 && type.getGenericInterfaces()[0].getClass().isEnum();
    }
}
