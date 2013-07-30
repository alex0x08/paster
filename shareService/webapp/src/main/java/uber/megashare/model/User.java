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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uber.megashare.base.MD5Util;

/**
 * User object
 */
@Entity
@Table(name = "s_users")
//@Audited
@Indexed(index = "indexes/user")
public class User extends Struct implements Serializable, UserDetails {

    /**
     *
     */
    private static final long serialVersionUID = 617292263402304412L;
    
    @Pattern(regexp = "[a-zA-Z0-9._-]+", message = "{user.login.validator}")
    @NotNull(message = "{validator.not-null}")
    @Column(nullable = false, length = 50, unique = true)
    @Field
    private String login;
    
    @NotNull(message = "{validator.not-null}")
    @Column(nullable = false, length = Integer.MAX_VALUE)
    private String password;
    
    private transient String newPassword,repeatPassword;
    
    @Field
    @Column(unique = true)
    private String email;
    
    private AccessLevel defaultFileAccessLevel = AccessLevel.OWNER;
    /**
     * User's roles
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Role> roles = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE})
    @IndexColumn(name="sess_indx")
   //@NotAudited
    private List<SavedSession> savedSessions = new ArrayList<>();

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    
    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
    
    public List<SavedSession> getSavedSessions() {
        return savedSessions;
    }
    
    public boolean containsSavedSession(String key) {
        return savedSessions.contains(new SavedSession(key));
    }
    public SavedSession getSavedSession(String key) {
        for (SavedSession s:savedSessions) {
           if (s.getCode().equals(key)) {
               return s;
           }
        }
        return null;
    }
    
    public AccessLevel getDefaultFileAccessLevel() {
        return defaultFileAccessLevel;
    }

    public void setDefaultFileAccessLevel(AccessLevel defaultFileAccessLevel) {
        this.defaultFileAccessLevel = defaultFileAccessLevel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarHash() {
        return email == null ? null : MD5Util.getInstance().md5Hex(email);
    }

    /**
     * @return stored login
     */
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return stored password
     */
    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public boolean isPasswordSet() {
        return getPassword() != null && getPassword().trim().length() > 0;
    }

    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }

    public String getUsername() {
        return getLogin();
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return isPasswordSet();
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return !isDisabled();
    }

    public void setEnabled(boolean val) {
        setDisabled(!val);
    }

    @Override
    public String toString() {
        return super.toString() + ",user login=" + login;
    }

    public void loadFull() {
        //no lazy deps
    }
}
