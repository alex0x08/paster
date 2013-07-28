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
package uber.megashare.base;

import java.util.Date;
import uber.megashare.model.SystemProperties;

/**
 *
 * @author alex
 */
public class SettingsBuilder extends AbstractBuilder<SystemProperties> {

    /**
     *
     */
    private static final long serialVersionUID = 4042967321740095535L;

    public SettingsBuilder(SystemProperties p) {
        super(p);
    }

    public SettingsBuilder addInstallDate(Date date) {
        obj.setInstallDate(date);
        return this;
    }

    public SettingsBuilder addTmpDir(String dir) {
        obj.setTmpDir(dir);
        return this;
    }

    public SettingsBuilder addUploadDir(String dir) {
        obj.setUploadDir(dir);
        return this;
    }

   
    
    public SettingsBuilder addAppVersion() {
        obj.getAppDbVersion().fillFromManifest();
        return this;
    }
    
    public SettingsBuilder addBaseUrl(String url) {
        obj.setBaseUrl(url);
        return this;
    }

    public static SettingsBuilder getInstance() {
        return new SettingsBuilder(new SystemProperties());
    }
}
