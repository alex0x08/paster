/*
 * $Id: BasicTilesContainerFactory.java 1310865 2012-04-07 21:01:22Z nlebas $
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
package org.apache.tiles.factory;
import java.io.IOException;
import java.util.*;

import org.apache.tiles.Definition;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.dao.BaseLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.dao.DefinitionDAO;
import org.apache.tiles.definition.dao.ResolvingLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.definition.pattern.*;
import org.apache.tiles.definition.pattern.wildcard.WildcardDefinitionPatternMatcherFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.locale.impl.DefaultLocaleResolver;
import org.apache.tiles.preparer.factory.BasicPreparerFactory;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.renderer.DefinitionRenderer;
import org.apache.tiles.request.*;
import org.apache.tiles.request.render.*;

/**
 * Factory that builds a standard Tiles container using only Java code.
 *
 * @version $Rev: 1310865 $ $Date: 2012-04-08 07:01:22 +1000 (Sun, 08 Apr 2012) $
 * @since 2.1.0
 */
public class BasicTilesContainerFactory extends AbstractTilesContainerFactory {
    /**
     * The string renderer name.
     */
    protected static final String STRING_RENDERER_NAME = "string";
    /**
     * The template renderer name.
     */
    protected static final String TEMPLATE_RENDERER_NAME = "template";
    /**
     * The definition renderer name.
     */
    protected static final String DEFINITION_RENDERER_NAME = "definition";
    /**
     * {@inheritDoc}
     */
    @Override
    public TilesContainer createContainer(ApplicationContext applicationContext) {
        BasicTilesContainer container = instantiateContainer();
        container.setApplicationContext(applicationContext);
        LocaleResolver resolver = createLocaleResolver(applicationContext);
        container.setDefinitionsFactory(createDefinitionsFactory(applicationContext,
                resolver));
        AttributeEvaluatorFactory attributeEvaluatorFactory = createAttributeEvaluatorFactory(
                applicationContext, resolver);
        container.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
        container.setPreparerFactory(createPreparerFactory(applicationContext));
        TilesContainer injectedContainer = createDecoratedContainer(container, applicationContext);
        container.setRendererFactory(createRendererFactory(
                injectedContainer));
        return injectedContainer;
    }
    /**
     * Instantiate the container, without initialization.
     *
     * @return The instantiated container.
     * @since 2.1.1
     */
    protected BasicTilesContainer instantiateContainer() {
        return new BasicTilesContainer();
    }
    /**
     * Instantiate the container that will be injected to child objects.
     *
     * @param originalContainer The original instantiated container.
     * @param context           The Tiles application context object.
     * @return The instantiated container.
     * @since 3.0.0
     */
    protected TilesContainer createDecoratedContainer(TilesContainer originalContainer,
                                                      ApplicationContext context) {
        return originalContainer;
    }
    /**
     * Creates the definitions factory. By default, it creates a
     * {@link UnresolvingLocaleDefinitionsFactory} with default dependencies.
     *
     * @param applicationContext The Tiles application context.
     * @param resolver           The locale resolver.
     * @return The definitions factory.
     * @since 2.1.1
     */
    protected DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext,
                                                          LocaleResolver resolver) {
        UnresolvingLocaleDefinitionsFactory factory = instantiateDefinitionsFactory(
        );
        factory.setLocaleResolver(resolver);
        factory.setDefinitionDAO(createLocaleDefinitionDao(applicationContext,
                resolver));
        return factory;
    }
    /**
     * Instantiate a new definitions factory based on Locale.
     *
     * @return The definitions factory.
     * @since 2.2.1
     */
    protected UnresolvingLocaleDefinitionsFactory instantiateDefinitionsFactory() {
        return new UnresolvingLocaleDefinitionsFactory();
    }
    /**
     * Instantiate (and does not initialize) a Locale-based definition DAO.
     *
     * @param applicationContext The Tiles application context.
     * @param resolver           The locale resolver.
     * @return The definition DAO.
     * @since 2.1.1
     */
    protected BaseLocaleUrlDefinitionDAO instantiateLocaleDefinitionDao(ApplicationContext applicationContext,
                                                                        LocaleResolver resolver) {
        return new ResolvingLocaleUrlDefinitionDAO(applicationContext);
    }
    /**
     * Creates a Locale-based definition DAO.
     *
     * @param applicationContext The Tiles application context.
     * @param resolver           The locale resolver.
     * @return The definition DAO.
     * @since 2.1.1
     */
    @SuppressWarnings("unchecked")
    protected DefinitionDAO<Locale> createLocaleDefinitionDao(ApplicationContext applicationContext,
                                                              LocaleResolver resolver) {
        BaseLocaleUrlDefinitionDAO definitionDao = instantiateLocaleDefinitionDao(
                applicationContext, resolver);
        definitionDao.setReader(createDefinitionsReader(applicationContext));
        definitionDao.setSources(getSources(applicationContext));
        if (definitionDao instanceof PatternDefinitionResolverAware) {
            ((PatternDefinitionResolverAware<Locale>) definitionDao)
                    .setPatternDefinitionResolver(createPatternDefinitionResolver());
        }
        return definitionDao;
    }
    /**
     * Creates the locale resolver. By default it creates a
     * {@link DefaultLocaleResolver}.
     *
     * @param applicationContext The Tiles application context.
     * @return The locale resolver.
     * @since 2.1.1
     */
    protected LocaleResolver createLocaleResolver(ApplicationContext applicationContext) {
        return new DefaultLocaleResolver();
    }
    /**
     * Creates the definition's reader. By default, it creates a
     * {@link DigesterDefinitionsReader}.
     *
     * @param applicationContext The Tiles application context.
     * @return The definitions reader.
     * @since 2.1.1
     */
    protected DefinitionsReader createDefinitionsReader(
            ApplicationContext applicationContext) {
        return new DigesterDefinitionsReader();
    }
    /**
     * Returns a list containing the resources to be parsed. By default, it returns a
     * list containing the resource at "/WEB-INF/tiles.xml".
     *
     * @param applicationContext The Tiles application context.
     * @return The resources.
     * @since 2.1.1
     */
    protected List<ApplicationResource> getSources(ApplicationContext applicationContext) {
        List<ApplicationResource> retValue = new ArrayList<>(1);
        retValue.add(applicationContext.getResource("/WEB-INF/tiles.xml"));
        return retValue;
    }
    /**
     * Creates the attribute evaluator factory to use. By default it returns a
     * {@link BasicAttributeEvaluatorFactory} containing the
     * {@link DirectAttributeEvaluator} as the default evaluator.
     *
     * @param applicationContext The Tiles application context.
     * @param resolver           The locale resolver.
     * @return The evaluator factory.
     * @since 2.2.0
     */
    protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(
            ApplicationContext applicationContext,
            LocaleResolver resolver) {
        return new BasicAttributeEvaluatorFactory(new DirectAttributeEvaluator());
    }
    /**
     * Creates the preparer factory to use. By default it returns a
     * {@link BasicPreparerFactory}.
     *
     * @param applicationContext The Tiles application context.
     * @return The preparer factory.
     * @since 2.1.1
     */
    protected PreparerFactory createPreparerFactory(ApplicationContext applicationContext) {
        return new BasicPreparerFactory();
    }
    /**
     * Creates a renderer factory. By default it returns a
     * {@link BasicRendererFactory}, composed of an
     * {link UntypedAttributeRenderer} as default, and delegates of
     * {link StringRenderer}, {@link DispatchRenderer},
     * {@link DefinitionRenderer}.
     *
     * @param container                 The container.
     * @return The renderer factory.
     * @since 2.2.0
     */
    protected RendererFactory createRendererFactory(TilesContainer container) {
        BasicRendererFactory retValue = new BasicRendererFactory();
        registerAttributeRenderers(retValue, container
        );
        retValue.setDefaultRenderer(createDefaultAttributeRenderer(retValue
        ));
        return retValue;
    }
    /**
     * Creates the default attribute renderer. By default it is an
     * {@link ChainedDelegateRenderer}.
     *
     * @param rendererFactory           The renderer factory to configure.
     * @return The default attribute renderer.
     * @since 3.0.0
     */
    protected Renderer createDefaultAttributeRenderer(
            BasicRendererFactory rendererFactory) {
        ChainedDelegateRenderer retValue = new ChainedDelegateRenderer();
        retValue.addAttributeRenderer(rendererFactory.getRenderer(DEFINITION_RENDERER_NAME));
        retValue.addAttributeRenderer(rendererFactory.getRenderer(TEMPLATE_RENDERER_NAME));
        retValue.addAttributeRenderer(rendererFactory.getRenderer(STRING_RENDERER_NAME));
        return retValue;
    }
    /**
     * Creates a new pattern definition resolver. By default, it instantiate a
     * {@link BasicPatternDefinitionResolver} with
     * {@link WildcardDefinitionPatternMatcherFactory} to manage wildcard
     * substitution.
     *
     * @param <T>                   The type of the customization key.
     * @return The pattern definition resolver.
     * @since 2.2.0
     */
    protected <T> PatternDefinitionResolver<T> createPatternDefinitionResolver() {
        WildcardDefinitionPatternMatcherFactory definitionPatternMatcherFactory =
                new WildcardDefinitionPatternMatcherFactory();
        return new BasicPatternDefinitionResolver<>(
                definitionPatternMatcherFactory,
                definitionPatternMatcherFactory);
    }
    /**
     * Registers attribute renderers in a {@link BasicRendererFactory}. By
     * default, it registers delegates to {link StringRenderer},
     * {@link DispatchRenderer} and {@link DefinitionRenderer}.
     *
     * @param rendererFactory           The renderer factory to configure.
     * @param container                 The container.
     * @since 2.2.0
     */
    protected void registerAttributeRenderers(
            BasicRendererFactory rendererFactory,
            TilesContainer container) {
        rendererFactory.registerRenderer(STRING_RENDERER_NAME,
                createStringAttributeRenderer(
                ));
        rendererFactory.registerRenderer(TEMPLATE_RENDERER_NAME,
                createTemplateAttributeRenderer(
                ));
        rendererFactory.registerRenderer(DEFINITION_RENDERER_NAME,
                createDefinitionAttributeRenderer(
                        container));
    }
    /**
     * Creates an attribute renderer to render strings.
     *
     * @return The renderer.
     * @since 3.0.0
     */
    protected Renderer createStringAttributeRenderer() {
        return new StringRenderer();
    }
    /**
     * Creates a {link AttributeRenderer} that uses a {@link DispatchRenderer}.
     *
     * @return The renderer.
     * @since 2.2.1
     */
    protected Renderer createTemplateAttributeRenderer() {
        return new DispatchRenderer();
    }
    /**
     * Creates a {link AttributeRenderer} using a {@link DefinitionRenderer}.
     *
     * @param container                 The container.
     * @return The renderer.
     * @since 3.0.0
     */
    protected Renderer createDefinitionAttributeRenderer(
            TilesContainer container) {
        return new DefinitionRenderer(container);
    }

    /**
     * {@link DefinitionsFactory DefinitionsFactory} implementation that manages
     * Definitions configuration data from URLs, without resolving definition
     * inheritance when a definition is returned.<p/>
     * <p>
     * The Definition objects are read from the
     * {@link org.apache.tiles.definition.digester.DigesterDefinitionsReader DigesterDefinitionsReader}
     * class unless another implementation is specified.
     * </p>
     *
     * @version $Rev: 891884 $ $Date: 2009-12-18 07:43:12 +1100 (Fri, 18 Dec 2009) $
     * @since 2.2.1
     */
    public static class UnresolvingLocaleDefinitionsFactory implements DefinitionsFactory {
        /**
         * The definition DAO that extracts the definitions from the sources.
         *
         * @since 2.2.1
         */
        protected DefinitionDAO<Locale> definitionDao;
        /**
         * The locale resolver object.
         *
         * @since 2.2.1
         */
        protected LocaleResolver localeResolver;
        /**
         * Sets the locale resolver to use.
         *
         * @param localeResolver The locale resolver.
         * @since 2.2.1
         */
        public void setLocaleResolver(LocaleResolver localeResolver) {
            this.localeResolver = localeResolver;
        }
        /**
         * Sets the definition DAO to use. It must be locale-based.
         *
         * @param definitionDao The definition DAO.
         * @since 2.2.1
         */
        public void setDefinitionDAO(DefinitionDAO<Locale> definitionDao) {
            this.definitionDao = definitionDao;
        }
        /**
         * {@inheritDoc}
         */
        public Definition getDefinition(String name,
                                        Request tilesContext) {
            Locale locale = null;
            if (tilesContext != null)
                locale = localeResolver.resolveLocale(tilesContext);

            return definitionDao.getDefinition(name, locale);
        }
    }

    /**
     * Renders an attribute that has no associated renderer using delegation to
     * other renderers.
     *
     * @version $Rev: 1306435 $ $Date: 2012-03-29 02:39:11 +1100 (Thu, 29 Mar 2012) $
     */
    public static class ChainedDelegateRenderer implements Renderer {
        /**
         * The list of chained renderers.
         */
        private final List<Renderer> renderers;
        /**
         * Constructor.
         */
        public ChainedDelegateRenderer() {
            renderers = new ArrayList<>();
        }
        /**
         * Adds an attribute renderer to the list. The first inserted this way, the
         * first is checked when rendering.
         *
         * @param renderer The renderer to add.
         */
        public void addAttributeRenderer(Renderer renderer) {
            renderers.add(renderer);
        }
        @Override
        public void render(String value, Request request) throws IOException {
            if (value == null)
                throw new NullPointerException("The attribute value is null");

            for (Renderer renderer : renderers)
                if (renderer.isRenderable(value, request)) {
                    renderer.render(value, request);
                    return;
                }

            throw new CannotRenderException("Cannot renderer value '%s'".formatted(value));
        }
        /**
         * {@inheritDoc}
         */
        public boolean isRenderable(String value, Request request) {
            for (Renderer renderer : renderers)
                if (renderer.isRenderable(value, request))
                    return true;


            return false;
        }
    }
    /**
     * Renders an attribute that contains a string.
     *
     * @version $Rev: 1215008 $ $Date: 2011-12-16 11:31:49 +1100 (Fri, 16 Dec 2011) $
     */
    public static class StringRenderer implements Renderer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void render(String value, Request request) throws IOException {
            if (value == null)
                throw new CannotRenderException("Cannot render a null string");

            request.getWriter().write(value);
        }
        /**
         * {@inheritDoc}
         */
        public boolean isRenderable(String value, Request request) {
            return value != null;
        }
    }

    /**
     * Renders an attribute that contains a reference to a template.
     *
     * @version $Rev: 1375743 $ $Date: 2012-08-22 06:05:58 +1000 (Wed, 22 Aug 2012) $
     */
    public static class DispatchRenderer implements Renderer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void render(String path, Request request) throws IOException {
            if (path == null)
                throw new CannotRenderException("Cannot dispatch a null path");

            DispatchRequest dispatchRequest = getDispatchRequest(request);
            if (dispatchRequest == null)
                throw new CannotRenderException("Cannot dispatch outside of a web environment");

            dispatchRequest.dispatch(path);
        }
        /**
         * {@inheritDoc}
         */
        public boolean isRenderable(String path, Request request) {
            return path != null && getDispatchRequest(request) != null && path.startsWith("/");
        }
        private DispatchRequest getDispatchRequest(Request request) {
            Request result = request;
            while (!(result instanceof DispatchRequest) && result instanceof RequestWrapper rw)
                result =rw.getWrappedRequest();

            if (!(result instanceof DispatchRequest))
                result = null;

            return (DispatchRequest) result;
        }
    }
    /**
     * A pattern definition resolver that stores {@link DefinitionPatternMatcher}
     * separated by customization key. <br>
     * It delegates creation of definition pattern matchers to a
     * {@link DefinitionPatternMatcherFactory} and recgnizes patterns through the
     * use of a {@link PatternRecognizer}.
     *
     * @param <T> The type of the customization key.
     * @version $Rev: 836180 $ $Date: 2009-11-15 01:00:02 +1100 (Sun, 15 Nov 2009) $
     * @since 2.2.0
     */
    public static class BasicPatternDefinitionResolver<T> extends
            AbstractPatternDefinitionResolver<T> {
        /**
         * The factory of pattern matchers.
         */
        private final DefinitionPatternMatcherFactory definitionPatternMatcherFactory;
        /**
         * The pattern recognizer.
         */
        private final PatternRecognizer patternRecognizer;
        /**
         * Constructor.
         *
         * @param definitionPatternMatcherFactory The definition pattern matcher factory.
         * @param patternRecognizer               The pattern recognizer.
         */
        public BasicPatternDefinitionResolver(DefinitionPatternMatcherFactory definitionPatternMatcherFactory,
                                              PatternRecognizer patternRecognizer) {
            this.definitionPatternMatcherFactory = definitionPatternMatcherFactory;
            this.patternRecognizer = patternRecognizer;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected Map<String, Definition> addDefinitionsAsPatternMatchers(List<DefinitionPatternMatcher> matchers,
                                                                          Map<String, Definition> defsMap) {
            Set<String> excludedKeys = new LinkedHashSet<>();
            for (Map.Entry<String, Definition> de : defsMap.entrySet()) {
                String key = de.getKey();
                if (patternRecognizer.isPatternRecognized(key))
                    matchers.add(definitionPatternMatcherFactory
                            .createDefinitionPatternMatcher(key, de.getValue()));
                else
                    excludedKeys.add(key);

            }
            return PatternUtil.createExtractedMap(defsMap, excludedKeys);
        }
    }


}
