/*
 * Copyright 2014 Ubersoft, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.model

import java.util.Objects

class SortColumn(property:String, name:String) {
    
    def getName() = name

    def getProperty() = property

    override def equals(from:Any):Boolean = {
      return from.isInstanceOf[SortColumn] && property != null &&
      property.equals((from.asInstanceOf[SortColumn]).getProperty)
    }

    override def hashCode():Int = {        
        return 46 + Objects.hashCode(this.property)
    }
    
}
