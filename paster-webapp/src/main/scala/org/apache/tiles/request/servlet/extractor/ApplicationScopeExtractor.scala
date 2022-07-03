package org.apache.tiles.request.servlet.extractor

import jakarta.servlet.ServletContext
import org.apache.tiles.request.attribute.AttributeExtractor
import java.util

class ApplicationScopeExtractor(context: ServletContext)  extends AttributeExtractor{

  def setValue(name: String, value: Any): Unit = {
    context.setAttribute(name, value)
  }

  def removeValue(name: String): Unit = {
    context.removeAttribute(name)
  }

  @SuppressWarnings(Array("unchecked")) def getKeys: util.Enumeration[String] = context.getAttributeNames

  def getValue(key: String): Any = context.getAttribute(key)
}
