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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.search.annotations.Indexed;

@Entity
@Table(name = "s_folders")
// @Audited
@Indexed(index = "indexes/nodes")
public class Folder extends Node {

	public static final String ROOT_CODE="_root_";
	
	public static final String TRASH_CODE="_trash_";
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2025308492906065162L;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE,CascadeType.PERSIST })
	private List<Folder> folders = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE,CascadeType.PERSIST })
	private List<SharedFile> files = new ArrayList<>();

    public List<Folder> getFolders() {
        return folders;
    }

   

    public List<SharedFile> getFiles() {
        return files;
    }

    public void loadFull() {
    
        for (Folder f:folders) {
            f.loadFull();
        }
        
        for (SharedFile s:files) {
            s.loadFull();
        }
    }

	
}
