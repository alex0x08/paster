<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />


<c:if test="${not empty model.tags}">

    ( <c:forEach var="tag" items="${model.tags}" varStatus="loopStatus">
    <a href="<c:url value='/main/${modelName}/list/search?query=tags:${tag}'/>" 
       title="click to search with tag ${tag}"><c:out value=" ${tag}"/></a>
    <c:if test="${!loopStatus.last}"> , </c:if>
</c:forEach> )

</c:if>
