<%--

    Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%--
       Admin's menu
--%>    
<sec:authorize access="hasRole('ROLE_ADMIN')">
    <div class="row">
        <div class="hidden-sm hidden-xs col-md-10 col-lg-10">
            <a href="<c:url value='/main/paste/list'/>">
                <span class="i">/</span>
                <fmt:message key="paste.list.title"/></a> 
            <a href="<c:url value='/main/user/list'/>">
                <span class="i">x</span>
                <fmt:message key='user.list.title'/></a>
            <a href="<c:url value='/main/admin/settings/edit'/>">
                <span class="i">B</span>
                <fmt:message key='settings.edit.title'/></a>
            <a href="<c:url value='/main/admin/settings/dbconsole'/>">
                <span class="i">B</span>
                <fmt:message key="settings.dbconsole.title"/>
            </a>
        </div>
    </div>
</sec:authorize>
