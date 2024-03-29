/*
 * $Id: RefreshMonitor.java 666834 2008-06-11 20:49:05Z apetrelli $
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
/**
 * Implementing this interface means that the object monitors the sources it
 * uses to check when they change.
 *
 * @version $Rev: 666834 $ $Date: 2008-06-12 06:49:05 +1000 (Thu, 12 Jun 2008) $
 * @since 2.1.0
 */
public interface RefreshMonitor {
    /**
     * Indicates whether the sources are out of date and need to be reloaded.
     *
     * @return <code>true</code> if the sources need to be refreshed.
     * @since 2.1.0
     */
    boolean refreshRequired();
}
