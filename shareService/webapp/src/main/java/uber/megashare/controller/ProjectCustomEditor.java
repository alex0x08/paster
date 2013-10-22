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
package uber.megashare.controller;

import java.beans.PropertyEditorSupport;
import uber.megashare.model.Project;

/**
 * 
 * @author <a href="mailto:aachernyshev@it.ru">Alex Chernyshev</a>
 */
public class ProjectCustomEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        setValue(new Project(Long.valueOf(text)));
    }

    @Override
    public String getAsText() {
        return getValue() == null ? "" : ((Project) getValue()).getId().toString();
    }
}
