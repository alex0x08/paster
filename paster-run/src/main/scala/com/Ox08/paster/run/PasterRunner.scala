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
import org.eclipse.jetty.ee10.plus.webapp.{EnvConfiguration, PlusConfiguration}
import org.eclipse.jetty.ee10.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.ee10.webapp._
import org.eclipse.jetty.server.handler.{ContextHandlerCollection, DefaultHandler}
import org.eclipse.jetty.server.{Handler, Server, ServerConnector}
import org.eclipse.jetty.util.FileID
import org.eclipse.jetty.util.resource.{Resource, ResourceFactory, Resources}
import org.slf4j.LoggerFactory

import java.io.{File, IOException}
import java.net.{URI, URL, URLClassLoader}
import java.util
import java.util.Properties
import scala.jdk.CollectionConverters.IterableHasAsScala
/**
 * Runner
 * <p>
 * Combine jetty classes into a single executable jar and run webapps based on the args to it.
 *
 */
object PasterRunner {
  private val LOG = LoggerFactory.getLogger(classOf[PasterRunner])
  private val JETTY_CONFIGURATION_CLASSES: Array[String] = Array(
    classOf[org.eclipse.jetty.ee10.webapp.WebInfConfiguration].getCanonicalName,
    classOf[org.eclipse.jetty.ee10.webapp.WebXmlConfiguration].getCanonicalName,

/*    classOf[org.eclipse.jetty.ee10.webapp.MetaInfConfiguration].getCanonicalName,
    classOf[org.eclipse.jetty.ee10.webapp.FragmentConfiguration].getCanonicalName,
    classOf[EnvConfiguration].getCanonicalName,
    classOf[PlusConfiguration].getCanonicalName,
*/
    classOf[org.eclipse.jetty.ee10.annotations.AnnotationConfiguration].getCanonicalName,

//    classOf[org.eclipse.jetty.ee10.webapp.JettyWebXmlConfiguration].getCanonicalName,

    classOf[ org.eclipse.jetty.ee10.webapp.WebAppConfiguration].getCanonicalName,
    classOf[org.eclipse.jetty.ee10.webapp.JspConfiguration].getCanonicalName)
  private val DEFAULT_CONTEXT_PATH = "/"
  private val DEFAULT_PORT = 8080

  def main(args: Array[String]): Unit = {
    val runner = new PasterRunner
    try if (args.length > 0 && args(0).equalsIgnoreCase("--help"))
      runner.usage(null)
    else if (args.length > 0 && args(0).equalsIgnoreCase("--version"))
      runner.version()
    else {
      runner.loadConf()
      runner.configure(args)
      runner.run()
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
        runner.usage(null)
    }
  }
}
class PasterRunner {
  private var isDebug = false
  private var contextPath = PasterRunner.DEFAULT_CONTEXT_PATH
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
   * Classpath
   */
  private class Classpath {

    private val _classpath: util.List[URL] = new util.ArrayList[URL]()

    def addJars(lib: Resource): Unit = {
      if (!Resources.isReadableFile(lib) || !FileID.isJavaArchive(lib.getURI))
        throw new IllegalStateException("Invalid lib: " + lib)

      for (item <- lib.list.asScala) {
        if (item.isDirectory) addJars(item)
        else if (FileID.isLibArchive(item.getFileName)) _classpath.add(item.getURI.toURL)
      }
    }

    def asArray: Array[URL] = _classpath.toArray(new Array[URL](0))
  }

  /**
   * Generate helpful usage message and exit
   *
   * @param error the error header
   */
  def usage(error: String): Unit = {
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
  def version(): Unit = {
    System.out.printf("org.eclipse.jetty.runner.Runner: %s" , Server.getVersion)
    System.exit(1)
  }
  @throws(classOf[IOException])
  def loadConf(): Unit = {
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

    //this.warFile="c:/work/paster.war"

  }
  /**
   * Configure a jetty instance and deploy the webapps presented as args
   *
   * @param args the command line arguments
   * @throws Exception if unable to configure
   */
  @throws[Exception]
  def configure(args: Array[String]): Unit = {
    // handle classpath bits first so we can initialize the log mechanism.
    var i: Int = 0
    if (args.length > 0) {
      val resourceFactory = ResourceFactory.closeable()
        while (i < args.length) {
          val arg = args(i)
          if ("--lib" eq arg) {
            val lib = resourceFactory.newJarFileResource(URI.create(args({
              i += 1; i
            })))
            if (!lib.exists || !lib.isDirectory) usage(s"No such lib directory $lib")
            _classpath.addJars(lib)
          } else if (arg.startsWith("--"))
            i += 1
          i += 1
        }

    }
    initClassLoader()
    PasterRunner.LOG.info("Paster Runner, debug: {}",isDebug)
    PasterRunner.LOG.debug("Runner classpath {}", _classpath)
    i = 0
    if (args.length > 0) {
      while (i < args.length) {
        args(i) match {
          case "--port" =>
            port = args({i += 1; i}).toInt
          case "--host" =>
            host = args({i += 1; i})
          case "--path" =>
            contextPath = args({i += 1; i})
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
    PasterRunner.LOG.info("Loading WAR: {}",appFile)

    // process contexts
    if (!runnerServerInitialized) {
      // log handlers not registered, server maybe not created, etc
      if (_server == null) { // server not initialized yet
        // build the server
        _server = new Server
      }

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
      runnerServerInitialized = true
    }

    // Create a context
    val ctx = ResourceFactory.of(_server).newResource(appFile)
    try {

      if (!ctx.exists)
        usage(s"Context '$ctx' does not exist")

      if (contextPathSet && !contextPath.startsWith("/"))
        contextPath = "/" + contextPath
      PasterRunner.LOG.debug(s"war file: ${ctx.getURI.toURL}")
      // Configure the context
      // assume it is a WAR file
      val webapp = new WebAppContext(ctx.toString, contextPath)
      webapp.setClassLoader(new LiveWarClassLoader(false, ctx.getURI.toURL,
        Thread.currentThread.getContextClassLoader))
      import java.io.File
      val warFile = new File(ctx.getURI)
      System.setProperty("org.eclipse.jetty.livewar.LOCATION", warFile.getAbsolutePath)
      webapp.setExtractWAR(false)
      webapp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false")
      webapp.setConfigurationClasses(PasterRunner.JETTY_CONFIGURATION_CLASSES)
      var fname = getClass.getProtectionDomain.getCodeSource.getLocation.getFile
      fname = fname.substring(fname.lastIndexOf('/'))
      val incPattern = ".*" + fname.replace(".", "\\\\.") + "$"
      webapp.setAttribute(MetaInfConfiguration.CONTAINER_JAR_PATTERN, incPattern)

      _contexts.addHandler(webapp)
    } finally
     // if (ctx != null)
      //  ctx.close()
    //reset
    contextPathSet = false
    contextPath = PasterRunner.DEFAULT_CONTEXT_PATH
  }

  private def prependHandler(handler: Handler, handlers:  Handler.Sequence): Unit = {
    if (handler == null || handlers == null) return
    val existing = handlers.getHandlers
    val children = new util.ArrayList[Handler](existing.size + 1)
    children.add(handler)
    children.addAll(existing)
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
    val paths:Array[URL] = _classpath.asArray
    if (_classLoader == null && paths.nonEmpty) {
      val context = Thread.currentThread.getContextClassLoader
      if (context == null)
        _classLoader = new URLClassLoader(paths)
      else _classLoader = new URLClassLoader(paths, context)
      Thread.currentThread.setContextClassLoader(_classLoader)
    }
  }
}
