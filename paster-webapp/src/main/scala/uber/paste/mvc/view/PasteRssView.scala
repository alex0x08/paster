package uber.paste.mvc.view

import com.rometools.rome.feed.rss.{Channel, Content, Description, Item}
import org.springframework.web.servlet.view.feed.AbstractRssFeedView
import uber.paste.model.Paste
import uber.paste.mvc.GenericController
import  scala.jdk.CollectionConverters._

import java.util.Collections
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 14.03.13
 * Time: 14:50
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

    if (!model.containsKey(GenericController.NODE_LIST_MODEL)) {
      return Collections.emptyList[Item]()
    }


    val  contentList = model.get(GenericController.NODE_LIST_MODEL).asInstanceOf[java.util.List[Paste]]

    val entries:java.util.List[Item] = new java.util.ArrayList[Item](contentList.size())

    for (e:Paste<- contentList.asScala) {

      val entry = new Item()

      entry.setTitle(e.getName())
      entry.setLink(externalUrl+"/main/paste/"+e.getId())
      entry.setPubDate(e.getLastModified)
          entry.setAuthor( if (e.getOwner()!=null) { e.getOwner().getUsername()} else { "Anonymous"})
      val d = new Description

      d.setValue(e.getTitle())

      entry.setDescription(d)

      val summary = new Content()
      summary.setValue(e.getTitle())
      entry.setContent(summary)

      entries.add(entry)
    }

    return entries

  }

}
