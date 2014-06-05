package uber.paste.ws

import uber.paste.base.Loggered
import javax.jws.WebService
import org.springframework.beans.factory.annotation.Autowired
import uber.paste.dao.PasteDao
import uber.paste.manager.PasteManager
import uber.paste.model.{PasteSource, CodeType, Paste}

/**
 * Created with IntelliJ IDEA.
 * User: achernyshev
 * Date: 09.02.13
 * Time: 22:00
 * To change this template use File | Settings | File Templates.
 */

trait PasteWebservice {

  def addPaste(title:String,text:String,sourceType:String,codeType:String);

  }

@WebService(serviceName = "pasterWS")
class PasteWebserviceImpl extends PasteWebservice with Loggered{

  @Autowired
  private val pasteManager:PasteManager = null

  def addPaste(title:String,text:String,sourceType:String,codeType:String) {

    logger.debug("addPaste text="+text+" sourceType="+sourceType+" codeType="+codeType)

    val p = new Paste
    p.setCodeType(CodeType.valueOf(codeType))
    p.setPasteSource(PasteSource.valueOf(sourceType))
    p.setName(title)
    p.setText(text)

    pasteManager.save(p)
  }
}
