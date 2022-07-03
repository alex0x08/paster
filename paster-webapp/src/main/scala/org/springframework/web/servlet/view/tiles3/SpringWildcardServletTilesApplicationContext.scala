package org.springframework.web.servlet.view.tiles3

import org.apache.tiles.request.servlet.ServletApplicationContext
import jakarta.servlet.ServletContext
import org.apache.tiles.request.ApplicationResource
import org.apache.tiles.request.locale.URLApplicationResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.lang.Nullable
import org.springframework.util.{CollectionUtils, ObjectUtils}
import org.springframework.web.context.support.ServletContextResourcePatternResolver

import java.io.IOException
import java.util.Collections
import java.util.Locale


class SpringWildcardServletTilesApplicationContext(servletContext: ServletContext)
  extends ServletApplicationContext(servletContext) {

  private val resolver  = new ServletContextResourcePatternResolver(servletContext)


  @Nullable
  override def getResource(localePath: String): ApplicationResource = {
    val urlSet = getResources(localePath)
    if (!CollectionUtils.isEmpty(urlSet)) return urlSet.iterator.next
    null
  }

  @Nullable
  override def getResource(base: ApplicationResource, locale: Locale): ApplicationResource = {
    val urlSet = getResources(base.getLocalePath(locale))
    if (!CollectionUtils.isEmpty(urlSet)) return urlSet.iterator.next()
    null
  }

  override def getResources(path: String): java.util.Collection[ApplicationResource] = {
    System.out.println("__getResources "+path)
    var resources:Array[Resource] = null
    try resources = this.resolver.getResources(path)
    catch {
      case ex: IOException =>
        getContext.asInstanceOf[ServletContext].log("Resource retrieval failed for path: " + path, ex)
        return Collections.emptyList()
    }
    if (ObjectUtils.isEmpty(resources)) {
      getContext.asInstanceOf[ServletContext].log("No resources found for path pattern: " + path)
      return Collections.emptyList()
    }
    val resourceList:java.util.List[ApplicationResource] = new java.util.ArrayList[ApplicationResource](resources.length)

    for (resource <- resources) {
      try {
        System.out.println("url= " + resource.getFilename)
        val url = resource.getURL
       // resourceList.add(url)
        resourceList.add(new URLApplicationResource(url.toExternalForm, url))
      } catch {
        case ex: IOException =>
          // Shouldn't happen with the kind of resources we're using
          ex.printStackTrace()
        //throw new IllegalArgumentException("No URL for " + resource, ex)
      }
    }
    resourceList
  }

}
