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
package uber.megashare.base.logging;

import org.apache.commons.lang.builder.ToStringBuilder;
import uber.megashare.base.LoggedClass;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 08.03.13
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public abstract class LoggedCall<T> extends LoggedClass {

    public abstract T callImpl(ToStringBuilder log) throws Exception;

    public T call() {
        ToStringBuilder log = getNewProtocolBuilder();
        Exception exception=null;
        try {
            return callImpl(log);
        }  catch (Exception e) {
            exception=e;
            return null;
        } finally {
            if (exception!=null) {
                getLogger().error(log.toString());
                getLogger().error(exception.getLocalizedMessage(),exception);
            } else if(getLogger().isDebugEnabled()) {
                getLogger().debug(log.toString());
            }

        }
    }
}


