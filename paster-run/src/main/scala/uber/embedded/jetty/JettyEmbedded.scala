

package uber.embedded.jetty

import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.io.PrintStream
import java.io.PrintWriter
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.text.MessageFormat
import java.util.Locale
import java.util.Properties
import java.util.ResourceBundle
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import org.apache.commons.cli.{CommandLineParser,
                               Options,
                               Option,PosixParser,
                               OptionBuilder, ParseException, CommandLine, HelpFormatter}
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.SystemUtils
import org.apache.commons.lang.builder.StandardToStringStyle
import org.apache.commons.lang.builder.ToStringBuilder
import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.component.AbstractLifeCycle
import org.eclipse.jetty.util.component.LifeCycle
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.eclipse.jetty.webapp.WebAppContext
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import scala.util.Try

object JettyEmbedded {
  
  def main(args: Array[String]) {
    
    new JettyEmbedded()
                .setupConsole
                .validateInput(args)                
                .loadConf
                .mkDirs
                .createServer
                .startApp
                .startServer    
  }
}

class JettyEmbedded {
   
  private var appConf:String = "config.properties"
    
  private var addr:String = "0.0.0.0"
    
  private var contextPath = "/"
    
  private var port = 8080
    
  private var tempDir = new File("temp")
    
  private var appWar:File = null
    
  private var server:Server = null
    
  private var cmd:CommandLine = null
  
  private val cmdProgressExecutor = Executors.newSingleThreadExecutor()
  
  private final val messages:ResourceBundle = ResourceBundle
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
  def validateInput(args:Array[String]):JettyEmbedded = {

            // parse the command line arguments
            cmd= config.parser.parse(config.options, args)

             if (cmd.hasOption("help")) {
               
                if (Locale.getDefault.getLanguage.eq("ru") && SystemUtils.IS_OS_WINDOWS) {
      
                config.helpFormatter.printHelp(
                        new PrintWriter(new OutputStreamWriter(
                                    System.out, Charset.forName("cp866")), true         
                                        ), config.helpFormatter.getWidth(),
                          "app", null,
                          config.options,
                          config.helpFormatter.getLeftPadding(), 
                          config.helpFormatter.getDescPadding(),null,false)
                } else {
                  config.helpFormatter.printHelp("app",config.options)
                }
      
                 System.exit(0)               
             }
            
            if (cmd.hasOption("conf")) 
                appConf = cmd.getOptionValue("conf")
              
            
        return this
  }

  def createServer():JettyEmbedded = {

        val pool = new QueuedThreadPool(
          200,10,60000,
            new ArrayBlockingQueue(600)); pool.setDetailedDump(false)
       
        server = new Server(pool)
   
        val con = new ServerConnector(server)
        
        con.setHost(addr); con.setPort(port)
        
        server.setConnectors(Array[Connector](con))

        Runtime.getRuntime().addShutdownHook(new Thread() {
            override def run() {
                try 
                    server.stop()                   
                    
                   catch {
                        case e @ (_ : Exception ) => {
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
        }); return this
    }
 
  
  @throws(classOf[IOException])
  def loadConf():JettyEmbedded =  {

      var  is:InputStream = null

      log("info.using-config" , appConf)

        try {

            val conf_file = new File(appConf)    
      
            if (!conf_file.exists() || !conf_file.isFile()) {
              log("error.config-not-found",conf_file.getAbsolutePath())
              return this              
            }            

    
            is = new FileInputStream(conf_file)

            val p = new Properties(); p.load(is)

            val appWarS = p.getProperty("app.war", null)

    
      
            if (appWarS != null) 
                this.appWar = new File(appWarS)
            

            port = Integer.valueOf(
                      if (cmd.hasOption("port")) 
                          cmd.getOptionValue("port")
                      else 
                          p.getProperty("http.port", port + "")
                      )
      
            addr = if (cmd.hasOption("host")) { cmd.getOptionValue("host") } 
                  else p.getProperty("app.host", null)

            contextPath = if (cmd.hasOption("contextPath")) { 
                  cmd.getOptionValue("contextPath") 
                } else
                  p.getProperty("app.context", contextPath)


      if (cmd.hasOption("tmpPath")) 
        tempDir = new File(cmd.getOptionValue("tmpPath")) 
           
      
      log(getNewProtocolBuilder()
                  .append(i18n("info.dump-config"))
                  .append("host",addr)
                  .append("port",port)
                  .append("contextPath",contextPath)
                  .append("tmpPath",tempDir)
                  .toString)              
      
        } finally {
            if (is != null) {
                try 
                    is.close
                catch {
                  case e @ (_ : Exception ) => {}
                }
            }
        }
        return this
  }
  
  def mkDirs():JettyEmbedded = { 
    
       if (tempDir.exists()) 
            deleteDir(tempDir)        

        tempDir.mkdirs(); new File(tempDir, "work").mkdir()

        System.setProperty("jetty.home", tempDir.getAbsolutePath())
       return this
  }
  
  @throws(classOf[Exception])
  def startServer():JettyEmbedded = {
        server.start(); server.join(); return this
  }
  
  @throws(classOf[Exception])
  def startApp():JettyEmbedded =  {

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
          server.addEventListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
              override def lifeCycleStarted(event:LifeCycle) {
                cmdProgressExecutor.shutdown
              }
              
            override def lifeCycleStopped(event:LifeCycle) {
                deleteDir(tempDir)
              }
              } )
            
    

      
        } else 
          log("error.application-not-set")
      return this
  }

