/*
 * $Id: NoSuchRendererException.java 1306435 2012-03-28 15:39:11Z nlebas $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.render;


/**
 * It is raised when a named renderer has not been found with that name.
 *
 * @version $Rev: 1306435 $ $Date: 2012-03-29 02:39:11 +1100 (Thu, 29 Mar 2012) $
 */
public class NoSuchRendererException extends RenderException {

    /**
     * Constructor.
     */
    public NoSuchRendererException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param message The detail message.
     * @param e The exception to be wrapped.
     */
    public NoSuchRendererException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * Constructor.
     *
     * @param message The detail message.
     */
    public NoSuchRendererException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param e The exception to be wrapped.
     */
    public NoSuchRendererException(Throwable e) {
        super(e);
    }

}
