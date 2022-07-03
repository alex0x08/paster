/*
 * $Id: RequestWrapper.java 1306435 2012-03-28 15:39:11Z nlebas $
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
package org.apache.tiles.request;


/**
 * Delegate for ease of customization.
 *
 * @version $Rev: 1215002 $ $Date: 2011-12-16 01:27:17 +0100 (Fri, 16 Dec 2011) $
 */
public interface RequestWrapper extends Request {

    /**
     * Returns the wrapped Tiles request.
     *
     * @return The wrapped Tiles request.
     */
    Request getWrappedRequest();

}
