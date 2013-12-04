<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<c:if test="${pageItems.nrOfElements > 0}">
<div class="row" >                
    <div class="col-md-6"  > 
        <c:if test="${pageItems.pageCount > 1}">  
            <jsp:include
                page="/WEB-INF/jsp/templates/common/pagination.jsp">
                <jsp:param name="modelName" value="file" />
            </jsp:include>
        </c:if>
    </div>
    <div class="col-md-4"   >
        <jsp:include
            page="/WEB-INF/jsp/templates/common/sort.jsp">
            <jsp:param name="modelName" value="file" />
        </jsp:include>
        <jsp:include
            page="/WEB-INF/jsp/templates/common/page-size.jsp">
            <jsp:param name="modelName" value="file" />
        </jsp:include>
    </div>
</div>
    
</c:if>

<div class="row">
    <div id="gallery" class="col-md-12">      
    <c:forEach var="obj" items="${pageItems.pageList}" varStatus="status">
        <c:set var="model" value="${obj}" scope="request"></c:set>
        <jsp:include page="/WEB-INF/jsp/templates/common/preview.jsp" />
    </c:forEach>

    <c:if test="${pageItems.nrOfElements == 0}">
        <jsp:include page="/WEB-INF/jsp/templates/common/noFiles.jsp" />
    </c:if>
    </div>
</div>
    
      