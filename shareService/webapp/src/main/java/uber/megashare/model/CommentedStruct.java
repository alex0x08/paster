/**
 * Copyright (C) 2011 alex <alex@0x08.tk>
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
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.search.annotations.IndexedEmbedded;

/**
 *
 * @author alex
 */

@MappedSuperclass
@Audited
public class CommentedStruct extends Struct implements Serializable {
    
    private static final long serialVersionUID = 8839150374530598001L;
    
    
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE})
   // @ContainedIn
    //@IndexedEmbedded
    @NotAudited
   // @Audited(modStore=ModificationStore.FULL, targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @IndexedEmbedded(depth = 1, prefix = "comments_")
    //@XStreamImplicit(keyFieldName="comments")
    private List<Comment> comments = new ArrayList<>();

    @NotAudited
    private int commentsCount;
    
    public CommentedStruct addComment(Comment c) {
        
        comments.add(c);
        
        commentsCount=comments.size();
        
        return this;
    }
   
    public CommentedStruct removeComment(Comment c) {
        comments.remove(c);
        commentsCount=comments.size();
        return this;
    }
   
   public int getCommentsCount() {
       return commentsCount;
   }
    
//    @NotAudited
    //@JsonIgnore
    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public void loadFull() {
        for (Comment c : comments) {
            c.loadFull();
        }
    }
}
