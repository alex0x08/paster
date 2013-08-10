<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

 <jsp:include
    page="/WEB-INF/jsp/templates/common/comments.jsp">
    <jsp:param name="model" value="${model}" />
    <jsp:param name="modelName" value="file" />
  <jsp:param name="currentUser" value="${currentUser}" />
  
</jsp:include>

