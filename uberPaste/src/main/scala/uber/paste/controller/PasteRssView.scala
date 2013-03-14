package uber.paste.controller

import org.springframework.web.servlet.view.feed.AbstractRssFeedView
import scala.collection.JavaConversions._
import com.sun.syndication.feed.rss.{Description, Item, Channel, Content}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import uber.paste.model.Paste

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 14.03.13
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
class PasteRssView extends AbstractRssFeedView{

  private var externalUrl:String = null

  def getExternalUrl():String = externalUrl

  def setExternalUrl(url:String) { externalUrl = url}

  override protected def buildFeedMetadata(model:java.util.Map[String, Object], feed:Channel,
    request:HttpServletRequest) {

    feed.setTitle("Paster RSS")
    feed.setDescription("Code Review Tool")
    feed.setLink(externalUrl)

    super.buildFeedMetadata(model, feed, request)
  }


  override protected def buildFeedItems(model:java.util.Map[String, Object],
     request:HttpServletRequest, response:HttpServletResponse):java.util.List[Item] = {

    val  contentList = model.get(GenericController.NODE_LIST_MODEL).asInstanceOf[java.util.List[Paste]]

    val entries:java.util.List[Item] = new java.util.ArrayList[Item](contentList.size())

    for (e:Paste<- contentList) {

      var entry = new Item()

      entry.setTitle(e.getName())
      entry.setLink(externalUrl+"/main/paste/"+e.getId())
      entry.setPubDate(e.getLastModified())
          entry.setAuthor( if (e.getOwner()!=null) { e.getOwner().getUsername()} else { "Anonymous"})
      var d = new Description

      d.setValue(e.getTitle())

      entry.setDescription(d)

      var summary = new Content()
      summary.setValue(e.getTitle())
      entry.setContent(summary)

      entries.add(entry)
    }

    return entries

  }

}
