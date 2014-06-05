<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<fmt:message key="versions.list"/>:
<c:forEach var="rev" items="${availableRevisions}">

    <c:choose>
        <c:when test="${rev == param.revision }">
            [${rev}
            <c:if test="${not empty param.revision and lastRevision ne param.revision}">
                <a href="<c:url value='/main/${param.modelName}/revert'>
                       <c:param name='id' value='${model.id}'/>
                       <c:param name='revision' value='${param.revision}'/></c:url>"><fmt:message key="button.revert"/></a>
            </c:if>
            <c:if test="${rev eq lastRevision}">
                (<fmt:message key="versions.last"/>)
            </c:if>
            ]
        </c:when>

        <c:otherwise>
            <c:choose>
                <c:when test="${empty param.revision and rev eq lastRevision}">
                     <c:out value="${rev}"/> (<fmt:message key="versions.last"/>)
                </c:when>
                <c:otherwise>

                    <a href="<c:url value='/main/${param.modelName}/view'>
                           <c:param name='id' value='${model.id}'/>
                           <c:param name='revision' value='${rev}'/>
                       </c:url>">${rev}</a>

                    <c:if test="${rev eq lastRevision}">
                        (<fmt:message key="versions.last"/>)
                    </c:if>

                </c:otherwise>
            </c:choose>

            <c:if test="${rev ne lastRevision}">
               |
            </c:if>
        </c:otherwise>
    </c:choose>

</c:forEach>


