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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.UnresolvingLocaleDefinitionsFactory;
import org.apache.tiles.definition.dao.BaseLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.dao.DefinitionDAO;
import org.apache.tiles.definition.dao.ResolvingLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.definition.pattern.BasicPatternDefinitionResolver;
import org.apache.tiles.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.definition.pattern.PatternDefinitionResolverAware;
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
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.render.BasicRendererFactory;
import org.apache.tiles.request.render.ChainedDelegateRenderer;
import org.apache.tiles.request.render.DispatchRenderer;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.render.RendererFactory;
import org.apache.tiles.request.render.StringRenderer;
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
     * {@link StringRenderer}, {@link DispatchRenderer},
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
     * default, it registers delegates to {@link StringRenderer},
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
}
