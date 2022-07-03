package org.springframework.web.servlet.view.tiles3

import jakarta.el._
import jakarta.servlet.ServletContext
import jakarta.servlet.jsp.JspFactory
import org.apache.commons.logging.{Log, LogFactory}
import org.apache.tiles.{TilesContainer, TilesException}
import org.apache.tiles.definition.{DefinitionsFactory, DefinitionsReader}
import org.apache.tiles.definition.dao.{BaseLocaleUrlDefinitionDAO, CachingLocaleUrlDefinitionDAO}
import org.apache.tiles.definition.digester.DigesterDefinitionsReader
import org.apache.tiles.el.ScopeELResolver
import org.apache.tiles.evaluator.{AttributeEvaluator, AttributeEvaluatorFactory, BasicAttributeEvaluatorFactory}
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator
import org.apache.tiles.factory.BasicTilesContainerFactory
import org.apache.tiles.impl.mgmt.CachingTilesContainer
import org.apache.tiles.locale.LocaleResolver
import org.apache.tiles.preparer.factory.PreparerFactory
import org.apache.tiles.request.{ApplicationContext, ApplicationContextAware, ApplicationResource}
import org.apache.tiles.startup.{DefaultTilesInitializer, TilesInitializer}
import org.springframework.beans.{BeanUtils, PropertyAccessorFactory}
import org.springframework.beans.factory.{DisposableBean, InitializingBean}
import org.springframework.lang.Nullable
import org.springframework.util.{Assert, ClassUtils}
import org.springframework.web.context.ServletContextAware
import org.apache.tiles.el.ELAttributeEvaluator

import java.util

class TilesConfigurer extends ServletContextAware with InitializingBean with DisposableBean{

  private val tilesElPresent = ClassUtils.isPresent("org.apache.tiles.el.ELAttributeEvaluator", classOf[TilesConfigurer].getClassLoader)


  protected val logger: Log = LogFactory.getLog(getClass)

  @Nullable
  private var tilesInitializer:TilesInitializer = null

  @Nullable
  private var definitions:Array[String] = null

  private var checkRefresh = false

  private var validateDefinitions = true

  @Nullable
  private var definitionsFactoryClass:Class[_ <: DefinitionsFactory] = null

  @Nullable
  private var preparerFactoryClass: Class[_ <: PreparerFactory] = null

  private var useMutableTilesContainer = false

  @Nullable
  private var servletContext:ServletContext = null


  /**
   * Configure Tiles using a custom TilesInitializer, typically specified as an inner bean.
   * <p>Default is a variant of {@link org.apache.tiles.startup.DefaultTilesInitializer},
   * respecting the "definitions", "preparerFactoryClass" etc properties on this configurer.
   * <p><b>NOTE: Specifying a custom TilesInitializer effectively disables all other bean
   * properties on this configurer.</b> The entire initialization procedure is then left
   * to the TilesInitializer as specified.
   */
  def setTilesInitializer(tilesInitializer: TilesInitializer): Unit = {
    this.tilesInitializer = tilesInitializer
  }

  /**
   * Specify whether to apply Tiles 3.0's "complete-autoload" configuration.
   * for details on the complete-autoload mode.
   * <p><b>NOTE: Specifying the complete-autoload mode effectively disables all other bean
   * properties on this configurer.</b> The entire initialization procedure is then left
   *
   * @see org.apache.tiles.extras.complete.CompleteAutoloadTilesContainerFactory
   * @see org.apache.tiles.extras.complete.CompleteAutoloadTilesInitializer
   */
  def setCompleteAutoload(completeAutoload: Boolean): Unit = {
    /*if (completeAutoload)
        try this.tilesInitializer = new SpringCompleteAutoloadTilesInitializer
    catch {
      case ex: Throwable =>
        throw new IllegalStateException("Tiles-Extras 3.0 not available", ex)
    }*/
    //else
    this.tilesInitializer = null
  }

  /**
   * Set the Tiles definitions, i.e. the list of files containing the definitions.
   * Default is "/WEB-INF/tiles.xml".
   */
  def setDefinitions(definitions: Array[String]): Unit = {
    for (e<-definitions) {
      System.out.println("e="+e)
    }
    this.definitions = definitions
  }

  /**
   * Set whether to check Tiles definition files for a refresh at runtime.
   * Default is "false".
   */
  def setCheckRefresh(checkRefresh: Boolean): Unit = {
    this.checkRefresh = checkRefresh
  }

