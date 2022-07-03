package org.springframework.web.servlet.view.tiles3

import org.apache.tiles.preparer.factory.PreparerFactory
import org.apache.tiles.request.Request

abstract class AbstractSpringPreparerFactory extends   PreparerFactory{

  import org.apache.tiles.TilesException
  import org.apache.tiles.preparer.ViewPreparer
  import org.springframework.web.context.WebApplicationContext
  import org.springframework.web.servlet.DispatcherServlet

  def getPreparer(name: String, context:  Request): ViewPreparer = {
    var webApplicationContext = context.getContext("request")
      .get(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE).asInstanceOf[WebApplicationContext]
    if (webApplicationContext == null) {
      webApplicationContext = context.getContext("application")
        .get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE).asInstanceOf[WebApplicationContext]
      if (webApplicationContext == null) throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?")
    }
    getPreparer(name, webApplicationContext)
  }

  /**
   * Obtain a preparer instance for the given preparer name,
   * based on the given Spring WebApplicationContext.
   *
   * @param name    the name of the preparer
   * @param context the current Spring WebApplicationContext
   * @return the preparer instance
   * @throws TilesException in case of failure
   */
  @throws[TilesException]
  protected def getPreparer(name: String, context: WebApplicationContext): ViewPreparer
}
