<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<div id="notice"></div>

<c:url var="url" value='/main/file/list' />

    <div class="btn-group pull-right" >

        <a class="btn dropdown-toggle btn-danger" data-toggle="dropdown" href="#">
            <c:out value="${pageItems.pageSize}"/> 
            <span class="caret"></span>
        </a>

            <ul class="dropdown-menu" id="pageDropdown" >

            <c:forTokens items="5,10,50,100" delims="," var="pg" >
                <li >
                    <a href="<c:url value="/main/file/list/limit/${pg}">
                       </c:url>"><c:out value="${pg}"/></a>
                </li>
            </c:forTokens>   


        </ul>

    </div>


<c:if test="${pageItems.pageCount > 1}">

    <div class="pagination ">
        <ul>

            <c:if test="${!pageItems.firstPage}">
                <li>
                    <a href="<c:url value="/main/file/list/prev"/>" 
                       title="<fmt:message key="button.prev"/>"><b>&larr; </b></a>
                </li>
            </c:if>

            <c:forEach begin="1" end="${pageItems.pageCount}" step="1" var="pnumber">
                <c:choose>
                    <c:when test="${pnumber==pageItems.page+1}">
                        <li class="disabled"><a href="#">${pnumber}</a></li>                     
                    </c:when>
                    <c:otherwise>
                        <li >
                            <a title="<fmt:message key="list.page.goto"><fmt:param value="${pnumber}"/></fmt:message>" href="<c:url value="/main/file/list/${pnumber}">
                               </c:url>">${pnumber}</a>
                        </li>

                    </c:otherwise>
                </c:choose>

            </c:forEach>

            <c:if test="${!pageItems.lastPage}">
                <li >
                    <a href="<c:url value="/main/file/list/next">
                       </c:url>" title="<fmt:message key="button.next"/>"><b> &rarr;</b></a>
                </li>
            </c:if>

        </ul>
    </div>

</c:if>


<div id="gallery">
    <c:forEach var="obj" items="${pageItems.pageList}" varStatus="status">

        <c:set var="model" value="${obj}" scope="request"></c:set>

        <jsp:include page="/WEB-INF/jsp/templates/common/preview.jsp" />

    </c:forEach>

</div>


<c:if test="${pageItems.nrOfElements == 0}">
        <jsp:include page="/WEB-INF/jsp/templates/common/noFiles.jsp" />

</c:if>

