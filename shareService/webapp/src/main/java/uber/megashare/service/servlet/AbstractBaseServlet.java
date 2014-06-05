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
package uber.megashare.service.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import uber.megashare.base.LoggedClass;
import uber.megashare.listener.SessionHelper;
import uber.megashare.model.User;
import uber.megashare.service.UserManager;

/**
 * Базовый сервлет (абстрактный для невозможности инициализации)
 *
 * @author alex
 */
public abstract class AbstractBaseServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = -1284359983492408014L;
    
    protected UserManager userManager;
    
    protected ApplicationContext appContext;
    
    protected final Logger log = LoggedClass.newInstance(this.getClass()).getLogger();

    protected Logger getLogger() {
        return log;
    }

    @Override
    public void init() {
        appContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        userManager = appContext.getBean(UserManager.class);

        initImpl();
    }

    protected abstract void initImpl();

    public User getCurrentUser(HttpServletRequest request) {

        return request.getUserPrincipal() != null
                ? SessionHelper.getInstance().getUserForLogin(request.getUserPrincipal().getName())
                : null;
    }

    public boolean isAllowedToRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (getCurrentUser(request) == null) {
            writeError(response, "You are not allowed to view this content.");
            return false;
        }
        return true;
    }


    /**
     * пишет в ответ сервера сообщение об ошибке
     *
     * @param response http-ответ
     * @param msg сообщение
     * @throws IOException
     */
    public static void writeError(HttpServletResponse response, String msg)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(500);

        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>ERROR: " + msg + "</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>ERROR: " + msg + "</h1>");
            out.println("</body>");
            out.println("</html>");
            out.flush();
        }
    }
}
