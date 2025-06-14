/*
 * $Id: CompareUtil.java 787720 2009-06-23 15:39:21Z apetrelli $
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
import java.util.Objects;
/**
 * Utilities to work with comparation between objects.
 *
 * @version $Rev: 787720 $ $Date: 2009-06-24 01:39:21 +1000 (Wed, 24 Jun 2009) $
 * @since 2.2.0
 */
public final class CompareUtil {

    /**
     * Private constructor to avoid instantiation.
     */
    private CompareUtil() { }

    /**
     * Checks if two objects (eventually null) are the same. They are considered the same
     * even if they are both null.
     *
     * @param obj1 The first object to check.
     * @param obj2 The second object to check.
     * @return <code>true</code> if the objects are the same.
     * @since 2.2.0
     */
    public static boolean nullSafeEquals(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }

    /**
     * Returns <code>0</code> if the object is null, the hash code of the object
     * otherwise.
     *
     * @param obj The object from which the hash code must be calculated..
     * @return The hash code.
     * @since 2.2.0
     */
    public static int nullSafeHashCode(Object obj) {
        return obj != null ? obj.hashCode() : 0;
    }
}