  /**
   * Set whether to validate the Tiles XML definitions. Default is "true".
   */
  def setValidateDefinitions(validateDefinitions: Boolean): Unit = {
    this.validateDefinitions = validateDefinitions
  }

  /**
   * Set the {@link org.apache.tiles.definition.DefinitionsFactory} implementation to use.
   * Default is {@link org.apache.tiles.definition.UnresolvingLocaleDefinitionsFactory},
   * operating on definition resource URLs.
   * <p>Specify a custom DefinitionsFactory, e.g. a UrlDefinitionsFactory subclass,
   * to customize the creation of Tiles Definition objects. Note that such a
   * DefinitionsFactory has to be able to handle {@link java.net.URL} source objects,
   * unless you configure a different TilesContainerFactory.
   */
  def setDefinitionsFactoryClass(definitionsFactoryClass: Class[_ <: DefinitionsFactory]): Unit = {
    this.definitionsFactoryClass = definitionsFactoryClass
  }

  /**
   * Set the {@link org.apache.tiles.preparer.factory.PreparerFactory} implementation to use.
   * Default is {@link org.apache.tiles.preparer.factory.BasicPreparerFactory}, creating
   * shared instances for specified preparer classes.
   * <p>Specify {@link SimpleSpringPreparerFactory} to autowire
   * {@link org.apache.tiles.preparer.ViewPreparer} instances based on specified
   * preparer classes, applying Spring's container callbacks as well as applying
   * configured Spring BeanPostProcessors. If Spring's context-wide annotation-config
   * has been activated, annotations in ViewPreparer classes will be automatically
   * detected and applied.
   * <p>Specify {@link SpringBeanPreparerFactory} to operate on specified preparer
   * <i>names</i> instead of classes, obtaining the corresponding Spring bean from
   * the DispatcherServlet's application context. The full bean creation process
   * will be in the control of the Spring application context in this case,
   * allowing for the use of scoped beans etc. Note that you need to define one
   * Spring bean definition per preparer name (as used in your Tiles definitions).
   *
   * @see SimpleSpringPreparerFactory
   * @see SpringBeanPreparerFactory
   */
  def setPreparerFactoryClass(preparerFactoryClass: Class[_ <: PreparerFactory]): Unit = {
    this.preparerFactoryClass = preparerFactoryClass
  }

  /**
   * Set whether to use a MutableTilesContainer (typically the CachingTilesContainer
   * implementation) for this application. Default is "false".
   *
   * @see org.apache.tiles.mgmt.MutableTilesContainer
   * @see org.apache.tiles.impl.mgmt.CachingTilesContainer
   */
  def setUseMutableTilesContainer(useMutableTilesContainer: Boolean): Unit = {
    this.useMutableTilesContainer = useMutableTilesContainer
  }

  def setServletContext(servletContext: ServletContext): Unit = {
    this.servletContext = servletContext
  }

  /**
   * Creates and exposes a TilesContainer for this web application,
   * delegating to the TilesInitializer.
   *
   * @throws TilesException in case of setup failure
   */
  @throws[TilesException]
  def afterPropertiesSet(): Unit = {
    Assert.state(this.servletContext != null, "No ServletContext available")
    val preliminaryContext = new SpringWildcardServletTilesApplicationContext(this.servletContext)
    if (this.tilesInitializer == null)
        this.tilesInitializer = new SpringTilesInitializer
    this.tilesInitializer.initialize(preliminaryContext)
  }

  /**
   * Removes the TilesContainer from this web application.
   *
   * @throws TilesException in case of cleanup failure
   */
  @throws[TilesException]
  def destroy(): Unit = {
    if (this.tilesInitializer != null) this.tilesInitializer.destroy()
  }


  private class SpringTilesInitializer extends DefaultTilesInitializer {
    override protected def createContainerFactory(context: ApplicationContext) = new SpringTilesContainerFactory
  }


  private class SpringTilesContainerFactory extends BasicTilesContainerFactory {

    override protected def createDecoratedContainer(originalContainer: TilesContainer, context: ApplicationContext): TilesContainer
    = if (useMutableTilesContainer) new CachingTilesContainer(originalContainer)
    else originalContainer

