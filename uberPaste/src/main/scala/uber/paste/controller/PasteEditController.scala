/*
 * Copyright 2011 WorldWide Conferencing, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uber.paste.controller

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation._
import uber.paste.model._
import org.springframework.beans.factory.annotation.{Value, Autowired}
import java.io.ByteArrayOutputStream
import uber.paste.manager.{CommentManager, PasteManager}
import org.springframework.ui.Model
import uber.kaba.markup.parser.AppMode
import uber.kaba.markup.parser.KabaMarkupParser
import uber.kaba.markup.parser.KabaMarkupParser
import uber.paste.base.SessionStore
import scala.util.control.Breaks._
import org.springframework.validation.BindingResult
import javax.validation.Valid
import java.util.Locale
import scala.collection.JavaConversions._
import org.codehaus.jackson.annotate.JsonIgnore
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.{StringEscapeUtils, WordUtils, StringUtils}
import scala.Array
import com.google.gson.{JsonParser, GsonBuilder}
import java.net.URL
import com.google.gson.stream.MalformedJsonException
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import javax.annotation.Resource
import org.springframework.context.MessageSource
import java.util
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.nio.file.FileSystems
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import java.nio.file.FileSystems

/**
 * Paste model controller
 * Support for all operations related to one paste model: edit,view,delete,add comments..
 */
@Controller
@RequestMapping(Array("/paste"))
//@SessionAttributes(Array(GenericController.MODEL_KEY))
class PasteController extends VersionController[Paste]   {

  @Autowired
  val pasteManager:PasteManager = null

  @Autowired
  val commentManager:CommentManager = null

  @Value("${config.share.integration}")
  val shareIntegration:Boolean = false

  @Value("${config.share.url}")
  val shareUrl:String = null

  @Resource(name="mimeTypeSource")
  protected val mimeSource:MessageSource = null

  @Resource(name="mimeExtSource")
  protected val mimeExtSource:MessageSource = null

  def listPage()="redirect:/main/paste/list"
  def editPage()="paste/edit"
  def viewPage()="paste/view"

  def manager():PasteManager = return pasteManager

  @InitBinder
  def initBinder(binder:WebDataBinder):Unit = {
    binder.initDirectFieldAccess()
    binder.registerCustomEditor(classOf[CodeType], new CodeTypeEditor())
  //  binder.setDisallowedFields("id","lastModified")
  }
  
  override def fillEditModel(obj:Paste,model:Model,locale:Locale)  {
         super.fillEditModel(obj,model,locale)

    if (obj.isBlank) {
      model.addAttribute("title",getResource("paste.new",locale))
    } else {
      model.addAttribute("title",StringEscapeUtils.escapeHtml(getResource("paste.edit.title",Array(obj.getId,obj.getName()),locale)))
    }
    model.addAttribute("availableCodeTypes", CodeType.list)
    model.addAttribute("availablePriorities", Priority.list)

    if (!obj.getComments.isEmpty) {
      val commentLines = new util.ArrayList[Long]
      for (c<-obj.getComments) {
        commentLines.add(c.getLineNumber)
      }
      
      model.addAttribute("commentedLinesList", StringUtils.join(commentLines,","))
    }
  
    if (!obj.isBlank()) {

      val pnext = manager.getNextPaste(obj)
      val pprev = manager.getPreviousPaste(obj)
      
      model.addAttribute("availableNext",pnext)
      model.addAttribute("availablePrev",pprev)      
      model.addAttribute("availablePrevList",new IdList(manager.getPreviousPastasIdList(obj)))
      
    } else {
      model.addAttribute("availableNext",null)
      model.addAttribute("availablePrev",null)
    }

    model.addAttribute("shareIntegration",shareIntegration)
    model.addAttribute("shareUrl",shareUrl)

    //  obj.tagsAsString = for (s<-obj.getTags()) yield s+" "

    /**
     * concatenate all model tags objects to one string
     */
    obj.tagsAsString={
      val out =new StringBuilder
      for (s<-obj.getTags()) {
        out.append(s).append(" ")
      }
      out.toString }
  }

  def getNewModelInstance():Paste = {
    val p = new Paste
    p.setOwner(getCurrentUser)
    return p
  }

