/**
 * Copyright (C) 2010 alex <me@alex.0x08.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package uber.paste.base

import org.slf4j.LoggerFactory
import org.apache.commons.lang.builder.{StandardToStringStyle, ToStringBuilder}
import javax.jws.WebMethod

object Loggered {

  val  style = new StandardToStringStyle() {
    setFieldSeparator(", ")
    setUseClassName(false)
    setUseIdentityHashCode(false)
  }

  def getNewProtocolBuilder(clazz:AnyRef):ToStringBuilder = new ToStringBuilder(clazz.getClass.getName, Loggered.style)
}

trait Loggered {

  @transient
  @WebMethod(exclude = true)
  def logger = LoggerFactory.getLogger(this.getClass.getName)

  @transient
  @WebMethod(exclude = true)
  def getNewProtocolBuilder():ToStringBuilder = new ToStringBuilder(this, Loggered.style)

}
