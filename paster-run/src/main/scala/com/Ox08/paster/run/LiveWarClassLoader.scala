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
import java.io._
import java.net._
import java.util
import java.util.Collections
import java.util.jar.{JarEntry, JarFile,JarInputStream}
import java.util.zip.ZipEntry
import scala.collection.mutable
import scala.jdk.CollectionConverters._
/**
 * Stores constants used in classloader
 */
object LiveWarClassLoader {
  // folder prefix for classes defined in WAR file itself, not in nested jars
  private val CLASSES_BASE = "WEB-INF/classes/"
  // protocol prefix, like 'jar://' used to trigger seek in our 'in-memory' storage
  val PROTOCOL_PREFIX = "war-virtual"
  // primary map, used as flat filesystem
  val MAP = new mutable.HashMap[String, Array[Byte]]
  // map that stores duplicated resources
  val MAP_DUPLICATES = new mutable.HashMap[String, List[Array[Byte]]]
  private val EMPTY_BA = new Array[Byte](0)
}
/**
 * Custom classloader, to load WAR without unpacking
 *
 * @param debug
 * show/hide debug messages
 * @param warFileUrl
 * url to WAR file
 * @param parent
 * parent classloader
 */
private class LiveWarClassLoader(debug: Boolean, warFileUrl: URL, parent: ClassLoader)
  extends ClassLoader(parent) with Closeable {
  private val warFileUri = warFileUrl.toURI
  private val warFile = new JarFile(new File(warFileUri))
  //similar to call from constructor in Java
  loadData()
  /**
   * loads all resources into memory on start
   */
  private def loadData(): Unit = {
    // read WAR file contents
    for (e <- warFile.entries().asScala) {
      // if resource is a library
      if (e.getName.startsWith("WEB-INF/lib") && e.getName.endsWith(".jar")) {
        debug(s"found lib: ${e.getName}")
        // open stream for jar file
        val zip = new JarInputStream(warFile.getInputStream(e))
        var ze: JarEntry = null
        // iterate over internal resources in current jar file
        while ( {
          ze = zip.getNextJarEntry; ze != null
        }) {
          // we must load the directories too, their names should be present
          // in resources map
          if (ze.isDirectory) {
            val key = ze.getName //.substring(0, ze.getName.length - 1)
            LiveWarClassLoader.MAP.put(key, LiveWarClassLoader.EMPTY_BA)
          } else {
            // read file contents
            val data = readBytes(zip)
            // check for duplications
            // as common  - META-INF/* content
            if (LiveWarClassLoader.MAP.contains(ze.getName)) {
              debug(s"already loaded: ${ze.getName}")
              var datas: List[Array[Byte]] = if (LiveWarClassLoader.MAP_DUPLICATES.contains(ze.getName))
                LiveWarClassLoader.MAP_DUPLICATES(ze.getName)
              else
                List()
              datas = datas.appended(data)
              debug(s"duplicates ${ze.getName} = ${datas.length}")
              // put duplicates into dedicated map
              LiveWarClassLoader.MAP_DUPLICATES.put(ze.getName, datas)
            } else {
              // otherwise - put into primary map
              LiveWarClassLoader.MAP.put(ze.getName, data)
            }
          }
        }
      }
    }
    // set our custom stream handler factory to resolve URLs point to this in-memory storage
    URL.setURLStreamHandlerFactory(new VirtualWARURLStreamHandlerFactory(debug))
  }
  @throws[IOException]
  override def close(): Unit = {
    warFile.close()
  }
  /**
   * Seek for class and read it, if found
   * @param name
   *      full class name
   * @throws
   *    ClassNotFoundException if class was not found
   * @return
   */
  @throws[ClassNotFoundException]
  override protected def findClass(name: String): Class[_] = {
    // build path based on class name
    val path = name.replace('.', '/').concat(".class")
    // try to find ZipEntry first
    val entry = findEntry(path)
    // if found - means that class right in WAR file, in WEB-INF/classes folder
    if (entry != null) {
      debug(s"findClass: $name")
      try
        loadClass(name, entry)
      catch {
        case e: IOException =>
          throw new ClassNotFoundException(name, e)
      }
    // otherwise - seek in our 'flat filesystem'
    } else if (LiveWarClassLoader.MAP.contains(path)) {
      debug(s"findClass in map: $name")
      val classBytes = LiveWarClassLoader.MAP(path)
      defineClass(name, classBytes, 0, classBytes.length)
    // if not found (as last chance) - try to find in parent classloader
    } else {
      val clazz = super.findClass(name)
      if (clazz == null)
        debug(s"NOT findClass in war/map: $name")
      clazz
    }
  }
  /**
   * Find resource url from name
   * @param name
   *      resource name
   * @return
   */
  override protected def findResource(name: String): URL = {
    val entry = findEntry(name)
    if (entry != null) {
      try {
        debug(s"findResource: $name")
        return URI.create(s"jar:${this.warFileUri.toASCIIString}!/${entry.getName}").toURL
      } catch {
        case e: MalformedURLException =>
          if (debug)
            e.printStackTrace(System.err)
          return null
      }
    } else if (LiveWarClassLoader.MAP.contains(name)) {
      debug(s"findResource in map: $name")
      return URI.create(s"war-virtual:$name").toURL
    }
    val url = super.findResource(name)
    if (url == null)
      debug(s"NOT findResource : $name")
    url
  }
  /**
   * Get list of all urls for resource with same name
   * @param name
   * @throws
   * @return
   */
  @throws[IOException]
  override protected def findResources(name: String): util.Enumeration[URL] = {
    debug(s"findResources: $name")
    val urls = new util.ArrayList[URL]
    // first, try to find in primary map
    val self = findResource(name)
    if (self != null)
      urls.add(self)
    // check for duplicates
    if (LiveWarClassLoader.MAP_DUPLICATES.contains(name)) {
      val dups = LiveWarClassLoader.MAP_DUPLICATES(name)
      var count = 0
      // here we build our very specific url that points to each duplicated resource but
      // uses number instead of full path
      for (_ <- dups) {
        val u = URI.create(s"war-virtual:dup:$count:$name").toURL
        urls.add(u)
        count += 1
      }
      debug(s"returned $count duplicates for $name")
    }
    // try to find also in parent classloader
    if (getParent != null) {
      val parent = getParent.getResources(name)
      while (parent.hasMoreElements)
        urls.add(parent.nextElement)
    }
    Collections.enumeration(urls)
  }
  /**
   * loads class from resource in WAR file
   * @param name
   *      resource name
   * @param entry
   *      link to zip entry
   * @throws
   * @return
   */
  @throws[IOException]
  private def loadClass(name: String, entry: ZipEntry): Class[_] = {
    // open stream
    val in = warFile.getInputStream(entry)
    try {
      val classBytes = readBytes(in)
      defineClass(name, classBytes, 0, classBytes.length)
    } finally
      if (in != null)
        in.close()
  }
  /**
   * Seeks for zip entry in WAR file
   *
   * This is used to seek class file in  WEB-INF/classes/ by its name
   * @param name
   *      class name
   * @return
   *      link to ZipEntry or null if not found
   */
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
  /**
   * Reads content from input stream into byte array, without closing stream
   * @param is
   * @return
   */
  private def readBytes(is: InputStream): Array[Byte] = {
    val bufferSize = 4096
    val buf = Array.ofDim[Byte](bufferSize)
    val out = new ByteArrayOutputStream(bufferSize)
    var nRead = 0
    while ( {
      nRead = is.read(buf, 0, buf.length); nRead != -1
    })
      out.write(buf, 0, nRead)
    out.toByteArray
  }
  private def debug(format: String): Unit = {
    if (debug)
      System.out.println(format)
  }
}
/**
 * Custom stream handler factory, used to instantiate custom handlers if
 * passed url stats with 'war-virtual:' as protocol
 * @param debug
 *      if true - debug messages will be printed
 */
