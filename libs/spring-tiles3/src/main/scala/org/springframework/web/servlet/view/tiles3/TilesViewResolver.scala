/*
 * Copyright 2002-2020 the original author or authors.
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

import org.apache.tiles.request.render.Renderer
import org.springframework.lang.Nullable
import org.springframework.web.servlet.view.AbstractUrlBasedView
import org.springframework.web.servlet.view.UrlBasedViewResolver

/**
 * Convenience subclass of [[UrlBasedViewResolver]] that supports
 * [[TilesView]] (i.e. Tiles definitions) and custom subclasses of it.
 *
 * @author Nicolas Le Bas
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 3.2
 */
class TilesViewResolver

/**
 * This resolver requires [[TilesView]].
 */
  extends UrlBasedViewResolver {
  setViewClass(requiredViewClass)
  @Nullable private var renderer: Renderer = _
  @Nullable private var alwaysInclude = false

  /**
   * Set the [[Renderer]] to use. If not specified, a default
   * [[org.apache.tiles.renderer.DefinitionRenderer]] will be used.
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
   * @see TilesView#setAlwaysInclude
   * @since 4.1.2
   */
  def setAlwaysInclude(alwaysInclude: Boolean): Unit = {
    this.alwaysInclude = alwaysInclude
  }

  override final protected def requiredViewClass: Class[_] = classOf[TilesView]

  override protected def instantiateView: AbstractUrlBasedView =
    if (getViewClass eq classOf[TilesView]) new TilesView
  else super.instantiateView

  @throws[Exception]
  override protected def buildView(viewName: String): TilesView = {
    val view = super.buildView(viewName).asInstanceOf[TilesView]
    if (this.renderer != null) view.setRenderer(this.renderer)
    if (this.alwaysInclude) view.setAlwaysInclude(this.alwaysInclude)
    view
  }
}
