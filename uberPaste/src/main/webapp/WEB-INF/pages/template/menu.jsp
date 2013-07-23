<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
Serving <c:out value="${stats.total}"/> pastas
<sec:authorize ifAnyGranted="ROLE_ADMIN">
|
    <a href="<c:url value='/main/user/list'/>">Manage users</a>
</sec:authorize>