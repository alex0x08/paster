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
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Transient;
import org.apache.commons.codec.binary.Base64;
import uber.megashare.service.image.ImageBuilder;

/**
 * Аватарчик
 * @author alex
 * @since 3.0
 */
@Embeddable
public class Avatar implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4355488621046032004L;
    
    @Transient
    public static final String ICON_TYPE = "image/png";//все генерируемые изображния в PNG-формате
    
    //@Column(length=1024)
    @Lob
    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    
    public static Avatar fromFile(File f) {
        try {
            return fromStream(new FileInputStream(f));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

   

    public static Avatar fromStream(InputStream in) {

        
        try {
            Avatar a = new Avatar();

            a.setIcon(Base64.encodeBase64String(ImageBuilder.createInstance()
                    .setSource(in)
                    .scaleToIcon()
                    .getScaledAsBytes()));
            return a;
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
  

    }    

  
}
