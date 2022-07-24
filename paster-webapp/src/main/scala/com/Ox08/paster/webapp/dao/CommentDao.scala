package com.Ox08.paster.webapp.dao
import com.Ox08.paster.webapp.model.Comment
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.highlight.{Highlighter, InvalidTokenOffsetsException}
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.{EntityManager, PersistenceContext, Tuple}
import java.io.IOException
import java.util
import scala.:+
import scala.jdk.CollectionConverters._
/**
 * A repository for comments
 */
@Repository("commentDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class CommentDao extends SearchableDaoImpl[Comment](classOf[Comment]) {
  /**
   * Fetch list of ids of sub comments (replies) to specified comment
   * @param commentId
   *        selected comment's id
   * @return
   *      list of IDs
   */
  def getSubCommentsIdsFor(commentId: Integer): List[Integer] = {
    val out = List[Integer]()
    val cr = new CriteriaSet
    cr.ct.multiselect(cr.r.get("id"))
    cr.ct.where(Array(cr.cb.equal(cr.r.get("parentId"), commentId)): _*)
    val tupleResult: java.util.List[Tuple] = em.createQuery(cr.ct)
      .setMaxResults(BaseDao.MAX_RESULTS).getResultList
    for (t <- tupleResult.asScala) {
      out :+ t.get(0).asInstanceOf[Integer]
    }
    out
  }
  /**
   * Return all comments for selected paste object
   * @param pasteId
   *        selected paste's id
   * @return
   *    list of comments
   */
  def getCommentsForPaste(pasteId: Integer): java.util.List[Comment] =
    getListByKeyValue("pasteId", pasteId,
      Option("lastModified"),
      Option(true))


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
