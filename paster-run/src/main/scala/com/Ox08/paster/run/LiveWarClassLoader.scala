package com.Ox08.paster.run
import com.Ox08.paster.run.LiveWarClassLoader.{CLASSES_BASE, DEBUG}

import java.io.{ByteArrayOutputStream, Closeable, File, IOException, InputStream}
import java.net.{MalformedURLException, URI, URL, URLClassLoader}
import java.util
import java.util.Collections
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import scala.collection.mutable
import scala.io.Source
import scala.util.control.Breaks.{break, breakable}
object LiveWarClassLoader {

  private val ID = classOf[LiveWarClassLoader].getSimpleName
  private val DEBUG = true //Boolean.getBoolean("jetty.bootstrap.debug")
  private val CLASSES_BASE = "WEB-INF/classes/"
}

class LiveWarClassLoader(warFileUrl:URL,parent: ClassLoader )
        extends ClassLoader(parent) with Closeable {
  private val warFileUri = warFileUrl.toURI
  private val warFile =  new JarFile(new File(warFileUri))
  @throws[IOException]
  override def close(): Unit = {
    warFile.close
  }
  private def debug(format: String, args: Any*): Unit = {
    if (DEBUG)
          System.out.println(format)
  }
  @throws[ClassNotFoundException]
  override protected def findClass(name: String): Class[_] = {
    debug(s"findClass: ${name}")
    val path = name.replace('.', '/').concat(".class")
    val entry = findEntry(path)
    if (entry != null) try loadClass(name, entry)
    catch {
      case e: IOException =>
        throw new ClassNotFoundException(name, e)
    }
    else
      super.findClass(name)
      //throw new ClassNotFoundException(name)
  }
  private def findEntry(name: String): ZipEntry = {
    val path: mutable.StringBuilder = new mutable.StringBuilder
    path.append(CLASSES_BASE)
    if (name.charAt(0) == '/') path.append(name.substring(1))
    else path.append(name)
    val entry: ZipEntry = warFile.getEntry(path.toString)
    debug(s"findEntry(${name}) ${path} => ${entry}")
    entry
  }
  override protected  def findResource(name: String): URL = {
    debug(s"findResource: ${name}")
    val entry = findEntry(name)
    if (entry != null)
      try
        return URI.create("jar:" + this.warFileUri.toASCIIString + "!/" + entry.getName).toURL
      catch {
      case e: MalformedURLException =>
        e.printStackTrace(System.err)
        return null
    }
    //if (getParent != null) {
      super.findResource(name)
    //}
    //null
  }
  @throws[IOException]
  override protected def findResources(name: String): util.Enumeration[URL] = {
    debug(s"findResources: ${name}")
    val urls = new util.ArrayList[URL]
    val self = findResource(name)
    if (self != null) urls.add(self)
    if (getParent != null) {
      val parent = getParent.getResources(name)
      while (parent.hasMoreElements)
        urls.add(parent.nextElement)
    }
    Collections.enumeration(urls)
  }
  @throws[IOException]
  private def loadClass(name: String, entry: ZipEntry) = try {
    val in = warFile.getInputStream(entry)
      try {
      val classBytes =  readBytes(in,4096)

      defineClass(name, classBytes, 0, classBytes.length)
    } finally {
      if (in != null) in.close()
    }
  }

  val EOF: Int = -1

  def readBytes(is: InputStream, bufferSize: Int): Array[Byte] = {
    val buf = Array.ofDim[Byte](bufferSize)
    val out = new ByteArrayOutputStream(bufferSize)

    Stream.continually(is.read(buf)) takeWhile { _ != EOF } foreach { n =>
      out.write(buf, 0, n)
    }

    out.toByteArray
  }

}
