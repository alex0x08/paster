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

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.tiles.Attribute
import org.apache.tiles.AttributeContext
import org.apache.tiles.TilesContainer
import org.apache.tiles.TilesException
import org.springframework.util.StringUtils
import org.springframework.web.servlet.support.JstlUtils
import org.springframework.web.servlet.support.RequestContext
import org.springframework.web.util.WebUtils
import uber.paste.base.Loggered

/**
 * <p>Used for rendering and processing a dynamic tiles view.</p>
 * 
 * @author David Winterfeldt
 */
object TilesIntegrationConstants {

  final val DEFAULT_TEMPLATE:String = "mainTemplate"
  final val  THIRD_LEVELS:Array[String] = Array[String]("/raw","/integrated")

}


class DynamicTilesViewProcessor extends Loggered {

   /**
     * Keeps Tiles definition to use once derived.
     */
    private var derivedDefinitionName:String =null
    private var tilesDefinitionName =  TilesIntegrationConstants.DEFAULT_TEMPLATE
    private var tilesBodyAttributeName = "content"
    private var tilesDefinitionDelimiter = "."

    /**
     * Main template name.  The default is 'mainTemplate'.
     * 
     * @param 	tilesDefinitionName		Main template name used to lookup definitions.
     */
    def setTilesDefinitionName(tilesDefinitionName:String) {
        this.tilesDefinitionName = tilesDefinitionName;
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

               var beanName:String = bName.replaceAll("//","/")

               if (!beanName.startsWith("/")) {
                 beanName= "/"+beanName
               }

              beanName = if (org.springframework.util.StringUtils.countOccurrencesOf(bName, "/")>2) {

                if (contains3rdLevel(beanName)) {

                  if (org.springframework.util.StringUtils.countOccurrencesOf(beanName, "/")>3) {

                    logger.debug("found 3rd level, calc beanName="+beanName)
                    beanName.substring(0,org.apache.commons.lang.StringUtils.ordinalIndexOf(beanName, "/", 4) )

                  } else {
                    beanName
                  }


                }  else {
                  beanName.substring(0,org.apache.commons.lang.StringUtils.ordinalIndexOf(beanName, "/", 3) )
                }

               }  else {
                 beanName
              }

        JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));

        val definitionName:String = startDynamicDefinition(beanName, url, request, response, container);

        logger.debug("bName "+bName+" beanName "+beanName+" definition Name "+definitionName)

        container.render(definitionName, request, response);

        endDynamicDefinition(definitionName, beanName, request, response, container);
    }


  private def contains3rdLevel(beanName:String):Boolean = {
    for (s <-TilesIntegrationConstants.THIRD_LEVELS) {
      if (beanName.contains(s)) {
        return true
      }
    }
    return false
  }

    /**
     * Starts processing the dynamic Tiles definition by creating a temporary definition for rendering.
     */
    @throws(classOf[TilesException])
    protected def startDynamicDefinition(beanName:String, 
                                         url:String,
                                         request:HttpServletRequest,  
                                         response:HttpServletResponse,
                                         container:TilesContainer):String =
            {
        
      val definitionName:String = processTilesDefinitionName(beanName, container,
                request, response);

        // create a temporary context and render using the incoming url as the
        // body attribute
        if (!definitionName.equals(beanName)) {
           
            val attributeContext:AttributeContext = container.startContext(request, response)
            attributeContext.putAttribute(tilesBodyAttributeName, 
                                          new Attribute(tilesBodyAttributeName,
                                                        url.asInstanceOf[java.lang.Object]))

            logger.debug("URL used for Tiles body.  url='" + url + "'.");
        }

        return definitionName;
    }

    /**
     * Closes the temporary Tiles definition.
     */
    protected def endDynamicDefinition(definitionName:String,
                                       beanName:String,
                                       request:HttpServletRequest,
                                       response:HttpServletResponse,
                                        container:TilesContainer) {
        if (!definitionName.equals(beanName)) {
            container.endContext(request, response);
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
             request:HttpServletRequest,
             response:HttpServletResponse):String =
             {
        // if definition already derived use it, otherwise 
        // check if url (bean name) is a template definition, then 
        // check for main template
        if (derivedDefinitionName != null) {
            return derivedDefinitionName
        } else if (container.isValidDefinition(beanName, request, response)) {

            derivedDefinitionName = beanName

            return beanName
        } else {
            var result:String = null

            val sb = new StringBuilder()
            
            val lastIndex = beanName.lastIndexOf("/")
            
            var rootDefinition = false

            // if delim, tiles def will start with it
            if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                sb.append(tilesDefinitionDelimiter)
            }

            // if no '/', then at context root
            if (lastIndex == -1) {
                rootDefinition = true;
            } else {
                var path = if (beanName != null ) { beanName.substring(0, lastIndex) } else { "" }

                if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                    path = StringUtils.replace(path, "/", tilesDefinitionDelimiter);

                }

                sb.append(path);

                if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                    sb.append(tilesDefinitionDelimiter);
                }
            }

            sb.append(tilesDefinitionName);

            if (container.isValidDefinition(sb.toString(), request, response)) {
                result = sb.toString()

            } else if (!rootDefinition) {

                var root:String = null

                if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                    root = tilesDefinitionDelimiter;
                }

                root += tilesDefinitionName;

              System.out.println("_final "+root)

                if (container.isValidDefinition(root, request, response)) {
                    result = root
                } else {
                    throw new TilesException("No defintion of found for "
                            + "'" + root + "'"
                            + " or '" + sb.toString() + "'");
                }
            }

            derivedDefinitionName = result

            return result
        }
    }

}
