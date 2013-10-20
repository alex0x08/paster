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
package uber.megashare.service;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uber.megashare.dao.SettingsDao;
import uber.megashare.model.SystemProperties;

/**
 *
 * @author alex
 */
@Service("settingsManager")
public class SettingsManagerImpl extends GenericManagerImpl<SystemProperties, Long> implements SettingsManager {

    /**
     *
     */
    private static final long serialVersionUID = -8424104873701515425L;
    
    private SettingsDao settingsDao;

    @Autowired
    public SettingsManagerImpl(SettingsDao settingsDao) {
        super(settingsDao);
        this.settingsDao = settingsDao;
    }

    @Override
    public String getCalculatedFileDir(Date date, Long version) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);


        return new StringBuilder()
                .append(File.separator)
                .append(c.get(Calendar.YEAR))
                .append(File.separator)
                .append(c.get(Calendar.MONTH))
                .append(File.separator)
                .append(c.get(Calendar.DAY_OF_MONTH))
                .append(File.separator)
                .append(version)
                .toString();

    }

    @Override
    public SystemProperties getCurrentSettings() {
        return settingsDao.getCurrentSettings();
    }

    @Override
    public SystemProperties merge(SystemProperties t) {
        return settingsDao.saveObject(t);
    }
}
