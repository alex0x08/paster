package com.Ox08.paster.run
import org.eclipse.jetty.server.handler.{ContextHandlerCollection, DefaultHandler, HandlerCollection, HandlerList}
import org.eclipse.jetty.server.{Handler, Server, ServerConnector}
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.webapp._
import org.slf4j.LoggerFactory

import java.io.IOException
import java.net.{URL, URLClassLoader}
import java.util
import java.util.{Locale, Properties}
/**
 * Runner
 * <p>
 * Combine jetty classes into a single executable jar and run webapps based on the args to it.
 *
 */
object PasterRunner {
  private val LOG = LoggerFactory.getLogger(classOf[PasterRunner])
  val JETTY_CONFIGURATION_CLASSES: Array[String] = Array(
    classOf[WebInfConfiguration].getCanonicalName,
    classOf[WebXmlConfiguration].getCanonicalName,
    classOf[org.eclipse.jetty.annotations.AnnotationConfiguration].getCanonicalName,
    classOf[WebAppConfiguration].getCanonicalName,
    classOf[JspConfiguration].getCanonicalName)
  val DEFAULT_CONTEXT_PATH = "/"
  val DEFAULT_PORT = 8080
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
class PasterRunner() {
  private var isDebug = false
  private var contextPath = PasterRunner.DEFAULT_CONTEXT_PATH
  private var port = PasterRunner.DEFAULT_PORT
  private var host: String = _
  private var warFile: String = _
  private var contextPathSet = false
  private var runnerServerInitialized = false
  protected var _server: Server = _
  protected var _classLoader: URLClassLoader = _
  protected var _classpath = new Classpath
  protected var _contexts: ContextHandlerCollection = _
  /**
   * Classpath
   */
  class Classpath {
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
      while (i < args.length) {
        val arg = args(i)
        if ("--lib" eq arg) {
          val lib = Resource.newResource(args({ i += 1; i }))
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
    } finally
      if (ctx != null)
        ctx.close()
    //reset
    contextPathSet = false
    contextPath = PasterRunner.DEFAULT_CONTEXT_PATH
  }
  protected def prependHandler(handler: Handler, handlers: HandlerCollection): Unit = {
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
  protected def initClassLoader(): Unit = {
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
