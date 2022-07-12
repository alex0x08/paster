
package com.Ox08.paster.run
import com.Ox08.paster.run.JettyEmbedded.{CONTAINER_INCLUDE_PATTERN_KEY, CONTAINER_INCLUDE_PATTERN_VALUE}
import org.apache.commons.cli.{CommandLine, CommandLineParser, DefaultParser, HelpFormatter, Option, Options, ParseException}
import org.apache.tomcat.{InstanceManager, SimpleInstanceManager}
import org.eclipse.jetty.server.{Connector, Server, ServerConnector}
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.util.component.{AbstractLifeCycle, LifeCycle}
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.webapp.{Configuration, WebAppConfiguration, WebAppContext}
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer
import org.apache.jasper.servlet.JspServlet
import org.apache.tomcat.util.scan.{StandardJarScanFilter, StandardJarScanner}
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter
import org.eclipse.jetty.jsp.JettyJspServlet
import org.eclipse.jetty.plus.annotation.ContainerInitializer
import org.slf4j.bridge.SLF4JBridgeHandler

import java.util
import java.io._
import java.net.URL
import java.nio.charset.Charset
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.text.MessageFormat
import java.util.concurrent.{ArrayBlockingQueue, Executors}
import java.util.{Locale, Properties, ResourceBundle}
import scala.util.Try
object JettyEmbedded {

  private val CONTAINER_INCLUDE_PATTERN_KEY = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern"
  private val CONTAINER_INCLUDE_PATTERN_VALUE = ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\\\.jar$|.*/[^/]*taglibs.*\\.jar$"

  def main(args: Array[String]): Unit = {
    new JettyEmbedded()
      .setupConsole()
      .validateInput(args)
      .loadConf()
      .mkDirs()
      .createServer()
      .startApp()
      .startServer()
  }
}
class JettyEmbedded {
  private var appConf: String = "config.properties"
  private var addr: String = "0.0.0.0"
  private var contextPath = "/"
  private var port = 8080
  private var tempDir = new File("temp")
  private var appWar: File = _
  private var server: Server = _
  private var cmd: CommandLine = _
  private val cmdProgressExecutor = Executors.newSingleThreadExecutor()
  private final val messages: ResourceBundle = ResourceBundle
    .getBundle("messages", Locale.getDefault,
      ResourceBundle.Control.getControl(
        ResourceBundle.Control.FORMAT_PROPERTIES))

