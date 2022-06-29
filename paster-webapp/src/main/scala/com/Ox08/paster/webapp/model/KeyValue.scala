/*
 * Copyright 2011 WorldWide Conferencing, LLC.
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

package com.Ox08.paster.webapp.model

import java.beans.PropertyEditorSupport
import javax.persistence._

/**
 * The structure with key and value
 *
 */


object KeyValue extends Key {

  abstract class Builder[T <: KeyValue](model: T) extends Key.Builder[T](model) {
    def addValue(v: String): Builder[T] = {
      get().setValue(v)
      this
    }
  }
}

class KeyValueEditor[T <: KeyValue](vobj: KeyValueObj[T]) extends PropertyEditorSupport {
  override def setAsText(text: String) {
    setValue(vobj.valueOf(text.toLowerCase))
  }
  override def getAsText(): String = {
    if (getValue() == null) return null
    getValue().asInstanceOf[T].getCode()
  }
}

class KeyValueObj[T <: KeyValue] extends KeyObj[T] {}

@MappedSuperclass
class KeyValue(code: String, kvalue: String, kname: String)
  extends Key(code, kname) with java.io.Serializable {
  @Column(name = "pvalue")
  private var value: String = kvalue
  def getValue() = value
  def setValue(f: String) {
    value = f
  }

}
