/*
 * $Id: ScopeELResolver.java 1049676 2010-12-15 19:38:54Z apetrelli $
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
 * @version $Rev: 1049676 $ $Date: 2010-12-16 06:38:54 +1100 (Thu, 16 Dec 2010) $
 * @since 2.2.1
 */
public class ScopeELResolver extends ELResolver {
    /**
     * The length of the suffix: "Scope".
     */
    private static final int SUFFIX_LENGTH = 5;
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        // only resolve at the root of the context
        return base != null ? null : Map.class;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        return base != null || !(property instanceof String s)
                || !s.endsWith("Scope") ? null : Map.class;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base != null)
            return null;

        Object retValue = null;
        String propertyString = (String) property;
        if (property != null && propertyString.endsWith("Scope")) {
            Request request = (Request) context
                    .getContext(Request.class);
            retValue = request.getContext(propertyString.substring(0,
                    propertyString.length() - SUFFIX_LENGTH));
        }
        if (retValue != null)
            context.setPropertyResolved(true);

        return retValue;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (context == null)
            throw new NullPointerException();

        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(ELContext context, Object base, Object property,
                         Object value) {
        // Does nothing for the moment.
    }
}
