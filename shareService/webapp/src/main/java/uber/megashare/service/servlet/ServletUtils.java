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
package uber.megashare.service.servlet;

import uber.megashare.base.LoggedClass;

/**
 * Мелкие функции облегчающие жизнь с сервлетами
 * @author alex
 */

public class ServletUtils extends LoggedClass{

    /**
	 * 
	 */
	private static final long serialVersionUID = -7132232740369786417L;

	protected ServletUtils() {}

    
   
  

    /**
     *
     * @param name имя или путь файла
     * @return расширение файла
     */
    public static String getExt(String name) {

       
        if (name == null) {
            return null;
        }
        int pos = name.lastIndexOf('/');

        String tmp = name;
        // skip ./filename combination
        if (pos != -1) {

            tmp = tmp.substring(pos);
            pos = tmp.lastIndexOf('.');
        } else {
            pos = tmp.lastIndexOf('.');
        }

        if (pos > 0) {
            return tmp.substring(pos);
        } else {
            return null;
        }

    }

   
  
}