  @throws(classOf[UnsupportedEncodingException])
  def setupConsole(): JettyEmbedded = {   
    
    AnsiConsole.systemInstall(); Ansi.ansi().eraseScreen()
    
    System.out.write(IOUtils.toByteArray(
        getClass.getResource("/paster-rus.ans")))
  
    if (Locale.getDefault.getLanguage.eq("ru") && SystemUtils.IS_OS_WINDOWS) {
    
      System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out),true,"cp866"))
      System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err),true,"cp866"))
    
    }
    
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
    
   
    return this
  }
  
  private def deleteDir(from:File) = 
    if (from.exists && from.isDirectory) 
        Files.walkFileTree(Paths.get(from.toURI), new DeleteDirectory())
   
  def getNewProtocolBuilder():ToStringBuilder = new ToStringBuilder("",style)
  
  def error(e:Exception ) = e.printStackTrace
  
  def i18n(key:String) = if (messages.containsKey(key)) 
                messages.getString(key)
               else 
              key
  
  def format(args: String*):String = {
    if (args.length>1) {
            MessageFormat.format(
              i18n(args(0)), args.slice(1,args.length): _*)
        } else 
            i18n(args(0))        
  }
  
  def log(args: String*) =System.out.println(format(args: _*))
  
  
  class Config {
  
  val parser:CommandLineParser = new PosixParser()
  val options:Options = new Options()
  val helpFormatter:HelpFormatter =new HelpFormatter()
        
  options.addOption(new Option("h","help",false, i18n("args.msg.help")))
  options.addOption(new Option("v","version",false, i18n("args.msg.version")))
  
  OptionBuilder.withArgName("conf")
  OptionBuilder.hasArg
  OptionBuilder.withDescription(format("args.msg.use-given",appConf))
  
  options.addOption(OptionBuilder
                    .create("conffile"))
    
  OptionBuilder.withArgName("host")
  OptionBuilder.hasArg
  OptionBuilder.withDescription(format("args.msg.host",addr))
  
  options.addOption(OptionBuilder
                    .create("host"))
  
  
  OptionBuilder.withArgName("port")
  OptionBuilder.hasArg
  OptionBuilder.withDescription(format("args.msg.port",port.toString))
  
  options.addOption(OptionBuilder
                    .create("port"))
  
  
  OptionBuilder.withArgName("contextPath")
  OptionBuilder.hasArg
  OptionBuilder.withDescription(format("args.msg.contextPath",contextPath))
  
  options.addOption(OptionBuilder
                    .create("contextPath"))
 
  OptionBuilder.withArgName("tmpPath")
  OptionBuilder.hasArg
  OptionBuilder.withDescription(format("args.msg.tmpPath",tempDir.getPath))
  
  options.addOption(OptionBuilder
                    .create("tmpPath"))
 
  }
  
}

class DeleteDirectory extends SimpleFileVisitor[Path] {
  
  @throws(classOf[IOException])
  override def visitFile(file:Path , attributes:BasicFileAttributes):FileVisitResult = {
        try 
            Files.delete(file)
            catch {
                  case e @ (_ : Exception ) => {
                     // e.printStackTrace
                  }
                }
        
        return FileVisitResult.CONTINUE
  }

  @throws(classOf[IOException])
  override def  postVisitDirectory(directory:Path,
       exception:IOException):FileVisitResult =  return if (exception == null) {
       
         try 
             Files.delete(directory)
            catch {
                  case e @ (_ : Exception ) => {}
                }
                FileVisitResult.CONTINUE
            } else 
             FileVisitResult.TERMINATE
            //else 
            //throw exception
          
}