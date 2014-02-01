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
package uber.megashare.base;

import java.io.Serializable;
import org.apache.commons.lang.builder.StandardToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Каждый потомок этого класса использует логгер
 *
 *
 * @author alex
 */
public class LoggedClass implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2163919862005646481L;
    /**
     * инстанс логгера из slf4, название берется из класса потомка
     */
    private transient Logger log;

    protected LoggedClass() {
        log = LoggerFactory.getLogger(getClass().getName());
    }

    protected LoggedClass(Class<?> c) {
        log =  LoggerFactory.getLogger(c);
    }

    public Logger getLogger() {
        return log;
    }
   
    public void logDebug(String msg) {

        if (log.isDebugEnabled()) {
            log.debug(msg);
        }
    }

    public void logDebug(Exception e) {

        if (log.isDebugEnabled()) {
            log.debug(e.getLocalizedMessage(), e);
        }
    }

    public ToStringBuilder getNewProtocolBuilder() {
        return new ToStringBuilder(this, style);
    }

    private static final transient StandardToStringStyle style = new StandardToStringStyle();

    static {
        style.setFieldSeparator(", ");
        style.setUseClassName(false);
        style.setUseIdentityHashCode(false);
    }


    private static final LoggedClass INSTANCE = new LoggedClass();

    public static LoggedClass getStaticInstance() {
        return INSTANCE;
    }

    public static LoggedClass newInstance(Class<?> c) {
        return new LoggedClass(c);
    }


}

