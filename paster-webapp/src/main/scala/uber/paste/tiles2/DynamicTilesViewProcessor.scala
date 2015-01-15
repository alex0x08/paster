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

import java.util.Locale
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.tiles.Attribute
import org.apache.tiles.AttributeContext
import org.apache.tiles.TilesContainer
import org.apache.tiles.TilesException
import org.apache.tiles.request.ApplicationContext
import org.apache.tiles.request.Request
import org.apache.tiles.request.servlet.ServletUtil
import org.springframework.web.servlet.support.JstlUtils
import org.springframework.web.servlet.support.RequestContext
import org.springframework.web.servlet.support.RequestContextUtils
import uber.paste.base.Loggered



class DynamicTilesViewProcessor extends Loggered {

   /**
     * Keeps Tiles definition to use once derived.
     */
    private var derivedDefinitionName:String =null
    
    private var tilesDefinitionName =  ""
    
    private var tilesBodyAttributeName = "content"
    private var tilesDefinitionDelimiter = "."

    /**
     * Main template name.  The default is 'mainTemplate'.
     * 
     * @param 	tilesDefinitionName		Main template name used to lookup definitions.
     */
    def setTilesDefinitionName(tilesDefinitionName:String) {
        this.tilesDefinitionName = tilesDefinitionName
    }

    /**
     * Tiles body attribute name.  The default is 'body'.
     * 
     * @param 	tilesBodyAttributeName		Tiles body attribute name.
     */
    def setTilesBodyAttributeName(tilesBodyAttributeName:String) {
        this.tilesBodyAttributeName = tilesBodyAttributeName;
    }

    /**
     * Sets Tiles definition delimiter.  For example, instead of using 
     * the request 'info/about' to lookup the template definition 
     * 'info/mainTemplate', the default delimiter of '.' 
     * would look for '.info.mainTemplate' 
     * 
     * @param 	tilesDefinitionDelimiter	Optional delimiter to replace '/' in a url.
     */
    def setTilesDefinitionDelimiter(tilesDefinitionDelimiter:String) {
        this.tilesDefinitionDelimiter = tilesDefinitionDelimiter;
    }

    /**
     * Renders output using Tiles.
     */
    @throws(classOf[java.lang.Exception])
    def renderMergedOutputModel(bName:String, url:String,
             servletContext:ServletContext,
             request:HttpServletRequest,  response:HttpServletResponse,
             container:TilesContainer)
             {

    val tilesRequest = createTilesRequest(ServletUtil.getApplicationContext(servletContext),
                                          request, response);
	
    
               var beanName:String = bName.replaceAll("//","/")

               if (!beanName.startsWith("/")) {
                 beanName= "/"+beanName
               }

      
              while (!container.isValidDefinition(beanName, tilesRequest)) {
                
              val pos = beanName.lastIndexOf("/")
              if (pos<1) 
                 throw new TilesException("No defintion of found for "
                            + "'" + beanName + "'")
 
                beanName = beanName.substring(0,pos)
              }
    
    
        JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));

        val definitionName:String = startDynamicDefinition(beanName, url, tilesRequest, container)

      
        container.render(definitionName, tilesRequest)

        endDynamicDefinition(definitionName, beanName, tilesRequest, container)
    }


 

    /**
     * Starts processing the dynamic Tiles definition by creating a temporary definition for rendering.
     */
    @throws(classOf[TilesException])
    protected def startDynamicDefinition(beanName:String, 
                                         url:String,
                                         tilesRequest:Request,
                                         container:TilesContainer):String =
            {
        
      val definitionName:String = processTilesDefinitionName(beanName, container,
                tilesRequest)
        // create a temporary context and render using the incoming url as the
        // body attribute
        if (!definitionName.equals(beanName)) {
           
            val attributeContext:AttributeContext = container.startContext(tilesRequest)
            attributeContext.putAttribute(tilesBodyAttributeName, 
                                          new Attribute(url))

            logger.debug("URL used for Tiles body.  url='{0}'.",url)
        }

        return definitionName
    }

    /**
     * Closes the temporary Tiles definition.
     */
    protected def endDynamicDefinition(definitionName:String,
                                       beanName:String,
                                       tilesRequest:Request,
                                        container:TilesContainer) {
        if (!definitionName.equals(beanName)) {
            container.endContext(tilesRequest)
        }
    }

    /**
     * Processes values to get tiles template definition name.  First 
     * a Tiles definition matching the url is checked, then a 
     * url specific template is checked, and then just the 
     * default root definition is used.
     * 
     * @throws 	TilesException		If no valid Tiles definition is found. 
     */
    @throws(classOf[TilesException])
    protected def processTilesDefinitionName(beanName:String,
             container:TilesContainer,
             tilesRequest:Request):String =
             {
        // if definition already derived use it, otherwise 
        // check if url (bean name) is a template definition, then 
        // check for main template
        return if (derivedDefinitionName != null) {
            derivedDefinitionName
        } else if (container.isValidDefinition(beanName, tilesRequest)) {

            derivedDefinitionName = beanName ;  beanName
        }   else {
                    throw new TilesException("No defintion of found for "
                            + "'" + beanName + "'")
                }
            }

   
    protected def createTilesRequest(applicationContext:ApplicationContext,
                                     request:HttpServletRequest, response:HttpServletResponse):Request = 
	 new org.apache.tiles.request.servlet.ServletRequest(applicationContext, request, response) {
			override def getRequestLocale():Locale = {
				return RequestContextUtils.getLocale(request)
			}
		}  
}
