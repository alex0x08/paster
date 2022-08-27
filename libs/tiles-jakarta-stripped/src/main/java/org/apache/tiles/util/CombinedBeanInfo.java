/*
 * $Id: CombinedBeanInfo.java 995228 2010-09-08 19:50:09Z apetrelli $
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
package org.apache.tiles.util;
import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tiles.request.reflect.ClassUtil;
/**
 * Contains the bean infos about one or more classes.
 *
 * @version $Rev: 995228 $ $Date: 2010-09-09 05:50:09 +1000 (Thu, 09 Sep 2010) $
 * @since 2.2.0
 */
public class CombinedBeanInfo {
    /**
     * The descriptors of the introspected classes.
     */
    private final List<FeatureDescriptor> descriptors;
    /**
     * Maps analyzed classes to the map of introspected properties.
     */
    private final Map<Class<?>, Map<String, PropertyDescriptor>> class2descriptors;
    /**
     * Constructor.
     *
     * @param clazzes The list of classes to analyze and combine.
     * @since 2.2.0
     */
    public CombinedBeanInfo(Class<?>... clazzes) {
        descriptors = new ArrayList<>();
        class2descriptors = new LinkedHashMap<>();
        for (Class<?> clazz : clazzes) {
            Map<String, PropertyDescriptor> mappedDescriptors = new LinkedHashMap<>();
            ClassUtil.collectBeanInfo(clazz, mappedDescriptors);
            descriptors.addAll(mappedDescriptors.values());
            class2descriptors.put(clazz, mappedDescriptors);
        }
    }
    /**
     * Returns the descriptors of all the introspected classes.
     *
     * @return The feature descriptors.
     * @since 2.2.0
     */
    public List<FeatureDescriptor> getDescriptors() {
        return descriptors;
    }

    /**
     * Returns the set of properties for the given introspected class.
     *
     * @param clazz The class to get the properties from.
     * @return The set of properties.
     * @since 2.2.0
     */
    public Set<String> getProperties(Class<?> clazz) {
        return class2descriptors.get(clazz).keySet();
    }
}
