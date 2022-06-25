/*
 * Copyright 2011 Ubersoft, LLC.
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

package uber.paste.model


import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import org.hibernate.search.annotations.{Field, Index, Store, TermVector}
import java.beans.PropertyEditorSupport
import java.util.{Collection, HashMap}
import javax.persistence._
import javax.validation.constraints.NotNull

object Key extends Named {
  abstract class Builder[T <: Key](model: T) extends Named.Builder[T](model) {
    def addCode(code: String): Builder[T] = {
      get().setCode(code)
      return this
    }
  }
}


/**
 * Key structure. The struct which has special uniqie text key
 */

class KeyObj[T <: Key] {

  val map = new HashMap[String, T]

  def add(c: T) {
    map.put(c.getCode, c)
  }

  def getList: Collection[T] = list

  def list: Collection[T] = map.values

  def valueOf(key: String): T =
    if (map.containsKey(key)) map.get(key) else null.asInstanceOf[T]

}


class KeyEditorEnum[T <: Key](vobj: KeyObj[T]) extends PropertyEditorSupport {

  override def setAsText(text: String) {
    setValue(vobj.valueOf(text.toLowerCase));
  }

  override def getAsText(): String = {
    val s = getValue().asInstanceOf[T]
    return if (s == null)
      null
    else
      s.getCode

  }
}


class KeyEditor[T <: Key](vobj: Key) extends PropertyEditorSupport {

  override def setAsText(text: String) {

    setValue(vobj.create(text.toLowerCase))
  }

  override def getAsText(): String = {
    val s = getValue().asInstanceOf[T]
    return if (s == null)
      null
    else
      s.getCode

  }
}


@MappedSuperclass
class Key(kcode: String, kname: String) extends Named(kname) with java.io.Serializable {

  @NotNull
  @Column(nullable = false, length = 50, unique = true)
  @XStreamAsAttribute
  @Field(index = Index.YES, store = Store.YES, termVector = TermVector.YES) //,boost=@Boost(2f)
  //@SafeHtml(whitelistType=WhiteListType.NONE,message = "{validator.forbidden-symbols}")
  private var code: String = kcode

  @Column(name = "k_translated")
  private var translated: Boolean = false

  def this() = this(null, null)

  def isTranslated() = translated

  def setTranslated(v: Boolean) {
    translated = v
  }

  def getCode() = code

  def setCode(f: String) {
    code = if (f != null)
      f.toUpperCase else null
  }

  def create(code: String): Any = null

  override def hashCode(): Int = {
    var hash: Int = 53 * 7;

    // if (id != null) 
    //   hash+=id.hashCode();
    if (code != null)
      hash += code.hashCode();

    return hash;
  }

  override def equals(from: Any): Boolean = (from.isInstanceOf[Key] && getCode != null
    && from.asInstanceOf[Key].getCode().equals(code))


}
