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
import org.eclipse.jetty.server.handler.{ContextHandlerCollection, DefaultHandler, HandlerCollection, HandlerList}
import org.eclipse.jetty.server.{Handler, Server, ServerConnector}
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.webapp._
import org.slf4j.LoggerFactory

import java.io.{File, IOException}
import java.net.{URL, URLClassLoader}
import java.util
import java.util.{Locale, Properties}
/**
 * Paster Runner.
 * Based on 'Jetty Runner' project, but on Scala.
 * <p>
 * 'Combine jetty classes into a single executable jar and run webapps based on the args to it'
 *
 * This is the static instance, used to store constants
 */
object PasterRunner {
  private val LOG = LoggerFactory.getLogger(classOf[PasterRunner])
  // array with jetty configuration classes
  //  all required to support servlets + jsp + taglibs
  private val JETTY_CONFIGURATION_CLASSES: Array[String] = Array(
    classOf[WebInfConfiguration].getCanonicalName,
    classOf[WebXmlConfiguration].getCanonicalName,
    classOf[org.eclipse.jetty.annotations.AnnotationConfiguration].getCanonicalName,
    classOf[WebAppConfiguration].getCanonicalName,
    classOf[JspConfiguration].getCanonicalName)
  private val DEFAULT_CONTEXT_PATH = "/"
  private val DEFAULT_PORT = 8080
  /**
   * The start point
   * @param args
   *    array with command line arguments
   */
  def main(args: Array[String]): Unit = {
    // create instance
    val runner = new PasterRunner
    // show usage
    try if (args.length > 0 && args(0)
          .equalsIgnoreCase("--help"))
      runner.usage(null)
    // show version
    else if (args.length > 0 && args(0)
          .equalsIgnoreCase("--version"))
      runner.version()
    // start application
    else {
      // load configuration
      runner.loadConf()
      // apply settings
      runner.configure(args)
      // run server
      runner.run()
    }
    catch {
      case e: Exception =>
        LOG.error(e.getMessage,e)
        runner.usage(null)
    }
  }
}
/**
 * The runner class itself
 */
