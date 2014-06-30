<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<c:choose>
    <c:when test="${param.integrationCode ne null}">
        <c:set var="listUrl" value="/main/${param.modelName}/integrated/list/${param.integrationCode}" />
    </c:when>
    <c:otherwise>
        <c:set var="listUrl" value="/main/${param.modelName}/list" />
    </c:otherwise>
</c:choose>

<div class="btn-group">
    <button type="button" class="btn btn-sm btn-primary"><c:out value="${pageItems.pageSize}"/></button>
    <button type="button" class="btn btn-sm btn-primary dropdown-toggle" data-toggle="dropdown">
        <span class="caret"></span>
    </button>
    <ul class="dropdown-menu" role="menu">
        <c:forEach items="${fn:split('5,10,50,100,500',',')}" var="pg">
            <li >
                <a href="<c:url value="${listUrl}/limit/${pg}"></c:url>"><c:out value="${pg}"/></a>
                </li>
        </c:forEach>   
    </ul>
</div>   
