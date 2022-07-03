package org.apache.tiles.el

import jakarta.el.{ELManager, ExpressionFactory}

/**
 * Interface to define a factory of {@link ExpressionFactory}.
 *
 * @version $Rev$ $Date$
 * @since 2.2.1
 */

trait ExpressionFactoryFactory {

  /**
   * Returns the expression factory to use.
   *
   * @return The expression factory.
   * @since 2.2.1
   */
  def getExpressionFactory(): ExpressionFactory
}
