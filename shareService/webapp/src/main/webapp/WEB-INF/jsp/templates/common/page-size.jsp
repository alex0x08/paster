<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<div class="btn-group">
    <button type="button" class="btn btn-danger"><c:out value="${pageItems.pageSize}"/></button>
    <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
        <span class="caret"></span>
    </button>
    <ul class="dropdown-menu" role="menu">
        <c:forEach items="${fn:split('5,10,50,100,500',',')}" var="pg">
            <li >
                <a href="<c:url value="/main/${param.modelName}/list/limit/${pg}"></c:url>"><c:out value="${pg}"/></a>
                </li>
        </c:forEach>   
    </ul>
</div>   
