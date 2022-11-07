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

        Curent user (top right) dropdown

--%>
<li class="nav-item dropdown">
    <a href="#" id="userProfileSwitch" role="button" 
        class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
        <c:out value='${currentUser.name}' />
        <b class="caret"></b>
    </a>
    <span class="dropdown-arrow"></span>
    <ul class="dropdown-menu" role="menu" aria-labelledby="userProfileSwitch">      
        <li class="nav-item dropdown" >
            <c:url var="logoutUrl" value='/act/logout' />
            <form:form cssClass="form-inline" 
                        style="padding-left:1em;" 
                        role="form" action="${logoutUrl}" method="POST">
                <button type="submit" class="btn btn-sm">
                    <i class="fa fa-sign-in"></i> <fmt:message key="button.logout" /></a>
                </button>
            </form:form>
        </li>
    </ul>
</li>
