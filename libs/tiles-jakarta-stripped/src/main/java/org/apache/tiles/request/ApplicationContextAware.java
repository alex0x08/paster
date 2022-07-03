/*
 * $Id: ApplicationContextAware.java 1306435 2012-03-28 15:39:11Z nlebas $
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
 * It represents an object that can have a reference to the
 * {@link ApplicationContext}.
 *
 * @version $Rev: 893303 $ $Date: 2009-12-22 21:18:35 +0100 (Tue, 22 Dec 2009) $
 */
public interface ApplicationContextAware {

    /**
     * Sets the Tiles application context.
     *
     * @param applicationContext The Tiles application context.
     */
    void setApplicationContext(ApplicationContext applicationContext);
}
