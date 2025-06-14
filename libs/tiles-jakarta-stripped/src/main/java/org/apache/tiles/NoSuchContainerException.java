/*
 * $Id: NoSuchContainerException.java 927573 2010-03-25 20:01:07Z apetrelli $
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
package org.apache.tiles;


/**
 * Indicates that a keyed container has not been found.
 *
 * @version $Rev: 927573 $ $Date: 2010-03-26 07:01:07 +1100 (Fri, 26 Mar 2010) $
 * @since 2.1.0
 */
public class NoSuchContainerException extends TilesException {
    /**
     * Constructor.
     *
     * @param message The detail message.
     * @since 2.1.0
     */
    public NoSuchContainerException(String message) {
        super(message);
    }
}
