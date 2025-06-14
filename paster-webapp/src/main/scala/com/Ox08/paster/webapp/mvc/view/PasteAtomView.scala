/*
 * Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.mvc.view
import com.Ox08.paster.webapp.model.Paste
import com.Ox08.paster.webapp.mvc.MvcConstants
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import com.rometools.rome.feed.atom.{Content, Entry, Feed}
import java.time.ZoneOffset
import scala.jdk.CollectionConverters._
import java.util.{Collections, Date}
/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 14.03.13
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 */
class PasteAtomView extends AbstractAtomFeedView {
  override protected def buildFeedMetadata(model: java.util.Map[String, Object],
                                           feed: Feed, request: HttpServletRequest): Unit = {
    feed.setId("tag:paster")
    feed.setTitle("Latest pastas")
    val contentList = model.get(MvcConstants.NODE_LIST_MODEL).asInstanceOf[java.util.List[Paste]]
    for (e: Paste <- contentList.asScala) {
      val date = Date.from(e.lastModified.toInstant(ZoneOffset.UTC))
      if (feed.getUpdated == null || date.compareTo(feed.getUpdated) > 0)
        feed.setUpdated(date)
    }
  }
  @throws(classOf[Exception])
  override protected def buildFeedEntries(model: java.util.Map[String, Object],
                                          request: HttpServletRequest,
                                          response: HttpServletResponse): java.util.List[Entry] = {
    if (!model.containsKey(MvcConstants.NODE_LIST_MODEL))
      return Collections.emptyList[Entry]()
    val contentList = model.get(MvcConstants.NODE_LIST_MODEL).asInstanceOf[java.util.List[Paste]]
    val entries: java.util.List[Entry] = new java.util.ArrayList[Entry](contentList.size())
    for (e <- contentList.asScala) {
      val entry = new Entry()
      val date = String.format("%1$tY-%1$tm-%1$td", e.getLastModifiedDt)
      // see http://diveintomark.org/archives/2004/05/28/howto-atom-id#other
      entry.setId(String.format("tag:paster.com,%s:%d", date, e.id))
      entry.setTitle(String.format("On %s, %s wrote", date,
        if (e.author != null) e.author else "Anonymous"))
      entry.setUpdated(Date.from(e.lastModified.toInstant(ZoneOffset.UTC)))
      val summary = new Content()
      summary.setValue(e.text)
      entry.setSummary(summary)
      entries.add(entry)
    }
    entries
  }
}
