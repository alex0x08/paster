<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:forEach var="stat" items="${stats.list}">
     <a class="i ${stat.priority.cssClass}" style="font-size:2em;"
                   title="<fmt:message key="${stat.priority.name}"/>. Click to search with same priority."
                   href="<c:url value='/main/paste/list/search?query=priority:${stat.priority.code}'/>">/</a>
                   x <c:out value="${stat.counter}"/>
                   ,

</c:forEach>
                   

<sec:authorize ifAnyGranted="ROLE_ADMIN">
|
    <a href="<c:url value='/main/user/list'/>">Manage users</a>
</sec:authorize>