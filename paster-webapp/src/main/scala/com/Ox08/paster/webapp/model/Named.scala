/*
 * Copyright 2014 Ubersoft, LLC.
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

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import jakarta.persistence.{Column, MappedSuperclass}
import jakarta.validation.constraints.{NotNull, Size}
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField

object Named extends Struct {

  abstract class Builder[T <: Named](model: T) extends Struct.Builder[T](model) {
    def addName(name: String): Builder[T] = {
      get().setName(name)
      this
    }
  }
}

@MappedSuperclass
//@Audited
class Named(kname: String) extends Struct {

  @NotNull
  @FullTextField
  @Column(length = 256)
  //@Pattern(regexp = "(.+)", message = "{struct.name.validator}")
  @Size(min = 3, message = "{struct.name.validator}")
  @XStreamAsAttribute
  private var name: String = kname

  def this() = this(null)

  override def terms(): List[String] = super.terms() ::: List[String]("name")

  /*
   override def fillFromHits(ch:CompassHighlighter)  {
    super.fillFromHits(ch)
    
    val f = ch.fragment("name")
    if (f!=null) {
      setName(f)
    }
  }
  */

  def fillFromDTO(dto: Named): Unit = {
    setName(dto.name)
  }

  def getName() = name

  def setName(f: String): Unit = {
    name = f
  }

}
