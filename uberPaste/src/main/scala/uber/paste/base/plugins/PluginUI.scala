/*
 * Copyright 2013 Ubersoft, LLC.
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

package uber.paste.base.plugins

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import java.io.InputStream
import java.util.{Map,HashMap, AbstractMap}
import javax.xml.bind.annotation.XmlRootElement
import uber.paste.model.Key
import scala.collection.JavaConversions._

/**
 * 
 * <plugin-ui>
  <section disabled="false" name="Pastas list" code="paste-list">
    <element disabled="false" name="top panel" code="top-panel">
      <extension disabled="false" name="Sample plugin" code="sample-binding">
        <jspUrl>/fuck.jsp</jspUrl>
      </extension>
    </element>
  </section>
</plugin-ui>
 * 
 */

object PluginUI {
  
  private val xStream = new XStream()
     
  xStream.processAnnotations(classOf[PluginUI])
  
  //xStream.registerConverter(new JavaBeanConverter(xStream.getMapper()))
  
  private var store = new PluginUI
  
  def load(xml:String) {
    store =xStream.fromXML(xml).asInstanceOf[PluginUI]
  }
  
  def load(xml:InputStream) {
    store =xStream.fromXML(xml).asInstanceOf[PluginUI]
  }
  
  def append(xml:String) {
    
    val a =xStream.fromXML(xml).asInstanceOf[PluginUI]
    store.merge(a.sections)
    
  }
  
  def append(xml:InputStream) {
    
    val a =xStream.fromXML(xml).asInstanceOf[PluginUI]
    store.merge(a.sections)
    
  }
  
  def append(xml:java.net.URL) {
   val a =xStream.fromXML(xml).asInstanceOf[PluginUI]
    store.merge(a.sections)
  }
  
  def getXml():String = xStream.toXML(store)
 
  def getInstance = store
}

//@XmlRootElement(name="plugin-ui")
@XStreamAlias("plugin-ui")
class PluginUI {
 
  @XStreamImplicit(itemFieldName="section",keyFieldName="code")
  private var sections:Map[String,UISection] = new HashMap[String,UISection]()

  def getSections() = sections
  def setSections(el:Map[String,UISection]) { 
    if (el!=null && !el.isEmpty) {
      this.sections = el
    }
  }

  def addSection(s:UISection ) {
    sections.put(s.getCode, s)
  }
  
  def merge(el:Map[String,UISection]) {
    
     for (e<-el.values) {
      if (sections.containsKey(e.getCode)) {
         val section = sections.get(e.getCode)
         section.merge(e.getElements)
        sections.put(section.getCode, section)
      }
    }
    
  }
  
}

@XStreamAlias("ui-section")
class UISection extends Key {
  
  @XStreamImplicit(itemFieldName="element",keyFieldName="code")
  private var elements:Map[String,UIElement] = new HashMap[String,UIElement]()
  
  def this(name:String,code:String) = {
    this()
    setName(name)
    setCode(code)    
  }

   def addElement(s:UIElement ) {
    elements.put(s.getCode, s)
  }
  
  def getElements() = elements
  def setElements(el:Map[String,UIElement]) { 
    if (el!=null && !el.isEmpty) {
      this.elements = el
    }
  }

  def merge(el:Map[String,UIElement]) {
    for (e<-el.values) {
      if (elements.containsKey(e.getCode)) {
        val element = elements.get(e.getCode)
        element.merge(e.getExtensions)
        elements.put(element.getCode, element)
      }
    }
  }
}

@XStreamAlias("ui-element")
class UIElement extends Key{
  
  @XStreamImplicit(itemFieldName="extension",keyFieldName="code")
  private var extensions:Map[String,UIExtension] = new HashMap[String,UIExtension]()
  
  def this(name:String,code:String) = {
    this()
    setName(name)
    setCode(code)    
  }

   def addExtension(s:UIExtension ) {
    extensions.put(s.getCode, s)
  }
  
  def getExtensions() = extensions
  def setExtensions(el:Map[String,UIExtension]) {
    if (el!=null && !el.isEmpty) {
    this.extensions = el
    }
  }

  def merge(from:Map[String,UIExtension]) {
    if (from!=null && !from.isEmpty) {
      //System.out.println("ext="+extensions+" from="+from)
      
      if (extensions==null) {
        extensions = new HashMap[String,UIExtension]()
      }
      extensions.putAll(from)
    }
  }
}

@XStreamAlias("extension")
class UIExtension extends Key {
  
   private var jspUrl:String = null

def this(name:String,code:String,jspUrl:String) = {
    this()
    setName(name)
    setCode(code)
    this.jspUrl=jspUrl
  }
  
  def getJspUrl() = jspUrl
  def setJspUrl(url:String) { this.jspUrl = url}
}

/*
class MapEntryConverter[T <: Key] extends Converter {
  
 override def canConvert(clazz:Class[_]):Boolean = classOf[java.util.AbstractMap[String,T]].isAssignableFrom(clazz)
    

    override def marshal(value:Object,
                        writer:HierarchicalStreamWriter,
                        context:MarshallingContext)
    {
        //noinspection unchecked
        val  map:AbstractMap[String, T] = value.asInstanceOf[AbstractMap[String,T]]
        
      for (entry <- map.entrySet())
        {
            //noinspection RedundantStringToString
            writer.startNode(entry.getKey().toString())
            //noinspection RedundantStringToString
            writer.setValue(entry.getValue().toString())
            writer.endNode()
        }
    }

    override def unmarshal(reader:HierarchicalStreamReader,
                            context:UnmarshallingContext):Object =
    {
        val map = new HashMap[String, T]()

        while (reader.hasMoreChildren())
        {
            reader.moveDown();
            map.put(reader.getAttribute("code"), reader.getValue)
            reader.moveUp()
        }

        return map
    }
}
*/