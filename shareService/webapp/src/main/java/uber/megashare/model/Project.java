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
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;
import org.hibernate.validator.constraints.SafeHtml;

/**
 *
 * @author aachernyshev
 */
@Entity
@Table(name = "s_projects")
//@Audited
@Indexed(index = "indexes/project")
@XStreamAlias("project")
@Audited
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project extends AvatarStruct{

    @Pattern(regexp = "(.+)", message = "{struct.name.validator}")
    @Column(name = "s_desc", nullable = true)
    @Field(index = Index.YES, store = Store.YES, termVector = TermVector.YES, boost=@Boost(3f))
    //   @Analyzer(definition = "customanalyzer")
    @XStreamAsAttribute
    @SafeHtml(whitelistType=SafeHtml.WhiteListType.NONE,message = "{validator.forbidden-symbols}")
    private String description;
       
    public Project() {}
    
    public Project(Long id) {
        this.setId(id);
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }      
    
    @Override
    public void loadFull() {
    }
    
}