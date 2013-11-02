/**
 * Copyright (C) 2011 aachernyshev <alex@0x08.tk>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uber.megashare.listener;

import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.util.logging.Level;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import uber.megashare.base.SystemInfo;
import uber.megashare.model.AppVersion;

public class SystemPropertiesListener implements ServletContextListener {

    private static final String APP_BASE = ".apps",appName="share";

    private File app_home;
    
    private Logger log;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {

       try {
            Manifests.append(event.getServletContext());
        } catch (IOException ex) {
            throw new IllegalStateException("Error reading manifest "+ex.getLocalizedMessage(),ex);
         }
              
        
        if (!System.getProperties().containsKey("share.app.home")) {

            String user_home = System.getProperty("user.home");

            app_home = new File(user_home, APP_BASE);

            app_home = new File(app_home, appName);

            System.setProperty("share.app.home", app_home.getAbsolutePath());
        } else {
            app_home = new File(System.getProperty("share.app.home"));
        }

        if (!app_home.exists() || !app_home.isDirectory()) {

            if (!app_home.mkdirs()) {
                throw new IllegalStateException(
                        "Cannot create directory " + app_home.getAbsolutePath());
            }
        }


        AppVersion mf_version = new AppVersion().fillFromManifest();
                
        SystemInfo.getInstance().setRuntimeVersion(mf_version);
        
        
        System.setProperty("share.app.version", mf_version.getImplBuildNum());
        
        //getLogger().info("application home:"+System.getProperty("app.home"));

        
        setupLogger();
        
        fixH2();
        
        
        
        
    }
    
     
    

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
    
    
    private void setupLogger() {
    
        
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        RollingFileAppender<ILoggingEvent> rfAppender = new RollingFileAppender<>();
        rfAppender.setContext(loggerContext);
        rfAppender.setFile(new File(app_home, appName+".log").getAbsolutePath());
        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(loggerContext);
        // rolling policies need to know their parent
        // it's one of the rare cases, where a sub-component knows about its parent
        rollingPolicy.setParent(rfAppender);
        rollingPolicy.setFileNamePattern(appName+"_%i.log.zip");
        rollingPolicy.start();

        SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<>();
        triggeringPolicy.setMaxFileSize("15MB");
        triggeringPolicy.start();

        PatternLayout layout = new PatternLayout();
        layout.setContext(loggerContext);
        layout.setPattern("%-4relative [%thread] %-5level %logger{35} - %msg%n");
        layout.start();

        rfAppender.setLayout(layout);
        rfAppender.setRollingPolicy(rollingPolicy);
        rfAppender.setTriggeringPolicy(triggeringPolicy);

        rfAppender.start();

        // attach the rolling file appender to the logger of your choice
        log = loggerContext.getLogger("ROOT");
        log.addAppender(rfAppender);

        log = loggerContext.getLogger("uber.megashare");
        log.addAppender(rfAppender);


        // OPTIONAL: print logback internal status messages
        //StatusPrinter.print(loggerContext);

        // log something
        log.debug("application home:" + app_home.getAbsolutePath());
        
    
    }
    
    
      private void fixH2()
            {
        try {
            //Get the Class implementation byte code
             ClassPool cpool =ClassPool.getDefault(); 
            cpool.appendClassPath(new ClassClassPath(this.getClass()));
              
            CtClass ctClass = cpool.get("org.h2.server.web.WebServer");
            
            ctClass.stopPruning(true);
            
            //Get the method from the Class byte code
            CtMethod method= ctClass.getDeclaredMethod("loadProperties");

          
            
            StringBuilder content = new StringBuilder();
            content.append("{\n java.util.Properties p = new java.util.Properties(); p.setProperty(\"0\",\"Local DB|org.h2.Driver|jdbc\\:h2\\:tcp\\://localhost:6666/filesdb;DB_CLOSE_ON_EXIT=TRUE|bo|bo\"); return p;  \n } ");    

            /**
             * Inserting the content
             */
            method.setBody(content.toString());
                  
        
            method= ctClass.getDeclaredMethod("saveProperties");

            method.setBody("{ System.out.println(\"NO\");}");
           
            
             method= ctClass.getDeclaredMethod("startTranslate");

              method.setBody("{ System.out.println(\"NO\"); return null;}");
         
            Class out =ctClass.toClass(this.getClass().getClassLoader(),this.getClass().getProtectionDomain());
            
            ctClass.stopPruning(false);
            
            out.newInstance();
        } catch (NotFoundException | CannotCompileException | InstantiationException | IllegalAccessException ex) {
            log.error(ex.getLocalizedMessage(),ex);
        }
   }

   
    
}
