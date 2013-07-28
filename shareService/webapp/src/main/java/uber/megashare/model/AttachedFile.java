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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Приаттаченный файл
 *
 * @author alex
 */
@Entity
@Table(name = "ATTACHES")
public class AttachedFile extends Struct implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2536130579096562810L;
    
    @Transient
    public static final String ICON_TYPE = "image/png";//все генерируемые изображния в PNG-формате
    
    @Lob
    @Column(length = Integer.MAX_VALUE)
    private byte[] icon;
    @Lob
    @Column(name = "content", length = Integer.MAX_VALUE)
    private byte[] data;
    
    private String mime;

    /**
     * является ли файл картинкой
     *
     * @return true - да, false -нет
     */
    public boolean isImage() {
        return icon != null;
    }

    /**
     * строка mime-типа файла
     *
     * @return
     */
    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    /**
     * блоб с иконкой (если файл - картинка)
     *
     * @return массив байт
     */
    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon.clone();
    }

    /**
     * массив байтов (блоб в базе) с содержимым файла
     *
     * @return массив байт
     */
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data.clone();
    }

    @Override
    public void loadFull() {

        getData();
        getIcon();

    }
}
