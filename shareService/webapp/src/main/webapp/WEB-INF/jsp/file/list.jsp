<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<c:if test="${pageItems.nrOfElements > 0}">
<div class="row" style="margin: 0; padding: 0;">                

    <div class="col-md-6 centered text-center"  > 
        <c:if test="${pageItems.pageCount > 1}">  
            <jsp:include
                page="/WEB-INF/jsp/templates/common/pagination.jsp">
                <jsp:param name="modelName" value="file" />
            </jsp:include>
        </c:if>
    </div>
    <div class="col-md-4" style="padding-top: 1em;"  >
        <jsp:include
            page="/WEB-INF/jsp/templates/common/sort.jsp">
            <jsp:param name="modelName" value="file" />
        </jsp:include>
    </div>
    <div class="col-md-2 centered" style="padding-top: 1em;"  >

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
    
      