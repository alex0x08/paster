package org.apache.tiles.request.servlet.extractor

import jakarta.servlet.http.HttpServletRequest
import org.apache.tiles.request.attribute.AttributeExtractor
import jakarta.servlet.http.HttpSession
import java.util

class SessionScopeExtractor(request:HttpServletRequest) extends AttributeExtractor{


  def setValue(name: String, value: Any): Unit = {
    request.getSession.setAttribute(name, value)
  }

  def removeValue(name: String): Unit = {
    val session = request.getSession(false)
    if (session != null) session.removeAttribute(name)
  }

  @SuppressWarnings(Array("unchecked")) def getKeys: util.Enumeration[String] = {
    val session = request.getSession(false)
    if (session != null) return session.getAttributeNames
    null
  }

  def getValue(key: String): Any = {
    val session = request.getSession(false)
    if (session != null) return session.getAttribute(key)
    null
  }

}
