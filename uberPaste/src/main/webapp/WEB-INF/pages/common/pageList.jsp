<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<tiles:importAttribute name="listMode" />
<tiles:importAttribute name="pageItems" />
<tiles:importAttribute name="sortDesc" />

<c:if test="${listMode eq 'search'}">
    <tiles:importAttribute name="result" />
</c:if>


<%-- processing elements per page and sort setup --%>

<div class="paging"  >
    <c:choose>
        <c:when test="${listMode eq 'search' }">

            <c:forEach var="page" items="${pageSet}" varStatus="loopStatus">

                <c:choose>
                    <c:when test="${pageItems.pageSize eq page}">
                        <span style="font-size: larger; "><c:out value="${page}"/> </span>
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value="/main/paste/list/search/${result}/limit/${page}"/>">${page}</a>
                    </c:otherwise>
                </c:choose>

                <c:if test="${!loopStatus.last}"> | </c:if>
            </c:forEach>


        </c:when>
        <c:when test="${listMode eq 'list' }">

            <c:forEach var="page" items="${pageSet}" varStatus="loopStatus">

                <c:choose>
                    <c:when test="${pageItems.pageSize eq page}">
                        <span style="font-size: larger; "><c:out value="${page}"/> </span>
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value="/main/paste/list/${sourceType}/limit/${page}"/>">${page}</a>
                    </c:otherwise>
                </c:choose>

                <c:if test="${!loopStatus.last}"> | </c:if>
            </c:forEach>

                    <span style="padding-left: 1em;">
                  
            <c:choose>
                <c:when test="${pageItems.sort.ascending == true}">
                    <span style="font-size: larger; "> &#x2191; </span>
                    <a href="<c:url value="/main/paste/list/${sourceType}/older"/>"> &#8595;</a>
                </c:when>
                <c:otherwise>
                    <span style="font-size: larger; "> &#8595; </span>
                    <a href="<c:url value="/main/paste/list/${sourceType}/earlier"/>">&#x2191; </a>
                </c:otherwise>
            </c:choose>
        </span>


        </c:when>

    </c:choose>


</div>

<%-- processing page list --%>
<div class="paging" style="margin: auto; text-align: center; background-color: #eeeeee; " >

    <c:choose>
        <%-- for search results --%>
        <c:when test="${listMode eq 'search' }">

            <c:if test="${!pageItems.firstPage}">
                <a href="<c:url value="/main/paste/list/search/${result}/prev"/>">&#8592;</a>
            </c:if>
            <c:if test="${pageItems.pageCount > 1}">
                <c:forEach begin="1" end="${pageItems.pageCount}" step="1" var="pnumber">
                    <c:choose>
                        <c:when test="${pnumber==pageItems.page+1}">
                            <c:out value="${pnumber}"/>
                        </c:when>
                        <c:otherwise>
                            <small>
                                <a href="<c:url value='/main/paste/list/search/${result}/${pnumber}'/>">${pnumber}</a>
                            </small>
                        </c:otherwise>
                    </c:choose>
                    &nbsp;
                </c:forEach>
            </c:if>

            <c:if test="${!pageItems.lastPage}">
                <a href="<c:url value='/main/paste/list/search/${result}/next'/>">&#8594;</a>
            </c:if>

        </c:when>
        <c:when test="${listMode eq 'list'}">
            <%-- for list --%>


            
            <c:if test="${pageItems.pageCount > 1}">
                <c:forEach begin="1" end="${pageItems.pageCount}" step="1" var="pnumber">
                    <c:choose>
                        <c:when test="${pnumber==pageItems.page+1}">
                            <c:out value="${pnumber}"/>
                        </c:when>
                        <c:otherwise>
                            <small>
                                <a href="<c:url value='/main/paste/list/${sourceType}/${pnumber}'/>">${pnumber}</a>
                            </small>
                        </c:otherwise>
                    </c:choose>
                    &nbsp;
                </c:forEach>

            </c:if>

                    <br/>
             <c:if test="${!pageItems.firstPage}">
                <a href="<c:url value="/main/paste/list/${sourceType}/prev"/>">&#8592;</a>
            </c:if>           
                <span style="font-weight: bold"><c:out value='${pageItems.page+1}'/></span>
            <c:if test="${!pageItems.lastPage}">
                <a href="<c:url value='/main/paste/list/${sourceType}/next'/>">&#8594;</a>
            </c:if>


        </c:when>
    </c:choose>



</div>


