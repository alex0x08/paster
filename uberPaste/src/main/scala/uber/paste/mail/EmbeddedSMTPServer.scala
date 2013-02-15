package uber.paste.mail

import org.subethamail.smtp.server.SMTPServer
import org.springframework.context.ApplicationContext
import uber.paste.base.Loggered
import uber.paste.base.Loggered
import org.subethamail.smtp.{MessageHandler, MessageHandlerFactory,MessageContext}
import org.springframework.context.ApplicationContext

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 06.02.13
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */

object EmbeddedSMTPServer {

        private var instance:EmbeddedSMTPServer = null

        def createInstance(ctx:ApplicationContext):EmbeddedSMTPServer = {
          if (instance == null) {
            instance = new EmbeddedSMTPServer();
            instance.init(ctx);
          }
          return instance;
        }

        def getInstance():EmbeddedSMTPServer = instance;
 }

class EmbeddedSMTPServer extends Loggered{

  private var mailFactory:MailMessageHandlerFactory=null
  private var smtpServer:SMTPServer=null

  def start() {
    smtpServer.start();

    logger.info("SMTP daemon started")

  }

  def stop() {
    smtpServer.stop();

    logger.info("SMTP daemon stopped")

  }

  private def init(ctx:ApplicationContext) {

    mailFactory = new MailMessageHandlerFactory(ctx)

    smtpServer = new SMTPServer(mailFactory)
    smtpServer.setPort(2525)

  }


}

class MailMessageHandlerFactory(appContext:ApplicationContext) extends Loggered with MessageHandlerFactory {


  def create(ctx:MessageContext):MessageHandler = {
    return new MailHandler(appContext, ctx);
  }
}
