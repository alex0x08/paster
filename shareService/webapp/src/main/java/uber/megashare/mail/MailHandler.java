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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uber.megashare.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;
import uber.megashare.base.LoggedClass;
import uber.megashare.base.MimeSupport;
import uber.megashare.dao.SharedFileDao;
import uber.megashare.dao.UserDao;
import uber.megashare.model.SharedFile;
import uber.megashare.model.User;
import uber.megashare.service.SettingsManager;
import uber.megashare.service.image.ImageBuilder;
import uber.megashare.service.servlet.ServletUtils;

/**
 *
 * @author alex
 */
public class MailHandler extends LoggedClass implements MessageHandler {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6096683256230460055L;
	//private MessageContext ctx;
  //  private ApplicationContext appContext;
    private UserDao userManager;
    private SharedFileDao shareManager;
    private SettingsManager settingsManager;
    private boolean allowed;
    private User currentUser;

    public MailHandler(ApplicationContext appContext, MessageContext ctx) {
      //  this.ctx = ctx;
    //    this.appContext = appContext;

        userManager = (UserDao) appContext.getBean("userDao");
        shareManager = (SharedFileDao) appContext.getBean("shareDao");
        settingsManager = (SettingsManager) appContext.getBean("settingsManager");

    }

    public void from(String from) throws RejectException {
        getLogger().debug("FROM:" + from);


        getLogger().debug("seek for user with email " + from);

        currentUser = userManager.getUserByEmail(from);
        if (currentUser != null) {

            getLogger().debug("user found. allowed to proceed.");
            allowed = true;
        } else {
            getLogger().debug("user not found. skip processing.");
            throw new RejectException(553, "<" + from + "> user unknown");
        }
    }

    public void recipient(String recipient) throws RejectException {
        getLogger().debug("RECIPIENT:" + recipient);

    }

    private void saveAttachment(BodyPart bodyPart) throws MessagingException {

        getLogger().debug("uploadSave currentUser=" + currentUser+",fileName="+bodyPart.getFileName()+",mime="+bodyPart.getContentType());

        if (bodyPart.getFileName() == null || bodyPart.getFileName().trim().length() == 0) {
            getLogger().debug("no file name in attachment, skip for now.");
            return;
        }

        SharedFile out = new SharedFile();
        out.setName(bodyPart.getFileName());

        String ext = ServletUtils.getExt(out.getName());
        if (ext == null || ext.trim().length() == 0) {
            ext = ".data";
        }


        out.setOwner(currentUser);
        out.setMime(bodyPart.getContentType());
        
        
        
        out.setFileSize(bodyPart.getSize());

        out.setType(MimeSupport.getInstance().lookupType(out.getMime()));


        out.setUrl(System.currentTimeMillis() + ext);

        File fout = new File(
                settingsManager.getCurrentSettings().getUploadDir(),
                out.getUrl());

        //FileNameMap fileNameMap = URLConnection.getFileNameMap();
        //String mimeType = fileNameMap.getContentTypeFor(out.getName());

        try {

         
            IOUtils.copy(bodyPart.getInputStream(), new FileOutputStream(fout));



            ImageBuilder builder = ImageBuilder.createInstance().setSource(new FileInputStream(fout));

            if (!builder.isUnsupported()) {
                byte[] img = builder.scaleToProfile().getScaledAsBytes();

                out.setPreviewUrl(System.currentTimeMillis() + "_preview.png");
                FileUtils.writeByteArrayToFile(
                        new File(settingsManager.getCurrentSettings().getUploadDir(),
                        out.getPreviewUrl()), img);
            }


        } catch (IOException ex) {
            getLogger().debug(ex.getLocalizedMessage(), ex);
        }

        SharedFile result = shareManager.saveObject(out);

        // set id from create
        if (out.getId() == null) {
            out.setId(result.getId());
        }

        getLogger().debug("saved file id=" + out.getId() + " access=" + out.getAccessLevel() + ", owner=" + out.getOwner().getLogin());

    }

    public void data(InputStream data) throws IOException {
        getLogger().debug("MAIL DATA");

        if (!allowed) {
            return;
        }

        try {
            MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()), data);

            if (!(message.getContent() instanceof Multipart)) {
                getLogger().debug("non multipart message, skip for now");
                return;
            }
            Multipart multipart = (Multipart) message.getContent();
            //System.out.println(multipart.getCount());

            for (int i = 0; i < multipart.getCount(); i++) {
                //System.out.println(i);
                //System.out.println(multipart.getContentType());
                BodyPart bodyPart = multipart.getBodyPart(i);
                saveAttachment(bodyPart);
            }


        } catch (MessagingException ex) {
            getLogger().error(ex.getLocalizedMessage(), ex);
        }



    }

    public void done() {
        //System.out.println("Finished");
    }
}
