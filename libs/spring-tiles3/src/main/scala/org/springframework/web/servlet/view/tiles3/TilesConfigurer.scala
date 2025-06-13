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
package org.springframework.web.servlet.view.tiles3

import jakarta.el._
import jakarta.servlet.ServletContext
import jakarta.servlet.jsp.JspFactory
import org.apache.tiles.TilesException
import org.apache.tiles.definition.{DefinitionsFactory, DefinitionsReader}
import org.apache.tiles.definition.dao.BaseLocaleUrlDefinitionDAO
import org.apache.tiles.definition.digester.DigesterDefinitionsReader
import org.apache.tiles.el.{ELAttributeEvaluator, ScopeELResolver, TilesContextBeanELResolver, TilesContextELResolver}
import org.apache.tiles.evaluator.{AttributeEvaluator, AttributeEvaluatorFactory, BasicAttributeEvaluatorFactory}
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator
import org.apache.tiles.factory.BasicTilesContainerFactory
import org.apache.tiles.locale.LocaleResolver
import org.apache.tiles.preparer.factory.PreparerFactory
import org.apache.tiles.request.{ApplicationContext, ApplicationContextAware, ApplicationResource}
import org.apache.tiles.startup.{DefaultTilesInitializer, TilesInitializer}
import org.springframework.beans.{BeanUtils, PropertyAccessorFactory}
import org.springframework.beans.factory.{DisposableBean, InitializingBean}
import org.springframework.lang.Nullable
import org.springframework.util.{Assert, ClassUtils}
import org.springframework.web.context.ServletContextAware

import java.util

