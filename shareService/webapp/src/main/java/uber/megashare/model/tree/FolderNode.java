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
package uber.megashare.model.tree;

import java.util.HashSet;
import java.util.Set;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.support.index.IndexType;
import uber.megashare.model.AccessLevel;
import uber.megashare.model.SharedFile;


/**
 *
 * @author aachernyshev
 */
@NodeEntity
public class FolderNode extends BaseTreeObject {
    
    
   
    @Indexed(indexType = IndexType.FULLTEXT, indexName = "owner_name")
    private String ownerName;

    
    @RelatedTo(direction = Direction.INCOMING, type = "FOLDER_CHILD")
    private FolderNode parent;

    @RelatedTo(direction = Direction.OUTGOING, type = "FOLDER_CHILD")
    private Set<FolderNode> children = new HashSet<>();

    @Indexed
    private NodeType nodeType = NodeType.FOLDER;

    @Indexed
    private AccessLevel accessLevel = AccessLevel.PROJECT;
    
    
    @RelatedTo(direction = Direction.OUTGOING, type = "PROJECT_CHILD")
    private Set<RelatedProject> relatedProjects = new HashSet<>();

    @RelatedTo(direction = Direction.OUTGOING, type = "USER_CHILD")
    private Set<RelatedUser> relatedUsers = new HashSet<>();

    
    //@Fetch
    //@RelatedToVia(type="RELATED_TO_PROJECTS")
    //private Set<RelatedProject> relatedProjects = new HashSet<>();

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }
    
    public Set<RelatedUser> getRelatedUsers() {
        return relatedUsers;
    }

    public void setRelatedUsers(Set<RelatedUser> relatedUsers) {
        this.relatedUsers = relatedUsers;
    }
   
    
    public Set<RelatedProject> getRelatedProjects() {
        return relatedProjects;
    }

    public void setRelatedProjects(Set<RelatedProject> relatedProjects) {
        this.relatedProjects = relatedProjects;
    }
    
    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType type) {
        this.nodeType = type;
    }    
    
    public FolderNode getParent() {
        return parent;
    }

    public void setParent(FolderNode parent) {
        this.parent = parent;
    }

    public Set<FolderNode> getChildren() {
        return children;
    }

    public void setChildren(Set<FolderNode> children) {
        this.children = children;
    }
    
   
    
    public SharedFile toSharedFile() {
        SharedFile out = new SharedFile();
        out.setNodeType(nodeType);
        out.setId(getId());
        out.setName(getName());
        out.setOwnerName(ownerName);
        
        for (RelatedProject rp:relatedProjects) {
            out.getRelatedProjects().add(rp.toProject());
        }
        
        for (RelatedUser rp:relatedUsers) {
            out.getRelatedUsers().add(rp.toUser());
        }
        
       return out;
    }
 
}
