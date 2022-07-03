package org.apache.tiles.request.servlet.extractor

import jakarta.servlet.http.HttpServletRequest
import org.apache.tiles.request.attribute.HasKeys
import java.util

class ParameterExtractor(request:HttpServletRequest) extends HasKeys[String]{


  @SuppressWarnings(Array("unchecked"))
  def getKeys: util.Enumeration[String] = request.getParameterNames

  def getValue(key: String): String = request.getParameter(key)

}
