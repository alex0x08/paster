package org.apache.tiles.el

import jakarta.el.ELResolver
import org.apache.tiles.request.Request

import java.util.Map.Entry
import scala.jdk.CollectionConverters._

class TilesContextBeanELResolver extends ELResolver{

  import jakarta.el.ELContext
  import java.beans.FeatureDescriptor
  import java.util

  /** {@inheritDoc } */
  def getCommonPropertyType(context: ELContext, base: Any): Class[_] = { // only resolve at the root of the context
    if (base != null) return null
    classOf[String]
  }

  def getFeatureDescriptors(context: ELContext, base: Any): java.util.Iterator[FeatureDescriptor] = {
    val list = new util.ArrayList[FeatureDescriptor]
    val request = context.getContext(classOf[Request]).asInstanceOf[Request]

    for (scope:String <- request.getAvailableScopes.asScala) {
      collectBeanInfo(request.getContext(scope), list)
    }
    list.iterator
  }

  def getType(context: ELContext, base: Any, property: Any): Class[_] = {
    if (base != null) return null
    val obj = findObjectByProperty(context, property)
    if (obj != null) {
      context.setPropertyResolved(true)
      return obj.getClass
    }
    null
  }

  def getValue(context: ELContext, base: Any, property: Any): Any = {
    if (base != null) return null
    val retValue = findObjectByProperty(context, property)
    if (retValue != null)
      context.setPropertyResolved(true)
    retValue
  }

  def isReadOnly(context: ELContext, base: Any, property: Any): Boolean = {
    if (context == null) throw new NullPointerException
    true
  }

  def setValue(context: ELContext, base: Any, property: Any, value: Any): Unit = {
    // Does nothing for the moment.
  }

  /**
   * Collects bean infos from a map's values and filling a list.
   *
   * @param map  The map containing the bean to be inspected.
   * @param list The list to fill.
   * @since 2.2.1
   */
  protected def collectBeanInfo(map: java.util.Map[String,Object],
                                list: java.util.List[FeatureDescriptor]): Unit = {
    if (map == null || map.isEmpty) return
    for (entry:Entry[String,Object] <- map.entrySet.asScala) {
      val descriptor = new FeatureDescriptor
      descriptor.setDisplayName(entry.getKey)
      descriptor.setExpert(false)
      descriptor.setHidden(false)
      descriptor.setName(entry.getKey)
      descriptor.setPreferred(true)
      descriptor.setShortDescription("")
      descriptor.setValue("type", classOf[String])
      descriptor.setValue("resolvableAtDesignTime", java.lang.Boolean.FALSE)
      list.add(descriptor)
    }
  }

  /**
   * Finds an object in request, session or application scope, in this order.
   *
   * @param context  The context to use.
   * @param property The property used as an attribute name.
   * @return The found bean, if it exists, or <code>null</code> otherwise.
   * @since 2.2.1
   */
  protected def findObjectByProperty(context: ELContext, property: Any): Any = {
    var retValue:Any = null
    val request = context.getContext(classOf[Request]).asInstanceOf[Request]
    val prop = property.toString
    val scopes = request.getAvailableScopes.toArray(new Array[String](0))
    var i = 0
    do {
      retValue = getObject(request.getContext(scopes(i)), prop)
      i += 1
    } while ( {
      retValue == null && i < scopes.length
    })
    retValue
  }

  /**
   * Returns an object from a map in a null-safe manner.
   *
   * @param map      The map to use.
   * @param property The property to use as a key.
   * @return The object, if present, or <code>null</code> otherwise.
   * @since 2.2.1
   */
  protected def getObject(map: java.util.Map[String,_], property: String): Any = {
    var retValue:Any = null
    if (map != null)
        retValue = map.get(property)
    retValue
  }
}
