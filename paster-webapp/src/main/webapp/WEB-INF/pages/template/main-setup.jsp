<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<%--

        Main configuration template

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
                 <ul class="nav navbar-nav me-auto">
                 </ul>
                      <p class="navbar-text hidden-sm hidden-xs" >
                        Installation
                      </p>
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
                             <c:when test="${s.stepKey eq step.stepKey}">
                                   <li class="breadcrumb-item active" aria-current="page">
                                     <c:out value="${loopStatus.index+1}"/>. <c:out value="${s.stepName}"/>
                                     <c:if test="${s.completed}">
                                        <i class="fa fa-check" aria-hidden="true"></i>
                                     </c:if>
                                   </li>
                             </c:when>
                             <c:otherwise>
                                <c:url var="stepUrl" value='/main/setup/${s.stepKey}' />
                                <li class="breadcrumb-item">
                                               <c:out value="${loopStatus.index+1}"/>.
                                               <a href="${stepUrl}">
                                                   <c:out value="${s.stepName}"/>
                                               </a>
                                               <c:if test="${s.completed}">
                                                     <i class="fa fa-check" aria-hidden="true"></i>
                                               </c:if>
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