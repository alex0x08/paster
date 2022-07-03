package org.apache.tiles.el

import org.apache.tiles.request.{ApplicationContext, ApplicationContextAware}
import jakarta.el.ExpressionFactory
import jakarta.servlet.ServletContext
import jakarta.servlet.jsp.JspFactory

class JspExpressionFactoryFactory extends  ExpressionFactoryFactory with ApplicationContextAware{

  /**
   * The servlet context.
   *
   * @since 2.2.1
   */
  protected var servletContext: ServletContext = null

  /** {@inheritDoc } */
  def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    val context = applicationContext.getContext
    if (context.isInstanceOf[ServletContext])
        this.servletContext = context.asInstanceOf[ServletContext]
    else throw new IllegalArgumentException("The application context does not hold an instance of " + "ServletContext, consider using JuelExpressionFactoryFactory")
  }

  def getExpressionFactory: ExpressionFactory = JspFactory
    .getDefaultFactory.getJspApplicationContext(servletContext).getExpressionFactory
}
