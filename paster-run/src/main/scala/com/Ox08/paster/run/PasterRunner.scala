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
import org.eclipse.jetty.ee10.apache.jsp.JettyJasperInitializer
import org.eclipse.jetty.ee10.webapp._
import org.eclipse.jetty.server.handler.{ContextHandlerCollection, DefaultHandler}
import org.eclipse.jetty.server.{Handler, Server, ServerConnector}
import org.eclipse.jetty.util.FileID
import org.eclipse.jetty.util.resource.{Resource, ResourceFactory}
import org.slf4j.LoggerFactory

import java.io.{File, FileReader, IOException}
import java.net.{URL, URLClassLoader}
import java.util
import java.util.Properties
import scala.jdk.CollectionConverters.IterableHasAsScala
/**
 * Runner
 *
 * @since 3.0
 * @author 0x08
 *
 * Combines jetty classes into a single executable jar and run webapps based on the args to it.
 *
 * Based on Jetty Runner:
 * https://github.com/jetty/jetty.project/tree/jetty-12.0.x/jetty-ee10/jetty-ee10-runner
 *
 */
object PasterRunner {
  private val LOG = LoggerFactory.getLogger(classOf[PasterRunner])
  // set of configuration classes, used to boot Jetty
  private val JETTY_CONFIGURATION_CLASSES: Array[String] = Array(
    classOf[org.eclipse.jetty.ee10.webapp.WebInfConfiguration].getCanonicalName,
    classOf[org.eclipse.jetty.ee10.webapp.WebXmlConfiguration].getCanonicalName,
    classOf[org.eclipse.jetty.ee10.annotations.AnnotationConfiguration].getCanonicalName,
    classOf[ org.eclipse.jetty.ee10.webapp.WebAppConfiguration].getCanonicalName,
    classOf[org.eclipse.jetty.ee10.webapp.JspConfiguration].getCanonicalName)
  // default context path
  private val DEFAULT_CONTEXT_PATH = "/"
  // default port
  private val DEFAULT_PORT = 8080

  /**
   * Main function, here execution starts
   * @param args
   */
  def main(args: Array[String]): Unit = {
    // create Runner instance
    val runner = new PasterRunner
     // load config
      runner.loadConf()
     // configure Jetty
      runner.configure(args)
     // run server
      runner.run()
  }
}

/**
 * Actual Runner logic
 */
class PasterRunner {
  // if true, there will be verbose messages
  private var isDebug = false
  // current context path
  private var contextPath = PasterRunner.DEFAULT_CONTEXT_PATH
  // current port
  private var port = PasterRunner.DEFAULT_PORT
  // current hostname or IP address, to bind to
  private var host: String = _
  // path to WAR file with Paster
  private var warFile: String = _
  // Jetty Server instance
  private var _server: Server = _
  // Jetty parent classloader
  private var _classLoader: URLClassLoader = _
  // additional class to keep classpath jars
  private val _classpath = new Classpath
  // current Jetty contexts
  private var _contexts: ContextHandlerCollection = _
  /**
   * Stores runner classpath jars.
   * Has been taken from original Jetty Runner
   */
  private class Classpath {
    private val _classpath: util.List[URL] = new util.ArrayList[URL]()
    def addJars(lib: Resource): Unit = {
      for (item <- lib.list.asScala) {
        if (item.isDirectory)
          addJars(item)
        else if (FileID.isLibArchive(item.getFileName)) {
          _classpath.add(item.getURI.toURL)
          PasterRunner.LOG.info("added lib: {}",item.getFileName)
        }
      }
    }
    def asArray: Array[URL] = _classpath.toArray(new Array[URL](0))
  }

