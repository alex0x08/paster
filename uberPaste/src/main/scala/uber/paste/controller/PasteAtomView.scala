package uber.paste.controller

import org.springframework.web.servlet.view.feed.AbstractAtomFeedView
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import com.sun.syndication.feed.atom.{Content, Entry, Feed}
import uber.paste.model.Paste
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 14.03.13
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 */
class PasteAtomView extends AbstractAtomFeedView{

  override protected def buildFeedMetadata(model:java.util.Map[String, Object], feed:Feed, request:HttpServletRequest) {

    feed.setId("tag:springsource.com")
    feed.setTitle("Sample Content")

    val contentList =model.get(GenericController.NODE_LIST_MODEL).asInstanceOf[java.util.List[Paste]]

    for (e:Paste<- contentList) {
      val date = e.getLastModified()
      if (feed.getUpdated() == null || date.compareTo(feed.getUpdated()) > 0) {
        feed.setUpdated(date);
      }
    }
  }

  @throws(classOf[Exception])
  override protected def buildFeedEntries(model:java.util.Map[String, Object] ,
                                          request:HttpServletRequest , response:HttpServletResponse ):java.util.List[Entry]=  {

    val  contentList = model.get(GenericController.NODE_LIST_MODEL).asInstanceOf[java.util.List[Paste]]

    val entries:java.util.List[Entry] = new java.util.ArrayList[Entry](contentList.size())

    for (e:Paste<- contentList) {

      var entry = new Entry()
      var date = String.format("%1$tY-%1$tm-%1$td", e.getLastModified())
      // see http://diveintomark.org/archives/2004/05/28/howto-atom-id#other
      entry.setId(String.format("tag:springsource.com,%s:%d", date, e.getId()))
      entry.setTitle(String.format("On %s, %s wrote", date, if (e.getOwner()!=null) { e.getOwner().getUsername()} else { "Anonymous"}))
      entry.setUpdated(e.getLastModified())



      var summary = new Content()
      summary.setValue(e.getText())
      entry.setSummary(summary)

      entries.add(entry)
    }

    return entries

  }

}