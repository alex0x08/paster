package com.Ox08.paster.webapp.mvc.view
import com.Ox08.paster.webapp.model.Paste
import com.Ox08.paster.webapp.mvc.MvcConstants
import com.rometools.rome.feed.rss.{Channel, Content, Description, Item}
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.web.servlet.view.feed.AbstractRssFeedView
import java.time.ZoneOffset
import java.util.{Collections, Date}
import scala.jdk.CollectionConverters._
/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 14.03.13
 * Time: 14:50
 */
class PasteRssView extends AbstractRssFeedView {
  private var externalUrl: Option[String] = None
  def getExternalUrl: String = externalUrl.get
  def setExternalUrl(url: String): Unit = {
    externalUrl = Some(url)
  }
  override protected def buildFeedMetadata(model: java.util.Map[String, Object], feed: Channel,
                                           request: HttpServletRequest): Unit = {
    feed.setTitle("Paster RSS")
    feed.setDescription("Code Review Tool")
    feed.setLink(externalUrl.get)
    super.buildFeedMetadata(model, feed, request)
  }
  override protected def buildFeedItems(model: java.util.Map[String, Object],
                                        request: HttpServletRequest, response: HttpServletResponse): java.util.List[Item] = {
    if (!model.containsKey(MvcConstants.NODE_LIST_MODEL)) {
      return Collections.emptyList[Item]()
    }
    val contentList = model.get(MvcConstants.NODE_LIST_MODEL).asInstanceOf[java.util.List[Paste]]
    val entries: java.util.List[Item] = new java.util.ArrayList[Item](contentList.size())
    for (e: Paste <- contentList.asScala) {
      val entry = new Item()
      entry.setTitle(e.title)
      entry.setLink(s"$externalUrl/main/paste/${e.id}")
      entry.setPubDate(Date.from(e.lastModified.toInstant(ZoneOffset.UTC)))
      entry.setAuthor(if (e.author != null) e.author else "Anonymous")
      val d = new Description()
      d.setValue(e.title)
      entry.setDescription(d)
      val summary = new Content()
      summary.setValue(e.title)
      entry.setContent(summary)
      entries.add(entry)
    }
    entries
  }
}
