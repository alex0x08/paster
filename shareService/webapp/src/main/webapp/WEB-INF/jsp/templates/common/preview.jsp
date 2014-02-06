<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:choose>
    
    <c:when test="${model.nodeType eq 'FOLDER'}">
        Folder:
        <c:out value='${model.name}'/>
        
    </c:when>
    
       
        
    <c:when test="${model.nodeType eq 'FILE'}">
      <jsp:include page="/WEB-INF/jsp/templates/common/file_preview.jsp" />
    </c:when>
</c:choose>

