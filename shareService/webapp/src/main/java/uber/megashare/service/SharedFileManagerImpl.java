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

import java.io.File;
import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import org.apache.lucene.queryParser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import uber.megashare.dao.SharedFileDao;
import uber.megashare.model.AccessLevel;
import uber.megashare.model.FileType;
import uber.megashare.model.SharedFile;
import uber.megashare.model.SharedFileSearchQuery;

/**
 * Manager level for actions related to shared files (Implementation)
 *
 * @author alex
 */
@Service("shareManager")
public class SharedFileManagerImpl extends GenericSearchableManagerImpl<SharedFile, SharedFileSearchQuery> implements SharedFileManager {

    /**
     *
     */
    private static final long serialVersionUID = -7991981586910555468L;
    private SharedFileDao shareDao;
    private SettingsManager settings;
    
    @Resource(name = "mimeIconsSource")
    private MessageSource mimeIcons;
    
    @Resource(name = "mimeExtSource")
    private MessageSource mimeExt;

    @Autowired
    public SharedFileManagerImpl(SharedFileDao shareDao, SettingsManager settings) {
        super(shareDao);
        this.shareDao = shareDao;
        this.settings = settings;
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Override
    public void remove(Long id) {

        SharedFile obj = get(id);
        if (obj.isContainsPreview()) {

            File preview = new File(settings.getCurrentSettings().getUploadDir(), obj.getPreviewUrl());
            if (preview.exists() && preview.isFile()) {
                if (!preview.delete()) {
                    throw new IllegalStateException("Cannot delete file " + preview.getAbsolutePath());
                }
            }

        }

        File f = new File(settings.getCurrentSettings().getUploadDir(), obj.getUrl());
        if (f.exists() && f.isFile()) {
            if (!f.delete()) {
                throw new IllegalStateException("Cannot delete file " + f.getAbsolutePath());

            }
        }


        dao.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Override
    public SharedFile save(SharedFile object) {
        return super.save(object);
    }

    @Override
    public List<SharedFile> search(SharedFileSearchQuery query) throws ParseException {
        return shareDao.search(query.getQuery(), query.getUserId(), query.getLevels());
    }

    public List<SharedFile> getFiles(AccessLevel[] levels) {
        return shareDao.getFiles(levels);
    }

    public List<SharedFile> getFilesForIntegration(String intergationCode) {
        return shareDao.getFilesForIntegration(intergationCode);
    }

    public List<SharedFile> getFilesForUser(Long id, AccessLevel[] levels) {
        return shareDao.getFilesForUser(id, levels);
    }

    public SharedFile getFileFromUUID(String uuid) {
        return shareDao.getFileFromUUID(uuid);
    }
    
    public String getMimeExt(String ext) {
        return mimeExt.getMessage(ext, null, null, Locale.getDefault());
    }
    
    public FileType lookupType(String mime) {
        return FileType.lookup(mimeIcons.getMessage(mime, null, "type.binary", Locale.getDefault()));
    }

}
