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

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.validation.constraints.Pattern;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import uber.megashare.base.LoggedClass;


/**
 * Базовая структура (id и название) Сохраняет дату своей последней модификации
 *
 * @author alex
 */
@MappedSuperclass
@AnalyzerDef(name = "customanalyzer",
tokenizer =
@TokenizerDef(factory = StandardTokenizerFactory.class),
filters = {
    @TokenFilterDef(factory = LowerCaseFilterFactory.class),
    @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
        @Parameter(name = "language", value = "Russian")
    })
})
@Audited

public abstract class Struct extends BaseDBObject {

    
    /**
     *
     */
    private static final long serialVersionUID = -974110180786730119L;
    
    @Pattern(regexp = "(.+)", message = "{struct.name.validator}")
    @Column(name = "s_name", nullable = false)
    @Field(index = Index.YES, store = Store.YES, termVector = TermVector.YES, boost=@Boost(2f))
    //   @Analyzer(definition = "customanalyzer")
    @XStreamAsAttribute
    private String name;
    
    @Column(name = "last_modified", columnDefinition = "timestamp")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Field(index = Index.YES)
    @DateBridge(resolution = Resolution.DAY)
    @NotAudited
    @XStreamAsAttribute
    Date lastModified;

    @Column(updatable = false,name = "d_created", columnDefinition = "timestamp")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Field(index = Index.YES)
    @DateBridge(resolution = Resolution.DAY)    
    @XStreamAsAttribute
    Date created = Calendar.getInstance().getTime();

    @XStreamAsAttribute
    @NotAudited
    protected Integer versionsCount;

    public boolean isEverModified() {
        return lastModified!=null && lastModified.after(created);
    }
    
    public Integer getVersionsCount() {
        return versionsCount;
    }

    public void setVersionsCount(Integer versionsCount) {
        this.versionsCount = versionsCount;
    }

    
    /**
     * Дата последней модификации объекта
     *
     * @return дата
     */
    public Date getLastModified() {
        return lastModified;
    }
    
    
     public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
     
     

    /**
     * данный метод не используется
     *
     * @param d
     */
    public void setLastModified(Date d) {
        this.lastModified=d;
    }

    /**
     * Название объекта
     *
     * @return строка
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void fillFrom(Struct source) {
        super.fillFrom(source);
        
        //System.out.println("__fill from created="+source.created);
        this.created=source.created;
        this.lastModified=source.lastModified;
        this.name=source.name;
        this.versionsCount=source.versionsCount;
    }
    
    @Override
    public String toString() {
        return LoggedClass.getStaticInstance()
                .getNewProtocolBuilder()
                .append("name", name)
                .append("lastModified", lastModified)
                .toString() + super.toString();
    }
}
