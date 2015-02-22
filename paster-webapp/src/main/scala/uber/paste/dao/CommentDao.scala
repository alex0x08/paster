package uber.paste.dao

import org.springframework.transaction.annotation.Transactional
import javax.persistence.Query
import org.springframework.stereotype.Repository
import uber.paste.model.{Comment, Paste}


@Repository("commentDao")
@Transactional(readOnly = true)
class CommentDaoImpl extends SearchableDaoImpl[Comment](classOf[Comment]) {

  
  def getCommentsForPaste(pasteId:Long):java.util.List[Comment] = {

    logger.debug("get comments id= {0} rev={1}",+pasteId)
    
    val cr = new CriteriaSet

    val query = em.createQuery[Comment](
      cr.cr.where(Array(cr.cb.equal(cr.r.get("pasteId"), pasteId)):_*)
        .orderBy(cr.cb.asc(cr.r.get("lastModified"))))
        .setMaxResults(BaseDaoImpl.MAX_RESULTS)
    return query.getResultList()
  
  }

  
}
