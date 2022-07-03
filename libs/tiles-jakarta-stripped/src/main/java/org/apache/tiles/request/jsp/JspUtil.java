/*
 * $Id: JspUtil.java 1229087 2012-01-09 10:35:14Z mck $
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
package org.apache.tiles.request.jsp;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.PageContext;

import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;

/**
 * JSP utilities for JSP requests and related.
 *
 * @version $Rev: 1229087 $ $Date: 2012-01-09 21:35:14 +1100 (Mon, 09 Jan 2012) $
 */
public final class JspUtil {

    /**
     * Constructor.
     */
    private JspUtil() {
    }

    /**
     * Returns the application context. It must be
     * first saved creating an {@link ApplicationContext} and using
     * {@link org.apache.tiles.request.ApplicationAccess#register(ApplicationContext)}.
     *
     * @param jspContext The JSP context.
     * @return The application context.
     */
    public static ApplicationContext getApplicationContext(JspContext jspContext) {
        return (ApplicationContext) jspContext.getAttribute(
                ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE,
                PageContext.APPLICATION_SCOPE);
    }
}
