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
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package uber.megashare.mail;

import org.springframework.context.ApplicationContext;
import org.subethamail.smtp.server.SMTPServer;

/**
 *
 * @author alex
 */
public class EmbeddedSMTPServer {

    private MailMessageHandlerFactory mailFactory;
    private SMTPServer smtpServer;

    public void start() {
        smtpServer.start();
    }

    public void stop() {
        smtpServer.stop();
    }

    private void init(ApplicationContext ctx) {

        mailFactory = new MailMessageHandlerFactory(ctx);

        smtpServer = new SMTPServer(mailFactory);
        smtpServer.setPort(2525);

    }
    private static EmbeddedSMTPServer instance;

    public static EmbeddedSMTPServer createInstance(ApplicationContext ctx) {

        if (instance == null) {
            instance = new EmbeddedSMTPServer();
            instance.init(ctx);
        }
        return instance;
    }

    public static EmbeddedSMTPServer getInstance() {
        if (instance == null) {
            throw new RuntimeException("EmbeddedServer was not created fist!");
        }
        return instance;
    }
}
