<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:url value="/act/download" var="downloadUrl">
  <c:param name="id" value="${model.uuid}"/>
</c:url>

 <jsp:include page="/WEB-INF/jsp/templates/common/preview.jsp" />

