/*
 * Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import scala.collection.mutable
import scala.jdk.CollectionConverters._

object ElementsDao {
  val FORBIDDEN_CHARS: Array[Char] = "@\"'[]{}$#!^&*()`:;?=,./\\".toCharArray
}

/**
 * A preudo-DAO classes to store collection of supported code types
 *
 * @param codeTypesString
 *        list of supported code types, separated by comma (,)
 * @param codeTypeDefault
 *        a default code type
 */
@Service
class CodeTypeDao(@Value("${paster.codeTypes:null}")
                  codeTypesString: String,
                  @Value("${paster.codeTypes.default:'plain'}")
                  codeTypeDefault: String
                 ) extends AbstractStringBasedDao(codeTypesString, codeTypeDefault) {}
@Service
class PriorityDao(@Value("${paster.priorities:null}")
                  prioritiesString: String,
                  @Value("${paster.priorities.default:'Normal'}")
                  priorityDefault: String) extends AbstractStringBasedDao(prioritiesString, priorityDefault) {}
@Service
class ChannelDao(@Value("${paster.channels:null}")
                 channelsString: String, @Value("${paster.channels.default:'Default'}")
                 channelDefault: String) extends AbstractStringBasedDao(channelsString, channelDefault) {}
/**
 * Abstract String Based DAO
 * Takes data from single comma-separated string
 *
 * @param elementsAsString
 *          comma-separated list of elements
 * @param defaultElement
 *        default element, will be appended if not found in list
 */
abstract class AbstractStringBasedDao(elementsAsString: String,
                                      defaultElement: String) extends Logged {
  var elements: mutable.Set[String] = mutable.Set[String]()
  if (StringUtils.isBlank(elementsAsString))
      elements += defaultElement
  else {
    for (ch <- elementsAsString.split(",")) {
      if (!StringUtils.isBlank(ch)) {
        val ch2 = removeForbidden(ch)
        if (!StringUtils.isBlank(ch2))
            elements += ch2
      }
    }
    if (!elements.contains(defaultElement))
      elements.add(defaultElement)
  }
  private[dao] def removeForbidden(ch:String): String = {
    var out = ch
    for (c<-ElementsDao.FORBIDDEN_CHARS) {
      out = out.replace(c,null.asInstanceOf[Char])
    }
    out
  }

  def getAvailableElements: Set[String] = elements.toSet
  def getDefault: String = defaultElement
  def exist(name: String): Boolean = elements.exists(p => p.equals(name) || p.toLowerCase.equals(name) )
}
/**
 * A repository for tags
 */
@Repository("tagDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class TagDao extends BaseDao[Tag, java.lang.Long](classOf[Tag]) {
  /**
   * Fetch all available tags as map
   * @return
   *    map with key = tag string and value = tag object
   */
  def getTagsMap: Map[String, Tag] = {
    val out = mutable.Map[String, Tag]()
    for (t <- getAll.asScala) {
      out +=(t.name -> t)
    }
    out.toMap
  }
  /**
   * Fetch all tags as list with count for each tag
   * @return
   *    list of all tags
   */
  def getTags: List[Tag] = {
    var out = List[Tag]()
    val l = em.createQuery("select t, count(t) from Paste p join p.tagsMap t group by t")
      .setMaxResults(100)
      .getResultList
    for (o <- l.asScala) {
      val oo = o.asInstanceOf[Array[Object]]
      val tag = oo(0).asInstanceOf[Tag]
      val total = oo(1).asInstanceOf[java.lang.Long]
      tag.total = total.toInt
      out = out :+ tag
    }
    out
  }
}
