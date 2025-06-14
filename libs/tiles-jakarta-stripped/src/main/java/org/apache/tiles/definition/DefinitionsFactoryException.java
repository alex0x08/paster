/*
 * $Id: DefinitionsFactoryException.java 942880 2010-05-10 19:58:07Z apetrelli $
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
package org.apache.tiles.definition;
import org.apache.tiles.TilesException;
/**
 * Exception thrown when an error occurs while the impl tries to
 * create a new instance mapper.
 *
 * @version $Rev: 942880 $ $Date: 2010-05-11 05:58:07 +1000 (Tue, 11 May 2010) $
 */
public class DefinitionsFactoryException extends TilesException {
    /**
     * Constructor.
     *
     * @param message The error or warning message.
     */
    public DefinitionsFactoryException(String message) {
        super(message);
    }
    /**
     * Create a new <code>DefinitionsFactoryException</code> from an existing exception.
     * <p/>
     * <p>The existing exception will be embedded in the new
     * one, but the new exception will have its own message.</p>
     *
     * @param message The detail message.
     * @param e       The exception to be wrapped.
     */
    public DefinitionsFactoryException(String message, Throwable e) {
        super(message, e);
    }
}
