/*
 * Copyright 2015 Ubersoft, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Ox08.paster.webapp.mvc.view
import org.apache.commons.lang3.StringUtils
import org.springframework.oxm.Marshaller
import org.springframework.web.servlet.view.xml.MarshallingView
class ConfigurableXmlView(marshaller: Marshaller, modelKeys: String) extends MarshallingView(marshaller) {
  if (logger.isDebugEnabled)
    logger.debug(String.format("generating xml view, available model keys %s",
      modelKeys))
  override protected
  def locateToBeMarshalled(model: java.util.Map[String, Object]): Object = {
    if (StringUtils.isBlank(modelKeys)) {
      return super.locateToBeMarshalled(model)
    }
    for (modelKey <- modelKeys.split(",")) {
      if (model.containsKey(modelKey)) {
        val o = model.get(modelKey)
        if (o != null && marshaller.supports(o.getClass)) {
          if (logger.isDebugEnabled)
            logger.debug(String.format("generating xml view, for model %s",
              modelKey))
          return o
        }
      }
    }
    null
  }
}