package com.Ox08.paster.common
import com.Ox08.paster.webapp.base.Logged
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.{File, IOException}
import java.lang.management.ManagementFactory
import java.util
import scala.jdk.CollectionConverters._
trait SystemManagementService {
  def restartApplication(): Unit
}
@Service
class SystemManagementServiceImpl extends SystemManagementService with Logged {
  private var inRestart = false
  /**
   * Перезапустить JVM
   *
   * @throws IOException общая ошибка в случае проблем с перезапуском
   */
  @Async("pasterTaskExecutor")
  @throws[IOException]
  def restartApplication(): Unit = {
    if (inRestart) return
    inRestart = true
    val thName = Thread.currentThread().getName
    logger.debug("Restarting, current thread: {}", thName)
    if (!thName.startsWith("pasterTaskExecutor"))
      throw new IllegalStateException("Incorrect Task Executor")

    /**
     * пауза в секунду для отрисовки страницы с прогрессом перезагрузки
     */
    try Thread.sleep(1000)
    catch {
      case e: Exception =>
        e.printStackTrace()
      //ignore
    }
    try { // java binary
      val java = System.getProperty("java.home") + "/bin/java"
      // vm arguments
      val vmArguments = ManagementFactory.getRuntimeMXBean.getInputArguments
      val vmArgsOneLine = new StringBuffer()
      for (arg <- vmArguments.asScala) { // if it's the agent argument : we ignore it otherwise the
        // address of the old application and the new one will be in conflict

        if (!arg.contains("-agentlib")) {
          vmArgsOneLine.append(arg)
          vmArgsOneLine.append(" ")
        }
      }
      // init the command to execute, add the vm args
      val command = new util.ArrayList[String]
      command.add(java)
      for (s <- vmArguments.asScala) {
        command.add(s)
      }
      // program main and program arguments (be careful a sun property. might not be supported by all JVM)
      val mainCommand = System.getProperty("sun.java.command").split(" ")
      // program main is a jar
      if (mainCommand(0).endsWith(".jar")) { // if it's a jar, add -jar mainJar
        command.add("-jar")
        command.add(new File(mainCommand(0)).getPath)
      }
      else { // else it's a .class, add the classpath and mainClass
        command.add("-cp")
        command.add(System.getProperty("java.class.path"))
        command.add(mainCommand(0))
      }
      // finally add program arguments
      for (i <- 1 until mainCommand.length) {
        command.add(mainCommand(i))
      }
      logger.info("cmd: {}", command.toString)
      val builder = new ProcessBuilder(command)
      val env = System.getenv
      for (key <- env.keySet.asScala) {
        builder.environment.put(key, env.get(key))
      }
      // execute the command in a shutdown hook, to be sure that all the
      // resources have been disposed before restarting the application
      Runtime.getRuntime.addShutdownHook(new Thread() {
        override def run(): Unit = {
          try builder.start
          catch {
            case _: IOException =>
            // e.printStackTrace();
          }
        }
      })
      System.exit(0)
    } catch {
      case e: Exception =>
        // something went wrong
        throw new IOException("Error while trying to restart the application", e)
    }
  }
}
