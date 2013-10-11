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
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import uber.megashare.base.SystemInfo;
import uber.megashare.dao.ProjectDao;
import uber.megashare.model.Project;
import uber.megashare.model.SystemProperties;
import uber.megashare.model.tree.FolderNode;
import uber.megashare.model.tree.NodeType;
import uber.megashare.model.tree.Rels;
import uber.megashare.service.tree.FolderService;
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
    
    private ProjectDao projectDao;

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
        try {
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
            projectDao = (ProjectDao) ctx.getBean("projectDao");
            

    //        UserCustomExtendedRepository repo = (UserCustomExtendedRepository) ctx.getBean("userCustomExtendedRepository");

            // EmbeddedSMTPServer.createInstance(ctx).start();


            SystemProperties sp = settingsDao.getCurrentSettings();
            getLogger().debug("__currentSettings:" + sp);
           
            if (sp != null && sp.getInstallDate() != null) {
                
                int check =  SystemInfo.getInstance().getRuntimeVersion().compareTo(sp.getAppVersion());
                
                switch(check) {
           
                    case 0:
                        getLogger().info("Application and db versions match.");
                        break;
                    case 1:
                        getLogger().warn("DB version is older than application: "
                                +sp.getAppVersion().getImplVersionFull()+" | "+SystemInfo.getInstance().getRuntimeVersion().getImplVersionFull()+". You can get problems.");
                        break;
                        
                    case -1:    
                        getLogger().warn("Application version is older than database: "
                                +sp.getAppVersion().getImplVersionFull()+" | "+SystemInfo.getInstance().getRuntimeVersion().getImplVersionFull()+". You can get problems.");
                        break;
                    
                    case -2:
                    default:    
                    getLogger().error("Uncomparable db and application versios: "
                             +sp.getAppVersion().getImplVersionFull()+" | "+SystemInfo.getInstance().getRuntimeVersion().getImplVersionFull()+". Cannot continue.");
           
                        
                }
                                
                
                 getLogger().info("Loading application ver. "+SystemInfo.getInstance().getRuntimeVersion().getImplVersionFull()+" db ver. "+sp.getAppVersion().getImplVersionFull());
               
                
                reindex();

                getLogger().info("Database already set. Aborting creation..");

                return;
            }

            sp =settingsDao.merge(SettingsBuilder.getInstance() //install date
                    .addInstallDate(Calendar.getInstance().getTime())
                    .addAppVersion()                    
                    .addUploadDir("files")
                    .get());

            SystemInfo.getInstance().setRuntimeVersion(sp.getAppDbVersion());
                
            
            getLogger().info("Loading application ver. "+sp.getAppVersion().getImplVersionFull());
            
            File uf = new File(settingsDao.getCurrentSettings().getUploadDir());
            if (!uf.exists()) {

                if (!uf.mkdirs()) {
                    throw new IllegalStateException("Cannot create directory " + uf.getAbsolutePath());
                }
            }

            Project test_p = new Project();
            test_p.setDescription("Sample project description");
            test_p.setName("Sample Project");
            
            test_p = projectDao.saveObject(test_p);
            
             Project test_p2 = new Project();
            test_p2.setDescription("Sample project2 description");
            test_p2.setName("Sample Project2");
           
            test_p2 = projectDao.saveObject(test_p2);
           
            
            User admin = new User();
            admin.setName("Administrator");
            admin.setLogin("admin");
            admin.setRelatedProject(test_p);
            admin.getRoles().add(Role.ROLE_ADMIN);

            admin = userManager.changePassword(admin, "admin");
            admin = userDao.saveObject(admin);
        
        


            User alex = new User();
            alex.setName("Alex");
            alex.setLogin("alex");
            alex.setEmail("alex@0x08.tk");
            alex.getRoles().add(Role.ROLE_USER);
            alex.setRelatedProject(test_p2);
            alex = userManager.changePassword(alex, "login");
            alex = userDao.saveObject(alex);

            getLogger().info("alex=" + alex.getLogin() + "|" + alex.getPassword());


            List<User> found_users;
         
                found_users = userDao.search("name:" + alex.getName());

                getLogger().info("Found " + found_users.size() + " users containing login : " + alex.getLogin());
                for (User u : found_users) {
                    getLogger().info("login=" + u.getLogin() + "|" + u.getPassword());

                }
                
                
               FolderService folderManager = (FolderService)ctx.getBean("folderService");
               
               FolderNode defaultParent = new FolderNode();
                          defaultParent.setName("Default parent");
                          defaultParent.setType(NodeType.PARENT);
                          
                        defaultParent =  folderManager.save(defaultParent);
                
                        System.out.println("_parent id: "+defaultParent.getId()+" type "+defaultParent.getType());
                        
                         FolderNode sub1 = new FolderNode();
                          sub1.setName("Sub 1");
                          sub1.setType(NodeType.FOLDER);
                          
                          
                        FolderNode sub2 = new FolderNode();
                          sub2.setName("Sub 2");
                         sub2.setType(NodeType.FOLDER);
                          
                          sub1.setParent(defaultParent);
                          
                        /*  Set<FolderNode> ch = new HashSet<>();
                          ch.add(sub2);
                          sub1.setChildren(ch);
                          */
                          
                          
                          
                          sub1 =  folderManager.save(sub1);

                          sub2.setParent(sub1);

                          sub2 =  folderManager.save(sub2);

                          
                        System.out.println("_sub1 id: "+sub1.getId()+" type "+sub1.getType()+" sub2 id: "+sub2.getId()+" sub2 parent "+sub2.getParent().getId());
                    
            
          final TraversalDescription FRIENDS_TRAVERSAL = Traversal.description()
        .depthFirst()
        .relationships( Rels.CHILD )
         .evaluator(Evaluators.includingDepths(1, 2))
        .uniqueness( Uniqueness.RELATIONSHIP_GLOBAL );
               
          for (FolderNode friend : folderManager.findAllByQuery("START a=node(1)\n" +
"MATCH a-[:CHILD*1..3]->x\n" +
"RETURN a,x", uf)) {
    
              System.out.println("node from parent: " + friend.getId());

          }
          
          
          /* for (FolderNode friend : folderManager.findAllByTraversal(sub1,FRIENDS_TRAVERSAL)) {
    
              System.out.println("node from sub1: " + friend.getId());

          }*/
          
        
          /*
            for (FolderNode f : folderManager.findAllByPropertyValue("type", NodeType.PARENT)) {
                    System.out.println("parent node: " + f.getName());
            
                for (FolderNode ff : f.getChildren()) {
                    System.out.println("sub nodes : " + ff.getName());
                }
            }
            */ 
             

   
                getLogger().info("__done creation");
   
            } catch (ParseException ex) {
                getLogger().error(ex.getLocalizedMessage(), ex);
         
        }

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
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
     
    }

   
}
