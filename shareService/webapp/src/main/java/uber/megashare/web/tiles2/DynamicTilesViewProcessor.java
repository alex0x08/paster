/**
 * Copyright (C) 2011 aachernyshev <alex@0x08.tk>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uber.megashare.web.tiles2;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;

/**
 * <p>Used for rendering and processing a dynamic tiles view.</p>
 * 
 * @author David Winterfeldt
 */
public class DynamicTilesViewProcessor {

    final Logger logger = LoggerFactory.getLogger(DynamicTilesViewProcessor.class);
    /**
     * Keeps Tiles definition to use once derived.
     */
    private String derivedDefinitionName = null;
    private String tilesDefinitionName = "mainTemplate";
    private String tilesBodyAttributeName = "content";
    private String tilesDefinitionDelimiter = ".";

    /**
     * Main template name.  The default is 'mainTemplate'.
     * 
     * @param 	tilesDefinitionName		Main template name used to lookup definitions.
     */
    public void setTilesDefinitionName(String tilesDefinitionName) {
        this.tilesDefinitionName = tilesDefinitionName;
    }

    /**
     * Tiles body attribute name.  The default is 'body'.
     * 
     * @param 	tilesBodyAttributeName		Tiles body attribute name.
     */
    public void setTilesBodyAttributeName(String tilesBodyAttributeName) {
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
    public void setTilesDefinitionDelimiter(String tilesDefinitionDelimiter) {
        this.tilesDefinitionDelimiter = tilesDefinitionDelimiter;
    }

    /**
     * Renders output using Tiles.
     */
    protected void renderMergedOutputModel(String beanName, String url,
            ServletContext servletContext,
            HttpServletRequest request, HttpServletResponse response,
            TilesContainer container)
            throws Exception {
        
        
        beanName = beanName.replaceAll("//","/");
        
        if (!beanName.startsWith("/")) {
            beanName= "/"+beanName;
        }
        
         logger.debug("start beanName="+beanName);
        
        /**
         * there we extract bean name from url
         */
        if (org.springframework.util.StringUtils.countOccurrencesOf(beanName, "/")>2) {
        
            if (contains3rdLevel(beanName) ) {
            
                if (org.springframework.util.StringUtils.countOccurrencesOf(beanName, "/")>3) {
                    beanName = beanName.substring(0,StringUtils.ordinalIndexOf(beanName, "/", 4) );
                }
                
                logger.debug("found 3rd level, calc beanName="+beanName);
                
            } else {
            
                beanName = beanName.substring(0,StringUtils.ordinalIndexOf(beanName, "/", 3) );
            }
       }
        
                JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));       
        
    
        String definitionName = startDynamicDefinition(beanName, url, request, response, container);

        
        logger.debug("found definition "+definitionName+" beanName="+beanName);
        
        container.render(definitionName, request, response);

        endDynamicDefinition(definitionName, beanName, request, response, container);
    }

    private static final String[] third_level = new String[] {"/raw","/integrated"};
    
    private boolean contains3rdLevel(String beanName) {
        for (String s:third_level) {
            if (beanName.contains(s)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Starts processing the dynamic Tiles definition by creating a temporary definition for rendering.
     */
    protected String startDynamicDefinition(String beanName, String url,
            HttpServletRequest request, HttpServletResponse response,
            TilesContainer container)
            throws TilesException {
        String definitionName = processTilesDefinitionName(beanName, container,
                request, response);

        // create a temporary context and render using the incoming url as the
        // body attribute
        if (!definitionName.equals(beanName)) {
           
            AttributeContext attributeContext = container.startContext(request, response);
            attributeContext.putAttribute(tilesBodyAttributeName, new Attribute(tilesBodyAttributeName,(Object)url));

            logger.debug("URL used for Tiles body.  url='" + url + "'.");
        }

        return definitionName;
    }

    /**
     * Closes the temporary Tiles definition.
     */
    protected void endDynamicDefinition(String definitionName, String beanName,
            HttpServletRequest request, HttpServletResponse response,
            TilesContainer container) {
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
    protected String processTilesDefinitionName(String beanName,
            TilesContainer container,
            HttpServletRequest request,
            HttpServletResponse response)
            throws TilesException {
        
           
        
        // if definition already derived use it, otherwise 
        // check if url (bean name) is a template definition, then 
        // check for main template
        if (derivedDefinitionName != null) {
            return derivedDefinitionName;
        } 
        
        if (container.isValidDefinition(beanName, request, response)) {
            derivedDefinitionName = beanName;        
            return beanName;
        } 
           
        String result = null;

            StringBuilder sb = new StringBuilder();
            int lastIndex = beanName.lastIndexOf("/");
            boolean rootDefinition = false;

            // if delim, tiles def will start with it
            if (org.springframework.util.StringUtils.hasLength(tilesDefinitionDelimiter)) {
                sb.append(tilesDefinitionDelimiter);
            }

            // if no '/', then at context root
            if (lastIndex == -1) {
                rootDefinition = true;
            } else {
                String path = (beanName != null ? beanName.substring(0, lastIndex) : "");

                if (org.springframework.util.StringUtils.hasLength(tilesDefinitionDelimiter)) {
                    path = StringUtils.replace(path, "/", tilesDefinitionDelimiter);
                }

                sb.append(path);

                if (org.springframework.util.StringUtils.hasLength(tilesDefinitionDelimiter)) {
                    sb.append(tilesDefinitionDelimiter);
                }
            }

            sb.append(tilesDefinitionName);

            
            if (container.isValidDefinition(sb.toString(), request, response)) {
                result = sb.toString();
            } else if (!rootDefinition) {
                String root = null;

                if (org.springframework.util.StringUtils.hasLength(tilesDefinitionDelimiter)) {
                    root = tilesDefinitionDelimiter;
                }

                root += tilesDefinitionName;

                if (container.isValidDefinition(root, request, response)) {
                    result = root;
                } else {
                    throw new TilesException("No defintion of found for "
                            + "'" + root + "'"
                            + " or '" + sb.toString() + "'");
                }
            }

            derivedDefinitionName = result;
 
            return result;
        
    }
}