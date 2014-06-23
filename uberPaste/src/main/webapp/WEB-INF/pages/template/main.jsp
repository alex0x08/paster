<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<!DOCTYPE html>
<compress:html enabled="true" 
               removeComments="true" compressJavaScript="true" compressCss="true" yuiJsDisableOptimizations="false">
<html lang="en">
    <head>
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
                    
                    <c:url var="defaultBgUrl" value='/main/static/${appVersion}/images/ninjas/ninja-back.png'/>
                    
                <style>
                    body {
                        background: url('${defaultBgUrl}') no-repeat;
                    }
                </style>
                </c:otherwise>
            </c:choose>
                
        <jsp:include page="/WEB-INF/pages/template/template-common-head.jsp"/>
       
        <title><tiles:insertAttribute name="pageTitle" ignore="true"/></title>
         <tiles:insertAttribute name="head" />
    </head>
    <body>

    <div class="row">
        <div class="column grid-10">
            <tiles:insertAttribute name="menu" />
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

                <spring:hasBindErrors name="input">
                    <c:forEach items="${errors.globalErrors}" var="errorMessage">
                        <div id="globalErrors" class="error">
                            <c:out value="${errorMessage.defaultMessage}" />
                        </div>
                    </c:forEach>
                </spring:hasBindErrors>    
                
        </div>
        <div class="column grid-3">
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

        </div>
            
            <div class="column grid-2">
                <i class="fa fa-flag error"></i> 
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
                        <span title="<c:out value='${locale.displayLanguage}'/>">
                            <c:out value="${locale.language}"/>
                        </span>
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
    </body>

</html>
</compress:html>
