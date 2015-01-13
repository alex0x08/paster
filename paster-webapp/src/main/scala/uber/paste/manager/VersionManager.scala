package uber.paste.manager

import uber.paste.model.{Query, Struct}
import uber.paste.dao.{VersionDao, SearchableDao}

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 09.04.13
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */


trait VersionManager[T <: Struct] extends StructManager[T] {

  def getCurrentRevisionNumber(id:Long):Number

  def getRevisions(id:java.lang.Long):java.util.List[Number]

  def getRevision(id:java.lang.Long, rev:java.lang.Number):T

  def revertToRevision(id:java.lang.Long, rev:java.lang.Number)
}

abstract class VersionManagerImpl[T <: Struct] extends StructManagerImpl[T] {

  protected override def getDao:VersionDao[T]

  def getCurrentRevisionNumber(id:Long):Number = getDao.getCurrentRevisionNumber(id)

  def getRevisions(id:java.lang.Long):java.util.List[Number] =getDao.getRevisions(id)

  def getRevision(id:java.lang.Long, rev:java.lang.Number):T =getDao.getRevision(id,rev)

  def revertToRevision(id:java.lang.Long, rev:java.lang.Number) =getDao.revertToRevision(id,rev)

}


