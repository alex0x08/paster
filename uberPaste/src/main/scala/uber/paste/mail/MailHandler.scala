package uber.paste.mail

import util.logging.Logged

import org.springframework.context.ApplicationContext
import org.subethamail.smtp.{RejectException, MessageContext, MessageHandler}
import uber.paste.manager.{PasteManager, UserManager, ConfigManager}

import uber.paste.model._
import java.io._
import java.util.Properties

import javax.mail.{BodyPart, MessagingException, Session, Multipart}
import javax.mail.internet.MimeMessage
import uber.paste.base.Loggered
import org.apache.commons.io.IOUtils
import uber.paste.dao.UserExistsException

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 06.02.13
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
class MailHandler(appContext:ApplicationContext, ctx:MessageContext) extends Loggered with MessageHandler {

  private val userManager:UserManager = appContext.getBean("userManager").asInstanceOf[UserManager]
  private val pasteManager:PasteManager = appContext.getBean("pasteManager").asInstanceOf[PasteManager]

  private val settingsManager:ConfigManager = appContext.getBean("configManager").asInstanceOf[ConfigManager]
  private var allowed:Boolean =true
  private var currentUser:User=null



  @throws(classOf[RejectException])
  override def from(from:String)   {


    logger.debug("seek for user with email " + from)

  /*  currentUser = userManager..getUserByEmail(from);
    if (currentUser != null) {

      getLogger().debug("user found. allowed to proceed.");
      allowed = true;
    } else {
      getLogger().debug("user not found. skip processing.");
      throw new RejectException(553, "<" + from + "> user unknown");
    }*/
  }

  @throws(classOf[RejectException])
  override def recipient(recipient:String)  {
    logger.debug("RECIPIENT:" + recipient);

  }

  @throws(classOf[MessagingException])
  private def saveAttachment(bodyPart:BodyPart,subj:String)  {

    logger.debug("uploadSave currentUser=" + currentUser+",fileName="+bodyPart.getFileName()+",mime="+bodyPart.getContentType());

   /* if (bodyPart.getFileName() == null || bodyPart.getFileName().trim().length() == 0) {
      logger.debug("no file name in attachment, skip for now.");
      return;
    }*/


    var p1:Paste = new Paste
    //p1.setOwner(admin)
    p1.setName("Mail "+subj)
    p1.setPasteSource(PasteSource.MAIL)

    p1.setOwner(null)
   // p1.setCodeType(CodeType.Plain)

    try {

      val data = new ByteArrayOutputStream()

      IOUtils.copy(bodyPart.getInputStream(),data)

      val text = new java.lang.String(data.toByteArray())

      if (text.length()<=0) {
        logger.debug("zero length data.cannot save.")
        return
      }

      p1.setText(text)
      p1 = pasteManager.save(p1)

      logger.debug("saved file id=" + p1.getId() );

    } catch {
      case e:IOException => {
      logger.debug(e.getLocalizedMessage(), e)
    }



  }
  }
  @throws(classOf[IOException])
  override def data(data:InputStream) {
    logger.debug("MAIL DATA");

    if (!allowed) {
      return;
    }

    try {
      val message = new MimeMessage(Session.getDefaultInstance(new Properties()), data);



      if (message.getContent.isInstanceOf[Multipart]==false ) {
        logger.debug("non multipart message, skip for now, class="+message.getContent.getClass.getName);
        return;
      }

      val multipart = message.getContent().asInstanceOf[Multipart]
    //System.out.println(multipart.getCount());
      for (i <- 0 to multipart.getCount()-1) {

       val bodyPart:BodyPart = multipart.getBodyPart(i);

        logger.debug("multipart "+i+" type "+bodyPart.getContentType)
        if (bodyPart.getContentType.startsWith("text/html") || bodyPart.getContentType.startsWith("text/plain")) {
                                if (bodyPart.getInputStream.available()>0)
          saveAttachment(bodyPart,message.getSubject);

        }
    }

    } catch {
      case e:MessagingException => {
        logger.error(e.getLocalizedMessage(), e);

      }
    }



  }

  override def done() {
    //System.out.println("Finished");
  }


}
