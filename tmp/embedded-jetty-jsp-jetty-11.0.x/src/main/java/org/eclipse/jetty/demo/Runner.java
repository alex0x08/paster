//
// ========================================================================
// Copyright (c) 1995-2022 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.demo;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Runner
 * <p>
 * Combine jetty classes into a single executable jar and run webapps based on the args to it.
 *
 * @deprecated No replacement provided or available.  Migrate to jetty-home (and use {@code ${jetty.base}} directory).
 */
public class Runner {
    private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

    public static final String[] PLUS_CONFIGURATION_CLASSES =
            {
                    org.eclipse.jetty.webapp.WebInfConfiguration.class.getCanonicalName(),
                    org.eclipse.jetty.webapp.WebXmlConfiguration.class.getCanonicalName(),
                    org.eclipse.jetty.annotations.AnnotationConfiguration.class.getCanonicalName(),
                    org.eclipse.jetty.webapp.WebAppConfiguration.class.getCanonicalName(),
                    org.eclipse.jetty.webapp.JspConfiguration.class.getCanonicalName()
            };
    public static final String CONTAINER_INCLUDE_JAR_PATTERN = ".*/jetty-runner-[^/]*\\.jar$";
    public static final String DEFAULT_CONTEXT_PATH = "/";
    public static final int DEFAULT_PORT = 8080;

    protected Server _server;
    protected URLClassLoader _classLoader;
    protected Classpath _classpath = new Classpath();
    protected ContextHandlerCollection _contexts;

    /**
     * Classpath
     */
    public class Classpath {
        private List<URL> _classpath = new ArrayList<>();

        public void addJars(Resource lib) throws IOException {
            if (lib == null || !lib.exists())
                throw new IllegalStateException("No such lib: " + lib);

            String[] list = lib.list();
            if (list == null)
                return;

            for (String path : list) {
                if (".".equals(path) || "..".equals(path))
                    continue;

                try (Resource item = lib.addPath(path)) {
                    if (item.isDirectory())
                        addJars(item);
                    else {
                        String lowerCasePath = path.toLowerCase(Locale.ENGLISH);
                        if (lowerCasePath.endsWith(".jar") ||
                                lowerCasePath.endsWith(".zip")) {
                            _classpath.add(item.getURI().toURL());
                        }
                    }
                }
            }
        }


        public URL[] asArray() {
            return _classpath.toArray(new URL[0]);
        }
    }

    public Runner() {
    }

    /**
     * Generate helpful usage message and exit
     *
     * @param error the error header
     */
    public void usage(String error) {
        if (error != null)
            System.err.println("ERROR: " + error);
        System.err.println("Usage: java [-Djetty.home=dir] -jar jetty-runner.jar [--help|--version] [ server opts] [[ context opts] context ...] ");
        System.err.println("Server opts:");
        System.err.println(" --version                           - display version and exit");
        System.err.println(" --host name|ip                      - interface to listen on (default is all interfaces)");
        System.err.println(" --port n                            - port to listen on (default 8080)");
        System.err.println(" [--lib dir]*n                       - each tuple specifies an extra directory of jars to be added to the classloader");
        System.err.println("Context opts:");
        System.err.println(" [[--path /path] context]*n          - WAR file, web app dir or context xml file, optionally with a context path");
        System.exit(1);
    }

    /**
     * Generate version message and exit
     */
    public void version() {
        System.err.println("org.eclipse.jetty.runner.Runner: " + Server.getVersion());
        System.exit(1);
    }

