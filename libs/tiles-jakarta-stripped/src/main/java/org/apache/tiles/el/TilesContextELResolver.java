/*
 * $Id: TilesContextELResolver.java 1049676 2010-12-15 19:38:54Z apetrelli $
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
package org.apache.tiles.el;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * Resolves properties of {@link Request} and
 * {@link ApplicationContext}.
 *
 * @version $Rev: 1049676 $ $Date: 2010-12-16 06:38:54 +1100 (Thu, 16 Dec 2010) $
 * @since 2.2.1
 */
public class TilesContextELResolver extends ELResolver {

    /**
     * Internal bean resolver to resolve beans in any context.
     */
    private final ELResolver beanElResolver;

    /**
     * Constructor.
     *
     * @param beanElResolver The used bean resolver.
     */
    public TilesContextELResolver(ELResolver beanElResolver) {
        this.beanElResolver = beanElResolver;
    }

    /**
     * The beaninfos about {@link Request} and {@link ApplicationContext}.
     */
    private final CombinedBeanInfo requestBeanInfo = new CombinedBeanInfo(
            Request.class, ApplicationContext.class);

    /** {@inheritDoc} */
    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        // only resolve at the root of the context
        return base != null ? null : String.class;
    }
    /** {@inheritDoc} */
    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        // only resolve at the root of the context
        if (base != null)
            return null;

        if (property==null)
            return null;


        Class<?> retValue = null;
        if (requestBeanInfo.getProperties(Request.class).contains(property.toString())) {
            Request request = (Request) context
                    .getContext(Request.class);
            retValue = beanElResolver.getType(context, request, property);
        } else if (requestBeanInfo.getProperties(ApplicationContext.class).contains(property.toString())) {
            ApplicationContext applicationContext = (ApplicationContext) context
                    .getContext(ApplicationContext.class);
            retValue = beanElResolver.getType(context, applicationContext, property);
        }

        if (retValue != null)
            context.setPropertyResolved(true);

        return retValue;
    }

    /** {@inheritDoc} */
    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        // only resolve at the root of the context
        if (base != null)
            return null;


        if (property==null)
            return null;


        Object retValue = null;

        if (requestBeanInfo.getProperties(Request.class).contains(property.toString())) {
            Request request = (Request) context
                    .getContext(Request.class);
            retValue = beanElResolver.getValue(context, request, property);
        } else if (requestBeanInfo.getProperties(ApplicationContext.class)
                .contains(property.toString())) {
            ApplicationContext applicationContext = (ApplicationContext) context
                    .getContext(ApplicationContext.class);
            retValue = beanElResolver.getValue(context, applicationContext, property);
        }

        if (retValue != null)
            context.setPropertyResolved(true);


        return retValue;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (context == null)
            throw new NullPointerException();


        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(ELContext context, Object base, Object property,
            Object value) {
        // Does nothing for the moment.
    }

    /**
     * Contains the bean infos about one or more classes.
     *
     * @version $Rev: 995228 $ $Date: 2010-09-09 05:50:09 +1000 (Thu, 09 Sep 2010) $
     * @since 2.2.0
     */
    public static class CombinedBeanInfo {
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
                collectBeanInfo(clazz, mappedDescriptors);
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

    static final Logger log = LoggerFactory.getLogger(TilesContextELResolver.class);

    /**
     * Collects bean infos from a class and filling a list.
     *
     * @param clazz           The class to be inspected.
     * @param name2descriptor The map in the form: name of the property ->
     *                        descriptor.
     */
    public static void collectBeanInfo(Class<?> clazz,
                                       Map<String, PropertyDescriptor> name2descriptor) {
        BeanInfo info = null;
        try {
            info = Introspector.getBeanInfo(clazz);
        } catch (Exception ex) {
            if (log.isDebugEnabled())
                log.debug("Cannot inspect class {}" ,clazz.getName(), ex);
        }
        if (info == null)
            return;

        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            pd.setValue("type", pd.getPropertyType());
            pd.setValue("resolvableAtDesignTime", Boolean.TRUE);
            name2descriptor.put(pd.getName(), pd);
        }
    }
}
