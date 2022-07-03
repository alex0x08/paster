package org.springframework.web.servlet.view.tiles3

import org.apache.tiles.TilesException
import org.apache.tiles.preparer.ViewPreparer
import org.springframework.web.context.WebApplicationContext

class SpringBeanPreparerFactory  extends AbstractSpringPreparerFactory{

  @throws[TilesException]
  protected def getPreparer(name: String, context: WebApplicationContext): ViewPreparer =
        context.getBean(name, classOf[ViewPreparer])
}
