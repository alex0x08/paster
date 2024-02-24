/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.servlet.view.tiles3;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.el.ArrayELResolver;
import jakarta.el.BeanELResolver;
import jakarta.el.CompositeELResolver;
import jakarta.el.ListELResolver;
import jakarta.el.MapELResolver;
import jakarta.el.ResourceBundleELResolver;
import jakarta.servlet.ServletContext;
import jakarta.servlet.jsp.JspFactory;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.dao.BaseLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.el.ELAttributeEvaluator;
import org.apache.tiles.el.ScopeELResolver;
import org.apache.tiles.el.TilesContextBeanELResolver;
import org.apache.tiles.el.TilesContextELResolver;
import org.apache.tiles.evaluator.AttributeEvaluator;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationContextAware;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.startup.DefaultTilesInitializer;
import org.apache.tiles.startup.TilesInitializer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ServletContextAware;
/**
 * Helper class to configure Tiles 3.x for the Spring Framework. See
 * <a href="https://tiles.apache.org">https://tiles.apache.org</a>
 * for more information about Tiles, which basically is a templating mechanism
 * for web applications using JSPs and other template engines.
 *
 * <p>The TilesConfigurer simply configures a TilesContainer using a set of files
 * containing definitions, to be accessed by {@link TilesView} instances. This is a
 * Spring-based alternative (for usage in Spring configuration) to the Tiles-provided
 * {@code ServletContextListener}
 * (e.g. {link org.apache.tiles.extras.complete.CompleteAutoloadTilesListener}
 * for usage in {@code web.xml}.
 *
 * <p>TilesViews can be managed by any {@link org.springframework.web.servlet.ViewResolver}.
 * For simple convention-based view resolution, consider using {@link TilesViewResolver}.
 *
 * <p>A typical TilesConfigurer bean definition looks as follows:
 * <pre class="code">
 * &lt;bean id="tilesConfigurer" class="org.springframework.web.servlet.view.tiles3.TilesConfigurer"&gt;
 *   &lt;property name="definitions"&gt;
 *     &lt;list&gt;
 *       &lt;value&gt;/WEB-INF/defs/general.xml&lt;/value&gt;
 *       &lt;value&gt;/WEB-INF/defs/widgets.xml&lt;/value&gt;
 *       &lt;value&gt;/WEB-INF/defs/administrator.xml&lt;/value&gt;
 *       &lt;value&gt;/WEB-INF/defs/customer.xml&lt;/value&gt;
 *       &lt;value&gt;/WEB-INF/defs/templates.xml&lt;/value&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * <p>
 * The values in the list are the actual Tiles XML files containing the definitions.
 * If the list is not specified, the default is {@code "/WEB-INF/tiles.xml"}.
 *
 * <p>Note that in Tiles 3 an underscore in the name of a file containing Tiles
 * definitions is used to indicate locale information, for example:
 * <p>
 * <pre class="code">
 * &lt;bean id="tilesConfigurer" class="org.springframework.web.servlet.view.tiles3.TilesConfigurer"&gt;
 *   &lt;property name="definitions"&gt;
 *     &lt;list&gt;
 *       &lt;value&gt;/WEB-INF/defs/tiles.xml&lt;/value&gt;
 *       &lt;value&gt;/WEB-INF/defs/tiles_fr_FR.xml&lt;/value&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * @author mick semb wever
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @see TilesView
 * @see TilesViewResolver
 * @since 3.2
 */
public class TilesConfigurer implements ServletContextAware, InitializingBean, DisposableBean {
    private static final boolean tilesElPresent =
            ClassUtils.isPresent("org.apache.tiles.el.ELAttributeEvaluator",
                    TilesConfigurer.class.getClassLoader());
    @Nullable
    private TilesInitializer tilesInitializer;
    @Nullable
    private String[] definitions;
    @Nullable
    private final Class<? extends DefinitionsFactory> definitionsFactoryClass;
    @Nullable
    private final Class<? extends PreparerFactory> preparerFactoryClass;
    @Nullable
    private ServletContext servletContext;

    public TilesConfigurer(@Nullable Class<? extends DefinitionsFactory> definitionsFactoryClass, @Nullable Class<? extends PreparerFactory> preparerFactoryClass) {
        this.definitionsFactoryClass = definitionsFactoryClass;
        this.preparerFactoryClass = preparerFactoryClass;
    }

