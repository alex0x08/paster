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

import com.Ox08.paster.webapp.model.{Channel, Tag}
import org.apache.commons.csv.{CSVFormat, CSVRecord}

import java.util.ArrayList
import java.util.HashMap
import org.springframework.stereotype.{Repository, Service}
import org.springframework.transaction.annotation.Transactional

import java.io.{File, FileReader, InputStreamReader}
import java.util
import scala.jdk.CollectionConverters._

//@Repository("channelDao")
//@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
@Service
class ChannelDao extends KeyDaoImpl[Channel](classOf[Channel]) {

  val channels: util.Map[String,Channel] = new util.LinkedHashMap()

  def reload(src: File): Int = {

    channels.clear()

    loadDefaults(src, (record: CSVRecord) => {
      val ch = new Channel(
        record.get("CODE"),
        record.get("DESC"),
        record.get("ISDEFAULT").toBoolean)
        ch.setTranslated(true)

      channels.put(ch.getCode().toLowerCase(),ch)

    })

    channels.size()
  }

  override def getByKey(code: String): Channel = if (channels.containsKey(code)) channels.get(code) else null

  def loadDefaults(csv: File, callback: CSVRecord => Unit) {
    val r = new FileReader(csv)
    try {
      val records = CSVFormat.DEFAULT.withHeader().parse(r)
      for (record <- records.asScala) {
        callback(record)
      }
    } finally r.close
  }

  def getDefault() = getByKey("default")
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
