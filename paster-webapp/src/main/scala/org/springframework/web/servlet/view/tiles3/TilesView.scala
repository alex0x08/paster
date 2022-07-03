package org.springframework.web.servlet.view.tiles3

import jakarta.servlet.ServletContext
import org.apache.tiles.request.servlet.{ServletRequest, ServletUtil}
import org.springframework.lang.Nullable
import org.springframework.web.servlet.view.AbstractUrlBasedView
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.tiles.access.TilesAccess
import org.apache.tiles.renderer.DefinitionRenderer
import org.apache.tiles.request.render.Renderer
import org.apache.tiles.request.{AbstractRequest, ApplicationContext, Request}
import org.springframework.util.Assert
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.support.{JstlUtils, RequestContext, RequestContextUtils}

import java.util.Locale

class TilesView extends AbstractUrlBasedView{


  @Nullable
  private var renderer:Renderer = null

  private var exposeJstlAttributes = true

  private var alwaysInclude = false

  @Nullable
  private var applicationContext:ApplicationContext = null


  /**
   * Set the {@link Renderer} to use.
   * If not set, by default {@link DefinitionRenderer} is used.
   */
  def setRenderer(renderer: Renderer): Unit = {
    this.renderer = renderer
  }

  /**
   * Whether to expose JSTL attributes. By default set to {@code true}.
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
   * @since 4.1.2
   * @see TilesViewResolver#setAlwaysInclude
   */
  def setAlwaysInclude(alwaysInclude: Boolean): Unit = {
    this.alwaysInclude = alwaysInclude
  }

  @throws[Exception]
  override def afterPropertiesSet(): Unit = {
    super.afterPropertiesSet
    val servletContext: ServletContext = getServletContext()
    Assert.state(servletContext != null, "No ServletContext")
    applicationContext = ServletUtil.getApplicationContext(servletContext)
    if (this.renderer == null) {
      val container = TilesAccess.getContainer(this.applicationContext)
      this.renderer = new DefinitionRenderer(container)
    }
  }


  @throws[Exception]
  override def checkResource(locale: Locale): Boolean = {
    Assert.state(this.renderer != null, "No Renderer set")
    var servletRequest:HttpServletRequest = null
    val requestAttributes = RequestContextHolder.getRequestAttributes
    if (requestAttributes.isInstanceOf[ServletRequestAttributes])
          servletRequest = requestAttributes.asInstanceOf[ServletRequestAttributes]
            .getRequest
    val request = new ServletRequest(this.applicationContext, servletRequest, null) {
      override def getRequestLocale: Locale = locale
    }
    this.renderer.isRenderable(getUrl, request)
  }

  @throws[Exception]
  protected def renderMergedOutputModel(model: java.util.Map[String,AnyRef],
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
   * Create a Tiles {@link Request}.
   * <p>This implementation creates a {@link ServletRequest}.
   *
   * @param request  the current request
   * @param response the current response
   * @return the Tiles request
   */
  protected def createTilesRequest(request: HttpServletRequest,
                                   response: HttpServletResponse): Request
  = new ServletRequest(this.applicationContext, request, response) {
    override def getRequestLocale: Locale = return RequestContextUtils.getLocale(request)
  }

}
