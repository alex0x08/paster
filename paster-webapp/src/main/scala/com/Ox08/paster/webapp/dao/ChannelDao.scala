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

import com.Ox08.paster.webapp.base.Loggered
import com.Ox08.paster.webapp.model.Tag
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.{Repository, Service}
import org.springframework.transaction.annotation.Transactional
import java.util
import java.util.{ArrayList, Collections, HashMap}
import scala.jdk.CollectionConverters._


@Service
class PriorityDao(@Value("${paster.priorities:null}")
                 prioritiesString: String) extends Loggered {

  val priorities: util.Set[String] = new util.TreeSet[String]()

  if (StringUtils.isBlank(prioritiesString)) {
    priorities.add("Blocker")
    priorities.add("Normal")
  } else
    for (ch <- prioritiesString.split(",")) {
      priorities.add(ch)
    }

  def getAvailablePriorities() = Collections.unmodifiableSet(priorities)

  def getDefault() = priorities.iterator().next()

  def exist(name: String) = priorities.contains(name)

}

@Service
class ChannelDao(@Value("${paster.channels:null}")
                 channelsString: String) extends Loggered {

  val channels: util.Set[String] = new util.TreeSet[String]()

  if (StringUtils.isBlank(channelsString))
    channels.add("Default") else
    for (ch <- channelsString.split(",")) {
      channels.add(ch)
    }

  def getAvailableChannels() = Collections.unmodifiableSet(channels)

  def getDefault() = channels.iterator().next()

  def exist(name: String) = channels.contains(name)

}

@Repository("tagDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class TagDao extends StructDaoImpl[Tag](classOf[Tag]) {

  def getTagsMap(): java.util.Map[String, Tag] = {
    val out = new HashMap[String, Tag]

    for (t <- getAll().asScala) {
      out.put(t.getName, t)
    }

    out
  }


  def getTags(): java.util.List[Tag] = {

    val out = new ArrayList[Tag]
    val l = em.createQuery("select t, count(t) from Paste p join p.tagsMap t group by t")
      .getResultList()

    for (o <- l.asScala) {
      val oo = o.asInstanceOf[Array[Object]]
      val tag = oo(0).asInstanceOf[Tag]
      val total = oo(1).asInstanceOf[java.lang.Long]
      tag.setTotal(total.toInt)
      out.add(tag)
    }
    out
  }
}
