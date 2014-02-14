<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
    <head>
       
            <link href="<c:url value="/main/static/${appVersion}/css/app.less"/>" media="all" rel="stylesheet/less" />

        <jsp:include page="/WEB-INF/pages/template/template-common-head.jsp"/>
       
        <title><tiles:insertAttribute name="pageTitle" ignore="true"/></title>
         <tiles:insertAttribute name="head" />
    </head>
    <body>

    <div class="row">
        <div class="column grid-10">
            <tiles:insertAttribute name="menu" />
            <c:if test="${not empty statusMessageKey}">
                <p><fmt:message key="${statusMessageKey}"/></p>
            </c:if>
        </div>
        <div class="column grid-4">
        <sec:authorize access="!isAuthenticated()" >
           <a href="<c:url value='/main/login'/>">
               <span style="font-size: 1em;vertical-align: middle;" class="i" title="Login here">x</span></a>

            <c:forEach var="server" items="${availableServers}">
                <a href="<c:url value='/act/openid-login?openid_identifier=${server.code}'/>">
                    <img src="<c:url value='/main/static/${appVersion}/images/openid/${server.icon}'/>" alt="${server.name}" title="${server.name}" border="0" style=" vertical-align: middle; " />
                </a>
            </c:forEach>
        </sec:authorize>


            <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
                <jsp:include
                        page="/WEB-INF/pages/common/currentUser.jsp">
                <jsp:param name="currentUser" value="${currentUser}" />
            </jsp:include>

            </sec:authorize>

        </div>
            
            <div class="column grid-2">
                        <c:forEach items="${availableLocales}" var="locale" >
            <c:if test="${pageContext.response.locale ne locale}">
                    <a href="<c:url value="${request.requestURL}">
                           <c:param name="locale" value="${locale.language}_${locale.country}" />
                           <c:forEach items="${param}" var="currentParam">
                               <c:if test="${currentParam.key ne 'locale'}">
                                   <c:param name="${currentParam.key}" value="${currentParam.value}"/>
                               </c:if>
                           </c:forEach>

                       </c:url>">
                        <div style="font-size: small;" title="<c:out value='${locale.displayLanguage}'/>">
                            <c:out value="${locale.language}"/>
                        </div>
                    </a>
            </c:if>
        </c:forEach> 
            </div>   
        </div>

            <div class="row">
                <div column grid-16>
              <tiles:insertAttribute name="content" />
                </div>
            </div>
        
              <div class="row">
                  <div column grid-14>
                      <p title="<c:out value="${systemInfo.runtimeVersion.full}"/>">
                          <c:out value="${systemInfo.runtimeVersion.implVersion}"/>
                      </p>
                      <small>2014. (c) Alex</small>
                  </div>
              </div>
                  
         <jsp:include page="/WEB-INF/pages/template/template-common-body.jsp"/>
          <tiles:insertAttribute name="footer" />
    </body>

</html>
