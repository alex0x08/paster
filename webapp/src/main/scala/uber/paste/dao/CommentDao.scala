package uber.paste.dao

import org.springframework.transaction.annotation.Transactional
import javax.persistence.Query
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
  
 def getCommentsForPaste(pasteId:Long, pasteRev:Long):java.util.List[Comment]

}

@Repository("commentDao")
@Transactional(readOnly = true)
class CommentDaoImpl extends SearchableDaoImpl[Comment](classOf[Comment]) with CommentDao{

  
  def getCommentsForPaste(pasteId:Long, pasteRev:Long):java.util.List[Comment] = {

    System.out.println("get comments id="+pasteId+" rev="+pasteRev)
    
    val cr = new CriteriaSet

    val query:Query = em.createQuery(
      cr.cr.where(Array(cr.cb.equal(cr.r.get("pasteId"), pasteId),
                        cr.cb.equal(cr.r.get("pasteRev"), pasteRev)):_*)
        .orderBy(cr.cb.desc(cr.r.get("lastModified"))))
        .setMaxResults(BaseDaoImpl.MAX_RESULTS)
    return query.getResultList().asInstanceOf[java.util.List[Comment]]
  }

  
}
