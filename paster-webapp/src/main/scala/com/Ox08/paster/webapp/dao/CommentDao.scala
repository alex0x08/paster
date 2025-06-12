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
package com.Ox08.paster.webapp.dao
import com.Ox08.paster.webapp.model.Comment
import jakarta.persistence.Tuple
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.highlight.{Highlighter, InvalidTokenOffsetsException}
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import scala.jdk.CollectionConverters._
/**
 * A repository for comments
 */
@Repository("commentDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class CommentDao extends SearchableDaoImpl[Comment](classOf[Comment]) {
  /**
   * Deletes all existing comments for selected paste id
   * @param pasteId
   *        selected paste id
   */
  @Transactional
  def deleteCommentsFor(pasteId: Integer): Unit = {
    val cr = new CriteriaSet
    val cd =cr.cb.createCriteriaDelete(classOf[Comment])
    val r = cd.from(classOf[Comment])
    cd.where(Array(cr.cb.equal(r.get("pasteId"), pasteId)): _*)
    em.createQuery(cd).executeUpdate()
  }
  /**
   * Fetch list of ids of sub comments (replies) to specified comment
   *
   * @param commentId
   * selected comment's id
   * @return
   * list of IDs
   */
  def getSubCommentsIdsFor(commentId: Integer): List[Integer] = {
    var out = List[Integer]()
    val cr = new CriteriaSet
    cr.ct.select(cr.r.get("id"))
    cr.ct.where(Array(cr.cb.equal(cr.r.get("parentId"), commentId)): _*)
    val tupleResult: java.util.List[Tuple] = em.createQuery(cr.ct)
      .setMaxResults(BaseDao.MAX_RESULTS).getResultList
    for (t <- tupleResult.asScala)
     out= out.appended (t.get(0).asInstanceOf[Integer])
    out
  }
  /**
   * Retrieve comments for selected paste
   *
   * @param pasteId
   *      selected paste's id
   * @return
   *      list of comments
   */
  def getCommentsForPaste(pasteId: Integer): java.util.List[Comment] =
    getListByKeyValue("pasteId", pasteId,
      Option("lastModified"),
      Option(true))

  /**
   * Fill highlighted text
   * @param highlighter
   *        Lucene highlighter
   * @param queryParser
   *      Lucene query parser
   * @param model
   *        selected comment DTO
   */
  override def fillHighlighted(highlighter: Highlighter,
                               queryParser: QueryParser,
                               model: Comment): Unit = {
    try {
      val hl = highlighter
        .getBestFragments(queryParser.getAnalyzer
          .tokenStream("text", model.text),
          model.text, 3, " ...")
      if (hl != null && hl.trim().nonEmpty)
        model.text = hl
    } catch {
      case e@(_: IOException | _: InvalidTokenOffsetsException) =>
        logger.error(e.getLocalizedMessage, e)
    }
  }
}
