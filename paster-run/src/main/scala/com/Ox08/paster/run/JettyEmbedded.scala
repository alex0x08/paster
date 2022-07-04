

package com.Ox08.paster.run

import org.apache.commons.cli.{CommandLine, CommandLineParser, HelpFormatter, Option, Options, ParseException, DefaultParser}
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.SystemUtils
import org.apache.commons.lang.builder.{StandardToStringStyle, ToStringBuilder}
import org.eclipse.jetty.server.{Connector, Server, ServerConnector}
import org.eclipse.jetty.util.component.{AbstractLifeCycle, LifeCycle}
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.webapp.WebAppContext
import java.io._
import java.nio.charset.Charset
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.text.MessageFormat
import java.util.{Locale, Properties, ResourceBundle}
import java.util.concurrent.{ArrayBlockingQueue, Executors}
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

  private var appWar: File = null

  private var server: Server = null

  private var cmd: CommandLine = null

  private val cmdProgressExecutor = Executors.newSingleThreadExecutor()

  private final val messages: ResourceBundle = ResourceBundle
    .getBundle("messages", Locale.getDefault,
      ResourceBundle.Control.getControl(
        ResourceBundle.Control.FORMAT_PROPERTIES))

  private final val style = new StandardToStringStyle() {
    setFieldSeparator(", ")
    setUseClassName(false)
    setUseIdentityHashCode(false)
    setNullText(i18n("default.not-set"))
  }

  private val config = new Config

  @throws(classOf[ParseException])
  def validateInput(args: Array[String]): JettyEmbedded = {

    // parse the command line arguments
    cmd = config.parser.parse(config.options, args)

    if (cmd.hasOption("help")) {

      if (Locale.getDefault.getLanguage.eq("ru") && SystemUtils.IS_OS_WINDOWS) {

        config.helpFormatter.printHelp(
          new PrintWriter(new OutputStreamWriter(
            System.out, Charset.forName("cp866")), true
          ), config.helpFormatter.getWidth(),
          "app", null,
          config.options,
          config.helpFormatter.getLeftPadding(),
          config.helpFormatter.getDescPadding(), null, false)
      } else {
        config.helpFormatter.printHelp("app", config.options)
      }

      System.exit(0)
    }

    if (cmd.hasOption("conf"))
      appConf = cmd.getOptionValue("conf")


    return this
  }

  def createServer(): JettyEmbedded = {

    val pool = new QueuedThreadPool(
      200, 10, 60000,
      new ArrayBlockingQueue(600));
    pool.setDetailedDump(false)

    server = new Server(pool)

    val con = new ServerConnector(server)

    con.setHost(addr);
    con.setPort(port)

    server.setConnectors(Array[Connector](con))

    Runtime.getRuntime().addShutdownHook(new Thread() {
      override def run(): Unit = {
        try
          server.stop()

        catch {
          case e@(_: Exception) => {
            error(e)
          }

        } finally {

          synchronized {
            wait(1000)
          }
          deleteDir(tempDir)

          Try(cmdProgressExecutor.shutdown)
        }


      }
    });
    return this
  }


  @throws(classOf[IOException])
  def loadConf(): JettyEmbedded = {

    var is: InputStream = null

    log("info.using-config", appConf)

    try {

      val conf_file = new File(appConf)

      if (!conf_file.exists() || !conf_file.isFile()) {
        log("error.config-not-found", conf_file.getAbsolutePath())
        return this
      }


      is = new FileInputStream(conf_file)

      val p = new Properties();
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


      log(getNewProtocolBuilder()
        .append(i18n("info.dump-config"))
        .append("host", addr)
        .append("port", port)
        .append("contextPath", contextPath)
        .append("tmpPath", tempDir)
        .toString)

    } finally {
      if (is != null) {
        try
          is.close
        catch {
          case e@(_: Exception) => {}
        }
      }
    }
    return this
  }

  def mkDirs(): JettyEmbedded = {

    if (tempDir.exists())
      deleteDir(tempDir)

    tempDir.mkdirs();
    new File(tempDir, "work").mkdir()

    System.setProperty("jetty.home", tempDir.getAbsolutePath())
    return this
  }

  @throws(classOf[Exception])
  def startServer(): JettyEmbedded = {
    server.start();
    server.join();
    return this
  }

  @throws(classOf[Exception])
  def startApp(): JettyEmbedded = {

    val webapp = new WebAppContext()

    val servletHandler = webapp.getServletHandler()

    webapp.setInitParameter("org.eclipse.jetty.jsp.precompiled", "true")
    webapp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false")

    webapp.setContextPath(contextPath)
    webapp.setTempDirectory(tempDir)

    if (appWar != null) {
      webapp.setWar(appWar.getAbsolutePath())

      if (appWar.isDirectory())
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
    return this
  }

  @throws(classOf[UnsupportedEncodingException])
  def setupConsole(): JettyEmbedded = {

    // AnsiConsole.systemInstall(); Ansi.ansi().eraseScreen()

    System.out.write(IOUtils.toByteArray(
      getClass.getResource("/paster-rus.ans")))
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
    return this
  }

  private def deleteDir(from: File) =
    if (from.exists && from.isDirectory)
      Files.walkFileTree(Paths.get(from.toURI), new DeleteDirectory())

  def getNewProtocolBuilder(): ToStringBuilder = new ToStringBuilder("", style)

  def error(e: Exception) = e.printStackTrace

  def i18n(key: String) = if (messages.containsKey(key))
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

  def log(args: String*) = System.out.println(format(args: _*))


  class Config {

    val parser: CommandLineParser = new DefaultParser()
    val options: Options = new Options()
    val helpFormatter: HelpFormatter = new HelpFormatter()

    options.addOption(new Option("h", "help", false, i18n("args.msg.help")))
    options.addOption(new Option("v", "version", false, i18n("args.msg.version")))

    val oconf = Option.builder().argName("conf")
      .hasArg()
      .desc(format("args.msg.use-given", appConf)).build()

    options.addOption(oconf)

    val ohost = Option.builder().argName("host")
      .hasArg().desc(format("args.msg.host", addr)).build()

    options.addOption(ohost)


    val oport = Option.builder().argName("port")
      .hasArg()
      .desc(format("args.msg.port", port.toString)).build()

    options.addOption(oport)


    val octx = Option.builder().argName("contextPath")
      .hasArg()
      .desc(format("args.msg.contextPath", contextPath)).build()

    options.addOption(octx)

    val otmp = Option.builder().argName("tmpPath")
      .hasArg()
      .desc(format("args.msg.tmpPath", tempDir.getPath)).build()

    options.addOption(otmp)

  }

}

class DeleteDirectory extends SimpleFileVisitor[Path] {

  @throws(classOf[IOException])
  override def visitFile(file: Path, attributes: BasicFileAttributes): FileVisitResult = {
    try
      Files.delete(file)
    catch {
      case e@(_: Exception) => {
        // e.printStackTrace
      }
    }

    FileVisitResult.CONTINUE
  }

  @throws(classOf[IOException])
  override def postVisitDirectory(directory: Path,
                                  exception: IOException): FileVisitResult = if (exception == null) {

    try
      Files.delete(directory)
    catch {
      case e@(_: Exception) => {}
    }
    FileVisitResult.CONTINUE
  } else
    FileVisitResult.TERMINATE

}