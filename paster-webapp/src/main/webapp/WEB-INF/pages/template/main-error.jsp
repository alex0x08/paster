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
