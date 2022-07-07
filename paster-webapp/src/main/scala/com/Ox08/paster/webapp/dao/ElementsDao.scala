/*
 * Copyright 2016 Ubersoft, LLC.
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

package com.Ox08.paster.webapp.dao

import com.Ox08.paster.webapp.base.Logged
import com.Ox08.paster.webapp.model.Tag
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.{Repository, Service}
import org.springframework.transaction.annotation.Transactional
import java.util
import java.util.Collections
import scala.jdk.CollectionConverters._


@Service
class CodeTypeDao(@Value("${paster.codeTypes:null}")
                  codeTypesString: String) extends AbstractStringBasedDao(codeTypesString, "plain") {

}


@Service
class PriorityDao(@Value("${paster.priorities:null}")
                  prioritiesString: String) extends AbstractStringBasedDao(prioritiesString, "Normal") {

}

@Service
class ChannelDao(@Value("${paster.channels:null}")
                 channelsString: String) extends AbstractStringBasedDao(channelsString, "Default") {

}

abstract class AbstractStringBasedDao(elementsAsString: String,
                                      defaultElement: String) extends Logged {

  val elements: util.Set[String] = new util.TreeSet[String]()

  if (StringUtils.isBlank(elementsAsString))
    elements.add(defaultElement) else
    for (ch <- elementsAsString.split(",")) {
      elements.add(ch)
    }

  def getAvailableElements: util.Set[String] = Collections.unmodifiableSet(elements)

  def getDefault: String = defaultElement

  def exist(name: String): Boolean = elements.contains(name)

}


@Repository("tagDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class TagDao extends BaseDao[Tag, java.lang.Long](classOf[Tag]) {

  def getTagsMap: java.util.Map[String, Tag] = {
    val out = new util.HashMap[String, Tag]
    for (t <- getAll.asScala) {
      out.put(t.name, t)
    }
    out
  }

  def getTags: java.util.List[Tag] = {
    val out = new util.ArrayList[Tag]
    val l = em.createQuery("select t, count(t) from Paste p join p.tagsMap t group by t")
      .setMaxResults(100)
      .getResultList

    for (o <- l.asScala) {
      val oo = o.asInstanceOf[Array[Object]]
      val tag = oo(0).asInstanceOf[Tag]
      val total = oo(1).asInstanceOf[java.lang.Long]
      tag.total = total.toInt
      out.add(tag)
    }
    out
  }
}
