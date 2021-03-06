package uber.paste.dao

import org.springframework.transaction.annotation.Transactional
import javax.persistence.Query
import org.springframework.stereotype.Repository
import uber.paste.model.{ Comment, Paste }

@Repository("commentDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class CommentDaoImpl extends SearchableDaoImpl[Comment](classOf[Comment]) {

  def getCommentsForPaste(pasteId: Long): java.util.List[Comment] =

    getListByKeyValue("pasteId", pasteId, 
                      Option("lastModified"),
                      Option(true))

}
