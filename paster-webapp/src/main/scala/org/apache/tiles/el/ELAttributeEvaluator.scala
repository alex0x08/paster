package org.apache.tiles.el

import org.apache.tiles.evaluator.AbstractAttributeEvaluator
import jakarta.el.ELResolver
import jakarta.el.ExpressionFactory
import org.apache.tiles.request.{ApplicationContext, Request}

class ELAttributeEvaluator extends AbstractAttributeEvaluator{

  /**
   * Initialization parameter to decide the implementation of
   * {@link ExpressionFactoryFactory}.
   *
   * @since 2.2.1
   */
  val EXPRESSION_FACTORY_FACTORY_INIT_PARAM = "org.apache.tiles.evaluator.el.ExpressionFactoryFactory"

  /**
   * The EL expression factory.
   *
   * @since 2.2.1
   */
  protected var expressionFactory: ExpressionFactory = null

  /**
   * The EL resolver to use.
   *
   * @since 2.2.1
   */
  protected var resolver: ELResolver = null

  /**
   * Sets the expression factory to use.
   *
   * @param expressionFactory The expression factory.
   * @since 2.2.1
   */
  def setExpressionFactory(expressionFactory: ExpressionFactory): Unit = {
    this.expressionFactory = expressionFactory
  }

  /**
   * Sets the EL resolver to use.
   *
   * @param resolver The EL resolver.
   * @since 2.2.1
   */
  def setResolver(resolver: ELResolver): Unit = {
    this.resolver = resolver
  }

  /** {@inheritDoc } */
  def evaluate(expression: String, request: Request): Object = {
    val context = new  ELContextImpl(resolver)
    context.putContext(classOf[Request], request)
    context.putContext(classOf[ApplicationContext], request.getApplicationContext)
    val valueExpression = expressionFactory.createValueExpression(context, expression, classOf[Any])
    valueExpression.getValue(context)
  }
}
