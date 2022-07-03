package org.springframework.web.servlet.view.tiles3

import org.springframework.util.ClassUtils

class SimpleSpringPreparerFactory extends  AbstractSpringPreparerFactory {

  import org.apache.tiles.TilesException
  import org.apache.tiles.preparer.PreparerException
  import org.apache.tiles.preparer.ViewPreparer
  import org.apache.tiles.preparer.factory.NoSuchPreparerException
  import org.springframework.web.context.WebApplicationContext
  import java.util
  import java.util.concurrent.ConcurrentHashMap

  /** Cache of shared ViewPreparer instances: bean name -> bean instance. */
  private val sharedPreparers = new ConcurrentHashMap[String, ViewPreparer](16)


  @throws[TilesException]
  protected def getPreparer(name: String, context: WebApplicationContext): ViewPreparer = { // Quick check on the concurrent map first, with minimal locking.
    var preparer = this.sharedPreparers.get(name)
    if (preparer == null) {

      this.sharedPreparers.synchronized {
        preparer  = this.sharedPreparers.get(name)
      }
      if (preparer == null) try {
        val beanClass = ClassUtils.forName(name, context.getClassLoader)
        if (!classOf[ViewPreparer].isAssignableFrom(beanClass))
            throw new PreparerException("Invalid preparer class [" + name + "]: does not implement ViewPreparer interface")
        preparer = context.getAutowireCapableBeanFactory.createBean(beanClass).asInstanceOf[ViewPreparer]
        this.sharedPreparers.put(name, preparer)
      } catch {
        case ex: ClassNotFoundException =>
          throw new NoSuchPreparerException("Preparer class [" + name + "] not found", ex)
      }

    }
    preparer
  }

}
