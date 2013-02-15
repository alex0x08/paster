/*
 * Copyright 2011 Ubersoft, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.tiles2

import org.springframework.util.StringUtils
import org.springframework.web.servlet.view.AbstractUrlBasedView
import org.springframework.web.servlet.view.UrlBasedViewResolver

class TilesUrlBasedViewResolver extends UrlBasedViewResolver{

  
  private var tilesDefinitionName:String = null
  private var tilesBodyAttributeName:String = null
  private var tilesDefinitionDelimiter:String =null

    /**
     * Main template name.
     */
    def setTilesDefinitionName(tilesDefinitionName:String) {
        this.tilesDefinitionName = tilesDefinitionName
    }

    /**
     * Tiles body attribute name. 
     */
    def setTilesBodyAttributeName(tilesBodyAttributeName:String) {
        this.tilesBodyAttributeName = tilesBodyAttributeName;
    }

    /**
     * Sets Tiles definition delimiter.  
     */
    def setTilesDefinitionDelimiter(tilesDefinitionDelimiter:String) {
        this.tilesDefinitionDelimiter = tilesDefinitionDelimiter;
    }

    /**
     * Does everything the <code>UrlBasedViewResolver</code> does and 
     * also sets some Tiles specific values on the view.
     * 
     * @param viewName the name of the view to build
     * @return the View instance
     * @throws Exception if the view couldn't be resolved
     * @see #loadView(String, java.util.Locale)
     */
    @throws(classOf[java.lang.Exception])
    protected override def buildView(viewName:String):AbstractUrlBasedView = {
         val view:AbstractUrlBasedView = super.buildView(viewName)

        // if DynamicTilesView, set tiles specific values
        if (view.isInstanceOf[DynamicTilesView] ) {
            
      val dtv =  view.asInstanceOf[DynamicTilesView]

            if (StringUtils.hasLength(tilesDefinitionName)) {
                dtv.setTilesDefinitionName(tilesDefinitionName);
            }

            if (StringUtils.hasLength(tilesBodyAttributeName)) {
                dtv.setTilesBodyAttributeName(tilesBodyAttributeName);
            }

            if (tilesDefinitionDelimiter != null) {
                dtv.setTilesDefinitionDelimiter(tilesDefinitionDelimiter);
            }
        }

        return view;
    }
}
