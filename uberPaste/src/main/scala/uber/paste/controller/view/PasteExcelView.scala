package uber.paste.controller.view

import org.springframework.web.servlet.view.document.AbstractExcelView
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.poi.hssf.usermodel.{HSSFCell, HSSFRow, HSSFSheet, HSSFWorkbook}
import uber.paste.controller.GenericController
import uber.paste.model.Paste
import org.springframework.beans.support.PagedListHolder
import java.util.Collections
import scala.collection.JavaConversions._
import java.util
import net.sf.jxls.transformer.{Workbook, XLSTransformer}
import org.apache.poi.ss.usermodel.WorkbookFactory
import uber.paste.base.SpringAppContext

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 03.05.13
 * Time: 15:54
 */
class PasteExcelView  extends AbstractExcelView {


  setUrl("classpath:/templates/pastas.xls")

 override protected def getTemplateSource(url:String, request:HttpServletRequest):HSSFWorkbook
 =WorkbookFactory.create(getApplicationContext().getResource(url).getInputStream).asInstanceOf[HSSFWorkbook]


  @throws(classOf[Exception])
  protected def buildExcelDocument(
    model:java.util.Map[String,Object],
     wb:HSSFWorkbook,
    req:HttpServletRequest,
     resp:HttpServletResponse)
   {

  //  val sheet:HSSFSheet = wb.createSheet("Spring")
  //  var  sheetRow:HSSFRow = null


    // Go to the first sheet
    // getSheetAt: only if wb is created from an existing document
    // sheet = wb.getSheetAt(0);
    //sheet.setDefaultColumnWidth((short) 12);

    // write a text at A1
    //var cell:HSSFCell = getCell(sheet, 0, 0);
    //setText(cell, "Spring-Excel test");

    val ph = model.get(GenericController.NODE_LIST_MODEL_PAGE).asInstanceOf[PagedListHolder[Paste]]

    val list:java.util.List[Paste] = if (ph!=null) {
      ph.getPageList
    } else {
      Collections.emptyList[Paste]()
    }

     val beans:java.util.Map[String,Object] = new java.util.HashMap()
     beans.put("pastas", list)

     val transformer = new XLSTransformer()

     transformer.transformWorkbook(wb,beans)


     /*book.getSheets

     wb.createSheet().

     var i = -1
    for (p:Paste<-list) {
      cell = getCell(sheet, 2+i.+(1), 0)
      setText(cell,p.getTitle())

    }
       */
  }
}