  @ModelAttribute("comment")
  def getNewCommentInstance():Comment = {
    val p = new Comment
    p.setOwner(getCurrentUser)
    return p
  }


  @RequestMapping(value = Array("/removeComment"), method = Array(RequestMethod.POST,RequestMethod.GET))
  def removeComment(@RequestParam(required = true) pasteId:java.lang.Long,
                    @RequestParam(required = true) commentId:java.lang.Long,
                    @RequestParam(required = true) lineNumber:java.lang.Long,
                    model:Model,locale:Locale):String = {

    logger.debug("_removeComment pasteId="+pasteId+" commentId="+commentId+" lineNumber="+lineNumber)

    val p = manager.getFull(pasteId)

    if (p==null) {
      return page404
    }

    val c = new Comment
    c.setId(commentId)

    p.getComments().remove(c)

    manager.save(p)

    model.asMap().clear()

    return "redirect:/main/paste/" + pasteId + "#line_" + lineNumber
  }

  /**
   * add new comment
   * @param pasteId
   * @param b
   * @param result
   * @param model
   * @param locale
   * @return
   */
  @RequestMapping(value = Array("/saveComment"), method = Array(RequestMethod.POST))
  def saveComment(@RequestParam(required = true) pasteId:java.lang.Long,
                    @Valid b:Comment,
                    result:BindingResult, model:Model,locale:Locale):String = {

        val p = manager.getFull(pasteId)

        if (p==null) {
          return page404
        }

        p.setTitle(null) // magic to invoke event listeners

        logger.debug("adding comment "+b)

    if (result.hasErrors()) {
      logger.debug("form has errors " + result.getErrorCount());
      model.addAttribute("comment", b)
      fillEditModel(p,model,locale)

      return viewPage
    }

    if (b.getOwner()!=null) {
      b.getOwner().increaseTotalComments()
    }
    
    b.setText(
      KabaMarkupParser.getInstance().setSource(b.getText).setMode(AppMode.PASTE)
      .setShareUrl(shareUrl)
                .parseAll().get());
    
    
      p.getComments().add(b)

      manager.save(p)

    if (b.getOwner()!=null) {
      SessionStore.instance.updateUser(b.getOwner())
    }

    model.asMap().clear()

    return "redirect:/main/paste/" + pasteId + "#line_" + b.getLineNumber
  }

  @RequestMapping(value = Array("/integrated/new/{integrationCode:[a-z0-9_]+}"),
    method = Array(RequestMethod.GET))
  @ResponseStatus(HttpStatus.CREATED)
  def createNewIntegrated(model:Model,
                          @PathVariable("integrationCode") integrationCode:String,
                          locale:Locale):String= {

    val newPaste =  getNewModelInstance()
        newPaste.setIntegrationCode(integrationCode)

    fillEditModel(newPaste,model,locale)

    return editPage
  }


 @RequestMapping(value = Array("/save-plain"), method = Array(RequestMethod.POST))
  override def save(@RequestParam(required = false) cancel:String,

                              @Valid @ModelAttribute(GenericController.MODEL_KEY) b:Paste,
                              result:BindingResult, model:Model,locale:Locale
                              ,redirectAttributes:RedirectAttributes):String = {
    return saveIntegrated(cancel,false,b,result,model,locale,redirectAttributes)
  }

