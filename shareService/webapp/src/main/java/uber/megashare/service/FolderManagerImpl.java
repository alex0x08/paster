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
package uber.megashare.service;

import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uber.megashare.base.LoggedClass;
import uber.megashare.dao.tree.FolderDao;
import uber.megashare.model.tree.FolderNode;
import uber.megashare.model.tree.NodeType;
import uber.megashare.model.tree.Rels;

/**
 *
 * @author aachernyshev
 */
@Service("folderManager")
public class FolderManagerImpl extends LoggedClass implements FolderManager{

    private FolderDao folderDao;
    
    protected static final TraversalDescription FRIENDS_TRAVERSAL = Traversal.description()
        .depthFirst()
        .relationships( Rels.CHILD )
         .evaluator(Evaluators.includingDepths(1, 2))
        .uniqueness( Uniqueness.RELATIONSHIP_GLOBAL );
    
   @Autowired 
   public FolderManagerImpl(FolderDao dao) {
       this.folderDao = dao;
   } 
   
   public FolderNode getParentFolder() {
    return  folderDao.findByPropertyValue("nodeType", NodeType.PARENT);
   }
   
   public FolderNode save(FolderNode node) {
       return folderDao.save(node);
   }
   
   public boolean exists(Long id) {
       return folderDao.exists(id);
   }
   
   public FolderNode getFolder(Long id) {
       return folderDao.findOne(id);
   }
       
    public Iterable<FolderNode> getChildren(Long parentId) {
        return getChildren(folderDao.findOne(parentId));
    }

    public Iterable<FolderNode> getChildren(FolderNode node) {
        return folderDao.findAllByTraversal(node, FRIENDS_TRAVERSAL);
    }
}
