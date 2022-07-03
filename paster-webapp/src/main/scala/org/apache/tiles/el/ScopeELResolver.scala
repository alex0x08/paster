package org.apache.tiles.el

import jakarta.el.ELResolver
import org.apache.tiles.request.Request
import scala.jdk.CollectionConverters._
import jakarta.el.ELContext
import java.beans.FeatureDescriptor
import java.util
import java.util.Collections


/**
 * Resolves beans in request, session and application scope.
 *
 * @version $Rev$ $Date$
 * @since 2.2.1
 */
class ScopeELResolver extends ELResolver{


  /**
   * The length of the suffix: "Scope".
   */
  private val SUFFIX_LENGTH = 5

  /** {@inheritDoc } */
  def getCommonPropertyType(context: ELContext, base: Any): Class[_] = { // only resolve at the root of the context
    if (base != null)
        return null
    classOf[java.util.Map[_,_]]
  }

  def getFeatureDescriptors(context: ELContext, base: Any): java.util.Iterator[FeatureDescriptor] = {
    if (base != null) {
      val retValue:java.util.List[FeatureDescriptor] = Collections.emptyList
      return retValue.iterator
    }
    val list = new util.ArrayList[FeatureDescriptor]
    val request = context.getContext(classOf[Request]).asInstanceOf[Request]
    for (scope:String <- request.getAvailableScopes.asScala) {
      val descriptor = new FeatureDescriptor
      descriptor.setDisplayName(scope + "Scope")
      descriptor.setExpert(false)
      descriptor.setHidden(false)
      descriptor.setName(scope + "Scope")
      descriptor.setPreferred(true)
      descriptor.setShortDescription("")
      descriptor.setValue("type", classOf[java.util.Map[_,_]])
      descriptor.setValue("resolvableAtDesignTime", java.lang.Boolean.FALSE)
      list.add(descriptor)
    }
    list.iterator
  }

  def getType(context: ELContext, base: Any, property: Any): Class[_] = {
    if (base != null || property == null || !property.isInstanceOf[String]
      || !(property.asInstanceOf[String]).endsWith("Scope")) return null
    classOf[java.util.Map[_,_]]
  }

  def getValue(context: ELContext, base: Any, property: Any): Any = {
    if (base != null) return null
    var retValue:Object = null
    val propertyString = property.asInstanceOf[String]
    if (property.isInstanceOf[String] && propertyString.endsWith("Scope")) {
      val request = context.getContext(classOf[Request]).asInstanceOf[Request]
      retValue = request.getContext(propertyString
        .substring(0, propertyString.length - SUFFIX_LENGTH))
    }
    if (retValue != null) context.setPropertyResolved(true)
    retValue
  }

  def isReadOnly(context: ELContext, base: Any, property: Any): Boolean = {
    if (context == null) throw new NullPointerException
    true
  }

  def setValue(context: ELContext, base: Any, property: Any, value: Any): Unit = {
    // Does nothing for the moment.
  }
}