    override protected def getSources(applicationContext: ApplicationContext):
          util.List[ApplicationResource] = if (definitions != null) {
      val result = new util.ArrayList[ApplicationResource]
      for (definition <- definitions) {
        val resources = applicationContext.getResources(definition)
        if (resources != null) result.addAll(resources)
      }
      result
    }
    else super.getSources(applicationContext)

    override protected def instantiateLocaleDefinitionDao(applicationContext: ApplicationContext,
                                                          resolver: LocaleResolver): BaseLocaleUrlDefinitionDAO = {
      val dao = super.instantiateLocaleDefinitionDao(applicationContext, resolver)
      if (checkRefresh && dao.isInstanceOf[CachingLocaleUrlDefinitionDAO])
        dao.asInstanceOf[CachingLocaleUrlDefinitionDAO].setCheckRefresh(true)
      dao
    }

    override protected def createDefinitionsReader(context: ApplicationContext): DefinitionsReader = {
      val reader = super.createDefinitionsReader(context).asInstanceOf[DigesterDefinitionsReader]
      reader.setValidating(validateDefinitions)
      reader
    }

    override protected def createDefinitionsFactory(applicationContext: ApplicationContext,
                                                    resolver: LocaleResolver):
    DefinitionsFactory = if (definitionsFactoryClass != null) {
      val factory:DefinitionsFactory  = BeanUtils.instantiateClass(definitionsFactoryClass)
      if (factory.isInstanceOf[ApplicationContextAware])
            factory.asInstanceOf[ApplicationContextAware]
              .setApplicationContext(applicationContext)

      val bw = PropertyAccessorFactory.forBeanPropertyAccess(factory)
      if (bw.isWritableProperty("localeResolver"))
          bw.setPropertyValue("localeResolver", resolver)
      if (bw.isWritableProperty("definitionDAO"))
        bw.setPropertyValue("definitionDAO",
          createLocaleDefinitionDao(applicationContext, resolver))
      factory
    }
    else super.createDefinitionsFactory(applicationContext, resolver)

    override protected def createPreparerFactory(context: ApplicationContext):
          PreparerFactory = if (preparerFactoryClass != null)
            BeanUtils.instantiateClass(preparerFactoryClass)
    else super.createPreparerFactory(context)

    override protected def createLocaleResolver(context: ApplicationContext) = new SpringLocaleResolver

    override protected def createAttributeEvaluatorFactory(context: ApplicationContext, resolver: LocaleResolver):
          AttributeEvaluatorFactory = {
      var evaluator:AttributeEvaluator = null
      if (tilesElPresent && JspFactory.getDefaultFactory != null)
        evaluator = new TilesElActivator().createEvaluator
      else evaluator = new DirectAttributeEvaluator
      new BasicAttributeEvaluatorFactory(evaluator)
    }
  }
/*
  import org.apache.tiles.extras.complete.CompleteAutoloadTilesContainerFactory
  import org.apache.tiles.extras.complete.CompleteAutoloadTilesInitializer

  private class SpringCompleteAutoloadTilesInitializer extends CompleteAutoloadTilesInitializer{
    protected def createContainerFactory(context: Nothing) = new SpringCompleteAutoloadTilesContainerFactory
  }


  private class SpringCompleteAutoloadTilesContainerFactory extends CompleteAutoloadTilesContainerFactory {
    protected def createLocaleResolver(applicationContext: ApplicationContext) = new SpringLocaleResolver
  }
*/


  private class TilesElActivator {
    def createEvaluator: AttributeEvaluator = {
      val evaluator:ELAttributeEvaluator  = new ELAttributeEvaluator()
      evaluator.setExpressionFactory(JspFactory.getDefaultFactory
        .getJspApplicationContext(servletContext).getExpressionFactory)
      evaluator.setResolver(new CompositeELResolverImpl)
      evaluator
    }
  }

  import org.apache.tiles.el.TilesContextBeanELResolver
  import org.apache.tiles.el.TilesContextELResolver


  private class CompositeELResolverImpl extends CompositeELResolver {
    add(new ScopeELResolver())
    add(new TilesContextELResolver(new TilesContextBeanELResolver()))
    add(new TilesContextBeanELResolver)
    add(new ArrayELResolver(false))
    add(new ListELResolver(false))
    add(new MapELResolver(false))
    add(new ResourceBundleELResolver)
    add(new BeanELResolver(false))
  }

}
