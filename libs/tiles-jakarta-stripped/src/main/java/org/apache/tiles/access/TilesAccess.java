/*
 * $Id: TilesAccess.java 1044659 2010-12-11 14:16:04Z apetrelli $
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
package org.apache.tiles.access;
import java.util.Map;

import org.apache.tiles.NoSuchContainerException;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Provides static access to the tiles container.
 *
 * @version $Rev: 1044659 $ $Date: 2010-12-12 01:16:04 +1100 (Sun, 12 Dec 2010) $
 */
public final class TilesAccess {
    /**
     * Name of the attribute used to store the current used container.
     */
    public static final String CURRENT_CONTAINER_ATTRIBUTE_NAME =
            "org.apache.tiles.servlet.context.ServletTilesRequestContext.CURRENT_CONTAINER_KEY";
    /**
     * Constructor, private to avoid instantiation.
     */
    private TilesAccess() {
    }
    /**
     * The name of the attribute to use when getting and setting the container
     * object in a context.
     */
    public static final String CONTAINER_ATTRIBUTE =
            "org.apache.tiles.CONTAINER";
    /**
     * Configures the container to be used in the application.
     *
     * @param context   The Tiles application context object to use.
     * @param container The container object to set.
     * @param key       The key under which the container will be stored.
     * @since 2.1.2
     */
    public static void setContainer(ApplicationContext context,
                                    TilesContainer container, String key) {
        Logger log = LoggerFactory.getLogger(TilesAccess.class);
        if (key == null)
            key = CONTAINER_ATTRIBUTE;

        if (container == null) {
            if (log.isInfoEnabled())
                log.info("Removing TilesContext for context: %s".formatted(context.getClass().getName()));

            context.getApplicationScope().remove(key);
        } else {
            if (log.isInfoEnabled())
                log.info("Publishing TilesContext for context: %s"
                        .formatted(context.getClass().getName()));

            context.getApplicationScope().put(key, container);
        }
    }
    /**
     * Returns default the container to be used in the application.
     *
     * @param context The Tiles application context object to use.
     * @return The default container object.
     * @since 3.0.0
     */
    public static TilesContainer getContainer(ApplicationContext context) {
        return getContainer(context, CONTAINER_ATTRIBUTE);
    }
    /**
     * Returns the container to be used in the application registered under a specific key.
     *
     * @param context The Tiles application context object to use.
     * @param key     The key under which the container will be stored.
     * @return The container object.
     * @since 3.0.0
     */
    public static TilesContainer getContainer(ApplicationContext context,
                                              String key) {
        if (key == null)
            key = CONTAINER_ATTRIBUTE;

        return (TilesContainer) context.getApplicationScope().get(key);
    }
    /**
     * Sets the current container to use in web pages.
     *
     * @param request The request to use.
     * @param key     The key under which the container is stored.
     * @since 2.1.0
     */
    public static void setCurrentContainer(Request request,
                                           String key) {
        ApplicationContext applicationContext = request.getApplicationContext();
        TilesContainer container = getContainer(applicationContext, key);
        if (container != null)
            request.getContext("request").put(CURRENT_CONTAINER_ATTRIBUTE_NAME, container);
         else
            throw new NoSuchContainerException("The container with the key '%s' cannot be found".formatted(key));

    }
    /**
     * Returns the current container that has been set, or the default one.
     *
     * @param request The request to use.
     * @return The current Tiles container to use in web pages.
     * @since 2.1.0
     */
    public static TilesContainer getCurrentContainer(Request request) {
        ApplicationContext context = request.getApplicationContext();
        Map<String, Object> requestScope = request.getContext("request");
        TilesContainer container = (TilesContainer) requestScope.get(CURRENT_CONTAINER_ATTRIBUTE_NAME);
        if (container == null) {
            container = getContainer(context);
            requestScope.put(CURRENT_CONTAINER_ATTRIBUTE_NAME, container);
        }
        return container;
    }
}
