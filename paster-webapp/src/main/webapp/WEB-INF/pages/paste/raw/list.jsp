<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<%--

    Render paged list of pastas without template (raw)

--%>

<div id="paste_list_${param.page}" class="page">
    <h4 class="f-h4">
        <fmt:formatDate pattern="${dateTimePattern}" var="fromDate" value="${pageItems.firstElement.lastModified}" />
        <fmt:formatDate pattern="${dateTimePattern}" var="toDate" value="${pageItems.lastElement.lastModified}" />

        <a href="<c:url value="/main/paste/list/${sourceType}/${param.page}"/>">

            <fmt:message key='common.list.page'>
                <fmt:param value="${param.page}"/>
                <fmt:param value="${fromDate}"/>
                <fmt:param value="${toDate}"/>
                <fmt:param value="${pageItems.elementsOnPage}"/>
            </fmt:message>

        </a>
    </h4>


    <c:forEach var="paste" items="${pageItems.pageList}" varStatus="status">

        <c:choose>
            <c:when test="${paste['class'].name eq 'uber.paste.model.Paste'}">

                <c:set var="priorTitle"><fmt:message key="${paste.priority.name}"/></c:set>

                    <div class="row">
                        <div class="column grid-16">
                        <c:if test="${paste.sticked}">
                            <span class="i" title="Paste sticked">]</span>
                        </c:if>

                        <a class="i ${paste.priority.cssClass}" style="font-size:2em;"
                           title="<c:out value="${paste.id}"/>: ${priorTitle}. Click to search with same priority."
                           href="<c:url value='/main/paste/list/search?query=priority:${paste.priority.code}'/>">/</a>

                        <a href="<c:url value="/main/paste/${paste.id}"></c:url>" title="Click to view paste vol. ${paste.id}">
                            <span  class="pasteTitle"><c:out value="${paste.name}" escapeXml="true"  /></span>
                        </a>
                    </div>
                </div>

                <div class="row">


                    <a class="pastePreviewLink" href="<c:url value="/${paste.id}"></c:url>" pasteId="${paste.id}" title="Click to view paste vol. ${paste.id}">

                        <c:choose>
                            <c:when test="${not empty paste.thumbImage}">
                                <div class="column grid-4" >


                                    <c:url value='/main/resources/${appId}/t/${paste.lastModified.time}/${paste.thumbImage}' var="thumbUrl">
                                    </c:url>


                                    <img src="${thumbUrl}" width="300" height="200"  />

                                </div>

                                <c:set var="currentRowSize" value="12"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="currentRowSize" value="16"/>
                            </c:otherwise>
                        </c:choose>
                    </a>

                    <div class="column grid-${currentRowSize}" >

                        <div class="row">
                            <div class="column grid-10">
                                <div class="pasteTitle" style="padding: 1em;">
                                    <a class="listLinkLine" href="<c:url value="/main/paste/${paste.id}"></c:url>" pasteId="${paste.id}" title="Click to view paste vol. ${paste.id}"><c:out value="${paste.title}"  escapeXml="true"/></a>
                                    </div>
                                </div>
                            </div>

                            <div class="row">

                                <div class="column grid-10">

                                <tiles:insertDefinition name="/common/tags" >
                                    <tiles:putAttribute name="model" value="${paste}"/>
                                    <tiles:putAttribute name="modelName" value="paste"/>
                                </tiles:insertDefinition>

                                <tiles:insertDefinition name="/common/commentCount" >
                                    <tiles:putAttribute name="model" value="${paste}"/>
                                    <tiles:putAttribute name="modelName" value="paste"/>
                                </tiles:insertDefinition>

                                <small>
                                    <tiles:insertDefinition name="/common/owner" >
                                        <tiles:putAttribute name="model" value="${paste}"/>
                                        <tiles:putAttribute name="modelName" value="paste"/>
                                    </tiles:insertDefinition>

                                    ,<kc:prettyTime date="${paste.lastModified}" locale="${pageContext.response.locale}"/>
                                </small>

                                <sec:authorize access="hasRole('ROLE_ADMIN')">
                                    |  <a href="<c:url value='/main/paste/delete'><c:param name="id" value="${paste.id}"/> </c:url>"><fmt:message key='button.delete'/></a>
                                </sec:authorize>

                            </div>
                        </div>

                    </div>

                </div>
            </c:when>
        </c:choose>
    </c:forEach>

</div>


