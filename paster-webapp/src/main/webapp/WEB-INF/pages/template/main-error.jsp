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
        Template for error pages
--%>
<!DOCTYPE html>
    <html lang="en">
        <head>
            <meta name="viewport" content="initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, 
                  user-scalable=yes" />
            <jsp:include page="/WEB-INF/pages/template/template-common-head.jsp"/>
            <title><c:out value="${pageTitle}"/></title>            
        </head>
        <body>         
            <div class="container-fluid">  
                <div class="row">
                    <div class="col-xs-6 col-sm-8 col-md-12 col-lg-14">
                        <div class="notices">
                        </div>                       
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-10 col-sm-10 col-md-12 col-lg-14">
                        <spring:hasBindErrors name="input">
                            <c:forEach items="${errors.globalErrors}" var="errorMessage">
                                <div id="globalErrors" class="error">
                                    <c:out value="${errorMessage.defaultMessage}" />
                                </div>
                            </c:forEach>
                        </spring:hasBindErrors>    
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-10 col-sm-12 col-md-12 col-lg-16 ">
                        <tiles:insertAttribute name="content" />
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-10 offset2">
                        <p title="<c:out value='${systemInfo.runtimeVersion.full}'/>">
                            <c:out value="${systemInfo.runtimeVersion.full}"/>
                        </p>
                        <fmt:formatDate pattern="${datePattern}" var="startDate" 
                                        value="${systemInfo.dateStart}" />
                        <small><fmt:message key="site.footer"/></small>
                    </div>
                </div>
                <jsp:include page="/WEB-INF/pages/template/template-common-body.jsp"/>               
            </div>
        </body>
</html>
