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
package com.Ox08.paster.run
import org.eclipse.jetty.util.resource.{ResourceFactory, URLResourceFactory}

import java.io._
import java.net._
import java.util
import java.util.Collections
import java.util.jar.{JarEntry, JarFile}
import java.util.zip.ZipEntry
import scala.collection.mutable
import scala.jdk.CollectionConverters._
/**
 *
 */
object LiveWarClassLoader {
  private val CLASSES_BASE = "WEB-INF/classes/"
  val MAP = new mutable.HashMap[String, Array[Byte]]
  val MAP_DUPLICATES = new mutable.HashMap[String, List[Array[Byte]]]
  private val EMPTY_BA = new Array[Byte](0)
}
/**
 * Custom classloader to load WAR without unpacking
 *
 * @param debug
 *    show/hide debug messages
 * @param warFileUrl
 *    url to WAR file
 * @param parent
 *    parent classloader
 */
class LiveWarClassLoader(debug: Boolean,
                         warFileUrl: URL, parent: ClassLoader)
  extends ClassLoader(parent) with Closeable {
  private val warFileUri = warFileUrl.toURI
  private val warFile = new JarFile(new File(warFileUri))
  loadData()
  private def loadData(): Unit = {
    for (e <- warFile.entries().asScala) {
      if (e.getName.startsWith("WEB-INF/lib")
        && e.getName.endsWith(".jar")) {
        debug(s"found lib: ${e.getName}")
        import java.util.jar.JarInputStream
        val zip = new JarInputStream(warFile.getInputStream(e))
        var ze: JarEntry = null
        while ( {
          ze = zip.getNextJarEntry; ze != null
        }) {
          // we must load directories too, their names should be present
          // in resources map
          if (ze.isDirectory) {
            val key = ze.getName //.substring(0, ze.getName.length - 1)
            LiveWarClassLoader.MAP.put(key, LiveWarClassLoader.EMPTY_BA)
          } else {
            val data = readBytes(zip, 4096)
            if (LiveWarClassLoader.MAP.contains(ze.getName)) {
              debug(s"already loaded: ${ze.getName}")
              var datas: List[Array[Byte]] = if (LiveWarClassLoader.MAP_DUPLICATES.contains(ze.getName))
                LiveWarClassLoader.MAP_DUPLICATES(ze.getName)
              else
                List()
              datas = datas.appended(data)
              debug(s"duplicates ${ze.getName} = ${datas.length}")
              LiveWarClassLoader.MAP_DUPLICATES.put(ze.getName, datas)
            } else LiveWarClassLoader.MAP.put(ze.getName, data)
          }
        }
      }
    }
    import java.net.URL
    URL.setURLStreamHandlerFactory(new VirtualWARURLStreamHandlerFactory(debug))
    ResourceFactory.registerResourceFactory("war-virtual",new URLResourceFactory())
  }
  @throws[IOException]
  override def close(): Unit = {
    warFile.close()
  }
  private def debug(format: String): Unit = {
    if (debug)
      System.out.println(format)
  }
  @throws[ClassNotFoundException]
  override protected def findClass(name: String): Class[_] = {
    val path = name.replace('.', '/').concat(".class")
    val entry = findEntry(path)
    if (entry != null) {
      debug(s"findClass: $name")
      try
        loadClass(name, entry)
      catch {
        case e: IOException =>
          throw new ClassNotFoundException(name, e)
      }
    } else if (LiveWarClassLoader.MAP.contains(path)) {
      debug(s"findClass in map: $name")
      val classBytes = LiveWarClassLoader.MAP(path)
      defineClass(name, classBytes, 0, classBytes.length)
    } else {
      val clazz = super.findClass(name)
      if (clazz == null)
        debug(s"NOT findClass in war/map: $name")
      clazz
    }
  }
  private def findEntry(name: String): ZipEntry = {
    val path: mutable.StringBuilder = new mutable.StringBuilder
    path.append(LiveWarClassLoader.CLASSES_BASE)
    if (name.charAt(0) == '/')
      path.append(name.substring(1))
    else
      path.append(name)
    val entry: ZipEntry = warFile.getEntry(path.toString)
    if (entry != null)
      debug(s"findEntry($name) found $path => $entry")
    entry
  }
  override protected def findResource(name: String): URL = {
    val entry = findEntry(name)
    if (entry != null)
      try {
        debug(s"findResource: $name")
        return URI.create(s"jar:${this.warFileUri.toASCIIString}!/${entry.getName}").toURL
      } catch {
        case e: MalformedURLException =>
          if (debug)
            e.printStackTrace(System.err)
          return null
      }
    else if (LiveWarClassLoader.MAP.contains(name)) {
      debug(s"findResource in map: $name")
      return URI.create(s"war-virtual:$name").toURL
    }
    val url = super.findResource(name)
    if (url == null)
      debug(s"NOT findResource : $name")
    url
  }
  @throws[IOException]
  override protected def findResources(name: String): util.Enumeration[URL] = {
    debug(s"findResources: $name")
    val urls = new util.ArrayList[URL]
    val self = findResource(name)
    if (self != null)
      urls.add(self)
    if (LiveWarClassLoader.MAP_DUPLICATES.contains(name)) {
      val dups = LiveWarClassLoader.MAP_DUPLICATES(name)
      var count = 0
      for (_ <- dups) {
        val u = URI.create(s"war-virtual:dup:$count:$name").toURL
        urls.add(u)
        count += 1
      }
      debug(s"returned $count duplicates for $name")
    }
    if (getParent != null) {
      val parent = getParent.getResources(name)
      while (parent.hasMoreElements)
        urls.add(parent.nextElement)
    }
    Collections.enumeration(urls)
  }
  @throws[IOException]
  private def loadClass(name: String, entry: ZipEntry): Class[_] = {
    val in = warFile.getInputStream(entry)
    try
      defineClass(name, readBytes(in, 4096), 0, 4096)
    finally
      if (in != null)
        in.close()
  }
  private def readBytes(is: InputStream, bufferSize: Int): Array[Byte] = {
    val buf = Array.ofDim[Byte](bufferSize)
    val out = new ByteArrayOutputStream(bufferSize)
    var nRead = 0
    while ( {
      nRead = is.read(buf, 0, buf.length); nRead != -1
    })
    out.write(buf, 0, nRead)
    out.toByteArray
  }
}
class VirtualWARURLStreamHandlerFactory(val debug: Boolean)
  extends URLStreamHandlerFactory {
  override def createURLStreamHandler(protocol: String): URLStreamHandler = {
    if ("war-virtual" == protocol)
      return new VirtualWARUrlStreamHandler(debug)
    null
  }
}
class VirtualWARUrlStreamHandler(val debug: Boolean) extends URLStreamHandler {
  @throws[IOException]
  protected def openConnection(u: URL): URLConnection = {
    var fileName = u.getFile
    if (debug)
      debug(s"opening file $fileName")
    if (fileName.startsWith("war-virtual:"))
      fileName = fileName.substring(0, "war-virtual:".length)
    if (fileName.startsWith("dup:")) {
      fileName = fileName.substring("dup:".length)
      val n = fileName
        .substring(0, fileName.indexOf(":"))
        .toInt
      fileName = fileName.substring(fileName.indexOf(":") + 1)
      if (!LiveWarClassLoader.MAP_DUPLICATES.contains(fileName)) {
        debug(s"not found: $fileName")
        throw new FileNotFoundException(fileName)
      }
      val data = LiveWarClassLoader.MAP_DUPLICATES.get(fileName)
      if (data.isEmpty)
        throw new FileNotFoundException(fileName)
      val dupsData = data.get(n)
      new URLConnection(u) {
        @throws[IOException]
        def connect(): Unit = {}
        @throws[IOException]
        override def getInputStream: InputStream = new ByteArrayInputStream(dupsData)
      }
    } else {
      if (!LiveWarClassLoader.MAP.contains(fileName))
        throw new FileNotFoundException(fileName)

      val data = LiveWarClassLoader.MAP.get(fileName)
      if (data.isEmpty)
        throw new FileNotFoundException(fileName)
      new URLConnection(u) {
        @throws[IOException]
        def connect(): Unit = {}
        @throws[IOException]
        override def getInputStream: InputStream = new ByteArrayInputStream(data.get)
      }
    }
  }
  private def debug(format: String): Unit = {
    if (debug)
      System.out.println(format)
  }
}
