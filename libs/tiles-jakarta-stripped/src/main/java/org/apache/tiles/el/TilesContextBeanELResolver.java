/*
 * $Id: TilesContextBeanELResolver.java 1291847 2012-02-21 15:09:30Z nlebas $
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

import java.util.Map;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;

import org.apache.tiles.request.Request;

/**
 * Resolves beans in request, session and application scope.
 *
 * @version $Rev: 1291847 $ $Date: 2012-02-22 02:09:30 +1100 (Wed, 22 Feb 2012) $
 * @since 2.2.1
 */
public class TilesContextBeanELResolver extends ELResolver {

    /** {@inheritDoc} */
    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        // only resolve at the root of the context
        if (base != null) {
            return null;
        }

        return String.class;
    }
    /** {@inheritDoc} */
    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base != null) {
            return null;
        }

        Object obj = findObjectByProperty(context, property);
        if (obj != null) {
            context.setPropertyResolved(true);
            return obj.getClass();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base != null) {
            return null;
        }

        Object retValue = findObjectByProperty(context, property);

        if (retValue != null) {
            context.setPropertyResolved(true);
        }

        return retValue;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (context == null) {
            throw new NullPointerException();
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(ELContext context, Object base, Object property,
            Object value) {
        // Does nothing for the moment.
    }
    /**
     * Finds an object in request, session or application scope, in this order.
     *
     * @param context The context to use.
     * @param property The property used as an attribute name.
     * @return The found bean, if it exists, or <code>null</code> otherwise.
     * @since 2.2.1
     */
    protected Object findObjectByProperty(ELContext context, Object property) {
        Object retValue;

        Request request = (Request) context
                .getContext(Request.class);

        String prop = property.toString();

        String[] scopes = request.getAvailableScopes().toArray(new String[0]);
        int i = 0;
        do {
            retValue = getObject(request.getContext(scopes[i]), prop);
            i++;
        } while (retValue == null && i < scopes.length);

        return retValue;
    }

    /**
     * Returns an object from a map in a null-safe manner.
     *
     * @param map The map to use.
     * @param property The property to use as a key.
     * @return The object, if present, or <code>null</code> otherwise.
     * @since 2.2.1
     */
    protected Object getObject(Map<String, ?> map,
            String property) {
        Object retValue = null;
        if (map != null) {
            retValue = map.get(property);
        }
        return retValue;
    }
}
