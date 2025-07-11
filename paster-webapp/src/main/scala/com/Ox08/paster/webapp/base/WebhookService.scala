package com.Ox08.paster.webapp.base

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
 * @since 3.1
 * @author 0x08
 */
@Service
class WebhookService extends Logged {

  private val hooks:util.Set[Webhook] = new util.LinkedHashSet()

  private val rc = RestClient.create

  private val env = Boot.BOOT.getSystemInfo.getConfig

    // support up to 100 hooks
    for (i <- 1 to 100) {
      val v = env.getProperty(s"paster.webhook.${i}.url",null.asInstanceOf[String])

      if (v !=null && !v.isBlank) {
        if (logger.isDebugEnabled)
            logger.debug(s"hook: '${v}'")
        hooks.add(new Webhook(URI.create (v).toURL))
      }
    }


  def notifyHooks(event:PasteEvent): Unit = {
    if (logger.isDebugEnabled)
      logger.debug(s"notify ${hooks.size()} hooks to event: '${event.getActionType}'")

    // notify each webhook url
    for (h:Webhook <- hooks.asScala) {
      try {
      // first make head request, if succeed - make POST with data
        rc.head()
          .uri(h.getUrl.toURI).asInstanceOf[RequestHeadersUriSpec[_]]
          .retrieve()
          .onStatus(new HeadResponseHandler(event,h))
          .toBodilessEntity
      } catch {
        case e@(_: Exception) =>
          logger.error(e.getMessage, e)
      }
    }
    // .exchange(null)

  }


private class HeadResponseHandler(event:PasteEvent,
                                  hook: Webhook) extends ResponseErrorHandler {
  override def hasError(response: ClientHttpResponse): Boolean = {
    // check if response for HEAD request is non error
    hook.available = !response.getStatusCode.isError
    if (logger.isDebugEnabled)
      logger.debug(s"head result: ${response.getStatusCode} ")

    // if so, try to make POST
    if (hook.available)
        rc.post()
          .contentType(MediaType.APPLICATION_JSON)
          .body(event).asInstanceOf[RequestHeadersUriSpec[_]]
          .uri(hook.getUrl.toURI).asInstanceOf[RequestHeadersUriSpec[_]]
          .retrieve()
          .toBodilessEntity
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
                 author: String) {
  // getters required for json generation
  def getActionType: String = actionType
  def getPasteId: Integer = pasteId
  def getTitle: String = pasteTitle
  def getAuthor: String = author
}