  private val config = new Config
  @throws(classOf[ParseException])
  def validateInput(args: Array[String]): JettyEmbedded = {

    // parse the command line arguments
    cmd = config.parser.parse(config.options, args)
    /*if (cmd.hasOption("help")) {
      if (Locale.getDefault.getLanguage.eq("ru") && SystemUtils.IS_OS_WINDOWS) config.helpFormatter.printHelp(
        new PrintWriter(new OutputStreamWriter(
          System.out, Charset.forName("cp866")), true
        ), config.helpFormatter.getWidth,
        "app", null,
        config.options,
        config.helpFormatter.getLeftPadding,
        config.helpFormatter.getDescPadding, null, false) else {
        config.helpFormatter.printHelp("app", config.options)
      }
      System.exit(0)
    }*/
    if (cmd.hasOption("conf"))
      appConf = cmd.getOptionValue("conf")
    this
  }
  def createServer(): JettyEmbedded = {
    System.setProperty("org.apache.jasper.compiler.disablejsr199", "true")

    val pool = new QueuedThreadPool(
      200, 10, 60000,
      new ArrayBlockingQueue(600))
    pool.setDetailedDump(false)
    server = new Server(pool)
    val con = new ServerConnector(server)
    con.setHost(addr)
    con.setPort(port)
    server.setConnectors(Array[Connector](con))
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        try
          server.stop()
        catch {
          case e@(_: Exception) =>
            error(e)
        } finally {
          synchronized {
            wait(1000)
          }
          deleteDir(tempDir)
          Try(cmdProgressExecutor.shutdown())
        }
      }
    })
    this
  }
  @throws(classOf[IOException])
  def loadConf(): JettyEmbedded = {
    var is: InputStream = null
    log("info.using-config", appConf)
    try {
      val conf_file = new File(appConf)
      if (!conf_file.exists() || !conf_file.isFile) {
        log("error.config-not-found", conf_file.getAbsolutePath)
        return this
      }
      is = new FileInputStream(conf_file)
      val p = new Properties()
      p.load(is)
      val appWarS = p.getProperty("app.war", null)
      if (appWarS != null)
        this.appWar = new File(appWarS)
      port = Integer.valueOf(
        if (cmd.hasOption("port"))
          cmd.getOptionValue("port")
        else
          p.getProperty("http.port", port + "")
      )
      addr = if (cmd.hasOption("host")) {
        cmd.getOptionValue("host")
      }
      else p.getProperty("app.host", null)
      contextPath = if (cmd.hasOption("contextPath")) {
        cmd.getOptionValue("contextPath")
      } else
        p.getProperty("app.context", contextPath)
      if (cmd.hasOption("tmpPath"))
        tempDir = new File(cmd.getOptionValue("tmpPath"))

    } finally {
      if (is != null) {
        try
          is.close()
        catch {
          case _: Exception =>
        }
      }
    }
    this
  }
  def mkDirs(): JettyEmbedded = {
    if (tempDir.exists())
      deleteDir(tempDir)
    tempDir.mkdirs()
    new File(tempDir, "work").mkdir()
    System.setProperty("jetty.home", tempDir.getAbsolutePath)
    this
  }
  @throws(classOf[Exception])
  def startServer(): JettyEmbedded = {
    server.start()
    server.join()
    this
  }
  @throws(classOf[Exception])
  def startApp(): JettyEmbedded = {
    //SLF4JBridgeHandler.install()

    //import org.eclipse.jetty.servlet.ServletContextHandler
    //val servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)
   // servletContextHandler.setContextPath("/")
   // servletContextHandler.setResourceBase(baseUri.toASCIIString)
    val webapp: WebAppContext = new WebAppContext()
   // webapp.setInitParameter("org.eclipse.jetty.jsp.precompiled", "true")
    webapp.setAttribute(classOf[InstanceManager].getName, new SimpleInstanceManager());

    webapp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false")
    webapp.setContextPath(contextPath)
    webapp.setParentLoaderPriority(true)
    webapp.setTempDirectory(tempDir)
    webapp.setAttribute("javax.servlet.context.tempdir", new File(tempDir,"jsp"))
    if (appWar != null) {
      webapp.setWar(appWar.getAbsolutePath)
      if (appWar.isDirectory)
        webapp.setExtractWAR(true)
      val sci = new JettyJasperInitializer
     val sciStarter = new ServletContainerInitializersStarter(webapp)
      val initializer = new ContainerInitializer(sci, null)
      val initializers = new util.ArrayList[ContainerInitializer]
      initializers.add(initializer)
      webapp.setAttribute("org.eclipse.jetty.containerInitializers", initializers)
      webapp.addBean(sciStarter, true)

      val jarScanner = new StandardJarScanner
      val jarScanFilter = new StandardJarScanFilter
      //  jarScanFilter.setTldScan("taglibs-standard-impl-*")
      //  jarScanFilter.setTldSkip("apache-*,ecj-*,jetty-*,asm-*,javax.servlet-*,javax.annotation-*,taglibs-standard-spec-*")
      jarScanner.setJarScanFilter(jarScanFilter)
      webapp.setAttribute("org.apache.tomcat.JarScanner", jarScanner)


      val holderJsp = new ServletHolder("jsp", classOf[JettyJspServlet])
      holderJsp.setInitOrder(0)
      holderJsp.setInitParameter("fork", "false")
      holderJsp.setInitParameter("keepgenerated", "true")
     // webapp.addServlet(holderJsp, "*.jsp")
      val holderDefault = new ServletHolder("default", classOf[DefaultServlet])
      holderDefault.setInitParameter("dirAllowed", "true")
    //  webapp.addServlet(holderDefault, "/")

      // This webapp will use jsps and jstl. We need to enable the// This webapp will use jsps and jstl. We need to enable the
      // AnnotationConfiguration in order to correctly
      // set up the jsp container
     // val classlist = new Configuration..ClassList.setServerDefault(server)
     // classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration")
      import org.eclipse.jetty.webapp.FragmentConfiguration
      import org.eclipse.jetty.webapp.MetaInfConfiguration
      import org.eclipse.jetty.webapp.WebInfConfiguration
      import org.eclipse.jetty.webapp.WebXmlConfiguration
      import org.eclipse.jetty.annotations.AnnotationConfiguration
      webapp.setConfigurations(Array[Configuration](new AnnotationConfiguration,
        new WebAppConfiguration,
        new WebXmlConfiguration, new WebInfConfiguration, new MetaInfConfiguration))
      webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
        ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/jakarta.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$")


    //  webapp.addBean(new EmbeddedJspStarter(webapp))


     // webapp.setAttribute(CONTAINER_INCLUDE_PATTERN_KEY, CONTAINER_INCLUDE_PATTERN_VALUE)
      import java.util
//      val serverClasses = new util.ArrayList[_](util.Arrays.asList(webapp.getServerClasses))
 //     serverClasses.remove("org.slf4j.")
    //  webapp.setS.setServerClasses(serverClasses.toArray(new Array[String](0)))
      import org.apache.jasper.servlet.JspServlet
     // import org.eclipse.jetty.servlet.ServletHolder
      // Add JSP Servlet (must be named "jsp")// Add JSP Servlet (must be named "jsp")
     // val holderJsp = new ServletHolder("jsp", classOf[JspServlet])
     // holderJsp.setInitOrder(0)
     // import org.eclipse.jetty.jsp.JettyJspServlet
  //    import org.eclipse.jetty.servlet.ServletHolder
      // Create / Register JSP Servlet (must be named "jsp" per spec)
     /* val holderJsp = new ServletHolder("jsp", classOf[JettyJspServlet])
      holderJsp.setInitOrder(0)
     // holderJsp.setInitParameter("scratchdir", scratchDir.toString)
      holderJsp.setInitParameter("logVerbosityLevel", "DEBUG")
      holderJsp.setInitParameter("fork", "false")
      holderJsp.setInitParameter("xpoweredBy", "false")
      holderJsp.setInitParameter("compilerTargetVM", "18")
      holderJsp.setInitParameter("compilerSourceVM", "18")
      holderJsp.setInitParameter("keepgenerated", "true")
*/
    /*  webapp.addServlet(holderJsp,"*.jsp")

      import org.eclipse.jetty.servlet.DefaultServlet
      import org.eclipse.jetty.servlet.ServletHolder
      // Add Default Servlet (must be named "default")// Add Default Servlet (must be named "default")
      val holderDefault = new ServletHolder("default", classOf[DefaultServlet])
     // holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString)
      holderDefault.setInitParameter("dirAllowed", "true")
      webapp.addServlet(holderDefault, "/")
*/
      import java.net.URLClassLoader
      import java.net.URLClassLoader
      import java.security.AccessController
      import java.security.PrivilegedAction
      import org.eclipse.jetty.webapp.WebAppClassLoader
      import java.io.IOException
      /*try // configure the class loader - webappClassLoader -> jetty nar -> web app's nar -> ...
        webapp.setClassLoader(new WebAppClassLoader(getClass.getClassLoader, webapp))
      catch {
        case ioe: IOException =>
          ioe.printStackTrace()
      }*/

      //    webapp.setClassLoader(Thread.currentThread().getContextClassLoader())

     // val jspClassLoader = new URLClassLoader(new Array[URL](0), this.getClass.getClassLoader)
     // webapp.setClassLoader(jspClassLoader)

  //    webapp.setAttribute(classOf[InstanceManager].getName, new SimpleInstanceManager());
      server.setHandler(webapp)

      server.addEventListener(new LifeCycle.Listener() {
        override def lifeCycleStarted(event: LifeCycle): Unit = {
          cmdProgressExecutor.shutdown()
        }
        override def lifeCycleStopped(event: LifeCycle): Unit = {
          deleteDir(tempDir)
        }
      })
    } else
      log("error.application-not-set")
    this
  }
  @throws(classOf[UnsupportedEncodingException])
  def setupConsole(): JettyEmbedded = {

    // AnsiConsole.systemInstall(); Ansi.ansi().eraseScreen()

    //System.out.write(IOUtils.toByteArray(
     // getClass.getResource("/paster-rus.ans")))
    /*
      if (Locale.getDefault.getLanguage.eq("ru") && SystemUtils.IS_OS_WINDOWS) {

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out),true,"cp866"))
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err),true,"cp866"))

      }*/
    /*
    cmdProgressExecutor.execute(new Runnable() {
      def run() {
        
        var step:Int = 1 
          
        while(true) {
          
          this.synchronized {
                notify
          }
          
         //System.out.println(Ansi.ansi().eraseScreen()
         //                   .fg(Ansi.Color.RED).a("Hello").fg(Ansi.Color.GREEN).a(" World").reset())   
            
           //  AnsiConsole.out.
          System.out.print("\r\b")
          System.out.print(
            step match {
              case 1 => "\\"
              case 2 |4 => "|"
              case 3 => "/"  
            }
            )
            //System.out.println
            
              step = if (step==4) 1 else step+1
            
          this.synchronized {
            wait(100)
          }
        }
      }
      })
    
   */
    this
  }
  private def deleteDir(from: File): Any =
    if (from.exists && from.isDirectory)
      Files.walkFileTree(Paths.get(from.toURI), new DeleteDirectory())
  def error(e: Exception): Unit = e.printStackTrace()
  def i18n(key: String): String = if (messages.containsKey(key))
    messages.getString(key)
  else
    key
  def format(args: String*): String = {
    if (args.length > 1) {
      MessageFormat.format(
        i18n(args(0)), args.slice(1, args.length): _*)
    } else
      i18n(args(0))
  }
  def log(args: String*): Unit = System.out.println(format(args: _*))
  class Config {
    val parser: CommandLineParser = new DefaultParser()
    val options: Options = new Options()
    val helpFormatter: HelpFormatter = new HelpFormatter()
    options.addOption(new Option("h", "help", false, i18n("args.msg.help")))
    options.addOption(new Option("v", "version", false, i18n("args.msg.version")))
    options.addOption(Option.builder().argName("conf")
      .hasArg()
      .required(false)
      .longOpt("confFile")
      .desc(format("args.msg.use-given", appConf))
      .build())
    options.addOption(Option.builder().argName("host")
      .hasArg()
      .required(false)
      .longOpt("hostName")
      .desc(format("args.msg.host", addr)).build())
    options.addOption(Option.builder().argName("port")
      .hasArg()
      .required(false)
      .longOpt("portNum")
      .desc(format("args.msg.port", port.toString)).build())
    options.addOption(Option.builder().argName("cxt")
      .hasArg()
      .required(false)
      .longOpt("contextPath")
      .desc(format("args.msg.contextPath", contextPath)).build())
    options.addOption(Option.builder().argName("tmp")
      .hasArg()
      .required(false)
      .longOpt("tmpPath")
      .desc(format("args.msg.tmpPath", tempDir.getPath)).build())
  }
}

