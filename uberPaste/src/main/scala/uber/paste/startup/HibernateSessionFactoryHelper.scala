package uber.paste.startup

import org.springframework.stereotype.Component
import javax.persistence.{EntityManager, PersistenceContext}
import org.hibernate.SessionFactory
import org.hibernate.ejb.HibernateEntityManagerFactory

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 09.02.13
 * Time: 19:49
 */
@Component("sessionFactoryHelper")
class HibernateSessionFactoryHelper {

  @PersistenceContext
  protected val em:EntityManager = null

  def getSessionFactory():SessionFactory = em.getEntityManagerFactory.asInstanceOf[HibernateEntityManagerFactory].getSessionFactory
}
