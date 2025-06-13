/*
 * Copyright 2002-2017 the original author or authors.
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

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.tiles.access.TilesAccess
import org.apache.tiles.renderer.DefinitionRenderer
import org.apache.tiles.request.{AbstractRequest, ApplicationContext, Request}
import org.apache.tiles.request.render.Renderer
import org.apache.tiles.request.servlet.{ServletRequest, ServletUtil}
import org.springframework.lang.Nullable
import org.springframework.util.Assert
import org.springframework.web.context.request.{RequestContextHolder, ServletRequestAttributes}
import org.springframework.web.servlet.support.{JstlUtils, RequestContext, RequestContextUtils}
import org.springframework.web.servlet.view.AbstractUrlBasedView

import java.util
import java.util.Locale

/**
 * [[org.springframework.web.servlet.View]] implementation that renders
 * through the Tiles Request API. The "url" property is interpreted as name of a
 * Tiles definition.
 *
 * @author Nicolas Le Bas
 * @author mick semb wever
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 * @since 3.2
 */
class TilesView extends AbstractUrlBasedView {
  @Nullable private var renderer: Renderer = _
  private var exposeJstlAttributes = true
  private var alwaysInclude = false
  @Nullable private var applicationContext: ApplicationContext = _

  /**
   * Set the [[Renderer]] to use.
   * If not set, by default [[DefinitionRenderer]] is used.
   */
  def setRenderer(renderer: Renderer): Unit = {
    this.renderer = renderer
  }

  /**
   * Whether to expose JSTL attributes. By default, set to <pre>true</pre>.
   *
   * @see JstlUtils#exposeLocalizationContext(RequestContext)
   */
  protected def setExposeJstlAttributes(exposeJstlAttributes: Boolean): Unit = {
    this.exposeJstlAttributes = exposeJstlAttributes
  }

  /**
   * Specify whether to always include the view rather than forward to it.
   * <p>Default is "false". Switch this flag on to enforce the use of a
   * Servlet include, even if a forward would be possible.
   *
   * @see TilesViewResolver#setAlwaysInclude
   * @since 4.1.2
   */
  def setAlwaysInclude(alwaysInclude: Boolean): Unit = {
    this.alwaysInclude = alwaysInclude
  }

  @throws[Exception]
  override def afterPropertiesSet(): Unit = {
    super.afterPropertiesSet()
    val servletContext = getServletContext
    Assert.state(servletContext != null, "No ServletContext")
    this.applicationContext = ServletUtil.getApplicationContext(servletContext)
    if (this.renderer == null) {
      val container = TilesAccess.getContainer(this.applicationContext)
      this.renderer = new DefinitionRenderer(container)
    }
  }

  override def checkResource(locale: Locale): Boolean = {
    Assert.state(this.renderer != null, "No Renderer set")
    var servletRequest: HttpServletRequest = null
    val requestAttributes = RequestContextHolder.getRequestAttributes
    requestAttributes match {
      case attributes: ServletRequestAttributes =>
        servletRequest = attributes.getRequest
      case _ =>
    }
    val request = new ServletRequest(this.applicationContext, servletRequest, null) {
      override def getRequestLocale: Locale = locale
    }
    this.renderer.isRenderable(getUrl, request)
  }

  @throws[Exception]
  override protected def renderMergedOutputModel(model: util.Map[String, AnyRef],
                                                 request: HttpServletRequest,
                                                 response: HttpServletResponse): Unit = {
    Assert.state(this.renderer != null, "No Renderer set")
    exposeModelAsRequestAttributes(model, request)
    if (this.exposeJstlAttributes)
      JstlUtils.exposeLocalizationContext(new RequestContext(request, getServletContext))
    if (this.alwaysInclude)
      request.setAttribute(AbstractRequest.FORCE_INCLUDE_ATTRIBUTE_NAME, true)
    val tilesRequest = createTilesRequest(request, response)
    this.renderer.render(getUrl, tilesRequest)
  }

  /**
   * Create a Tiles [[Request]].
   * <p>This implementation creates a [[ServletRequest]].
   *
   * @param request  the current request
   * @param response the current response
   * @return the Tiles request
   */
  protected def createTilesRequest(request: HttpServletRequest,
                                   response: HttpServletResponse): Request =
    new ServletRequest(this.applicationContext, request, response) {
    override def getRequestLocale: Locale = RequestContextUtils.getLocale(request)
  }
}