    @RequestMapping(value = Array("/save"), method = Array(RequestMethod.POST))
   def saveIntegrated(@RequestParam(required = false) cancel:String,
                      @RequestParam(required = false) integrationMode:Boolean,
           @Valid @ModelAttribute(GenericController.MODEL_KEY) b:Paste,
           result:BindingResult, model:Model,locale:Locale,
           redirectAttributes:RedirectAttributes):String = {

      /**
       * copy fields not filled in form
       */
      if (!b.isBlank()) {
        val current = manager.getFull(b.getId())
        b.getComments().addAll(current.getComments())
        b.setPasteSource(current.getPasteSource())
        b.setIntegrationCode(current.getIntegrationCode())
       // b.setThumbImg(current.getThumbImg())
      }

     val tags =  b.tagsAsString

     if (tags!=null) {
       b.getTags().clear()
       for (s<-tags.split(" ")) {
         if (!StringUtils.isBlank(s)) {
           b.getTags().add(s)
         }
       }
     }

      /**
       * check if normalized was set
       */
      if (b.isNormalized()) {

        b.getCodeType() match {

          case CodeType.JavaScript => {
            /**
             * try to normalize json
             */
            try {

              val o = new JsonParser().parse(b.getText()).getAsJsonObject()
              b.setText(new GsonBuilder().setPrettyPrinting().create().toJson(o))

            } catch{
              case e @ (_ : MalformedJsonException | _ :Exception) => {
                      //ignore
                }
              }
          }
          case CodeType.Plain => {
            /**
             * set word wrap for plain
             */
             b.setText(WordUtils.wrap(b.getText(),80))
          }
          case _ => {}
        }
      }

      if (b.isBlank()) {
        if (isCurrentUserLoggedIn()) {
          b.setOwner(getCurrentUser())
          b.getOwner().increaseTotalPastas()
          SessionStore.instance.updateUser(b.getOwner())
        }
      }

    

      logger.debug("__found thumbnail "+b.getThumbImage())
      logger.debug("__found comments "+b.getComments().size())

    
    if (b.getThumbImage()!=null) {
        
        val fimg = FileSystems.getDefault().getPath(System.getProperty("paste.app.home"),"images",b.getUuid).toFile
        
          FileUtils.writeStringToFile(fimg, b.getThumbImage())
          
          b.setThumbImage(fimg.getName)
      }
    
       val out =super.save(cancel,b,result,model,locale,redirectAttributes)
      return if (out.equals(listPage))  {
        model.asMap().clear()
        "redirect:/main/paste/"+b.getId()
      } else {out}
   }

  def getMimeResource(key:String):String = mimeSource.getMessage(key,
    new Array[java.lang.Object](0),null,Locale.getDefault)

  def getMimeExtResource(key:String):String = mimeExtSource.getMessage(key,
    new Array[java.lang.Object](0),null,Locale.getDefault)

  
   private def  getTrustingManager():Array[TrustManager] =  Array[TrustManager] { new X509TrustManager() {
            override def getAcceptedIssuers(): Array[java.security.cert.X509Certificate] = null

            override def checkClientTrusted(certs:Array[X509Certificate], authType:String) {
                // Do nothing
            }
           override def checkServerTrusted(certs:Array[X509Certificate], authType:String)  {
                // Do nothing
            }        
      }
    }
  

  @RequestMapping(value = Array("/loadFrom"), method = Array(RequestMethod.GET))
  def getRemote(@RequestParam(required = false) url:java.lang.String,
                         model:Model,
                         locale:Locale):String = {

    val client = new DefaultHttpClient; val method = new HttpGet(url)

     val sc:SSLContext = SSLContext.getInstance("SSL")
     
        sc.init(null, getTrustingManager(), new java.security.SecureRandom())

        val socketFactory:SSLSocketFactory = new SSLSocketFactory(sc)
        val sch:Scheme  = new Scheme("https", 443, socketFactory)
        client.getConnectionManager().getSchemeRegistry().register(sch)
    
    val response =  client.execute(method)

    var paste =  manager.getByRemoteUrl(url)

    if (paste==null) {
      paste =getNewModelInstance(); paste.setRemoteUrl(url)
    } else {
      paste.setName(null)
    }

    var fileName:String = null

    val cd =response.getFirstHeader("Content-Disposition")
    if (cd!=null) {
      //val loop = new Breaks
      breakable {
        for (e<-cd.getElements) {
          //     System.out.println("name="+e.getName)
          if (e.getName().equalsIgnoreCase("attachment")||e.getName().equalsIgnoreCase("inline")) {
            for (p<-e.getParameters) {
              //                System.out.println("p="+p.getName+" |"+p.getValue)
              if (p.getName.startsWith("filename")){
                paste.setName(p.getValue)
                fileName=p.getValue
                break
              }
            }
            break
          }
        }
      }
    }

    if (StringUtils.isEmpty(paste.getName())) {
      var fname =new URL(url).getFile
      if (fname!=null) {
        fname = FilenameUtils.getName(fname)
        fileName=fname
      }
      paste.setName(if (fname!=null) {fname} else {url})
    }

      var mime:String = null
      if (fileName!=null) {
          mime=getMimeExtResource(FilenameUtils.getExtension(fileName))
      }

    if (mime==null) {
      mime = response.getEntity.getContentType.getElements.head.getName
    }

    /*
    for (el<-response.getEntity.getContentType.getElements) {
               System.out.println("el "+el.getName+" "+el.getValue)
                for (p<-el.getParameters) {
                  System.out.println("p "+p.getName+" "+p.getValue)

                }
    } */

    val codeType = getMimeResource(mime)

    if (codeType == null) {
        logger.debug("__unsupported mime type "+response.getEntity.getContentType.getValue)
        model.addAttribute("statusMessageKey", "unsupported mime type "+response.getEntity.getContentType.getValue)
      return "redirect:/main/paste/new"
    }

     paste.setCodeType(CodeType.valueOf(codeType))
     paste.setPasteSource(PasteSource.REMOTE)

    val buf =new ByteArrayOutputStream

    response.getEntity.writeTo(buf)

    paste.setText(new String(buf.toByteArray))

    paste=manager.save(paste)

    model.asMap().clear()

    return "redirect:/main/paste/integrated/view/" + paste.getId()
 }

