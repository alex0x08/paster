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
import java.util.Calendar;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.lucene.queryParser.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;
import uber.megashare.base.LoggedClass;
import uber.megashare.base.SettingsBuilder;
import uber.megashare.dao.SharedFileDao;
import uber.megashare.dao.UserDao;
import uber.megashare.model.Role;
import uber.megashare.model.User;
import uber.megashare.service.SettingsManager;
import uber.megashare.service.UserManager;

/**
 * <p>StartupListener class used to initialize and database settings and
 * populate any application-wide drop-downs.
 * <p/>
 * <p>Keep in mind that this listener is executed outside of
 * OpenSessionInViewFilter, so if you're using Hibernate you'll have to
 * explicitly initialize all loaded data at the GenericDao or service level to
 * avoid LazyInitializationException. Hibernate.initialize() works well for
 * doing this.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class StartupListener extends LoggedClass implements ServletContextListener {

    /**
     *
     */
    private static final long serialVersionUID = -132229174806241432L;
    
    private UserDao userDao;
    
    private SharedFileDao shareDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        getLogger().debug("Initializing context...");

        ServletContext context = event.getServletContext();
        setupContext(context);
    }

    /**
     * This method uses the LookupManager to lookup available roles from the
     * data layer.
     *
     * @param context The servlet context
     */
    private void setupContext(ServletContext context) {
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);


        User startup = new User();
        startup.setName("DB Startup");
        startup.setLogin("startup");
        startup.setPassword("startup");
        startup.getRoles().add(Role.ROLE_ADMIN);

        // log user in automatically
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "startup", "startup", startup.getAuthorities());
        auth.setDetails(startup);
        SecurityContextHolder.getContext().setAuthentication(auth);


        SettingsManager settingsDao = (SettingsManager) ctx.getBean("settingsManager");
        UserManager userManager = (UserManager) ctx.getBean("userManager");
        userDao = (UserDao) ctx.getBean("userDao");
        shareDao = (SharedFileDao) ctx.getBean("shareDao");

//        UserCustomExtendedRepository repo = (UserCustomExtendedRepository) ctx.getBean("userCustomExtendedRepository");

        // EmbeddedSMTPServer.createInstance(ctx).start();


        getLogger().debug("__currentSettings:" + settingsDao.getCurrentSettings());
        if (settingsDao.getCurrentSettings() != null && settingsDao.getCurrentSettings().getInstallDate() != null) {

            reindex();

            getLogger().info("Database already set. Aborting creation..");

            return;
        }

        settingsDao.merge(SettingsBuilder.getInstance() //install date
                .addInstallDate(Calendar.getInstance().getTime()) // обратный адрес для почты
                .addUploadDir("files")
                .get());

        File uf = new File(settingsDao.getCurrentSettings().getUploadDir());
        if (!uf.exists()) {

            if (!uf.mkdirs()) {
                throw new IllegalStateException("Cannot create directory " + uf.getAbsolutePath());
            }
        }

        User admin = new User();
        admin.setName("Administrator");
        admin.setLogin("admin");
        admin.getRoles().add(Role.ROLE_ADMIN);

        admin = userManager.changePassword(admin, "admin");
        admin = userDao.saveObject(admin);
        
        


        User alex = new User();
        alex.setName("Alex");
        alex.setLogin("alex");
        alex.setEmail("alex@0x08.tk");
        alex.getRoles().add(Role.ROLE_USER);
        alex = userManager.changePassword(alex, "login");
        alex = userDao.saveObject(alex);

        getLogger().info("alex=" + alex.getLogin() + "|" + alex.getPassword());






        List<User> found_users;
        try {
            found_users = userDao.search("name:" + alex.getName());

            getLogger().info("Found " + found_users.size() + " users containing login : " + alex.getLogin());
            for (User u : found_users) {
                getLogger().info("login=" + u.getLogin() + "|" + u.getPassword());

            }

        } catch (ParseException ex) {
            getLogger().error(ex.getLocalizedMessage(), ex);
        }

        getLogger().info("__done creation");

    }

    private void reindex() {

        getLogger().info("Reindexing..");

        userDao.indexAll();
        shareDao.indexAll();

    }

    /**
     * Shutdown servlet context (currently a no-op method).
     *
     * @param servletContextEvent The servlet context event
     */
    //  @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
     
    }

   
}