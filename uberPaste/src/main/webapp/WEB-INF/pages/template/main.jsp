<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<!DOCTYPE html>
<compress:html enabled="true" 
               removeComments="true" compressJavaScript="true" compressCss="true" yuiJsDisableOptimizations="false">
    <html lang="en">
        <head>
            <meta name="viewport" content="initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=yes" />
            <link href="<c:url value='/main/assets/${appVersion}/paster/less/app.css'/>" rel="stylesheet" type="text/css">
            <c:choose>
                <c:when test="${not empty systemInfo.project.clientImage}">
                    <style>
                        body {
                            background: url('${systemInfo.project.clientImage}') no-repeat;
                            background-position: right bottom;
                            background-attachment: fixed;

                        }
                    </style>
                </c:when>
                <c:otherwise>

                    <%--    <c:url var="defaultBgUrl" value='/main/static/${appVersion}/images/ninjas/ninja-back.png'/>
                        
                    <style>
                        body {
                            background: url('${defaultBgUrl}') no-repeat;
                        }
                    </style> --%>
                </c:otherwise>
            </c:choose>

            <jsp:include page="/WEB-INF/pages/template/template-common-head.jsp"/>

            <title><tiles:insertAttribute name="pageTitle" ignore="true"/></title>
            <tiles:insertAttribute name="head" />
        </head>
        <body>

            <header class="navbar" role="banner" >
                <div class="container-fluid">
                    <div class="navbar-header">
                        <button class="navbar-toggle" type="button" data-trigger="toggleClass"
                                data-toggleclass-options="
                                'target': '!.navbar .navbar-collapse',
                                'class': 'in'
                                ">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>

                        <ul class="nav navbar-nav" data-behavior="BS.Dropdown">
                            <li class="dropdown">
                                <a href="#" role="button" class="dropdown-toggle" data-toggle="dropdown" style="padding-bottom:0;padding-top:0.5em;margin:0;">

                                    <span class="i" style="font-size: 3em;">/</span>

                                    <span class="caret" style="padding-bottom:0;padding-top:0.5em;margin:0;"></span></a>
                                <ul class="dropdown-menu" role="menu">
                                    <li>
                                        <a id="createNewBtn" class="mainLinkLine" href="<c:url value="/main/paste/new"></c:url>" title="<fmt:message key='paste.create.new'/>">
                                            <fmt:message key='paste.create.new'/>
                                        </a>


                                    </li>
                                    <li><a href="#">From file..</a></li>

                                </ul>
                            </li>
                        </ul>






                    </div>
               
                                            <sec:authorize access="isAuthenticated()" >
                          <jsp:include page="/WEB-INF/pages/template/search.jsp"/>
                                               
                                            </sec:authorize>
                        
                                        
                    <nav class="collapse navbar-collapse bs-navbar-collapse" role="navigation">
                        <ul class="nav navbar-nav" data-behavior="BS.Dropdown">
                            <li >
                                <a href="/sandbox/about/overview">Overview</a>
                            </li>
                            <li class="">
                                <a href="/sandbox/about/getting_started">Getting Started</a>
                            </li>


                            <li class="">
                                <a href="/sandbox/JavaScript/Accordions,%20Carousels,%20and%20Tabs/Behavior.BS.Carousel">JavaScript</a>
                            </li>
                        </ul>
                        <ul class="nav navbar-nav navbar-right" data-behavior="BS.Dropdown">

                            <li >

                                <sec:authorize access="!isAuthenticated()" >
                                    <a href="<c:url value='/main/login'/>">
                                        <span style="font-size: 1em;vertical-align: middle;" class="i" title="Login here">x</span></a>

                                    <%--c:forEach var="server" items="${availableServers}">
                                        <a  class="img-map ${server.icon}" href="<c:url value='/act/openid-login?openid_identifier=${server.code}'/>">
                                        </a>
                                    </c:forEach--%>
                                </sec:authorize>


                                <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
                                    <jsp:include
                                        page="/WEB-INF/pages/common/currentUser.jsp">
                                        <jsp:param name="currentUser" value="${currentUser}" />
                                    </jsp:include>

                                </sec:authorize>


                            </li>


                            <li class="dropdown">
                                <a href="#" role="button" class="dropdown-toggle" data-toggle="dropdown" 
                                   >
                                    <c:out value="${pageContext.response.locale.displayLanguage}"/>
                                    <span class="caret" ></span></a>
                                <ul class="dropdown-menu" role="menu">
                                    <c:forEach items="${availableLocales}" var="locale" >

                                        <li>

                                            <a href="<c:url value="${request.requestURL}">
                                                   <c:param name="locale" value="${locale.language}_${locale.country}" />
                                                   <c:forEach items="${param}" var="currentParam">
                                                       <c:if test="${currentParam.key ne 'locale'}">
                                                           <c:param name="${currentParam.key}" value="${currentParam.value}"/>
                                                       </c:if>
                                                   </c:forEach>

                                               </c:url>"><span title="<c:out value='${locale.displayLanguage}'/>">
                                                    <c:out value="${locale.language}"/>
                                                </span>
                                            </a>

                                        </li>

                                    </c:forEach>



                                </ul>
                            </li>


                            <li></li>
                        </ul>

                    </nav>
                </div>
            </header>

            <div class="container">
                <div class="row">
                    <div class="col-md-12">

                        <tiles:insertAttribute name="menu" />

                        <div class="notices">
                        </div>

                        <c:if test="${not empty statusMessageKey}">
                            <script type="text/javascript">

                                var operationMessage = '<fmt:message key="${statusMessageKey}"/>';

                                window.addEvent('domready', function() {
                                    growl.notify(operationMessage);
                                });


                            </script>

                            <noscript>
                            <p><fmt:message key="${statusMessageKey}"/></p>
                            </noscript>  
                        </c:if>






                    </div>
                </div>
            

            <div class="row">
                <div class="col-md-10">

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
                <div class="col-md-15">
                    <tiles:insertAttribute name="content" />
                </div>
            </div>

            <div class="row">
                <div class="col-md-10 offset2">
                    <p title="<c:out value="${systemInfo.runtimeVersion.full}"/>">
                        <c:out value="${systemInfo.runtimeVersion.implVersion}"/>
                    </p>

                    <fmt:formatDate pattern="${datePattern}" var="startDate" 
                                    value="${systemInfo.dateStart}" />

                    <fmt:formatDate pattern="${datePattern}" var="installDate" 
                                    value="${systemInfo.dateInstall}" />
                    <p>
                        <fmt:message key='system.installed.title'>
                            <fmt:param value="${startDate}"/>
                            <fmt:param value="${installDate}"/>
                        </fmt:message>   
                    </p>


                    <small><fmt:message key="site.footer"/></small>
                </div>
            </div>

            <jsp:include page="/WEB-INF/pages/template/template-common-body.jsp"/>
            <tiles:insertAttribute name="footer" />
            </div>
        </body>

    </html>
</compress:html>
