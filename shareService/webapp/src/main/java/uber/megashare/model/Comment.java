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
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

/**
 * Комментарий
 *
 * @author alex
 */
@Entity
@Table(name = "COMMENTS")
@Indexed(index = "indexes/comments")
@XStreamAlias("comment")
public class Comment extends BaseDBObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6941001653905900312L;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    @IndexedEmbedded
    private User author;
    
    @Column(name = "created", columnDefinition = "timestamp")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created = Calendar.getInstance().getTime();
    
    @Lob
    @Column(length = Integer.MAX_VALUE)
    @Field(index = Index.YES, store = Store.YES, termVector = TermVector.YES, boost=@Boost(1.2f))
    private String message;
    
    

    /**
     * Автор комментария
     *
     * @return пользователь
     */
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    
   
    /**
     * Текст сообщения
     *
     * @return строка
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Дата создания
     *
     * @return дата
     */
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "comment, id=" + getId();
    }

    public void loadFull() {

        author.loadFull();
    }
}
