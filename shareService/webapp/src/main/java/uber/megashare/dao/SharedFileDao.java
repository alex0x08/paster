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
package uber.megashare.dao;

import java.util.List;
import org.apache.lucene.queryParser.ParseException;
import uber.megashare.model.AccessLevel;
import uber.megashare.model.SharedFile;

/**
 * DAO related to shared files
 * @author alex
 */
public interface SharedFileDao extends GenericSearchableDao<SharedFile> {
   /**
     * search for shared files
     * @param query
     *          query string
     * @param userId
     *          user owner's id
     * @param levels
     *          specified access levels
     * @return
     *          list of found files
     * @throws ParseException 
     */
    List<SharedFile> search(String query,Long userId,List<AccessLevel> levels) throws ParseException;
    /**
     * get list of shared files for specified user and access levels
     * @param id
     *          user owner's id
     * @param levels
     *          a list of access levels
     * @return 
     */
    List<SharedFile> getFilesForUser(Long id,AccessLevel[] levels);
    /**
     * get all files with specified access levels
     * @param levels
     *      a list of access levels
     * @return 
     */
    List<SharedFile> getFiles(AccessLevel[] levels);
    /**
     * get file from it's UUID
     * @param uuid
     *      UUID code
     * @return 
     */
     SharedFile getFileFromUUID(String uuid);
     
     List<SharedFile> getFilesForIntegration(String intergationCode);
    
}
