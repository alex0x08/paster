/**
 * Copyright (C) 2011 Alex <alex@0x08.tk>
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

public class SystemPropertiesListener implements ServletContextListener {

    private static final String APP_BASE = ".apps",appName="share";

    @Override
    public void contextInitialized(ServletContextEvent event) {

        File app_home;

        
        
        if (!System.getProperties().containsKey("app.home")) {

            String user_home = System.getProperty("user.home");

            app_home = new File(user_home, APP_BASE);

            app_home = new File(app_home, appName);

            System.setProperty("app.home", app_home.getAbsolutePath());
        } else {
            app_home = new File(System.getProperty("app.home"));
        }

        if (!app_home.exists() || !app_home.isDirectory()) {

            if (!app_home.mkdirs()) {
                throw new IllegalStateException(
                        "Cannot create directory " + app_home.getAbsolutePath());
            }
        }


        //getLogger().info("application home:"+System.getProperty("app.home"));

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        RollingFileAppender<ILoggingEvent> rfAppender = new RollingFileAppender<ILoggingEvent>();
        rfAppender.setContext(loggerContext);
        rfAppender.setFile(new File(app_home, appName+".log").getAbsolutePath());
        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(loggerContext);
        // rolling policies need to know their parent
        // it's one of the rare cases, where a sub-component knows about its parent
        rollingPolicy.setParent(rfAppender);
        rollingPolicy.setFileNamePattern(appName+"_%i.log.zip");
        rollingPolicy.start();

        SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
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
        Logger logbackLogger = loggerContext.getLogger("ROOT");
        logbackLogger.addAppender(rfAppender);

        logbackLogger = loggerContext.getLogger("uber.megashare");
        logbackLogger.addAppender(rfAppender);


        // OPTIONAL: print logback internal status messages
        //StatusPrinter.print(loggerContext);

        // log something
        logbackLogger.debug("application home:" + app_home.getAbsolutePath());
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
