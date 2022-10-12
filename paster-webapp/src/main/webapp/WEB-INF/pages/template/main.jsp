<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%--

        Main template
        
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, 
                  user-scalable=yes" />
    <jsp:include page="/WEB-INF/pages/template/template-common-head.jsp" />
    <title>
        <tiles:insertAttribute name="pageTitle" ignore="true" />
    </title>
    <tiles:insertAttribute name="head" />
</head>
<body class="d-flex flex-column h-100 min-vh-100">
    <header>
        <nav class="navbar navbar-expand-md">
            <div class="container-fluid">
                <a class="navbar-brand" href="<c:url value='/'/>">
                    <span class="i">/</span>
                    <fmt:message key="site.title" />
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse"
                    aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse justify-content-between" id="navbarCollapse">
                    <sec:authorize access="hasRole('ROLE_ADMIN')">    
                        <ul class="nav navbar-nav">
                            <li class="nav-item dropdown">
                                <a href="#" id="dropdown01" class="nav-link dropdown-toggle" role="button"
                                    data-bs-toggle="dropdown" aria-expanded="false">
                                    <fmt:message key="site.menu.admin" />
                                    <span class="caret"></span>
                                </a>    
                                <ul class="dropdown-menu" aria-labelledby="dropdown01">    
                                    <li>
                                        <a class="dropdown-item" href="<c:url value='/main/admin/settings/dbconsole'/>">
                                            <span class="i">B</span>
                                            <fmt:message key="settings.dbconsole.title" />
                                        </a>    
                                    </li>    
                                </ul>
                            </li>
                        </ul>
                    </sec:authorize>
                    <ul class="nav navbar-nav me-auto">
                        <li class="nav-item dropdown">
                            <a href="#" id="main-menu-dropdown" role="button" class="nav-link dropdown-toggle"
                                data-bs-toggle="dropdown" aria-expanded="false">
                                <fmt:message key="button.create" />
                                <span class="caret"></span>
                            </a>  
                            <ul class="dropdown-menu" role="menu" id="pasteNewMenu">
                                <li>
                                    <a role="menuitem" id="createNewBtn" class="dropdown-item mainLinkLine"
                                        href="<c:url value='/main/paste/new'></c:url>"
                                        title="<fmt:message key='paste.create.new' />">
                                        <fmt:message key='paste.create.new' />
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </ul>    
                    <tiles:insertAttribute name="search-top" />    
                    <p class="navbar-text hidden-sm hidden-xs" style="margin-top:5px;">
                        <c:forEach var="stat" items="${stats.list}">
                            <a class="i ${stat.priority}" style="font-size:2em;" title="<fmt:message key="
                                ${stat.priority}" />. Click to search with same priority."
                            href="
                            <c:url value='/main/paste/list/search?query=priority:${stat.priority}' />">/</a>
                            <span style="font-size: small;">x
                                <c:out value="${stat.counter}" />&nbsp;</span>
    
                        </c:forEach>
                    </p>    
                    <sec:authorize access="!isAuthenticated()">   
                        <ul class="nav navbar-nav">    
                            <li class="nav-item dropdown">
                                <a href="#" role="button" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
                                    <c:out value="${pageContext.response.locale.language}" />
                                    <span class="caret"></span>
                                </a>        
                                <ul class="dropdown-menu" role="menu">
                                    <c:forEach items="${availableLocales}" var="locale">        
                                        <li >
                                            <c:url var="switchLangUrl" value="${request.requestURL}">
                                                <c:param name="locale" value="${locale.language}_${locale.country}" />
                                                <c:forEach items="${param}" var="currentParam">
                                                    <c:if test="${currentParam.key ne 'locale'}">
                                                        <c:param name="${currentParam.key}" value="${currentParam.value}" />
                                                    </c:if>
                                                </c:forEach>        
                                            </c:url>        
                                            <a role="menuitem" href="${switchLangUrl}" class="dropdown-item">
                                                <span title="<c:out value='${locale.displayLanguage}'/>">
                                                    <c:out value="${locale.displayLanguage}" />
                                                </span>
                                            </a>        
                                        </li>        
                                    </c:forEach>   
                                </ul>
                            </li>
                        </ul>    
                        <p class="navbar-text hidden-sm hidden-xs" style="padding:0; margin:0;" >
                            <a href="<c:url value='/main/login'/>">
                                <span class="i" title="Login here">x</span>
                                <fmt:message key="button.login" />
                            </a>
                        </p>
                    </sec:authorize>    
                    <ul class="nav navbar-nav">                       
                        <sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER')">
                            <jsp:include page="/WEB-INF/pages/common/currentUser.jsp">
                                <jsp:param name="currentUser" value="${currentUser}" />
                            </jsp:include>    
                        </sec:authorize>     
                    </ul>  
                </div>
            </div>
        </nav>    
    </header>
    <main class="flex-shrink-0">
    <div class="container-fluid">     
        <div class="row">
            <div class="col-xs-10 col-sm-10 col-md-12 col-lg-12">
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
    </div>
    </main>
    <footer class="footer mt-auto">
        <div class="container-fluid">
            <div title="<c:out value='${systemInfo.runtimeVersion.full}' />">
                    <c:out value="${systemInfo.runtimeVersion.full}" />
            </div>
            <small>
                <fmt:message key="site.footer" />
            </small>
            </div>
    </footer>
    <jsp:include page="/WEB-INF/pages/template/template-common-body.jsp" />
    <tiles:insertAttribute name="footer" />
</body>
</html>
