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

import jakarta.servlet.ServletContext
import org.apache.tiles.request.ApplicationResource
import org.apache.tiles.request.locale.URLApplicationResource
import org.apache.tiles.request.servlet.ServletApplicationContext
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.lang.Nullable
import org.springframework.util.{CollectionUtils, ObjectUtils}
import org.springframework.web.context.support.ServletContextResourcePatternResolver

import java.io.IOException
import java.util
import java.util.{Collections, Locale}


/**
 * Spring-specific subclass of the Tiles ServletApplicationContext.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.2
 */
class SpringWildcardServletTilesApplicationContext(servletContext: ServletContext)
              extends ServletApplicationContext(servletContext) {
  final private val resolver: ResourcePatternResolver = new ServletContextResourcePatternResolver(servletContext)

  @Nullable override def getResource(localePath: String): ApplicationResource = {
    val urlSet = getResources(localePath)
    if (!CollectionUtils.isEmpty(urlSet)) return urlSet.iterator.next
    null
  }

  @Nullable override def getResource(base: ApplicationResource,
                                     locale: Locale): ApplicationResource = {
    val urlSet = getResources(base.getLocalePath(locale))
    if (!CollectionUtils.isEmpty(urlSet)) return urlSet.iterator.next
    null
  }

  override def getResources(path: String): util.Collection[ApplicationResource] = {
    var resources: Array[Resource] = null
    try resources = this.resolver.getResources(path)
    catch {
      case _: IOException =>

        // do not log this error: tiles tries to load tiles-defs/*.xml
        //  from META-INF by default.
        //	((ServletContext) getContext()).log("Resource retrieval failed for path: "
        //	+ path, ex);
        return Collections.emptyList
    }
    if (ObjectUtils.isEmpty(resources)) {
      getContext.asInstanceOf[ServletContext]
        .log(s"No resources found for path pattern: $path")
      return Collections.emptyList
    }
    val resourceList = new util.ArrayList[ApplicationResource](resources.length)
    for (resource <- resources) {
      try {
        val url = resource.getURL
        resourceList.add(new URLApplicationResource(url.toExternalForm, url))
      } catch {
        case ex: IOException =>
          // Shouldn't happen with the kind of resources we're using
          throw new IllegalArgumentException("No URL for " + resource, ex)
      }
    }
    resourceList
  }
}
