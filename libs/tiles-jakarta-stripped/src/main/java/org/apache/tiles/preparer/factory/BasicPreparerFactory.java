/*
 * $Id: BasicPreparerFactory.java 1310865 2012-04-07 21:01:22Z nlebas $
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
package org.apache.tiles.preparer.factory;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.RequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Default implementation of the {@link PreparerFactory}.
 * This factory provides no contextual configuration.  It
 * simply instantiates the named preparerInstance and returns it.
 *
 * @version $Rev: 1310865 $ $Date: 2012-04-08 07:01:22 +1000 (Sun, 08 Apr 2012) $
 * @since Tiles 2.0
 */
public class BasicPreparerFactory implements PreparerFactory {
    /**
     * The logging object.
     */
    private final Logger log = LoggerFactory
            .getLogger(BasicPreparerFactory.class);
    /**
     * Maps a preparer name to the instantiated preparer.
     */
    protected final Map<String, ViewPreparer> preparers;
    /**
     * Constructor.
     */
    public BasicPreparerFactory() {
        this.preparers = new HashMap<>();
    }
    /**
     * Create a new instance of the named preparerInstance.  This factory
     * expects all names to be qualified class names.
     *
     * @param name    the named preparerInstance
     * @param context current context
     * @return ViewPreparer instance
     */
    public ViewPreparer getPreparer(String name, Request context) {
        if (!preparers.containsKey(name))
            preparers.put(name, createPreparer(name));

        return preparers.get(name);
    }
    /**
     * Creates a view preparer for the given name.
     *
     * @param name The name of the preparer.
     * @return The created preparer.
     */
    protected ViewPreparer createPreparer(String name) {
        log.debug("Creating ViewPreparer '{}' . . .", name);
        Object instance = instantiate(name, true);
        log.debug("ViewPreparer created successfully");
        return (ViewPreparer) instance;
    }


    /*
     * Returns an instance of the given class name, by calling the default
     * constructor.
     *
     * @param className  The class name to load and to instantiate.
     * @param returnNull If <code>true</code>, if the class is not found it
     *                   returns <code>true</code>, otherwise it throws a
     *                   <code>TilesException</code>.
     * @return The new instance of the class name.
     * @throws CannotInstantiateObjectException If something goes wrong during
     *                                          instantiation.
     */
    public static Object instantiate(String className, boolean returnNull) {
        try {
            Class<?> namedClass = getClass(className, Object.class);
            return namedClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            if (returnNull)
                return null;

            throw new CannotInstantiateObjectException(
                    "Unable to resolve factory class: '%s'".formatted(className), e);
        } catch (IllegalAccessException e) {
            throw new CannotInstantiateObjectException(
                    "Unable to access factory class: '%s'".formatted(className), e);
        } catch (InstantiationException | NoSuchMethodException
                 | SecurityException | IllegalArgumentException |
                 InvocationTargetException e) {
            throw new CannotInstantiateObjectException(
                    "Unable to instantiate factory class: '%s'. Make sure that this class has a default constructor"
                            .formatted(className),
                    e);
        }
    }

    /**
     * Returns the class and casts it to the correct subclass.<br>
     * It tries to use the thread's current classloader first and, if it does
     * not succeed, uses the classloader of ClassUtil.
     *
     * @param <T>       The subclass to use.
     * @param className The name of the class to load.
     * @param baseClass The base class to subclass to.
     * @return The loaded class.
     * @throws ClassNotFoundException If the class has not been found.
     */
    public static <T> Class<? extends T> getClass(String className,
                                                  Class<T> baseClass) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        if (classLoader == null)
            classLoader = BasicPreparerFactory.class.getClassLoader();

        return Class.forName(className, true, classLoader)
                .asSubclass(baseClass);
    }
    /**
     * Indicates that an object cannot be instantiated.
     *
     * @version $Rev: 1306435 $ $Date: 2012-03-29 02:39:11 +1100 (Thu, 29 Mar 2012) $
     */
    public static class CannotInstantiateObjectException extends RequestException {

        /**
         * Constructor.
         *
         * @param message The detail message.
         * @param e       The exception to be wrapped.
         */
        public CannotInstantiateObjectException(String message, Throwable e) {
            super(message, e);
        }
    }

}
