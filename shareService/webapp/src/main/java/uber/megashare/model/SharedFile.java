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
package uber.megashare.model;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.*;
import org.springframework.web.multipart.MultipartFile;
import uber.megashare.base.LoggedClass;

/**
 * Shared file entity
 *
 * UUID is used for file lookup
 *
 * @author alex
 */
@Entity
@Indexed(index = "indexes/sharedfile")
@Table(name = "s_files")
@Audited
public class SharedFile extends Node {

    public static final String PASTER_PREFIX= "paste_";
    
    /**
     *
     */
    private static final long serialVersionUID = 9145586408783953213L;
    
    @Transient
    private MultipartFile file;
    
    @NotNull(message = "{validator.not-null}")
    @Size(min = 3, max = 1024, message = "{validator.not-null}")
    private String url;
    
    @NotNull(message = "{validator.not-null}")
    @Column(nullable = false, unique = true, length = 255)
    @Field(index = Index.YES, store = Store.YES, termVector = TermVector.NO)
    private String uuid = UUID.randomUUID().toString();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Field(index = Index.YES, store = Store.YES, termVector = TermVector.NO)
    private FileType type = FileType.BINARY;
    
    @Field(index = Index.YES, store = Store.YES, termVector = TermVector.NO)
    private String integrationCode;
    
    @Column(nullable = false)
    @Field(index = Index.YES, store = Store.YES, termVector = TermVector.NO)
    private String mime;
    
    private String previewUrl;
    
    private long fileSize;
    
    private int previewWidth, previewHeight;

    
    public boolean isPasterIntegrated() {
        return integrationCode!=null && integrationCode.startsWith(PASTER_PREFIX);
    }
    
    @JsonIgnore
    public Long getPasterId() {
        return Long.valueOf(integrationCode.substring(PASTER_PREFIX.length()));
    }
    
    public int getPreviewHeight() {
        return previewHeight;
    }

    public void setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public void setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
    }

    public String getIntegrationCode() {
        return integrationCode;
    }

    public void setIntegrationCode(String integrationCode) {
        this.integrationCode = integrationCode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public String getIcon() {
        return type.getIcon();
    }

    public String getFormattedFileSize() {
        return FileUtils.byteCountToDisplaySize(fileSize);
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isContainsPreview() {
        return previewUrl != null;
    }

    @JsonIgnore
    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    @JsonIgnore
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonIgnore
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;

        /*
         * if (file!=null && !file.isEmpty()) { resetContent();
        }
         */
    }

    public void resetContent() {

        url = null;
        type = FileType.BINARY;
        previewUrl = null;
        mime = null;
        fileSize = 0;
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return LoggedClass.getStaticInstance()
                .getNewProtocolBuilder()
                .append("url", url)
                .append("uuid", uuid)
                .append("owner", getOwner())
                .append("accessLevel", getAccessLevel())
                .append("integrationCode", integrationCode)
                .append("fileType", type)
                .append("previewUrl", previewUrl)
                .append("mime", mime)
                .append("fileSize", fileSize).toString() + super.toString();

    }
}