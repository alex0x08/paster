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

import java.util.Date;
import uber.megashare.model.SystemProperties;


/**
 * Сервис работы с настройками системы
 * @author alex
 */
public interface SettingsManager extends GenericManager<SystemProperties, Long> {

    /**
     * Получить текущие настройки
     * @return текущие настройки
     */
    SystemProperties getCurrentSettings();

    /**
     * Сохранить изменения в базе основные настроек
     * @param t основные настройки
     * @return сохраненная в базе копия
     */
    SystemProperties merge(SystemProperties t);

    String getCalculatedFileDir(Date date,Long version);
   
   
}
