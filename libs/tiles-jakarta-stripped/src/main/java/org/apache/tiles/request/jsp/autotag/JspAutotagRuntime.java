/*
 * $Id: JspAutotagRuntime.java 1360343 2012-07-11 18:35:52Z nlebas $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.jsp.autotag;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import org.apache.tiles.autotag.core.runtime.AbstractModelBody;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.autotag.core.runtime.AutotagRuntime;
import org.apache.tiles.request.*;
import org.apache.tiles.request.attribute.AttributeExtractor;
import org.apache.tiles.request.collection.ScopeMap;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.servlet.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * A Runtime for implementing JSP tag libraries.
 */
public class JspAutotagRuntime extends SimpleTagSupport implements AutotagRuntime<Request> {
    /** {@inheritDoc} */
    @Override
    public void doTag() {
        // do nothing like the parent implementation, 
        // but don't throw exceptions either
    }

    /** {@inheritDoc} */
    @Override
    public Request createRequest() {
        JspContext pageContext = getJspContext();
        return JspRequest.createServletJspRequest(JspUtil.getApplicationContext(pageContext),
                                                  (PageContext) pageContext);
    }

    /** {@inheritDoc} */
    @Override
    public ModelBody createModelBody() {
        return new JspModelBody(getJspBody(), getJspContext());
    }

    //@Override

