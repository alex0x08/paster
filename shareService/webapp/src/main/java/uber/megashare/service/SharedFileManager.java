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

import java.util.List;
import java.util.Locale;
import uber.megashare.model.AccessLevel;
import uber.megashare.model.FileType;
import uber.megashare.model.SharedFile;
import uber.megashare.model.SharedFileSearchQuery;

/**
 *Manager level for actions related to shared files 
 * 
 * @author alex
 */
public interface SharedFileManager extends GenericSearchableManager<SharedFile,SharedFileSearchQuery>{
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
    
     String getMimeExt(String ext);
    
    FileType lookupType(String mime);
    
     List<SharedFile> getFilesToRemoval();

}
