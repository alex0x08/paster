<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<%--

        Main template
        
        --%>


<!DOCTYPE html>
<html lang="en">

<head>
    <meta name="viewport" content="initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, 
                  user-scalable=yes" />

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

            <%--    <c:url var="defaultBgUrl" value='/main/static/${appId}/images/ninjas/ninja-back.png'/>
                        
                    <style>
                        body {
                            background: url('${defaultBgUrl}') no-repeat;
                        }
                    </style> --%>
        </c:otherwise>
    </c:choose>

    <jsp:include page="/WEB-INF/pages/template/template-common-head.jsp" />

    <title>
        <tiles:insertAttribute name="pageTitle" ignore="true" />
    </title>
    <tiles:insertAttribute name="head" />
</head>

<body>
    <nav class="navbar navbar-expand-md">
        <div class="container-fluid">
            <a class="navbar-brand" href="<c:url value='/'/>">
                <span class="i">/</span>
                Paster
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse"
                aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse  justify-content-between" id="navbarCollapse">

                <sec:authorize access="hasRole('ROLE_ADMIN')">

                    <ul class="nav navbar-nav">
                        <li class="nav-item dropdown">
                            <a href="#" id="dropdown01" class="nav-link dropdown-toggle" role="button"
                                data-bs-toggle="dropdown" aria-expanded="false">
                                Admin
                                <span class="caret"></span>
                            </a>

                            <ul class="dropdown-menu" aria-labelledby="dropdown01">

                                <li>
                                    <a class="dropdown-item" href="<c:url value='/main/user/list'/>">
                                        <span class="i">x</span>
                                        <fmt:message key='user.list.title' /></a>


                                </li>
                                <li>

                                    <a class="dropdown-item" href="<c:url value='/main/admin/settings/edit'/>">
                                        <span class="i">B</span>
                                        <fmt:message key='settings.edit.title' /></a>

                                </li>
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
                            New
                            <span class="caret"></span>
                        </a>


                        <ul class="dropdown-menu" role="menu" id="pasteNewMenu">
                            <li>
                                <a role="menuitem" id="createNewBtn" class="mainLinkLine"
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
                        <a class="i ${stat.priority.cssClass}" style="font-size:2em;" title="<fmt:message key="
                            ${stat.priority.name}" />. Click to search with same priority."
                        href="
                        <c:url value='/main/paste/list/search?query=priority:${stat.priority.code}' />">/</a>
                        <span style="font-size: small;">x
                            <c:out value="${stat.counter}" />&nbsp;</span>

                    </c:forEach>
                </p>



                <ul class="nav navbar-nav">

                    <sec:authorize access="!isAuthenticated()">
                        <li>
                            <a href="<c:url value='/main/login'/>">
                                <span class="i" title="Login here">x</span>
                                Authentication
                            </a>

                        </li>
                    </sec:authorize>


                    <sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER')">
                        <jsp:include page="/WEB-INF/pages/common/currentUser.jsp">
                            <jsp:param name="currentUser" value="${currentUser}" />
                        </jsp:include>

                    </sec:authorize>


                    <li class="nav-item dropdown">
                        <a href="#" role="button" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
                            <c:out value="${pageContext.response.locale.language}" />
                            <span class="caret"></span>
                        </a>

                        <ul class="dropdown-menu" role="menu">
                            <c:forEach items="${availableLocales}" var="locale">

                                <li role="presentation">

                                    <c:url var="switchLangUrl" value="${request.requestURL}">
                                        <c:param name="locale" value="${locale.language}_${locale.country}" />
                                        <c:forEach items="${param}" var="currentParam">
                                            <c:if test="${currentParam.key ne 'locale'}">
                                                <c:param name="${currentParam.key}" value="${currentParam.value}" />
                                            </c:if>
                                        </c:forEach>

                                    </c:url>

                                    <a role="menuitem" href="${switchLangUrl}">
                                        <span title="<c:out value='${locale.displayLanguage}'/>">
                                            <c:out value="${locale.displayLanguage}" />
                                        </span>
                                    </a>

                                </li>

                            </c:forEach>



                        </ul>
                    </li>


                </ul>


            </div>
        </div>
    </nav>



    <div class="container-fluid">

        <div class="row">
            <div class="col-xs-6 col-sm-8 col-md-12 col-lg-14">

                <div class="notices">
                </div>

                <c:if test="${not empty statusMessageKey}">
                    <script type="text/javascript">

                        window.addEventListener('load', function () {
                            pasterApp.showNotify('<fmt:message key="${statusMessageKey}"/>');
                        });


                    </script>

                    <noscript>
                        <p>
                            <fmt:message key="${statusMessageKey}" />
                        </p>
                    </noscript>
                </c:if>



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
                <p title="<c:out value='${systemInfo.runtimeVersion.full}' />">
                <c:out value="${systemInfo.runtimeVersion.implVersion}" />
                </p>

                <fmt:formatDate pattern="${datePattern}" var="startDate" value="${systemInfo.dateStart}" />

                <fmt:formatDate pattern="${datePattern}" var="installDate" value="${systemInfo.dateInstall}" />
                <p>
                    <fmt:message key='system.installed.title'>
                        <fmt:param value="${startDate}" />
                        <fmt:param value="${installDate}" />
                    </fmt:message>
                </p>


                <small>
                    <fmt:message key="site.footer" /></small>
            </div>
        </div>


    </div>

    <jsp:include page="/WEB-INF/pages/template/template-common-body.jsp" />


    <tiles:insertAttribute name="footer" />
</body>

</html>