private class VirtualWARURLStreamHandlerFactory(val debug: Boolean) extends URLStreamHandlerFactory {
  override def createURLStreamHandler(protocol: String): URLStreamHandler = {
    if (LiveWarClassLoader.PROTOCOL_PREFIX == protocol)
      return new VirtualWARUrlStreamHandler(debug)
    null
  }
}
/**
 * Custom stream handler, used to read content from 'in-memory filesystem'
 * @param debug
 *    if true - debug messages will be printed
 */
class VirtualWARUrlStreamHandler(val debug: Boolean) extends URLStreamHandler {
  @throws[IOException]
  protected def openConnection(u: URL): URLConnection = {
    var fileName = u.getFile
    if (debug)
      debug(s"opening file $fileName")
    // strip protocol
    if (fileName.startsWith(LiveWarClassLoader.PROTOCOL_PREFIX))
      fileName = fileName.substring(0, LiveWarClassLoader.PROTOCOL_PREFIX.length+1) //+1 is for ':'
    // check for dup part in path
    // if present - remove also, but seek in different map
    if (fileName.startsWith("dup:")) {
      fileName = fileName.substring("dup:".length)
      val n = fileName
        .substring(0, fileName.indexOf(":"))
        .toInt
      fileName = fileName.substring(fileName.indexOf(":") + 1)
      // if url has 'dup:' element but resource was not found in map with duplicates - this is a bug
      if (!LiveWarClassLoader.MAP_DUPLICATES.contains(fileName)) {
        debug(s"not found: $fileName")
        throw new FileNotFoundException(fileName)
      }
      // read resource content
      val data = LiveWarClassLoader.MAP_DUPLICATES.get(fileName)
      // should not happen, because we skip empty files on load
      if (data.isEmpty)
        throw new FileNotFoundException(fileName)
      // finally - construct new url connection with resource content and respond it
      val dupsData = data.get(n)
      new URLConnection(u) {
        @throws[IOException]
        def connect(): Unit = {}
        @throws[IOException]
        override def getInputStream: InputStream = new ByteArrayInputStream(dupsData)
      }
      // otherwise, for non-duplicated resource
    } else {
      // we most throw FileNotFound by API specs
      if (!LiveWarClassLoader.MAP.contains(fileName)) {
        throw new FileNotFoundException(fileName)
      }
      val data = LiveWarClassLoader.MAP.get(fileName)
      // similar check for emptiness
      if (data.isEmpty)
        throw new FileNotFoundException(fileName)
      // and respond new url connection
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
