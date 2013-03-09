package uber.paste.mail

import util.logging.Logged

import org.springframework.context.ApplicationContext
import org.subethamail.smtp.{RejectException, MessageContext, MessageHandler}
import uber.paste.manager.{PasteManager, UserManager, ConfigManager}

import uber.paste.model._
import java.io._
import java.util.Properties

import javax.mail.{BodyPart, MessagingException, Session, Multipart}
import javax.mail.internet.{ContentType, MimeMessage}
import uber.paste.base.Loggered
import org.apache.commons.io.IOUtils
import uber.paste.dao.UserExistsException
import org.apache.commons.codec.binary.StringUtils
import org.apache.tika.parser.html.HtmlParser
import org.apache.tika.sax.{BodyContentHandler, XHTMLContentHandler}
import org.xml.sax.ContentHandler
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.ParseContext

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
  private def saveText(body:String,subj:String)  {
    logger.debug("saving mail text..");


    var p1:Paste = new Paste
    p1.setName(mailFrom+" : " + ( if (subj==null) {""} else {subj}) )
    p1.setPasteSource(PasteSource.MAIL)

    p1.setOwner(null)

    p1.setText(body)
    p1 = pasteManager.save(p1)

    logger.debug("saved file id=" + p1.getId() );

  }

    @throws(classOf[MessagingException])
  private def saveAttachment(bodyPart:BodyPart,subj:String,charset:String)  {

    logger.debug("uploadSave currentUser=" + currentUser+",fileName="+bodyPart.getFileName()+",mime="+bodyPart.getContentType()+",charset="+charset);

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

      val data = new ByteArrayOutputStream()

      IOUtils.copy(bodyPart.getInputStream(),data)


      var text = if (charset!=null ) {
        data.toString(charset)
      } else {
        new java.lang.String(data.toByteArray())
     }

      logger.debug("before extract text "+text)

      text = extractText(new ByteArrayInputStream(text.getBytes()))

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
      val message = new MimeMessage(Session.getDefaultInstance(new Properties()), data);

          logger.debug("message encoding "+message.getEncoding)

      if (message.getContent.isInstanceOf[Multipart]==false ) {
        logger.debug("non multipart message,class="+message.getContent.getClass.getName);
        saveText(message.getContent().asInstanceOf[String],message.getSubject)
        return;
      }

      val multipart = message.getContent().asInstanceOf[Multipart]
    //System.out.println(multipart.getCount());
      for (i <- 0 to multipart.getCount()-1) {

       val bodyPart:BodyPart = multipart.getBodyPart(i);
        logger.debug("multipart "+i+" type "+bodyPart.getContentType)

        if (bodyPart.getInputStream.available()>0)   {

          if (bodyPart.isMimeType("multipart/alternative")) {
            saveAttachment(bodyPart,message.getSubject,"UTF-8");  //only for lotus
          } else if (bodyPart.isMimeType("text/*")) {
            val cType = new ContentType(bodyPart.getContentType).getParameter("charset")
            saveAttachment(bodyPart,message.getSubject,cType);
          }

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
    val handler:ContentHandler = new BodyContentHandler()
    val metadata = new Metadata()
    new HtmlParser().parse(in, handler, metadata, new ParseContext())
    return handler.toString()
  }

}
