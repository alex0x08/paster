package uber.paste.dao

import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Repository
import uber.paste.model.{Comment, Paste}

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 09.03.13
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */

trait CommentDao extends SearchableDao[Comment]{

}

@Repository("commentDao")
@Transactional(readOnly = true)
class CommentDaoImpl extends SearchableDaoImpl[Comment](classOf[Comment]) with CommentDao{

}
