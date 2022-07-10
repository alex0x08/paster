
package com.Ox08.paster.run
import org.apache.commons.cli.{CommandLine, CommandLineParser, DefaultParser, HelpFormatter, Option, Options, ParseException}
import org.eclipse.jetty.server.{Connector, Server, ServerConnector}
import org.eclipse.jetty.util.component.LifeCycle
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.webapp.WebAppContext

import java.io._
import java.nio.charset.Charset
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.text.MessageFormat
import java.util.concurrent.{ArrayBlockingQueue, Executors}
import java.util.{Locale, Properties, ResourceBundle}
import scala.util.Try
object JettyEmbedded {
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
    val webapp = new WebAppContext()
    webapp.setInitParameter("org.eclipse.jetty.jsp.precompiled", "true")
    webapp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false")
    webapp.setContextPath(contextPath)
    webapp.setTempDirectory(tempDir)
    if (appWar != null) {
      webapp.setWar(appWar.getAbsolutePath)
      if (appWar.isDirectory)
        webapp.setExtractWAR(true)
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