/*
 * Copyright 2015 Ubersoft, LLC.
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

package uber.paste.manager

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.security.PrivilegedActionException
import java.security.PrivilegedExceptionAction
import java.util.ArrayList

import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.HashMap
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.jcr.Node
import javax.jcr.NodeIterator
import javax.jcr.PropertyType
import javax.jcr.Repository
import javax.jcr.RepositoryException
import javax.jcr.Session
import javax.security.auth.Subject
import javax.servlet.ServletContext
import org.apache.jackrabbit.JcrConstants
import org.apache.jackrabbit.api.JackrabbitRepository
import org.apache.jackrabbit.commons.JcrUtils
import org.apache.jackrabbit.commons.cnd.CndImporter
import org.apache.jackrabbit.oak.api.AuthInfo
//import org.apache.jackrabbit.oak.plugins.index.IndexConstants
//import org.apache.jackrabbit.oak.plugins.index.lucene.IndexFormatVersion
//import org.apache.jackrabbit.oak.plugins.index.lucene.LuceneIndexConstants

import org.apache.jackrabbit.oak.run.osgi.OakOSGiRepositoryFactory
import org.apache.jackrabbit.oak.spi.security.authentication.AuthInfoImpl
import org.apache.jackrabbit.oak.spi.security.principal.AdminPrincipal
import org.apache.lucene.queryparser.classic.ParseException

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.core.io.Resource
import org.springframework.security.crypto.codec.Base64
import org.springframework.stereotype.Component
import org.springframework.web.context.ServletContextAware
import uber.paste.base.Loggered
import uber.paste.model.SavedFile

@Component("repositoryManager")
class RepositoryManager extends ServletContextAware with Loggered{

  private var repository: Repository = null

  private var sc:ServletContext = null
  
  @Value("classpath:/oak/repository-config.json")
  private var config: Resource = null

  @Value("classpath:/oak/shared-file.cnd")
  private var sharedFileDef: Resource = null

  @Value("${bundle.filter}")
  private var bundleFilter: String = null
  
 
    
  
    @PostConstruct
    private def init() {

       val repo_home = new File(System.getProperty("paste.app.home"), "repo")

        var s:Session  = null
        try {
            repository = createRepository(config.getFile(), repo_home);

            s = createAdministrativeSession();
            
            CndImporter.registerNodeTypes( new InputStreamReader(sharedFileDef.getInputStream()), s);
            
            /*if (!s.nodeExists("/oak:index/lucene")) {
                createFullTextIndex(s);
                s.save();
            }*/

            if (!s.nodeExists("/paste_content")) {
                s.getRootNode().addNode("paste_content", "nt:folder");
                s.save();

            }

            sc.setAttribute("javax.jcr.Repository", repository);
       } catch {
      case e @ (_ : RepositoryException |_ : IOException | _: ParseException) => {
          throw new IllegalStateException(e)
      }
       
       
        } finally {
            if (s != null) {
                s.logout();
            }
        }

    }
  
  override
    def setServletContext(sc:ServletContext) {
        this.sc = sc;
    }

  
   def getRepository() = repository

  /*
  @throws(classOf[RepositoryException])
   def  getFiles(path:String):List[SharedFile] =  {
    
        val out:List[SharedFile] = new ArrayList[SharedF]();
        
        Session s = null;
        try {
        s = createAdministrativeSession();
            Node root = s.getNode("/share_content"+path);
        
           NodeIterator ni = root.getNodes();
            
           while(ni.hasNext()) {
           
               
               
                out.add(new SharedFile().loadFromNode(ni.nextNode()));
           }
           
         } finally {
            if (s != null) {
                s.logout();
            }
        }
        return out;
    }*/
    
    @throws(classOf[RepositoryException])
    @throws(classOf[IOException])
    def saveImg(imgContent:String):SavedFile= {
    
       var  s:Session = null
        try {
        s = createAdministrativeSession();
            val root = s.getNode("/paste_content")
            
                      
             val nfile = root.addNode(System.currentTimeMillis+"_img.png", "sf:sharedFile")
             val resource = nfile.addNode(JcrConstants.JCR_CONTENT,JcrConstants.NT_RESOURCE)
                          
              val imgData = imgContent.substring(imgContent.indexOf(',') + 1)
      
      val data = Base64.decode(imgData.getBytes)
      
               resource.setProperty(JcrConstants.JCR_DATA,s.getValueFactory()
                       .createBinary(new ByteArrayInputStream(data)))
                                          
               resource.setProperty(JcrConstants.JCR_MIMETYPE, "image/png")
                      
                        
                     resource.setProperty(JcrConstants.JCR_LASTMODIFIED, new Date().getTime)
                       
                     nfile.setProperty("sf:fileSize", data.length)
                     
                     s.save();
                     
                     return new SavedFile(nfile)
         } finally {
            if (s != null) {
                s.logout();
            }
        }              
    
    
    }
  
    
  def getNode(s:Session,id:String):Node =  {s.getNodeByIdentifier(id)}
  
  def getNode(id:String):Node = {
    
     var  s:Session = null;
        try {
            s = createAdministrativeSession()
            return s.getNodeByIdentifier(id)

        } finally {
            if (s != null) {
                s.logout()
            }
        }
  }
  
  @throws(classOf[RepositoryException])
   def getContentRoot():Node = {
        var  s:Session = null;
        try {
            s = createAdministrativeSession()
            return s.getNode("/paste_content")

        } finally {
            if (s != null) {
                s.logout()
            }
        }
    }

    

    @throws(classOf[RepositoryException])
    def createAdministrativeSession():Session = {
        //Admin ID here can be any string and need not match the actual admin userId
       val adminId = "admin";
        val admin = new AdminPrincipal() {
            override def getName():String = adminId
            
        }
        val authInfo:AuthInfo = new AuthInfoImpl(adminId, null, Collections.singleton(admin))
        
        val subject:Subject = new Subject(true, 
                                          Collections.singleton(admin), Collections.singleton(authInfo), Collections.emptySet())
       
    
    try {
          
            return Subject.doAsPrivileged(subject, new PrivilegedExceptionAction[Session]() {
                
                @throws(classOf[Exception])
                override def run():Session =  repository.login() 
                
            }, null)
       } catch {
          case e @ (_ : PrivilegedActionException) => {
          throw new RepositoryException("failed to retrieve admin session.", e)
         
        }    
            
       }

       
    }

    @throws(classOf[RepositoryException])
    @throws(classOf[IOException])
    protected def createRepository(configJson:File, homedir:File):Repository
            = {
        val c:java.util.Map[String, Any] = new HashMap[String,Any]();
        c.put(OakOSGiRepositoryFactory.REPOSITORY_HOME, homedir.getAbsolutePath())
        c.put(OakOSGiRepositoryFactory.REPOSITORY_CONFIG_FILE, config.getFile().getAbsolutePath())
        c.put(OakOSGiRepositoryFactory.REPOSITORY_BUNDLE_FILTER, bundleFilter)
        c.put(OakOSGiRepositoryFactory.REPOSITORY_SHUTDOWN_ON_TIMEOUT, false)
        c.put(OakOSGiRepositoryFactory.REPOSITORY_TIMEOUT_IN_SECS, 300)
        configureActivator(c);
        //TODO oak-jcr also provides a dummy RepositoryFactory. Hence this
        //cannot be used
        //return  OakRepositoryFactory.
        // return JcrUtils.getRepository(homedir.getAbsolutePath());
        //.getRepository(config);
        return new OakOSGiRepositoryFactory().getRepository(c);
    }

    private def configureActivator(config:java.util.Map[String,Any]) {

        config.put(classOf[BundleActivator].getName(), new BundleActivator() {
            
            @throws(classOf[Exception])
            override def start(bundleContext:BundleContext) {
                registerOSGi(bundleContext);
            }

            @throws(classOf[Exception])
            override def stop(bundleContext:BundleContext)  {
                unregisterOSGi();
            }
        })

    }

    private def registerOSGi(bundleContext:Any) {
        sc.setAttribute("org.osgi.framework.BundleContext", bundleContext);
    }

    private def unregisterOSGi() {
        sc.removeAttribute("org.osgi.framework.BundleContext");
    }

    @PreDestroy
    private def destroy() {
        if (repository.isInstanceOf[JackrabbitRepository]) {
            repository.asInstanceOf[JackrabbitRepository].shutdown();
        }
    }

  
}
