package uber.paste.manager

import org.springframework.stereotype.Service
import uber.paste.model.{Comment, Paste}
import org.springframework.beans.factory.annotation.Autowired
import uber.paste.dao.{CommentDao, PasteDao}

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 09.03.13
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */

trait CommentManager extends GenericSearchManager[Comment]{

 def getCommentsForPaste(pasteId:Long, pasteRev:Long):java.util.List[Comment]

}

@Service("commentManager")
class CommentManagerImpl extends GenericSearchManagerImpl[Comment] with CommentManager {

  @Autowired
  val commentDao:CommentDao = null

  protected override def getDao:CommentDao = commentDao

  
  def getCommentsForPaste(pasteId:Long, pasteRev:Long):java.util.List[Comment] = commentDao.getCommentsForPaste(pasteId, pasteRev)

}