  /**
   * Loads configuration from properties file
   * @throws java.io.IOException
   *        on I/O errors
   */
  @throws(classOf[IOException])
  def loadConf(): Unit = {
    val p = new Properties()
    // first load from resources
    p.load(getClass.getResourceAsStream("/config.properties"))
    // then check if external config exist and load it
    val configFile = new File("./config.properties")
    if (configFile.exists() && configFile.isFile)
      p.load(new FileReader(configFile))
    // properties from external config will override existing
    // check for debug
    isDebug = p.containsKey("appDebug") && "true".equals(p.getProperty("appDebug"))

    if (p.containsKey("paster.runner.port"))
      this.port = p.getProperty("paster.runner.port").toInt
    if (p.containsKey("paster.runner.host"))
      this.host = p.getProperty("paster.runner.host")
    if (p.containsKey("paster.runner.contextPath"))
      this.contextPath = p.getProperty("paster.runner.contextPath")
    if (p.containsKey("paster.runner.warFile"))
      this.warFile = p.getProperty("paster.runner.warFile")
  }
  /**
   * Configure a jetty instance and deploy the webapps presented as args
   *
   * @param args the command line arguments
   * @throws Exception if unable to configure
   */
  @throws[Exception]
  def configure(args: Array[String]): Unit = {
    // checks and loads external libraries
    val libs = new File("libs")
    if (libs.exists() && libs.isDirectory)
      _classpath.addJars(ResourceFactory.closeable().newResource(libs.toPath))
    // configure system classloader
    initClassLoader()
    if (PasterRunner.LOG.isDebugEnabled)
        PasterRunner.LOG.debug("Runner classpath {}", _classpath)
    // setup contexts
    setupContexts(warFile)
    PasterRunner.LOG.info("Paster Runner is configured, debug: {}",isDebug)
  }

  /**
   * Starts Jetty server, then blocks current thread
   * @throws
   */
  @throws[Exception]
  def run(): Unit = {
    _server.start()
    _server.join()
  }

  /**
   * Configure Jetty contexts
   * @param appFile
   *          Paster app file (war)
   */
  private def setupContexts(appFile: String): Unit = {
      PasterRunner.LOG.info("Loading app: {}",appFile)
      // log handlers not registered, server maybe not created, etc
      if (_server == null)  // server not initialized yet
        // build the server
        _server = new Server

      var handlers = _server.getDescendant(classOf[Handler.Sequence])
      if (handlers == null) {
        handlers = new Handler.Sequence
        _server.setHandler(handlers)
      }
      //check if contexts already configured
      _contexts = handlers.getDescendant(classOf[ContextHandlerCollection])
      if (_contexts == null) {
        _contexts = new ContextHandlerCollection
        prependHandler(_contexts, handlers)
      }

      //ensure a DefaultHandler is present
      if (handlers.getDescendant(classOf[DefaultHandler]) == null)
        handlers.addHandler(new DefaultHandler)

      //check a connector is configured to listen on
      val connectors = _server.getConnectors
      if (connectors == null || connectors.isEmpty) {
        val connector = new ServerConnector(_server)
        connector.setPort(port)
        if (host != null)
          connector.setHost(host)
        _server.addConnector(connector)
      }

    // Create a context
    val ctx = ResourceFactory.of(_server).newResource(appFile)
    try {
      if (!contextPath.startsWith("/"))
              contextPath = "/" + contextPath
      PasterRunner.LOG.debug(s"war file: ${ctx.getURI.toURL}")
      // Configure the context
      // assume it is a WAR file
      val webapp = new WebAppContext(ctx.toString, contextPath)
      webapp.setClassLoader(new LiveWarClassLoader(false,
        ctx.getURI.toURL,
        Thread.currentThread.getContextClassLoader))
      import java.io.File
      val warFile = new File(ctx.getURI)
      System.setProperty("org.eclipse.jetty.livewar.LOCATION", warFile.getAbsolutePath)
      webapp.setExtractWAR(false)
     // required on Windows
      webapp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false")
      webapp.setConfigurationClasses(PasterRunner.JETTY_CONFIGURATION_CLASSES)
      var fname = getClass.getProtectionDomain.getCodeSource.getLocation.getFile
      fname = fname.substring(fname.lastIndexOf('/'))
      val incPattern = ".*" + fname.replace(".", "\\\\.") + "$"
      webapp.setAttribute(MetaInfConfiguration.CONTAINER_JAR_PATTERN, incPattern)

      // hack for Jetty 12
      webapp.addServletContainerInitializer(new JettyJasperInitializer)

      _contexts.addHandler(webapp)
    }
  }

  private def prependHandler(handler: Handler, handlers:  Handler.Sequence): Unit = {
    if (handler == null || handlers == null) return
    val existing = handlers.getHandlers
    val children = new util.ArrayList[Handler](existing.size + 1)
    children.add(handler)
    children.addAll(existing)
    handlers.setHandlers(children)
  }
  /**
   * Establish a classloader with custom paths (if any)
   */
  private def initClassLoader(): Unit = {
    val paths:Array[URL] = _classpath.asArray
    if (_classLoader == null && paths.nonEmpty) {
      val context = Thread.currentThread.getContextClassLoader
      if (context == null)
        _classLoader = new URLClassLoader(paths)
      else
        _classLoader = new URLClassLoader(paths, context)
      Thread.currentThread.setContextClassLoader(_classLoader)
    }
  }
}
