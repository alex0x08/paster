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
                    Paster
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse"
                    aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse justify-content-between" id="navbarCollapse">
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

                <nav aria-label="breadcrumb">
                  <ol class="breadcrumb">
                        <c:forEach var="s" items="${availableSteps}" varStatus="loopStatus">
                        <c:choose>
                             <c:when test="${s.value eq step.stepName}">
                                   <li class="breadcrumb-item active" aria-current="page">
                                      <c:out value="${s.value}"/>
                                   </li>
                             </c:when>
                             <c:otherwise>
                                <c:url var="stepUrl" value='/main/setup/${s.key}' />
                                <li class="breadcrumb-item">
                                               <a href="${stepUrl}">
                                                   <c:out value="${s.value}"/>
                                               </a>
                                </li>
                             </c:otherwise>
                         </c:choose>
                      </c:forEach>

                    <li class="breadcrumb-item active" aria-current="page">Complete</li>
                  </ol>
                </nav>



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
