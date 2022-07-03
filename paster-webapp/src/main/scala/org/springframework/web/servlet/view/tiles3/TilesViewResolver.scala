package org.springframework.web.servlet.view.tiles3

import org.apache.tiles.request.render.Renderer
import org.springframework.lang.Nullable
import org.springframework.web.servlet.view.UrlBasedViewResolver
import org.springframework.web.servlet.view.AbstractUrlBasedView

class TilesViewResolver extends UrlBasedViewResolver {


  @Nullable
  private var renderer:Renderer = null

  @Nullable
  private var alwaysInclude = false

  setViewClass(requiredViewClass)



  /**
   * Set the {@link Renderer} to use. If not specified, a default
   * {@link org.apache.tiles.renderer.DefinitionRenderer} will be used.
   *
   * @see TilesView#setRenderer(Renderer)
   */
  def setRenderer(renderer: Renderer): Unit = {
    this.renderer = renderer
  }

  /**
   * Specify whether to always include the view rather than forward to it.
   * <p>Default is "false". Switch this flag on to enforce the use of a
   * Servlet include, even if a forward would be possible.
   *
   * @since 4.1.2
   * @see TilesView#setAlwaysInclude
   */
  def setAlwaysInclude(alwaysInclude: Boolean): Unit = {
    this.alwaysInclude = alwaysInclude
  }

  override protected def requiredViewClass: Class[_] = classOf[TilesView]

  override protected def instantiateView: AbstractUrlBasedView = if ((getViewClass eq classOf[TilesView])) new TilesView
  else super.instantiateView

  @throws[Exception]
  override protected def buildView(viewName: String): TilesView = {
    val view = super.buildView(viewName).asInstanceOf[TilesView]
    if (this.renderer != null) view.setRenderer(this.renderer)
    if (this.alwaysInclude != null) view.setAlwaysInclude(this.alwaysInclude)
    view
  }

}
