/*
 * $Id: AbstractRequest.java 1375743 2012-08-21 20:05:58Z nlebas $
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
 * Base request.
 *
 * @version $Rev: 1375743 $ $Date: 2010-11-14 21:32:50 +0100 (dom, 14 nov 2010)$
 */
public abstract class AbstractRequest implements DispatchRequest {

    /**
     * Name of the attribute used to store the force-include option.
     *
     */
    public static final String FORCE_INCLUDE_ATTRIBUTE_NAME = AbstractRequest.class
            .getName() + ".FORCE_INCLUDE";

    /**
     * Sets the flag to force inclusion at next dispatch.
     *
     * @param forceInclude <code>true</code> means that, at the next dispatch, response
     * will be included and never forwarded.
     */
    protected void setForceInclude(boolean forceInclude) {
        getContext(REQUEST_SCOPE).put(FORCE_INCLUDE_ATTRIBUTE_NAME, forceInclude);
    }

    /**
     * Checks if, when dispatching to a resource, the result must be included
     * and not forwarded to.
     *
     * @return <code>true</code> if inclusion is forced.
     */
    protected boolean isForceInclude() {
        Boolean forceInclude = (Boolean) getContext(REQUEST_SCOPE).get(
                FORCE_INCLUDE_ATTRIBUTE_NAME);
        if (forceInclude != null) {
            return forceInclude;
        }
        return false;
    }
}
