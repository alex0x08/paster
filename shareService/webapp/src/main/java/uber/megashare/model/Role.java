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
package uber.megashare.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.springframework.security.core.GrantedAuthority;

/**
 * Simple embedded roles
 * 
 * @author alex
 */
@XStreamAlias("role")
public enum Role implements GrantedAuthority {

    ROLE_ADMIN("role.admin"),
    ROLE_MANAGER("role.manager"),
    ROLE_USER("role.user");
    private String desc;

    private Role(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return name();
    }

    public String getName() {
        return name();
    }

    
    public String getDesc() {
        return desc;
    }

    /**
     * @return the name property (getAuthority required by Acegi's GrantedAuthority interface)
     * @see org.springframework.security.GrantedAuthority#getAuthority()
     */
    //@Override
    public String getAuthority() {
        return name();
    }
}
