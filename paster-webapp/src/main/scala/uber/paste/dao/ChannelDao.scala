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

package uber.paste.dao

import java.util.ArrayList
import java.util.HashMap
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import uber.paste.model.Channel
import uber.paste.model.Tag
import scala.collection.JavaConversions._


@Repository("channelDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class ChannelDao extends KeyDaoImpl[Channel](classOf[Channel]){

  
   def getDefault() = getSingleByKeyValue("default", true)
}

@Repository("tagDao")
@Transactional(readOnly = true, rollbackFor = Array(classOf[Exception]))
class TagDao extends StructDaoImpl[Tag](classOf[Tag]){

  def getTagsMap():java.util.Map[String,Tag] = {
    val out = new HashMap[String,Tag]
    
    for (t <- getAll) {
        out.put(t.getName,t)
    }
    
    return out
  }
  
  
  def getTags():java.util.List[Tag] = {
      
    val out = new ArrayList[Tag]
    
   val l =  em.createQuery("select t, count(t) from Paste p join p.tagsMap t group by t")
   .getResultList
    
    for (o<-l) {
     val oo = o.asInstanceOf[Array[Object]]
      
      val tag = oo(0).asInstanceOf[Tag]
      val total = oo(1).asInstanceOf[java.lang.Long]
      
      tag.setTotal(total.toInt)
      
      out.add(tag)
    }
    return out
  }
}
