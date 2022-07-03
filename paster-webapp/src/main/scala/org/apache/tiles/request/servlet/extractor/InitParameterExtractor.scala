package org.apache.tiles.request.servlet.extractor

import jakarta.servlet.ServletContext
import org.apache.tiles.request.attribute.HasKeys
import java.util

class InitParameterExtractor(context:ServletContext ) extends HasKeys[String]{

  @SuppressWarnings(Array("unchecked")) def getKeys: util.Enumeration[String] = context.getInitParameterNames

  def getValue(key: String): String = context.getInitParameter(key)

}
