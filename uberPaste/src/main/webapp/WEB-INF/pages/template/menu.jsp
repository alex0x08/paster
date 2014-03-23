<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:forEach var="stat" items="${stats.list}">
     <a class="i ${stat.priority.cssClass}" style="font-size:2em;"
                   title="<fmt:message key="${stat.priority.name}"/>. Click to search with same priority."
                   href="<c:url value='/main/paste/list/search?query=priority:${stat.priority.code}'/>">/</a>
                   <span style="font-size: small;">x <c:out value="${stat.counter}"/>&nbsp;</span>
                   

</c:forEach>
                   

<sec:authorize ifAnyGranted="ROLE_ADMIN">
    
     <ul class="h-menu">
            <li>
<a href="<c:url value="/main/paste/list"/>"><fmt:message key="paste.list.title"/></a> 
            </li>
            <li>
<a href="<c:url value='/main/user/list'/>"><fmt:message key='user.list.title'/></a>
            </li>
            <li>
    <a href="<c:url value='/main/admin/settings/edit'/>"><fmt:message key='settings.edit.title'/></a>
            </li>
          </ul>
<hr/>    
</sec:authorize>