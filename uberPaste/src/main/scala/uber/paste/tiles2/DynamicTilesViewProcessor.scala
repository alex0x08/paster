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
import org.springframework.util.StringUtils
import org.springframework.web.servlet.support.JstlUtils
import org.springframework.web.servlet.support.RequestContext
import org.springframework.web.servlet.support.RequestContextUtils
import org.springframework.web.util.WebUtils
import uber.paste.base.Loggered
import uber.paste.model.KeyValue
import uber.paste.model.KeyValueObj

object PrefixType extends KeyValueObj[PrefixType] {
  
  val MAIN = new PrefixType("MAIN","Main site template","mainTemplate","/")
  val INTEGRATED = new PrefixType("INTEGRATED","Integrated usage site template","integratedTemplate","/integrated")
  val RAW = new PrefixType("RAW","Raw site template","rawTemplate","/raw") 

  val THIRD_LEVELS:Array[PrefixType] = Array[PrefixType](PrefixType.RAW,
                                                        PrefixType.INTEGRATED)
   
  add(MAIN)
  add(INTEGRATED)
  add(RAW) 
  
  
   def lookupLevelPrefixType(beanName:String):PrefixType = {
    for (s <-PrefixType.THIRD_LEVELS) {
      if (beanName.contains(s.getUrlPrefix)) {
        return s
      }
    }
    return null
  }
 }


class PrefixType(code:String,desc:String,templateName:String,urlPrefix:String) extends KeyValue(code,desc){

  def getCodeLowerCase() = super.getCode().toLowerCase
   
  def getTemplateName() = templateName
  
  def getUrlPrefix() = urlPrefix
}


class DynamicTilesViewProcessor extends Loggered {

   /**
     * Keeps Tiles definition to use once derived.
     */
    private var derivedDefinitionName:String =null
    
    private var tilesDefinitionName =  PrefixType.MAIN.getTemplateName
    
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

    val tilesRequest = createTilesRequest(ServletUtil.getApplicationContext(servletContext),request, response);
	
    
               var beanName:String = bName.replaceAll("//","/")

               if (!beanName.startsWith("/")) {
                 beanName= "/"+beanName
               }

               logger.debug("initial beanName "+beanName)

               var pType = PrefixType.MAIN
    
             if (org.springframework.util.StringUtils.countOccurrencesOf(beanName, "/")>2) {

              pType = PrefixType.lookupLevelPrefixType(beanName)
      
                //contains3rdLevel(beanName)
                if (pType!=null) {               
          
                 if (org.springframework.util.StringUtils.countOccurrencesOf(beanName, "/")>3) {
                    logger.debug("found 3rd level")
                    beanName = beanName.substring(0,org.apache.commons.lang.StringUtils.ordinalIndexOf(beanName, "/", 4) )                 
                  }
                }  else {
                    beanName =beanName.substring(0,org.apache.commons.lang.StringUtils.ordinalIndexOf(beanName, "/", 3) )
                }
               }  

     logger.debug("final beanName="+beanName)
                   
    
        JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));

        val definitionName:String = startDynamicDefinition(beanName, url, tilesRequest, container,pType)

        logger.debug("bName "+bName+" beanName "+beanName+" definition Name "+definitionName)

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
                                         container:TilesContainer,pType:PrefixType):String =
            {
        
      val definitionName:String = processTilesDefinitionName(beanName, container,
                tilesRequest,pType)
        // create a temporary context and render using the incoming url as the
        // body attribute
        if (!definitionName.equals(beanName)) {
           
            val attributeContext:AttributeContext = container.startContext(tilesRequest)
            attributeContext.putAttribute(tilesBodyAttributeName, 
                                          new Attribute(url))

            logger.debug("URL used for Tiles body.  url='" + url + "'.")
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
             tilesRequest:Request,pType:PrefixType):String =
             {
        // if definition already derived use it, otherwise 
        // check if url (bean name) is a template definition, then 
        // check for main template
        return if (derivedDefinitionName != null) {
            derivedDefinitionName
        } else if (container.isValidDefinition(beanName, tilesRequest)) {

            derivedDefinitionName = beanName

            beanName
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
                rootDefinition = true
            } else {
                var path = if (beanName != null ) { beanName.substring(0, lastIndex) } else { "" }

                if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                    path = StringUtils.replace(path, "/", tilesDefinitionDelimiter)
                }

                sb.append(path)

                if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                    sb.append(tilesDefinitionDelimiter);
                }
            }

      sb.append(pType.getTemplateName)

            if (container.isValidDefinition(sb.toString(), tilesRequest)) {
                result = sb.toString()

            } else if (!rootDefinition) {

                var root:String = null

                if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                    root = tilesDefinitionDelimiter
                }

                root += pType.getTemplateName

              logger.debug("_final "+root)

                if (container.isValidDefinition(root, tilesRequest)) {
                    result = root
                } else {
                    throw new TilesException("No defintion of found for "
                            + "'" + root + "'"
                            + " or '" + sb.toString() + "'");
                }
            }

            derivedDefinitionName = result

            result
        }
    }

  
    protected def createTilesRequest(applicationContext:ApplicationContext,
                                     request:HttpServletRequest, response:HttpServletResponse):Request = {
		return new org.apache.tiles.request.servlet.ServletRequest(applicationContext, request, response) {
			override def getRequestLocale():Locale = {
				return RequestContextUtils.getLocale(request)
			}
		};
	}

  
}
