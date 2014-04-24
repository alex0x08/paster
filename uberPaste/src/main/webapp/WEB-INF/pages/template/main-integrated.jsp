<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<!DOCTYPE html>
<compress:html enabled="true" 
               removeComments="true" compressJavaScript="true" yuiJsDisableOptimizations="false">

    <html lang="en">
        <head>
            <link href="<c:url value='/main/assets/${appVersion}/paster/less/app-integrated.css'/>" rel="stylesheet" type="text/css">
            <jsp:include page="/WEB-INF/pages/template/template-common-head.jsp"/>

            <title><tiles:insertAttribute name="pageTitle" ignore="true"/></title>
            <tiles:insertAttribute name="head" />

        </head>
        <body>


            <div class="row">

                <c:if test="${not empty statusMessageKey}">
                    <div class="column grid-16">
                        <p><fmt:message key="${statusMessageKey}"/></p>
                    </div>
                </c:if>

                <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
                    <div class="column grid-4" >
                        <jsp:include
                            page="/WEB-INF/pages/common/currentUser.jsp">
                            <jsp:param name="currentUser" value="${currentUser}" />
                        </jsp:include>
                    </div>
                </sec:authorize>

            </div>

            <div class="row">
                <div class="column grid-18"  >
                    <tiles:insertAttribute name="content" />
                </div>
            </div>

            <jsp:include page="/WEB-INF/pages/template/template-common-body.jsp"/>

            <tiles:insertAttribute name="footer" />


        </body>

    </html>
</compress:html>