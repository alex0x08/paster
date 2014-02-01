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

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import uber.megashare.base.SystemInfo;

/**
 * Базовые настройки системы
 *
 * @author alex
 */
@Entity
@Table(name = "system_properties")
public class SystemProperties extends BaseDBObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4774995385477116623L;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date installDate;
    
    private String tmpDir,baseUrl,uploadDir,uploadUrl;

    @Embedded
    private AppVersion appVer = new AppVersion();

      public AppVersion getAppDbVersion() {
        return appVer;
    }     
    
    public AppVersion getAppVersion() {
        return SystemInfo.getInstance().getRuntimeVersion();
    }       

    public String getUploadDir() {
        return System.getProperty("share.app.home") + File.separator + uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    /**
     * Базовый урл (для адресов в почте)
     *
     * @return
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Временный каталог
     *
     * @return
     */
    public String getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    /**
     *
     * @return дата установки системы (базы)
     */
    public Date getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Date installDate) {
        this.installDate = installDate;
    }

    public void loadFull() {
        //no lazy deps
    }
}
