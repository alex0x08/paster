package uber.paste.dao

import uber.paste.model.Struct
import org.springframework.transaction.annotation.{Propagation, Transactional}
import java.lang.Number
import org.hibernate.envers.{AuditReader, AuditReaderFactory}
import org.apache.poi.hssf.record.formula.functions.T

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 09.04.13
 * Time: 14:24
 * To change this template use File | Settings | File Templates.
 */
abstract trait VersionDao[T <: Struct] extends StructDao[T] {

  def getCurrentRevisionNumber(id:Long):Number

  def getRevisions(id:java.lang.Long):java.util.List[Number]

  def getRevision(id:java.lang.Long, rev:java.lang.Number):T

  def revertToRevision(id:java.lang.Long, rev:java.lang.Number)
}

@Transactional(readOnly = true)
abstract class VersionDaoImpl[T <: Struct](model:Class[T]) extends StructDaoImpl[T](model) with VersionDao[T] {

  def getCurrentRevisionNumber(id:Long):Number= {
    val revs = getRevisions(id)
    return if (revs!=null && !revs.isEmpty()) {
              revs.get(revs.size()-1)  } else { 0 }
  }

  /**
   * {@inheritDoc}
   */
  def getRevisions(id:java.lang.Long):java.util.List[Number]= {
    return getReader().getRevisions(model, id)
  }

  /**
   * {@inheritDoc}
   */
  def getRevision(id:java.lang.Long, rev:java.lang.Number):T = {
    /**
     * fix stupid envers bug
     */
    var r =rev
    if (rev.isInstanceOf[java.lang.Long]) {
      r = new java.lang.Integer(rev.intValue())
    }

    val out = getReader().find(model, id, r)
    if (out==null) {
      return null.asInstanceOf[T]
    }
      //.asInstanceOf[T]

    logger.debug("getRevision id=" + id + ",rev=" + rev + ",found=" + out)

    out.loadFull()

    return out
  }

  /**
   * {@inheritDoc}
   */
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  def revertToRevision(id:java.lang.Long, rev:java.lang.Number) {

    save(getRevision(id, rev))

  }

  /**
   * returns AuditReader object, used for all versioning actions
   *
   * @return AuditReader assigned to current hibernate session
   */
  protected def getReader():AuditReader = {
    return AuditReaderFactory.get(em)
  }

}
