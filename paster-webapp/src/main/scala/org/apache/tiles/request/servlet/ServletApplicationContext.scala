package org.apache.tiles.request.servlet

import jakarta.servlet.ServletContext
import org.apache.tiles.request.{ApplicationContext, ApplicationResource}

import java.net.URL
import java.util
import jakarta.servlet.ServletContext
import org.apache.tiles.request.collection.ReadOnlyEnumerationMap
import org.apache.tiles.request.collection.ScopeMap
import org.apache.tiles.request.locale.URLApplicationResource
import org.apache.tiles.request.servlet.extractor.ApplicationScopeExtractor
import org.apache.tiles.request.servlet.extractor.InitParameterExtractor

import java.io.IOException
import java.util.Locale

class ServletApplicationContext(servletContext: ServletContext)  extends ApplicationContext{




  /**
   * <p>The lazily instantiated <code>Map</code> of application scope
   * attributes.</p>
   */
  private var applicationScope: java.util.Map[String,Object] = null


  /**
   * <p>The lazily instantiated <code>Map</code> of context initialization
   * parameters.</p>
   */
  private var initParam:java.util.Map[String,String] = null



  /** {@inheritDoc } */
  def getContext: Any = servletContext

  def getApplicationScope: java.util.Map[String,Object] = {
    if ((applicationScope == null) && (servletContext != null))
        applicationScope = new ScopeMap(new ApplicationScopeExtractor(servletContext))
    applicationScope
  }


  def getInitParams: java.util.Map[String,String] = {
    if ((initParam == null) && (servletContext != null))
        initParam = new ReadOnlyEnumerationMap[String](new InitParameterExtractor(servletContext))
    initParam
  }

  @throws[IOException]
  def getResource(path: String): ApplicationResource   = {
    val url = servletContext.getResource(path)
    new URLApplicationResource(url.toExternalForm, url)
  }

  @throws[IOException]
  def getResources(path: String): java.util.Collection[ApplicationResource] = {
    val urls = new util.ArrayList[ApplicationResource]
    urls.add(getResource(path))
    urls
  }

  override def getResource(applicationResource: ApplicationResource, locale: Locale): ApplicationResource = null

}