  @RequestMapping(value = Array("/integrated/view/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  def getByPathIntegrated(@PathVariable("id") id:java.lang.Long,model:Model,locale:Locale):String = {
    val r = getByPath(id,model,locale)
    return if (!r.equals(viewPage))
      r
    else
      "/paste/integrated/view"
  }

  @RequestMapping(value = Array("/integrated/preview/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  def getByPathIntegratedPreview(@PathVariable("id") id:java.lang.Long,model:Model,locale:Locale):String = {
    val r = getByPath(id,model,locale)
    return if (!r.equals(viewPage))
      r
    else
      "/paste/integrated/preview"
  }

  
  @RequestMapping(value = Array("/raw/view"), method = Array(RequestMethod.GET))
  def getByPathRaw(@RequestParam(required = true) id:java.lang.Long,model:Model,locale:Locale):String = {
    val r = getByPath(id,model,locale)
    return if (!r.equals(viewPage))
      r
    else
      "/paste/raw/view"
  }


  @RequestMapping(value = Array("/{id:[0-9]+}"), method = Array(RequestMethod.GET))
  override def getByPath(@PathVariable("id") id:java.lang.Long,model:Model,locale:Locale):String = {

    val r = super.getByPath(id,model,locale)
         if (!r.equals(viewPage))
           return r

    val p = model.asMap().get(GenericController.MODEL_KEY).asInstanceOf[Paste]

    model.addAttribute("title",getResource("paste.view.title",Array(p.getId,StringEscapeUtils.escapeHtml(p.getName())),locale))
    
    return viewPage
  }



  /**
   *
   * @return available code types
   */
  @RequestMapping(value = Array("/codetypes"), method = Array(RequestMethod.GET))
  @ResponseBody
  @JsonIgnore
  def getAvailableCodeTypes():java.util.Collection[CodeType] = {
              return CodeType.list
  }

  /**
   * return plain paste text
   * @param id
   * @param model
   * @param locale
   * @return
   */
  @RequestMapping(value = Array("/plain/{id:[0-9]+}"), method = Array(RequestMethod.GET), produces = Array("text/plain;charset=UTF-8"))
  @ResponseBody
  def getBodyPlain(@PathVariable("id") id:java.lang.Long,model:Model,locale:Locale):String = {
    return loadModel(id).getText();
  }

  /**
   *
   * @return new search query object
   */
  @ModelAttribute("query")
  def newQuery():OwnerQuery = {
    val out =new OwnerQuery
    //  if (!isCurrentUserAdmin) {
    //    out.setOwnerId(getCurrentUser.getId)
    // }
    return out
  }

}

class IdList(list:java.util.List[Long]) {
  
  def getCount() = list.size
  
  def getItems() = list
  
  def getItemsAsString = StringUtils.join(list,",")
  
}
