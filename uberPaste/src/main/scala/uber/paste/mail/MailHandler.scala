package uber.paste.mail

import util.logging.Logged

import org.springframework.context.ApplicationContext
import org.subethamail.smtp.{RejectException, MessageContext, MessageHandler}
import uber.paste.manager.{PasteManager, UserManager, ConfigManager}

import uber.paste.model._
import java.io._
import java.util.Properties

import javax.mail.{BodyPart, MessagingException, Session}
import javax.mail.internet.{ContentType, MimeMessage}
import uber.paste.base.Loggered
import org.apache.commons.io.IOUtils
import uber.paste.dao.UserExistsException
import org.apache.commons.codec.binary.StringUtils
import org.apache.tika.parser.html.HtmlParser
import org.apache.tika.sax.{BodyContentHandler, XHTMLContentHandler}

import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.ParseContext
import org.apache.james.mime4j.dom._
import org.apache.james.mime4j.parser.ContentHandler
import org.apache.james.mime4j.parser.MimeStreamParser
import org.apache.james.mime4j.storage.MemoryStorageProvider
import org.apache.james.mime4j.message.DefaultMessageBuilder
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 06.02.13
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */

object MailHandler {
  val builder: DefaultMessageBuilder = new DefaultMessageBuilder

}

class MailHandler(appContext:ApplicationContext, ctx:MessageContext) extends Loggered with MessageHandler {

  private val userManager:UserManager = appContext.getBean("userManager").asInstanceOf[UserManager]
  private val pasteManager:PasteManager = appContext.getBean("pasteManager").asInstanceOf[PasteManager]

  private val settingsManager:ConfigManager = appContext.getBean("configManager").asInstanceOf[ConfigManager]
  private var allowed:Boolean =true
  private var currentUser:User=null

  private var mailFrom:String=null
  private var mailTo:String=null


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
    this.mailFrom=from
  }

  @throws(classOf[RejectException])
  override def recipient(recipient:String)  {
    logger.debug("RECIPIENT:" + recipient);
       this.mailTo=recipient
  }

  @throws(classOf[MessagingException])
  private def saveText(body:Body,subj:String)  {
    logger.debug("saving mail text..");


    var p1:Paste = new Paste
    p1.setName(mailFrom+" : " + ( if (subj==null) {""} else {subj}) )
    p1.setPasteSource(PasteSource.MAIL)

    p1.setOwner(null)

    val text = extractText(body.asInstanceOf[TextBody].getInputStream)

    logger.debug("after extract text "+text)


    if (text.length()<=0) {
      logger.debug("zero length data.cannot save.")
      return
    }


    p1.setText(text)
    p1 = pasteManager.save(p1)

    logger.debug("saved file id=" + p1.getId() );

  }

    @throws(classOf[MessagingException])
  private def saveAttachment(bodyPart:Entity,subj:String)  {

    logger.debug("uploadSave currentUser=" + currentUser+",fileName="+bodyPart.getFilename()+",mime="+bodyPart.getMimeType()+",charset="+bodyPart.getCharset);

   /* if (bodyPart.getFileName() == null || bodyPart.getFileName().trim().length() == 0) {
      logger.debug("no file name in attachment, skip for now.");
      return;
    }*/


    var p1:Paste = new Paste
    //p1.setOwner(admin)
    p1.setName(mailFrom +" : " + ( if (subj==null) {""} else {subj}))
    p1.setPasteSource(PasteSource.MAIL)

    p1.setOwner(null)
   // p1.setCodeType(CodeType.Plain)

    try {

      val text = extractText(bodyPart.getBody.asInstanceOf[TextBody].getInputStream)

      logger.debug("after extract text "+text)


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



      val message: Message = MailHandler.builder.parseMessage(data)

     // val message = new MimeMessage(Session.getDefaultInstance(new Properties()), data);

          logger.debug("message encoding "+message.getCharset)

      if (!message.isMultipart ) {
        logger.debug("non multipart message,class="+message.getBody.getClass.getName);
        saveText(message.getBody,message.getSubject)
        return;
      }


      val part: Multipart = message.getBody.asInstanceOf[Multipart]

      for (e:Entity<-part.getBodyParts()) {
                    if (!e.getMimeType().startsWith("text")) {
                      logger.debug("skip unsupported mime "+e.getMimeType)

                    } else {
                      saveAttachment(e,message.getSubject);
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

  def extractText(in:InputStream):String = {
    val handler:org.xml.sax.ContentHandler = new BodyContentHandler()
    val metadata = new Metadata()
    new HtmlParser().parse(in, handler, metadata, new ParseContext())
    return handler.toString()
  }

}
