package uber.paste.controller

import org.springframework.web.servlet.view.AbstractView
import java.util
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import fr.opensagres.xdocreport.template.{IContext, TemplateEngineKind}
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.document.IXDocReport
import uber.paste.model.{Comment, Paste}
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 19.05.13
 * Time: 21:16
 * To change this template use File | Settings | File Templates.
 */
class PasteDocxView extends AbstractView{

  setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")

  var report:IXDocReport = null

  var fieldsMetadata:FieldsMetadata = null

  def renderMergedOutputModel(model: util.Map[String, AnyRef], request: HttpServletRequest, response: HttpServletResponse) {

    if (report==null) {
        report =XDocReportRegistry.getRegistry().loadReport(
        getApplicationContext().getResource("classpath:/templates/paste.docx").getInputStream,
        TemplateEngineKind.Velocity)

    //    fieldsMetadata =report.createFieldsMetadata()

      //  fieldsMetadata.load("paste", classOf[Paste])

       // fieldsMetadata.load("comments", classOf[Comment],true)


    }

    val context:IContext = report.createContext()

    val paste = model.get(GenericController.MODEL_KEY).asInstanceOf[Paste]


    context.put("paste", paste)
    context.put("comments", paste.getComments())

    response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")

    report.process(context, response.getOutputStream)

  }
}
