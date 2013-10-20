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
package uber.megashare.base;

import java.io.IOException;
import java.util.Properties;
import uber.megashare.model.FileType;

/**
 *
 * @author alex
 */
public class MimeSupport extends LoggedClass {

    private Properties mimeToImage = new Properties();

    public FileType lookupType(String mime) {
        return FileType.lookup(mimeToImage.getProperty(mime, "type.binary"));
    }

    private MimeSupport load() {
        try {
            mimeToImage.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("mimeIcons.properties"));
            return this;
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static MimeSupport instance = new MimeSupport().load();

    public static MimeSupport getInstance() {return instance;}
}
