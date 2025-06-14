/*
 * Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.tiles2
import com.Ox08.paster.webapp.base.Logged
import jakarta.servlet.ServletContext
import java.util.Locale
import org.apache.tiles.Attribute
import org.apache.tiles.AttributeContext
import org.apache.tiles.TilesContainer
import org.apache.tiles.TilesException
import org.apache.tiles.request.ApplicationContext
import org.apache.tiles.request.Request
import org.apache.tiles.request.servlet.{ServletRequest, ServletUtil}
import org.springframework.web.servlet.support.JstlUtils
import org.springframework.web.servlet.support.RequestContext
import org.springframework.web.servlet.support.RequestContextUtils
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
/**
 * A processor implementation for Apache Tiles' dynamic view.
 *
 * Initially had been taken from famous 'Pet Store' Spring demo project, rewritten to Scala.
 */
class DynamicTilesViewProcessor extends Logged {
  /**
   * Keeps Tiles definition to use once derived.
   */
  private var derivedDefinitionName: String = _
  private var tilesDefinitionName = ""
  private var tilesBodyAttributeName = "content"
  private var tilesDefinitionDelimiter = "."
  /**
   * Main template name.  The default is 'mainTemplate'.
   *
   * @param tilesDefinitionName Main template name used to lookup definitions.
   */
  def setTilesDefinitionName(tilesDefinitionName: String): Unit = {
    this.tilesDefinitionName = tilesDefinitionName
  }
  /**
   * Tiles body attribute name.  The default is 'body'.
   *
   * @param tilesBodyAttributeName Tiles body attribute name.
   */
  def setTilesBodyAttributeName(tilesBodyAttributeName: String): Unit = {
    this.tilesBodyAttributeName = tilesBodyAttributeName
  }
  /**
   * Sets Tiles definition delimiter.  For example, instead of using
   * the request 'info/about' to lookup the template definition
   * 'info/mainTemplate', the default delimiter of '.'
   * would look for '.info.mainTemplate'
   *
   * @param tilesDefinitionDelimiter Optional delimiter to replace '/' in a url.
   */
  def setTilesDefinitionDelimiter(tilesDefinitionDelimiter: String): Unit = {
    this.tilesDefinitionDelimiter = tilesDefinitionDelimiter
  }
  /**
   * Renders output using Tiles.
   */
  @throws(classOf[java.lang.Exception])
  def renderMergedOutputModel(bName: String,
                              url: String,
                              servletContext: ServletContext,
                              request: HttpServletRequest, response: HttpServletResponse,
                              container: TilesContainer): Unit = {
    val tilesRequest = createTilesRequest(ServletUtil.getApplicationContext(servletContext),
      request, response)
    // stip slashes
    var beanName: String = bName replaceAll("//", "/")
    // append / to start if needed
    if (!(beanName startsWith "/")) beanName = s"/$beanName"
    // seek for tiles definition based on bean name
    while (!container.isValidDefinition(beanName, tilesRequest)) {
      val pos = beanName.lastIndexOf('/')
      if (pos < 1)
        throw new TilesException(s"No definition found for '$beanName'")
      beanName = beanName.substring(0, pos)
    }
    JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext))
    val definitionName: String = startDynamicDefinition(beanName, url, tilesRequest, container)
    try container.render(definitionName, tilesRequest) catch {
      case e: Exception =>
        logger.error(e.getMessage, e)
    }
    endDynamicDefinition(definitionName, beanName, tilesRequest, container)
  }
  /**
   * Starts processing the dynamic Tiles definition by creating a temporary definition for rendering.
   */
  @throws(classOf[TilesException])
  protected def startDynamicDefinition(beanName: String,
                                       url: String,
                                       tilesRequest: Request,
                                       container: TilesContainer): String = {
    val definitionName: String = processTilesDefinitionName(beanName, container,
      tilesRequest)
    // create a temporary context and render using the incoming url as the
    // body attribute
    if (!definitionName.equals(beanName)) {
      val attributeContext: AttributeContext = container.startContext(tilesRequest)
      attributeContext.putAttribute(tilesBodyAttributeName, new Attribute(url))
      if (logger.isDebugEnabled)
        logger.debug("URL used for Tiles body.  url='{}'.", url)
    }
    definitionName
  }
  /**
   * Closes the temporary Tiles definition.
   */
  protected def endDynamicDefinition(definitionName: String,
                                     beanName: String,
                                     tilesRequest: Request,
                                     container: TilesContainer): Unit = {
    if (!definitionName.equals(beanName))
      container.endContext(tilesRequest)
  }
  /**
   * Processes values to get tiles template definition name.  First
   * a Tiles definition matching the url is checked, then a
   * url specific template is checked, and then just the
   * default root definition is used.
   *
   * @throws TilesException If no valid Tiles definition is found.
   */
  @throws(classOf[TilesException])
  protected def processTilesDefinitionName(beanName: String,
                                           container: TilesContainer,
                                           tilesRequest: Request): String = {
    // if definition already derived use it, otherwise
    // check if url (bean name) is a template definition, then
    // check for main template
    if (derivedDefinitionName != null)
      derivedDefinitionName
    else if (container.isValidDefinition(beanName, tilesRequest)) {
      derivedDefinitionName = beanName
      beanName
    } else
      throw new TilesException(s"No definition found for '$beanName'")
  }
  protected def createTilesRequest(applicationContext: ApplicationContext,
                                   request: HttpServletRequest,
                                   response: HttpServletResponse): Request =
    new ServletRequest(applicationContext,
      request, response) {
      override def getRequestLocale: Locale = RequestContextUtils.getLocale(request)
    }
}
