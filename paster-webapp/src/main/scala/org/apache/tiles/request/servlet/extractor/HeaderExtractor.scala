package org.apache.tiles.request.servlet.extractor

import org.apache.tiles.request.attribute.EnumeratedValuesExtractor
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util

class HeaderExtractor(request: HttpServletRequest, response: HttpServletResponse)
    extends EnumeratedValuesExtractor {


  @SuppressWarnings(Array("unchecked"))
  def getKeys: util.Enumeration[String] = request.getHeaderNames

  def getValue(key: String): String = request.getHeader(key)

  @SuppressWarnings(Array("unchecked"))
  def getValues(key: String): util.Enumeration[String] = request.getHeaders(key)

  def setValue(key: String, value: String): Unit = {
    response.setHeader(key, value)
  }
}
