/*
 * $Id: HasRemovableKeys.java 1064782 2011-01-28 17:08:52Z apetrelli $
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
package org.apache.tiles.request.attribute;

/**
 * Allows to get and remove attributes.
 *
 * @version $Rev: 1064782 $ $Date: 2011-01-29 04:08:52 +1100 (Sat, 29 Jan 2011) $
 * @param <V> The type of the value of the attribute.
 */
public interface HasRemovableKeys<V> extends HasKeys<V> {

    /**
     * Removes an attribute.
     *
     * @param key The key of the attribute to remove.
     */
    void removeValue(String key);
}