    /**
     * Configure a jetty instance and deploy the webapps presented as args
     *
     * @param args the command line arguments
     * @throws Exception if unable to configure
     */
    public void configure(String[] args) throws Exception {
        // handle classpath bits first so we can initialize the log mechanism.
        for (int i = 0; i < args.length; i++) {
            if ("--lib".equals(args[i])) {
                try (Resource lib = Resource.newResource(args[++i])) {
                    if (!lib.exists() || !lib.isDirectory())
                        usage("No such lib directory " + lib);
                    _classpath.addJars(lib);
                }
            } else if (args[i].startsWith("--"))
                i++;
        }

        initClassLoader();

        LOG.info("Runner");
        LOG.debug("Runner classpath {}", _classpath);

        String contextPath = DEFAULT_CONTEXT_PATH;
        boolean contextPathSet = false;
        int port = DEFAULT_PORT;
        String host = null;

        boolean runnerServerInitialized = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--port":
                    port = Integer.parseInt(args[++i]);
                    break;
                case "--host":
                    host = args[++i];
                    break;
                case "--path":
                    contextPath = args[++i];
                    contextPathSet = true;
                    break;
                case "--lib":
                    ++i; //skip

                    break;
                default:

                    // process contexts

                    if (!runnerServerInitialized) // log handlers not registered, server maybe not created, etc
                    {
                        if (_server == null) // server not initialized yet
                        {
                            // build the server
                            _server = new Server();
                        }


                        //check that everything got configured, and if not, make the handlers
                        HandlerCollection handlers = _server.getChildHandlerByClass(HandlerCollection.class);
                        if (handlers == null) {
                            handlers = new HandlerList();
                            _server.setHandler(handlers);
                        }

                        //check if contexts already configured
                        _contexts = handlers.getChildHandlerByClass(ContextHandlerCollection.class);
                        if (_contexts == null) {
                            _contexts = new ContextHandlerCollection();
                            prependHandler(_contexts, handlers);
                        }


                        //ensure a DefaultHandler is present
                        if (handlers.getChildHandlerByClass(DefaultHandler.class) == null) {
                            handlers.addHandler(new DefaultHandler());
                        }

                        //check a connector is configured to listen on
                        Connector[] connectors = _server.getConnectors();
                        if (connectors == null || connectors.length == 0) {
                            ServerConnector connector = new ServerConnector(_server);
                            connector.setPort(port);
                            if (host != null)
                                connector.setHost(host);
                            _server.addConnector(connector);

                        }


                        runnerServerInitialized = true;
                    }

                    // Create a context
                    try (Resource ctx = Resource.newResource(args[i])) {
                        if (!ctx.exists())
                            usage("Context '" + ctx + "' does not exist");

                        if (contextPathSet && !(contextPath.startsWith("/")))
                            contextPath = "/" + contextPath;

                        // Configure the context

                        // assume it is a WAR file
                        WebAppContext webapp = new WebAppContext(_contexts, ctx.toString(), contextPath);
                        webapp.setConfigurationClasses(PLUS_CONFIGURATION_CLASSES);

                        String fname=getClass().getProtectionDomain().getCodeSource().getLocation().getFile();

                        fname = fname.substring(fname.lastIndexOf('/'));

                        System.out.println("fname=" + fname);


                        String incPattern = ".*" + fname.replace(".","\\\\.") +"$";
                        // ".*/jetty-runner-[^/]*\\.jar$";

                        System.out.println("pattern=" + incPattern);

                        webapp.setInitParameter("org.eclipse.jetty.jsp.precompiled", "true");
                        webapp.setAttribute(MetaInfConfiguration.CONTAINER_JAR_PATTERN,incPattern);

                    }
                    //reset
                    contextPathSet = false;
                    contextPath = DEFAULT_CONTEXT_PATH;
                    break;
            }
        }

        if (_server == null)
            usage("No Contexts defined");
        // _server.setStopAtShutdown(true);


    }

    protected void prependHandler(Handler handler, HandlerCollection handlers) {
        if (handler == null || handlers == null)
            return;

        Handler[] existing = handlers.getChildHandlers();
        Handler[] children = new Handler[existing.length + 1];
        children[0] = handler;
        System.arraycopy(existing, 0, children, 1, existing.length);
        handlers.setHandlers(children);
    }

    public void run() throws Exception {
        _server.start();

        _server.join();
    }


    /**
     * Establish a classloader with custom paths (if any)
     */
    protected void initClassLoader() {
        URL[] paths = _classpath.asArray();

        if (_classLoader == null && paths.length > 0) {
            ClassLoader context = Thread.currentThread().getContextClassLoader();

            if (context == null) {
                _classLoader = new URLClassLoader(paths);
            } else {
                _classLoader = new URLClassLoader(paths, context);
            }

            Thread.currentThread().setContextClassLoader(_classLoader);
        }
    }

    public static void main(String[] args) {

        System.setProperty("org.apache.jasper.compiler.disablejsr199","true");

        System.err.println("WARNING: jetty-runner is deprecated.");
        System.err.println("         See Jetty Documentation for startup options");
        System.err.println("         https://www.eclipse.org/jetty/documentation/");

        Runner runner = new Runner();

        try {
            if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
                runner.usage(null);
            } else if (args.length > 0 && args[0].equalsIgnoreCase("--version")) {
                runner.version();
            } else {
                runner.configure(args);
                runner.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
            runner.usage(null);
        }
    }
}