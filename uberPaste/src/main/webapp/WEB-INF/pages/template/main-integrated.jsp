<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <link href="<c:url value='/main/static/${appVersion}/css/app-integrated.less'/>" media="all" rel="stylesheet/less" />
       <jsp:include page="/WEB-INF/pages/template/template-common-head.jsp"/>
    
       <title><tiles:insertAttribute name="pageTitle" ignore="true"/></title>
         <tiles:insertAttribute name="head" />



    <script type="text/javascript" src="<c:url value='/main/static/${appVersion}/libs/syntax-highlight/scripts/xregexp.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/main/static/${appVersion}/libs/syntax-highlight/scripts/shCore.js'/>"></script>

   
    <link type="text/css" rel="stylesheet" href="<c:url value='/main/static/${appVersion}/libs/syntax-highlight/styles/shCoreDefault.css'/>"/>

    </head>
            <body>


            <div class="row">
                <div class="column grid-16">


            <c:if test="${not empty statusMessageKey}">
            <p><fmt:message key="${statusMessageKey}"/></p>
            </c:if>

            </div>
            <div class="column grid-4" >



            <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
                <jsp:include
                        page="/WEB-INF/pages/common/currentUser.jsp">
                    <jsp:param name="currentUser" value="${currentUser}" />
                </jsp:include>
             </sec:authorize>


</div>
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
