package org.apache.tiles.request.servlet.extractor

import jakarta.servlet.http.HttpServletRequest
import org.apache.tiles.request.attribute.AttributeExtractor

class RequestScopeExtractor(request:HttpServletRequest) extends  AttributeExtractor{

  import java.util

  def setValue(name: String, value: Any): Unit = {
    request.setAttribute(name, value)
  }

  def removeValue(name: String): Unit = {
    request.removeAttribute(name)
  }

  @SuppressWarnings(Array("unchecked"))
  def getKeys: util.Enumeration[String] = request.getAttributeNames

  def getValue(key: String): Any = request.getAttribute(key)

}
