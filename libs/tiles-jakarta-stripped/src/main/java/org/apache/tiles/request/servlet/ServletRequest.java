/*
 * $Id: ServletRequest.java 1375743 2012-08-21 20:05:58Z nlebas $
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
package org.apache.tiles.request.servlet;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.tiles.request.AbstractClientRequest;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.attribute.Addable;
import org.apache.tiles.request.attribute.AttributeExtractor;
import org.apache.tiles.request.attribute.EnumeratedValuesExtractor;
import org.apache.tiles.request.attribute.HasKeys;
import org.apache.tiles.request.collection.HeaderValuesMap;
import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;
import org.apache.tiles.request.collection.ScopeMap;

/**
 * Servlet-based implementation of the TilesApplicationContext interface.
 *
 * @version $Rev: 1375743 $ $Date: 2012-08-22 06:05:58 +1000 (Wed, 22 Aug 2012) $
 */
public class ServletRequest extends AbstractClientRequest {
    /**
     * The native available scopes: request, session and application.
     */
    private static final List<String> SCOPES
            = List.of(REQUEST_SCOPE, "session", APPLICATION_SCOPE);
    /**
     * The request object to use.
     */
    private final HttpServletRequest request;
    /**
     * The response object to use.
     */
    private final HttpServletResponse response;
    /**
     * The response output stream, lazily initialized.
     */
    private OutputStream outputStream;
    /**
     * The response writer, lazily initialized.
     */
    private PrintWriter writer;
    /**
     * <p>The lazily instantiated <code>Map</code> of header name-value
     * combinations (immutable).</p>
     */
    private Map<String, String> header = null;
    /**
     * <p>The lazily instantiated <code>Map</code> of header name-value
     * combinations (write-only).</p>
     */
    private Addable<String> responseHeaders = null;
    /**
     * <p>The lazily instantitated <code>Map</code> of header name-values
     * combinations (immutable).</p>
     */
    private Map<String, String[]> headerValues = null;
    /**
     * <p>The lazily instantiated <code>Map</code> of request
     * parameter name-value.</p>
     */
    private Map<String, String> param = null;
    /**
     * <p>The lazily instantiated <code>Map</code> of request scope
     * attributes.</p>
     */
    private Map<String, Object> requestScope = null;
    /**
     * <p>The lazily instantiated <code>Map</code> of session scope
     * attributes.</p>
     */
    private Map<String, Object> sessionScope = null;
    /**
     * Creates a new instance of ServletTilesRequestContext.
     *
     * @param applicationContext The application context.
     * @param request            The request object.
     * @param response           The response object.
     */
    public ServletRequest(
            ApplicationContext applicationContext,
            HttpServletRequest request, HttpServletResponse response) {
        super(applicationContext);
        this.request = request;
        this.response = response;
    }
    /**
     * {@inheritDoc}
     */
    public Map<String, String> getHeader() {
        if ((header == null) && (request != null)) {
            header = new ReadOnlyEnumerationMap<>(new HeaderExtractor(request, null));
        }
        return (header);
    }
    /**
     * {@inheritDoc}
     */
    public Addable<String> getResponseHeaders() {
        if ((responseHeaders == null) && (response != null)) {
            responseHeaders = new HeaderExtractor(null, response);
        }
        return (responseHeaders);
    }
    /**
     * {@inheritDoc}
     */
    public Map<String, String[]> getHeaderValues() {
        if ((headerValues == null) && (request != null)) {
            headerValues = new HeaderValuesMap(new HeaderExtractor(request, response));
        }
        return (headerValues);
    }
    /**
     * {@inheritDoc}
     */
    public Map<String, String> getParam() {
        if ((param == null) && (request != null)) {
            param = new ReadOnlyEnumerationMap<>(new ParameterExtractor(request));
        }
        return (param);
    }
    /**
     * {@inheritDoc}
     */
    public Map<String, String[]> getParamValues() {
        return request.getParameterMap();
    }
    @Override
    public Map<String, Object> getContext(String scope) {
        if (REQUEST_SCOPE.equals(scope)) {
            return getRequestScope();
        } else if ("session".equals(scope)) {
            return getSessionScope();
        } else if (APPLICATION_SCOPE.equals(scope)) {
            return getApplicationScope();
        }
        throw new IllegalArgumentException(scope + " does not exist. Call getAvailableScopes() first to check.");
    }
    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getRequestScope() {
        if ((requestScope == null) && (request != null)) {
            requestScope = new ScopeMap(new RequestScopeExtractor(request));
        }
        return (requestScope);
    }
    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getSessionScope() {
        if ((sessionScope == null) && (request != null)) {
            sessionScope = new ScopeMap(new SessionScopeExtractor(request));
        }
        return (sessionScope);
    }
    @Override
    public List<String> getAvailableScopes() {
        return SCOPES;
    }
    /**
     * {@inheritDoc}
     */
    public void doForward(String path) throws IOException {
        if (response.isCommitted()) {
            doInclude(path);
        } else {
            forward(path);
        }
    }
    /**
     * {@inheritDoc}
     */
    public void doInclude(String path) throws IOException {
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (rd == null) {
            throw new IOException("No request dispatcher returned for path '"
                    + path + "'");
        }
        try {
            rd.include(request, response);
        } catch (ServletException ex) {
            throw ServletUtil.wrapServletException(ex, "ServletException including path '"
                    + path + "'.");
        }
    }
    /**
     * Forwards to a path.
     *
     * @param path The path to forward to.
     * @throws IOException If something goes wrong during the operation.
     */
    private void forward(String path) throws IOException {
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (rd == null) {
            throw new IOException("No request dispatcher returned for path '%s'".formatted(path));
        }
        try {
            rd.forward(request, response);
        } catch (ServletException ex) {
            throw ServletUtil.wrapServletException(ex, "ServletException including path '%s'.".formatted(path));
        }
    }
    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = response.getOutputStream();
        }
        return outputStream;
    }
    /**
     * {@inheritDoc}
     */
    public Writer getWriter() throws IOException {
        return getPrintWriter();
    }
    /**
     * {@inheritDoc}
     */
    public PrintWriter getPrintWriter() throws IOException {
        if (writer == null) {
            writer = response.getWriter();
        }
        return writer;
    }
    /**
     * {@inheritDoc}
     */
    public boolean isResponseCommitted() {
        return response.isCommitted();
    }
    /**
     * {@inheritDoc}
     */
    public void setContentType(String contentType) {
        response.setContentType(contentType);
    }
    /**
     * {@inheritDoc}
     */
    public Locale getRequestLocale() {
        return request.getLocale();
    }
    public HttpServletRequest getRequest() {
        return request;
    }
    public HttpServletResponse getResponse() {
        return response;
    }
    /**
     * {@inheritDoc}
     */
    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    /**
     * Extract header values from an HTTP request.
     *
     * @version $Rev: 1066499 $ $Date: 2011-02-03 02:33:34 +1100 (Thu, 03 Feb 2011) $
     */
    public static class HeaderExtractor implements EnumeratedValuesExtractor {
        /**
         * The request.
         */
        private final HttpServletRequest request;
        /**
         * The response.
         */
        private final HttpServletResponse response;
        /**
         * Constructor.
         *
         * @param request  The request.
         * @param response The response.
         */
        public HeaderExtractor(HttpServletRequest request,
                               HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }
        @Override
        public Enumeration<String> getKeys() {
            return request.getHeaderNames();
        }
        @Override
        public String getValue(String key) {
            return request.getHeader(key);
        }
        @Override
        public Enumeration<String> getValues(String key) {
            return request.getHeaders(key);
        }
        @Override
        public void setValue(String key, String value) {
            response.setHeader(key, value);
        }
    }

    /**
     * Extract parameters from the request.
     *
     * @version $Rev: 1066499 $ $Date: 2011-02-03 02:33:34 +1100 (Thu, 03 Feb 2011) $
     */
    public static class ParameterExtractor implements HasKeys<String> {
        /**
         * The servlet request.
         */
        private final HttpServletRequest request;
        /**
         * Constructor.
         *
         * @param request The servlet request.
         */
        public ParameterExtractor(HttpServletRequest request) {
            this.request = request;
        }
        @Override
        public Enumeration<String> getKeys() {
            return request.getParameterNames();
        }
        @Override
        public String getValue(String key) {
            return request.getParameter(key);
        }
    }
    /**
     * Extracts attributes from request scope.
     *
     * @version $Rev: 1066499 $ $Date: 2011-02-03 02:33:34 +1100 (Thu, 03 Feb 2011) $
     */
    public static class RequestScopeExtractor implements AttributeExtractor {
        /**
         * The servlet request.
         */
        private final HttpServletRequest request;
        /**
         * Constructor.
         *
         * @param request The servlet request.
         */
        public RequestScopeExtractor(HttpServletRequest request) {
            this.request = request;
        }
        @Override
        public void setValue(String name, Object value) {
            request.setAttribute(name, value);
        }
        @Override
        public void removeValue(String name) {
            request.removeAttribute(name);
        }
        @Override
        public Enumeration<String> getKeys() {
            return request.getAttributeNames();
        }
        @Override
        public Object getValue(String key) {
            return request.getAttribute(key);
        }
    }
    /**
     * Extract attributes from session scope.
     *
     * @version $Rev: 1199216 $ $Date: 2011-11-08 23:25:24 +1100 (Tue, 08 Nov 2011) $
     */
    public static class SessionScopeExtractor implements AttributeExtractor {
        /**
         * The servlet request.
         */
        private final HttpServletRequest request;
        /**
         * Constructor.
         *
         * @param request The servlet request.
         */
        public SessionScopeExtractor(HttpServletRequest request) {
            this.request = request;
        }
        @Override
        public void setValue(String name, Object value) {
            request.getSession().setAttribute(name, value);
        }
        @Override
        public void removeValue(String name) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(name);
            }
        }
        @Override
        public Enumeration<String> getKeys() {
            HttpSession session = request.getSession(false);
            return session != null ? session.getAttributeNames()
                    : Collections.enumeration(Collections.emptySet());
        }
        @Override
        public Object getValue(String key) {
            HttpSession session = request.getSession(false);
            return session != null ? session.getAttribute(key) : null;
        }
    }

}