class  EmbeddedJspStarter(context:WebAppContext) extends AbstractLifeCycle
{
  import org.apache.tomcat.util.scan.StandardJarScanFilter
  import org.apache.tomcat.util.scan.StandardJarScanner
  import org.eclipse.jetty.apache.jsp.JettyJasperInitializer

  private val sci:JettyJasperInitializer = new JettyJasperInitializer

  val jarScanner = new StandardJarScanner
  val jarScanFilter = new StandardJarScanFilter
//  jarScanFilter.setTldScan("taglibs-standard-impl-*")
//  jarScanFilter.setTldSkip("apache-*,ecj-*,jetty-*,asm-*,javax.servlet-*,javax.annotation-*,taglibs-standard-spec-*")
  jarScanner.setJarScanFilter(jarScanFilter)
  this.context.setAttribute("org.apache.tomcat.JarScanner", jarScanner)
  @throws[Exception]
  override protected def doStart(): Unit = {
    val old = Thread.currentThread.getContextClassLoader
    Thread.currentThread.setContextClassLoader(context.getClassLoader)
    try {
      sci.onStartup(null, context.getServletContext)
      super.doStart()
    } finally Thread.currentThread.setContextClassLoader(old)
  }
}

class DeleteDirectory extends SimpleFileVisitor[Path] {
  @throws(classOf[IOException])
  override def visitFile(file: Path, attributes: BasicFileAttributes): FileVisitResult = {
    try
      Files.delete(file)
    catch {
      case _: Exception =>
      // e.printStackTrace
    }
    FileVisitResult.CONTINUE
  }
  @throws(classOf[IOException])
  override def postVisitDirectory(directory: Path,
                                  exception: IOException): FileVisitResult = if (exception == null) {
    try
      Files.delete(directory)
    catch {
      case _: Exception =>
    }
    FileVisitResult.CONTINUE
  } else
    FileVisitResult.TERMINATE
}