class PasterRunner {
  // if started in 'debug mode'
  // if most cases this will trigger more verbose logging
  private var isDebug = false
  // configured context path
  private var contextPath = PasterRunner.DEFAULT_CONTEXT_PATH
  // and port
  private var port = PasterRunner.DEFAULT_PORT
  private var host: String = _
  private var warFile: String = _
  private var contextPathSet = false
  private var runnerServerInitialized = false
  private var _server: Server = _
  private var _classLoader: URLClassLoader = _
  private val _classpath = new Classpath
  private var _contexts: ContextHandlerCollection = _
  /**
   * Virtual 'Classpath'
   * Holds list of classpath urls
   */
  private class Classpath {
    private val _classpath = new util.ArrayList[URL]
    @throws[IOException]
    def addJars(lib: Resource): Unit = {
      if (lib == null || !lib.exists)
        throw new IllegalStateException(s"No such lib: $lib")
      val list = lib.list
      if (list == null)
        return
      for (path <- list) {
        if ("." != path && ".." != path) {
          val item = lib.addPath(path)
          if (item.isDirectory)
            addJars(item)
          else {
            val lowerCasePath = path.toLowerCase(Locale.ENGLISH)
            if (lowerCasePath.endsWith(".jar"))
              _classpath.add(item.getURI.toURL)
          }
          if (item != null)
            item.close()
        }
      }
    }
    def asArray: Array[URL] = _classpath.toArray(new Array[URL](0))
  }
  /**
   * Generate helpful usage message and exit
   *
   * @param error the error header
   */
  private def usage(error: String): Unit = {
    if (error != null) System.err.println("ERROR: " + error)
    System.err.println("Usage: java [-Djetty.home=dir] -jar jetty-runner.jar [--help|--version] [ server opts] [[ context opts] context ...] ")
    System.err.println("Server opts:")
    System.err.println(" --version                           - display version and exit")
    System.err.println(" --host name|ip                      - interface to listen on (default is all interfaces)")
    System.err.println(" --port n                            - port to listen on (default 8080)")
    System.err.println(" [--lib dir]*n                       - each tuple specifies an extra directory of jars to be added to the classloader")
    System.err.println("Context opts:")
    System.err.println(" [[--path /path] context]*n          - WAR file, web app dir or context xml file, optionally with a context path")
    System.exit(1)
  }
  /**
   * Generate version message and exit
   */
  private def version(): Unit = {
    System.out.printf("org.eclipse.jetty.runner.Runner: %s", Server.getVersion)
    System.exit(1)
  }
  @throws(classOf[IOException])
  private def loadConf(): Unit = {
    val p = new Properties()
    p.load(getClass.getResourceAsStream("/config.properties"))
    if (p.containsKey("appDebug"))
      isDebug = "true".equals(p.getProperty("appDebug", "false"))
    else
      isDebug = "true".equals(System.getProperty("appDebug", "false"))
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
  private def configure(args: Array[String]): Unit = {
    // handle classpath bits first so we can initialize the log mechanism.
    var i: Int = 0
    if (args.length > 0) {
      while (i < args.length) {
        val arg = args(i)
        if ("--lib" eq arg) {
          val lib = Resource.newResource(args({
            i += 1; i
          }))
          if (!lib.exists || !lib.isDirectory) usage(s"No such lib directory $lib")
          _classpath.addJars(lib)
        } else if (arg.startsWith("--"))
          i += 1
        i += 1
      }
    }
    initClassLoader()
    PasterRunner.LOG.info("Paster Runner, debug: {}", isDebug)
    PasterRunner.LOG.debug("Runner classpath {}", _classpath)
    i = 0
    if (args.length > 0) {
      while (i < args.length) {
        args(i) match {
          case "--port" =>
            port = args({
              i += 1; i
            }).toInt
          case "--host" =>
            host = args({
              i += 1; i
            })
          case "--path" =>
            contextPath = args({
              i += 1; i
            })
            contextPathSet = true
          case "--lib" =>
            i += 1 //skip
          case _ =>
            setupContexts(args(i))
        }
        i += 1
      }
    } else
      setupContexts(warFile)
    if (_server == null)
      usage("No Contexts defined")
  }
  private def setupContexts(appFile: String): Unit = {
    PasterRunner.LOG.info("Loading WAR: {}", appFile)
    //TomcatURLStreamHandlerFactory.disable()
    // process contexts
    if (!runnerServerInitialized) {
      // log handlers not registered, server maybe not created, etc
      if (_server == null) { // server not initialized yet
        // build the server
        _server = new Server
      }
      //check that everything got configured, and if not, make the handlers
      var handlers = _server.getChildHandlerByClass(classOf[HandlerCollection])
      if (handlers == null) {
        handlers = new HandlerList
        _server.setHandler(handlers)
      }
      //check if contexts already configured
      _contexts = handlers.getChildHandlerByClass(classOf[ContextHandlerCollection])
      if (_contexts == null) {
        _contexts = new ContextHandlerCollection
        prependHandler(_contexts, handlers)
      }
      //ensure a DefaultHandler is present
      if (handlers.getChildHandlerByClass(classOf[DefaultHandler]) == null)
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
      runnerServerInitialized = true
    }
    // Create a context
    val ctx = Resource.newResource(appFile)
    try {
      if (!ctx.exists)
        usage(s"Context '$ctx' does not exist")
      if (contextPathSet && !contextPath.startsWith("/"))
        contextPath = "/" + contextPath
      PasterRunner.LOG.debug(s"war file: ${ctx.getURI.toURL}")
      // Configure the context
      // assume it is a WAR file
      val webapp = new WebAppContext(_contexts, ctx.toString, contextPath)
      webapp.setClassLoader(new LiveWarClassLoader(isDebug, ctx.getURI.toURL,
        Thread.currentThread.getContextClassLoader))
      val warFile = new File(ctx.getURI)
      System.setProperty("org.eclipse.jetty.livewar.LOCATION", warFile.getAbsolutePath)
      webapp.setExtractWAR(false)
      webapp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false")
      webapp.setConfigurationClasses(PasterRunner.JETTY_CONFIGURATION_CLASSES)
      var fName = getClass.getProtectionDomain.getCodeSource.getLocation.getFile
      fName = fName.substring(fName.lastIndexOf('/'))
      val incPattern = ".*" + fName.replace(".", "\\\\.") + "$"
      webapp.setAttribute(MetaInfConfiguration.CONTAINER_JAR_PATTERN, incPattern)
    } finally
      if (ctx != null)
        ctx.close()
    //reset
    contextPathSet = false
    contextPath = PasterRunner.DEFAULT_CONTEXT_PATH
  }
   private def prependHandler(handler: Handler, handlers: HandlerCollection): Unit = {
    if (handler == null || handlers == null)
      return
    val existing = handlers.getChildHandlers
    val children = new Array[Handler](existing.length + 1)
    children(0) = handler
    System.arraycopy(existing, 0, children, 1, existing.length)
    handlers.setHandlers(children)
  }
  @throws[Exception]
  def run(): Unit = {
    _server.start()
    _server.join()
  }
  /**
   * Establish a classloader with custom paths (if any)
   */
   private def initClassLoader(): Unit = {
    val paths = _classpath.asArray
    if (_classLoader == null && paths.length > 0) {
      val context = Thread.currentThread.getContextClassLoader
      if (context == null)
        _classLoader = new URLClassLoader(paths)
      else
        _classLoader = new URLClassLoader(paths, context)
      Thread.currentThread.setContextClassLoader(_classLoader)
    }
  }
}
