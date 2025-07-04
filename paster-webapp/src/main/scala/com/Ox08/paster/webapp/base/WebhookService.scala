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

/**
 * A service for webhooks notifications
 * @param env
 */
@Service
class WebhookService(@Autowired env: Environment) {

  private val hooks:util.Set[Webhook] = new util.LinkedHashSet()

  private val rc = RestClient.create

  // support up to 100 hooks
  for (i <- 1 to 100) {
    val k = s"paster.webhook.${i}.url"
    val v = env.getProperty(k,null.asInstanceOf[String])
    if (v !=null && !v.isBlank)
      hooks.add(new Webhook(URI.create (v).toURL))
  }

  def notifyHooks(event:PasteEvent): Unit = {
    // notify each webhook url
    for (h:Webhook <- hooks.asScala)
      // first make head request, if succeed - make POST with data
        rc.head()
          .uri(h.getUrl.toURI).asInstanceOf[RequestHeadersUriSpec[_]]
          .retrieve()
          .onStatus(new HeadResponseHandler(event,h))
  }


private class HeadResponseHandler(event:PasteEvent,
                                  hook: Webhook) extends ResponseErrorHandler {
  override def hasError(response: ClientHttpResponse): Boolean = {
    // check if response for HEAD request is non error
    hook.available = !response.getStatusCode.isError
    // if so, try to make POST
    if (hook.available)
        rc.post()
          .contentType(MediaType.APPLICATION_JSON)
          .body(event).asInstanceOf[RequestHeadersUriSpec[_]]
          .uri(hook.getUrl.toURI).asInstanceOf[RequestHeadersUriSpec[_]]
          .retrieve()
    !hook.available
  }
}

private class Webhook(url: URL) {
    var available: Boolean = true
    def getUrl:URL = url
  }
}

/**
 * Very basic structure for PUSH notification
 * @param pasteId
 *        record id
 * @param actionType
 *      type of action
 * @param pasteTitle
 *      title of record
 * @param author
 *    author's username
 */
class PasteEvent(pasteId: Integer,
                 actionType: String,
                 pasteTitle: String,
                 author: String) {}