    /**
     * Set the Tiles definitions, i.e. the list of files containing the definitions.
     * Default is "/WEB-INF/tiles.xml".
     */
    public void setDefinitions(String... definitions) {
        this.definitions = definitions;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    /**
     * Creates and exposes a TilesContainer for this web application,
     * delegating to the TilesInitializer.
     *
     * @throws TilesException in case of setup failure
     */
    @Override
    public void afterPropertiesSet() throws TilesException {
        Assert.state(this.servletContext != null, "No ServletContext available");
        ApplicationContext preliminaryContext = new SpringWildcardServletTilesApplicationContext(this.servletContext);
        if (this.tilesInitializer == null) {
            this.tilesInitializer = new SpringTilesInitializer();
        }
        this.tilesInitializer.initialize(preliminaryContext);
    }
    /**
     * Removes the TilesContainer from this web application.
     *
     * @throws TilesException in case of cleanup failure
     */
    @Override
    public void destroy() throws TilesException {
        if (this.tilesInitializer != null) {
            this.tilesInitializer.destroy();
        }
    }
    private class SpringTilesInitializer extends DefaultTilesInitializer {
        @Override
        protected AbstractTilesContainerFactory createContainerFactory(ApplicationContext context) {
            return new SpringTilesContainerFactory();
        }
    }
    private class SpringTilesContainerFactory extends BasicTilesContainerFactory {
        @Override
        protected List<ApplicationResource> getSources(ApplicationContext applicationContext) {
            if (definitions != null) {
                List<ApplicationResource> result = new ArrayList<>();
                for (String definition : definitions) {
                    Collection<ApplicationResource> resources = applicationContext.getResources(definition);
                    if (resources != null) {
                        result.addAll(resources);
                    }
                }
                return result;
            } else {
                return super.getSources(applicationContext);
            }
        }
        @Override
        protected BaseLocaleUrlDefinitionDAO instantiateLocaleDefinitionDao(ApplicationContext applicationContext,
                                                                            LocaleResolver resolver) {
            return super.instantiateLocaleDefinitionDao(applicationContext, resolver);
        }
        @Override
        protected DefinitionsReader createDefinitionsReader(ApplicationContext context) {
            DigesterDefinitionsReader reader = (DigesterDefinitionsReader) super.createDefinitionsReader(context);
            boolean validateDefinitions = true;
            reader.setValidating(validateDefinitions);
            return reader;
        }
        @Override
        protected DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext,
                                                              LocaleResolver resolver) {
            if (definitionsFactoryClass != null) {
                DefinitionsFactory factory = BeanUtils.instantiateClass(definitionsFactoryClass);
                if (factory instanceof ApplicationContextAware) {
                    ((ApplicationContextAware) factory).setApplicationContext(applicationContext);
                }
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(factory);
                if (bw.isWritableProperty("localeResolver")) {
                    bw.setPropertyValue("localeResolver", resolver);
                }
                if (bw.isWritableProperty("definitionDAO")) {
                    bw.setPropertyValue("definitionDAO", createLocaleDefinitionDao(applicationContext, resolver));
                }
                return factory;
            } else {
                return super.createDefinitionsFactory(applicationContext, resolver);
            }
        }
        @Override
        protected PreparerFactory createPreparerFactory(ApplicationContext context) {
            if (preparerFactoryClass != null) {
                return BeanUtils.instantiateClass(preparerFactoryClass);
            } else {
                return super.createPreparerFactory(context);
            }
        }
        @Override
        protected LocaleResolver createLocaleResolver(ApplicationContext context) {
            return new SpringLocaleResolver();
        }
        @Override
        protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(ApplicationContext context,
                                                                            LocaleResolver resolver) {
            final AttributeEvaluator evaluator;
            if (tilesElPresent && JspFactory.getDefaultFactory() != null) {
                evaluator = new TilesElActivator().createEvaluator();
            } else {
                evaluator = new DirectAttributeEvaluator();
            }
            return new BasicAttributeEvaluatorFactory(evaluator);
        }
    }

        private class TilesElActivator {
        public AttributeEvaluator createEvaluator() {
            ELAttributeEvaluator evaluator = new ELAttributeEvaluator();
            evaluator.setExpressionFactory(
                    JspFactory.getDefaultFactory().getJspApplicationContext(servletContext).getExpressionFactory());
            evaluator.setResolver(new CompositeELResolverImpl());
            return evaluator;
        }
    }
    private static class CompositeELResolverImpl extends CompositeELResolver {
        public CompositeELResolverImpl() {
            add(new ScopeELResolver());
            add(new TilesContextELResolver(new TilesContextBeanELResolver()));
            add(new TilesContextBeanELResolver());
            add(new ArrayELResolver(false));
            add(new ListELResolver(false));
            add(new MapELResolver(false));
            add(new ResourceBundleELResolver());
            add(new BeanELResolver(false));
        }
    }
}
