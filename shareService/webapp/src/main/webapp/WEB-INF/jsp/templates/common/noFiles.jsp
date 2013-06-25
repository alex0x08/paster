<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<center>
    <fmt:message key="files.not-found"/>
    
      <c:if test="${empty param.integrationMode}">
    <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
        <a class="new-file" href="<c:url value="/main/file/new"/>"><fmt:message key="file.upload.title"/></a> 
    </sec:authorize>
    
      </c:if>
    
  </center>
