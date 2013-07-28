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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uber.megashare.model.tree;

import java.util.Set;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.RelationshipType;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;


/**
 *
 * @author aachernyshev
 */


@NodeEntity
public class FolderNode {
    
    
    @GraphId
    private Long id;
    
    @Indexed
    private String name;

    @RelatedTo(direction = Direction.INCOMING, type = "CHILD")
    private FolderNode parent;

    @RelatedTo(direction = Direction.OUTGOING, type = "CHILD")
    private Set<FolderNode> children;

    @Indexed
    private NodeType type = NodeType.FOLDER;

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
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
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
 
}
