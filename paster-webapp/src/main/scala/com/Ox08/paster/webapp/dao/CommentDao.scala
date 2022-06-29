package com.Ox08.paster.webapp.dao

import com.Ox08.paster.webapp.model.Comment
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository("commentDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class CommentDaoImpl extends SearchableDaoImpl[Comment](classOf[Comment]) {

  def getCommentsForPaste(pasteId: Long): java.util.List[Comment] =

    getListByKeyValue("pasteId", pasteId,
      Option("lastModified"),
      Option(true))

}
