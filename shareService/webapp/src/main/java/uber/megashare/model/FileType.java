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

/**
 * Enum with abstact file types
 * 
 * 
 * @author alex
 */
public enum FileType {
        IMAGE("type.image","image.gif"),
        BINARY("type.binary","binary.gif"),
        ARCHIVE("type.archive","archive.gif"), 
        PDF("type.pdf","pdf.gif"), 
        MSWORD("type.msword","doc.gif"), 
        MSXLS("type.msxls","xls.gif"), 
        AUDIO("type.audio","audio.gif"),
        VIDEO("type.video","video.gif"),
        CODE("type.code","cpp.gif"),
        TEXT("type.text","text.gif");
    
    private FileType(String desc,String icon) {
        this.desc=desc;
        this.icon=icon;
    }
    
    private final String icon,desc;

    public String getDesc() {
        return desc;
    }

    public String getIcon() {
        return icon;
    }
    
    public static FileType lookup(String code) {
        
        if (code==null || code.trim().length()==0) {
            return BINARY;
        }
        
        for (FileType t:values()) {
            if (t.getDesc().equals(code)) {
                return t;
            }
        }
        
        return BINARY;
    }
    
}
