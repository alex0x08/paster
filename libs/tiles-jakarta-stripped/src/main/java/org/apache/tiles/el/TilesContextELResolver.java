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
import org.apache.tiles.util.CombinedBeanInfo;

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
}
