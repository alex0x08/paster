package com.Ox08.paster.webapp.dao
import com.Ox08.paster.webapp.model.Comment
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.highlight.{Highlighter, InvalidTokenOffsetsException}
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
@Repository("commentDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class CommentDao extends SearchableDaoImpl[Comment](classOf[Comment]) {
  def getCommentsForPaste(pasteId: Integer): java.util.List[Comment] =
    getListByKeyValue("pasteId", pasteId,
      Option("lastModified"),
      Option(true))
  override def fillHighlighted(highlighter: Highlighter, pparser: QueryParser, model: Comment): Unit = {
    try {
      val hl = highlighter
        .getBestFragments(pparser.getAnalyzer
          .tokenStream("text", model.text),
          model.text, 3, " ...")
      if (hl != null && hl.trim().nonEmpty) {
        model.text = hl
      }
    } catch {
      case e@(_: IOException | _: InvalidTokenOffsetsException) =>
        logger.error(e.getLocalizedMessage, e)
    }
  }
}
