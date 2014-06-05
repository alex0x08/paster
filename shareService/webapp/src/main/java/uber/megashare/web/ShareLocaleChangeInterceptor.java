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
package uber.megashare.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.LocaleUtils;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;
import uber.megashare.base.LoggedClass;
import uber.megashare.listener.SessionHelper;
import uber.megashare.model.User;
import uber.megashare.service.security.SecurityHelper;

/**
 * 
 * @author <a href="mailto:aachernyshev@it.ru">Alex Chernyshev</a>
 */
public class ShareLocaleChangeInterceptor extends LocaleChangeInterceptor {

        
        private Logger log = LoggedClass.newInstance(getClass()).getLogger();

	private String paramName = DEFAULT_PARAM_NAME;

       
	/**
	 * Set the name of the parameter that contains a locale specification
	 * in a locale change request. Default is "locale".
	 */
        @Override
	public void setParamName(String paramName) {
		this.paramName = paramName;
                super.setParamName(paramName);
	}

	/**
	 * Return the name of the parameter that contains a locale specification
	 * in a locale change request.
	 */
        @Override
	public String getParamName() {
		return this.paramName;
	}
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ServletException {

        String newLocale = request.getParameter(this.paramName);
        if (newLocale == null && SecurityHelper.getInstance().getCurrentUser() != null) {
            User currentUser = SecurityHelper.getInstance().getCurrentUser();
            if (currentUser.getCurrentLocale() == null) { 
               
                newLocale = SecurityHelper.getInstance().getCurrentUser().getPrefferedLocaleCode();
                
                try {
                currentUser.setCurrentLocale(LocaleUtils.toLocale(newLocale));
                SessionHelper.getInstance().updateUser(currentUser);
                } catch (java.lang.IllegalArgumentException e) {
                    log.error(e.getLocalizedMessage(),e);
                }
               
            }
        }
        

        if (newLocale != null) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }
            localeResolver.setLocale(request, response, StringUtils.parseLocaleString(newLocale));
        }
        // Proceed in any case.
        return true;
    }
}
