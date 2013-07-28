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
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;

/**
 *
 * @author alex
 */
public class MailMessageHandlerFactory implements MessageHandlerFactory {

    private ApplicationContext appContext;

    public MailMessageHandlerFactory(ApplicationContext ctx) {
        this.appContext = ctx;
    }

    public MessageHandler create(MessageContext ctx) {
        return new MailHandler(appContext, ctx);
    }


}