/**
 * Helper class to configure Tiles 3.x for the Spring Framework. See
 * <a href="https://tiles.apache.org">https://tiles.apache.org</a>
 * for more information about Tiles, which basically is a templating mechanism
 * for web applications using JSPs and other template engines.
 *
 * <p>The TilesConfigurer simply configures a TilesContainer using a set of files
 * containing definitions, to be accessed by [[TilesView]] instances. This is a
 * Spring-based alternative (for usage in Spring configuration) to the Tiles-provided
 * <pre>ServletContextListener</pre>
 * (e.g. {link org.apache.tiles.extras.complete.CompleteAutoloadTilesListener}
 * for usage in <pre>web.xml</pre>.
 *
 * <p>TilesViews can be managed by any [[org.springframework.web.servlet.ViewResolver]].
 * For simple convention-based view resolution, consider using [[TilesViewResolver]].
 *
 * <p>A typical TilesConfigurer bean definition looks as follows:
 * <pre class="code">
 * &lt;bean id="tilesConfigurer" class="org.springframework.web.servlet.view.tiles3.TilesConfigurer"&gt;
 * &lt;property name="definitions"&gt;
 * &lt;list&gt;
 * &lt;value&gt;/WEB-INF/defs/general.xml&lt;/value&gt;
 * &lt;value&gt;/WEB-INF/defs/widgets.xml&lt;/value&gt;
 * &lt;value&gt;/WEB-INF/defs/administrator.xml&lt;/value&gt;
 * &lt;value&gt;/WEB-INF/defs/customer.xml&lt;/value&gt;
 * &lt;value&gt;/WEB-INF/defs/templates.xml&lt;/value&gt;
 * &lt;/list&gt;
 * &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * <p>
 * The values in the list are the actual Tiles XML files containing the definitions.
 * If the list is not specified, the default is <pre>"/WEB-INF/tiles.xml"</pre>.
 *
 * <p>Note that in Tiles 3 an underscore in the name of a file containing Tiles
 * definitions is used to indicate locale information, for example:
 * <p>
 * <pre class="code">
 * &lt;bean id="tilesConfigurer" class="org.springframework.web.servlet.view.tiles3.TilesConfigurer"&gt;
 * &lt;property name="definitions"&gt;
 * &lt;list&gt;
 * &lt;value&gt;/WEB-INF/defs/tiles.xml&lt;/value&gt;
 * &lt;value&gt;/WEB-INF/defs/tiles_fr_FR.xml&lt;/value&gt;
 * &lt;/list&gt;
 * &lt;/property&gt;
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
object TilesConfigurer {
  private val tilesElPresent = ClassUtils.isPresent("org.apache.tiles.el.ELAttributeEvaluator",
    classOf[TilesConfigurer].getClassLoader)

  private class CompositeELResolverImpl extends CompositeELResolver {
    add(new ScopeELResolver)
    add(new TilesContextELResolver(new TilesContextBeanELResolver))
    add(new TilesContextBeanELResolver)
    add(new ArrayELResolver(false))
    add(new ListELResolver(false))
    add(new MapELResolver(false))
    add(new ResourceBundleELResolver)
    add(new BeanELResolver(false))
  }
}

class TilesConfigurer(@Nullable private val definitionsFactoryClass: Class[_ <: DefinitionsFactory],
                      @Nullable private val preparerFactoryClass: Class[_ <: PreparerFactory])
  extends ServletContextAware with InitializingBean with DisposableBean {
  @Nullable private var tilesInitializer: TilesInitializer = _
  @Nullable private var definitions: Array[String] = _
  @Nullable private var servletContext: ServletContext = _

  /**
   * Set the Tiles definitions, i.e. the list of files containing the definitions.
   * Default is "/WEB-INF/tiles.xml".
   */
  def setDefinitions(definitions: java.util.List[String]): Unit = {
    this.definitions = definitions.toArray(Array.ofDim[String](0))
  }

  override def setServletContext(servletContext: ServletContext): Unit = {
    this.servletContext = servletContext
  }

  /**
   * Creates and exposes a TilesContainer for this web application,
   * delegating to the TilesInitializer.
   *
   * @throws TilesException in case of setup failure
   */
  @throws[TilesException]
  override def afterPropertiesSet(): Unit = {
    Assert.state(this.servletContext != null, "No ServletContext available")
    val preliminaryContext = new SpringWildcardServletTilesApplicationContext(this.servletContext)
    if (this.tilesInitializer == null) this.tilesInitializer =
      new SpringTilesInitializer
    this.tilesInitializer.initialize(preliminaryContext)
  }

  /**
   * Removes the TilesContainer from this web application.
   *
   * @throws TilesException in case of cleanup failure
   */
  @throws[TilesException]
  override def destroy(): Unit = {
    if (this.tilesInitializer != null) this.tilesInitializer.destroy()
  }

  private class SpringTilesInitializer extends DefaultTilesInitializer {
    override protected def createContainerFactory(context: ApplicationContext)
    = new SpringTilesContainerFactory
  }

  private class SpringTilesContainerFactory extends BasicTilesContainerFactory {
    override protected def getSources(applicationContext: ApplicationContext): util.List[ApplicationResource] =
          if (definitions != null) {
      val result = new util.ArrayList[ApplicationResource]
      for (definition <- definitions) {
        val resources = applicationContext.getResources(definition)
        if (resources != null) result.addAll(resources)
      }
      result
    }
    else super.getSources(applicationContext)

    override protected def instantiateLocaleDefinitionDao(applicationContext: ApplicationContext,
                                                          resolver: LocaleResolver): BaseLocaleUrlDefinitionDAO =
              super.instantiateLocaleDefinitionDao(applicationContext, resolver)

    override protected def createDefinitionsReader(context: ApplicationContext): DefinitionsReader = {
      val reader = super.createDefinitionsReader(context).asInstanceOf[DigesterDefinitionsReader]
      val validateDefinitions = true
      reader.setValidating(validateDefinitions)
      reader
    }

    override protected def createDefinitionsFactory(applicationContext: ApplicationContext,
                                                    resolver: LocaleResolver): DefinitionsFactory =
      if (definitionsFactoryClass != null) {
      val factory = BeanUtils.instantiateClass(definitionsFactoryClass)
      factory match {
        case aware: ApplicationContextAware => aware.setApplicationContext(applicationContext)
        case _ =>
      }
      val bw = PropertyAccessorFactory.forBeanPropertyAccess(factory)
      if (bw.isWritableProperty("localeResolver"))
        bw.setPropertyValue("localeResolver", resolver)
      if (bw.isWritableProperty("definitionDAO"))
        bw.setPropertyValue("definitionDAO",
          createLocaleDefinitionDao(applicationContext, resolver))
      factory
    }
    else
      super.createDefinitionsFactory(applicationContext, resolver)

    override protected def createPreparerFactory(context: ApplicationContext): PreparerFactory =
      if (preparerFactoryClass != null) BeanUtils.instantiateClass(preparerFactoryClass)
    else
      super.createPreparerFactory(context)

    override protected def createLocaleResolver(context: ApplicationContext) = new SpringLocaleResolver

    override protected def createAttributeEvaluatorFactory(context: ApplicationContext,
                                                           resolver: LocaleResolver): AttributeEvaluatorFactory = {
      var evaluator: AttributeEvaluator = null
      if (TilesConfigurer.tilesElPresent && JspFactory.getDefaultFactory != null)
        evaluator = new TilesElActivator().createEvaluator
      else evaluator = new DirectAttributeEvaluator
      new BasicAttributeEvaluatorFactory(evaluator)
    }
  }

  private class TilesElActivator {
    def createEvaluator: AttributeEvaluator = {
      val evaluator = new ELAttributeEvaluator
      evaluator.setExpressionFactory(JspFactory.getDefaultFactory
        .getJspApplicationContext(servletContext).getExpressionFactory)
      evaluator.setResolver(new TilesConfigurer.CompositeELResolverImpl)
      evaluator
    }
  }
}
