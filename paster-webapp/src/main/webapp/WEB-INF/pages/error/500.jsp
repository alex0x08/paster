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


<c:set var="pageTitle" value="500: Internal error"/>

<div class="jumbotron">
    <img src="<c:url  value='/main/resources/${appId}/static/big/backstab.png'/>" 
         style="width: 128px;height:128px;" 
         class="img-responsive img-rounded" alt="<fmt:message key="error.500.haiku"/>"/>    
    <h1><fmt:message key="error.500.title"/></h1>
    <p>
        <fmt:message key="error.500.haiku"/>
    </p>
    <p>
        <a class="btn btn-primary btn-lg"
                href="<c:url value='/'/>"
                title="<fmt:message key="index.title"/>">
            <i class="fa fa-home"></i>
            <fmt:message key="button.obey"/>
        </a>
    </p>
</div>