    /**
     * Context implementation used for executing tiles within a
     * jsp tag library.
     *
     * @version $Rev: 1375743 $ $Date: 2012-08-22 06:05:58 +1000 (Wed, 22 Aug 2012) $
     */
    public static class JspRequest extends AbstractViewRequest {
        /**
         * The native available scopes.
         */
        private static final List<String> SCOPES
                = List.of("page", REQUEST_SCOPE, "session", APPLICATION_SCOPE);
        /**
         * The current page context.
         */
        private final PageContext pageContext;
        /**
         * <p>The lazily instantiated <code>Map</code> of page scope
         * attributes.</p>
         */
        private Map<String, Object> pageScope;
        /**
         * <p>The lazily instantiated <code>Map</code> of request scope
         * attributes.</p>
         */
        private Map<String, Object> requestScope;
        /**
         * <p>The lazily instantiated <code>Map</code> of session scope
         * attributes.</p>
         */
        private Map<String, Object> sessionScope;
        /**
         * <p>The lazily instantiated <code>Map</code> of application scope
         * attributes.</p>
         */
        private Map<String, Object> applicationScope;
        /**
         * Creates a JSP request.
         *
         * @param applicationContext The application context.
         * @param pageContext        The page context.
         * @return A new JSP request.
         */
        public static JspRequest createServletJspRequest(ApplicationContext applicationContext,
                                                                                      PageContext pageContext) {
            return new JspRequest(new ServletRequest(
                    applicationContext, (HttpServletRequest) pageContext
                    .getRequest(), (HttpServletResponse) pageContext
                    .getResponse()), pageContext);
        }
        /**
         * Constructor.
         *
         * @param enclosedRequest The request that is wrapped here.
         * @param pageContext     The page context to use.
         */
        public JspRequest(DispatchRequest enclosedRequest,
                          PageContext pageContext) {
            super(enclosedRequest);
            this.pageContext = pageContext;
        }
        @Override
        public List<String> getAvailableScopes() {
            return SCOPES;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected void doInclude(String path) throws IOException {
            try {
                pageContext.include(path, false);
            } catch (ServletException e) {
                throw ServletUtil.wrapServletException(e, "JSPException including path '%s'.".formatted(path));
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public PrintWriter getPrintWriter() {
            return new JspPrintWriterAdapter(pageContext.getOut());
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Writer getWriter() {
            return pageContext.getOut();
        }
        /**
         * Returns the page scope.
         *
         * @return The page scope.
         */
        public Map<String, Object> getPageScope() {
            if (pageScope == null && pageContext != null)
                pageScope = new ScopeMap(new ScopeExtractor(pageContext,
                        PageContext.PAGE_SCOPE));

            return pageScope;
        }
        /**
         * Returns the request scope.
         *
         * @return The request scope.
         */
        public Map<String, Object> getRequestScope() {
            if (requestScope == null && pageContext != null)
                requestScope = new ScopeMap(new ScopeExtractor(pageContext,
                        PageContext.REQUEST_SCOPE));
            return requestScope;
        }
        /**
         * Returns the session scope.
         *
         * @return The session scope.
         */
        public Map<String, Object> getSessionScope() {
            if (sessionScope == null && pageContext != null)
                sessionScope = new ScopeMap(new SessionScopeExtractor(pageContext));

            return sessionScope;
        }
        /**
         * Returns the application scope.
         *
         * @return The application scope.
         */
        public Map<String, Object> getApplicationScope() {
            if (applicationScope == null && pageContext != null)
                applicationScope = new ScopeMap(new ScopeExtractor(pageContext,
                        PageContext.APPLICATION_SCOPE));

            return applicationScope;
        }
        /**
         * Returns the page context that originated the request.
         *
         * @return The page context.
         */
        @Override
        public Map<String, Object> getContext(String scope) {
            if ("page".equals(scope)) return getPageScope();
            else if (REQUEST_SCOPE.equals(scope))
                return getRequestScope();
            else if ("session".equals(scope))
                return getSessionScope();
            else if (APPLICATION_SCOPE.equals(scope))
                return getApplicationScope();

            throw new IllegalArgumentException("%s does not exist. Call getAvailableScopes() first to check."
                    .formatted(scope));
        }
    }

    /**
     * Adapts a {@link JspWriter} to a {@link PrintWriter}, swallowing {@link IOException}.
     *
     * @version $Rev: 1306435 $ $Date: 2012-03-29 02:39:11 +1100 (Thu, 29 Mar 2012) $
     */
    public static class JspPrintWriterAdapter extends PrintWriter {
        /**
         * The JSP writer.
         */
        private final JspWriter writer;
        /**
         * The logging object.
         */
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        /**
         * Constructor.
         *
         * @param writer The JSP writer.
         */
        public JspPrintWriterAdapter(JspWriter writer) {
            super(writer);
            this.writer = writer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PrintWriter append(char c) {
            try {
                writer.append(c);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
            return this;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public PrintWriter append(CharSequence csq, int start, int end) {
            try {
                writer.append(csq, start, end);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
            return this;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public PrintWriter append(CharSequence csq) {
            try {
                writer.append(csq);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
            return this;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void close() {
            try {
                writer.close();
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void flush() {
            try {
                writer.flush();
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void print(boolean b) {
            try {
                writer.print(b);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void print(char c) {
            try {
                writer.print(c);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void print(char[] s) {
            try {
                writer.print(s);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void print(double d) {
            try {
                writer.print(d);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void print(float f) {
            try {
                writer.print(f);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void print(int i) {
            try {
                writer.print(i);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void print(long l) {
            try {
                writer.print(l);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void print(Object obj) {
            try {
                writer.print(obj);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void print(String s) {
            try {
                writer.print(s);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void println() {
            try {
                writer.println();
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void println(boolean x) {
            try {
                writer.println(x);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void println(char x) {
            try {
                writer.println(x);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void println(char[] x) {
            try {
                writer.println(x);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void println(double x) {
            try {
                writer.println(x);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void println(float x) {
            try {
                writer.println(x);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void println(int x) {
            try {
                writer.println(x);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void println(long x) {
            try {
                writer.println(x);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void println(Object x) {
            try {
                writer.println(x);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void println(String x) {
            try {
                writer.println(x);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void write(char[] buf, int off, int len) {
            try {
                writer.write(buf, off, len);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void write(char[] buf) {
            try {
                writer.write(buf);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void write(int c) {
            try {
                writer.write(c);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void write(String s, int off, int len) {
            try {
                writer.write(s, off, len);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void write(String s) {
            try {
                writer.write(s);
            } catch (IOException e) {
                log.error("Error when writing in JspWriter", e);
                setError();
            }
        }
    }

    /**
     * Extracts attributes from session scope from {@link PageContext}.
     *
     * @version $Rev: 1066790 $ $Date: 2011-02-03 23:06:20 +1100 (Thu, 03 Feb 2011) $
     */
    public static class SessionScopeExtractor implements AttributeExtractor {
        /**
         * The page context.
         */
        private final PageContext context;
        /**
         * Constructor.
         *
         * @param context The page context.
         */
        public SessionScopeExtractor(PageContext context) {
            this.context = context;
        }
        @Override
        public void removeValue(String name) {
            if (context.getSession() == null)
                return;

            context.removeAttribute(name, PageContext.SESSION_SCOPE);
        }
        @Override
        public Enumeration<String> getKeys() {
            return context.getSession() == null ? null
                    : context.getAttributeNamesInScope(PageContext.SESSION_SCOPE);
        }
        @Override
        public Object getValue(String key) {
            return context.getSession() == null ? null
                    : context.getAttribute(key, PageContext.SESSION_SCOPE);
        }
        @Override
        public void setValue(String key, Object value) {
            if (context.getSession() == null)
                return;
            context.setAttribute(key, value, PageContext.SESSION_SCOPE);
        }
    }

    /**
     * Extracts attributes from a numbered scope from {@link JspContext}.
     *
     * @version $Rev: 1066790 $ $Date: 2011-02-03 23:06:20 +1100 (Thu, 03 Feb 2011) $
     */
    public static class ScopeExtractor implements AttributeExtractor {
        /**
         * The JSP context.
         */
        private final JspContext context;
        /**
         * The scope number to use.
         */
        private final int scope;
        /**
         * Constructor.
         *
         * @param context The JSP context.
         * @param scope   The scope number.
         */
        public ScopeExtractor(JspContext context, int scope) {
            this.context = context;
            this.scope = scope;
        }
        @Override
        public void removeValue(String name) {
            context.removeAttribute(name, scope);
        }
        @Override
        public Enumeration<String> getKeys() {
            return context.getAttributeNamesInScope(scope);
        }
        @Override
        public Object getValue(String key) {
            return context.getAttribute(key, scope);
        }
        @Override
        public void setValue(String key, Object value) {
            context.setAttribute(key, value, scope);
        }
    }

    static final class JspUtil {
        /**
         * Constructor.
         */
        private JspUtil() {
        }
        /**
         * Returns the application context. It must be
         * first saved creating an {@link ApplicationContext} and using
         * {@link org.apache.tiles.request.ApplicationAccess#register(ApplicationContext)}.
         *
         * @param jspContext The JSP context.
         * @return The application context.
         */
        public static ApplicationContext getApplicationContext(JspContext jspContext) {
            return (ApplicationContext) jspContext.getAttribute(
                    ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE,
                    PageContext.APPLICATION_SCOPE);
        }
    }

    /**
     * The body abstraction in a JSP tag.
     *
     * @version $Rev: 1305546 $ $Date: 2012-03-27 07:34:37 +1100 (Tue, 27 Mar 2012) $
     */
    public static class JspModelBody extends AbstractModelBody {
        /**
         * The real body.
         */
        private final JspFragment jspFragment;
        /**
         * Constructor.
         *
         * @param jspFragment The real body.
         * @param jspContext  The page context.
         */
        public JspModelBody(JspFragment jspFragment, JspContext jspContext) {
            super(jspContext.getOut());
            this.jspFragment = jspFragment;
        }
        @Override
        public void evaluate(Writer writer) throws IOException {
            if (jspFragment == null)
                return;

            try {
                jspFragment.invoke(writer);
            } catch (JspException e) {
                throw new IOException("JspException when evaluating the body", e);
            }
        }
    }

}
