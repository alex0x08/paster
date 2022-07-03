package org.apache.tiles.el

import jakarta.el.ELResolver
import org.apache.tiles.request.{ApplicationContext, Request}
import jakarta.el.ELContext
import jakarta.el.ELResolver
import org.apache.tiles.util.CombinedBeanInfo

import java.beans.FeatureDescriptor

class TilesContextELResolver(beanElResolver:ELResolver) extends ELResolver{

  /**
   * The beaninfos about {@link Request} and {@link ApplicationContext}.
   */
  private val requestBeanInfo = new CombinedBeanInfo(classOf[Request],
                      classOf[ApplicationContext])

  /** {@inheritDoc } */
  def getCommonPropertyType(context: ELContext, base: Any): Class[_] = { // only resolve at the root of the context
    if (base != null) return null
    classOf[String]
  }

  def getFeatureDescriptors(context: ELContext, base: Any): java.util.Iterator[FeatureDescriptor] = {
    if (base != null)
          return null
    requestBeanInfo.getDescriptors.iterator
  }

  def getType(context: ELContext, base: Any, property: Any): Class[_] = {
    if (base != null) return null
    var retValue:Class[_] = null
    if (requestBeanInfo.getProperties(classOf[ApplicationContext]).contains(property)) {
      val request = context.getContext(classOf[Request]).asInstanceOf[Request]
      retValue = beanElResolver.getType(context, request, property)
    }
    else if (requestBeanInfo.getProperties(classOf[ApplicationContext]).contains(property)) {
      val applicationContext = context.getContext(classOf[Request]).asInstanceOf[Request]
      retValue = beanElResolver.getType(context, applicationContext, property)
    }
    if (retValue != null) context.setPropertyResolved(true)
    retValue
  }

  def getValue(context: ELContext, base: Any, property: Any): Any = {
    if (base != null)
            return null
    var retValue: Any = null
    if (requestBeanInfo.getProperties(classOf[Request]).contains(property)) {
      val request = context.getContext(classOf[Request]).asInstanceOf[Request]
      retValue = beanElResolver.getValue(context, request, property)
    }
    else if (requestBeanInfo.getProperties(classOf[ApplicationContext]).contains(property)) {
      val applicationContext = context.getContext(classOf[ApplicationContext]).asInstanceOf[ApplicationContext]
      retValue = beanElResolver.getValue(context, applicationContext, property)
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
