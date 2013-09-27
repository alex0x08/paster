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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.envers.Audited;
import uber.megashare.service.image.ImageBuilder;

/**
 * Аватарчик
 * @author alex
 * @since 3.0
 */
//@Embeddable кладет хер на lazy fetch почти во всех субд, увы
@Entity
@Table(name = "AVATARS")
@Audited
public class Avatar implements Serializable,IdentifiedObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4355488621046032004L;

	@Transient
    public static final String ICON_TYPE = "image/png";//все генерируемые изображния в PNG-формате

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(length=Integer.MAX_VALUE)
    private byte[] picture;

    @Lob
    @Column(length=Integer.MAX_VALUE)
    private byte[] icon;

    public Long getId() {
        return id;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon.clone();
    }

    public byte[] getPicture() {      
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture.clone();
    }

    public void loadFull() {
        getPicture();
        getIcon();
    }
    
    public static final Avatar EMPTY= fromResource("images/user_male.png");

    
    public static Avatar fromFile(File f) {
        try {
            return fromStream(new FileInputStream(f));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Avatar fromResource(String res) {

        InputStream in = Avatar.class.getClassLoader().getResourceAsStream(res);

        if (in == null) {
            /**
             * если поток = Null это значит что ресурс не был найден = косяк при сборке системы
             */
            throw new RuntimeException("Resource not found " + res);
            
        }
        return fromStream(in);
    }

    public static Avatar fromStream(InputStream in) {

        Avatar a = new Avatar();


        try {
            a.setPicture(ImageBuilder.createInstance()
                    .setSource(in)
                    .scaleToProfile().getScaledAsBytes());
            a.setIcon(ImageBuilder.createInstance()
                    .setSource(
                    a.getPicture())
                    .scaleToIcon().getScaledAsBytes());
            
        } catch (IOException ex) {
            /*
            нет никакого смысла объявлять это исключение (throws IOException) в заголовке функции , тк
            оно может появиться только из-за:
             *  99% - не найденного ресурса = ошибка при сборке = ошибка в программе
             *  1% - OutOfMemory -из-за перегрузки сервера
             *
             * 
             */
            throw new RuntimeException(ex);
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (Exception ee) {
                }
            }
            
       }
     return a;

    }    
}
