package com.Ox08.paster.webapp.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient.RequestHeadersUriSpec
import org.springframework.web.client.{ResponseErrorHandler, RestClient}

import java.net.{URI, URL}
import java.util
import scala.jdk.CollectionConverters.SetHasAsScala

@Service
class WebhookService(@Autowired env: Environment) {

  private val hooks:util.Set[Webhook] = new util.LinkedHashSet()

  private val rc = RestClient.create

  for (i <- 1 to 100) {
    val k = s"paster.webhook.${i}.url"
    val v = env.getProperty(k,null.asInstanceOf[String])
    if (v !=null && !v.isBlank)
      hooks.add(new Webhook(URI.create (v).toURL))
  }

  def notifyHooks(event:PasteEvent): Unit = {

    for (h:Webhook <- hooks.asScala) {
        rc.head()
          .uri(h.getUrl.toURI).asInstanceOf[RequestHeadersUriSpec[_]]
          .retrieve()
          .onStatus(new DefaultErrorHandler(event,h))
        }
    }


private class DefaultErrorHandler(event:PasteEvent,
                                  hook: Webhook) extends ResponseErrorHandler {
  override def hasError(response: ClientHttpResponse): Boolean = {
    hook.available = !response.getStatusCode.isError
    if (hook.available) {
      rc.post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(event).asInstanceOf[RequestHeadersUriSpec[_]]
        .uri(hook.getUrl.toURI).asInstanceOf[RequestHeadersUriSpec[_]]
        .retrieve()
     }
    !hook.available
  }
}


private class Webhook(url: URL) {
    var available: Boolean = true
    def getUrl:URL = url
  }

}

class PasteEvent(pasteId: Integer, actionType: String, pasteTitle: String, author: String) {}
