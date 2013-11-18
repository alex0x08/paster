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

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;
import uber.megashare.model.tree.NodeType;


@MappedSuperclass
//@Entity
@Audited
@EntityListeners({UpdateOwnerListener.class})
public abstract class Node extends CommentedStruct {

    /**
     *
     */
    private static final long serialVersionUID = 8335426398422307477L;
    
    @XStreamAsAttribute
    private String code;
    
    @Field(index = Index.YES, store = Store.YES, termVector = TermVector.YES)
    @XStreamAsAttribute
    String ownerName;
    
    /**
     * The owner of this file
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,
        CascadeType.MERGE})
    @JoinColumn(name = "owner_id")
    //@NotAudited
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    User owner;
    
    @Enumerated(EnumType.STRING)
    @Field(index = Index.YES, store = Store.YES, termVector = TermVector.NO)
    private AccessLevel accessLevel = AccessLevel.PROJECT;
    
    @Transient
    private NodeType nodeType = NodeType.FILE;

    @Transient
    private List<SharedFile> children = new ArrayList<>(); 

    public List<SharedFile> getChildren() {
        return children;
    }

    public void setChildren(List<SharedFile> children) {
        this.children = children;
    }
    
    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String name) {
        this.ownerName=name;
    }
    
    @JsonIgnore
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
