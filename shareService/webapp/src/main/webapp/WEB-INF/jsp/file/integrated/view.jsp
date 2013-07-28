<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:url value="/act/download" var="downloadUrl">
    <c:param name="id" value="${model.uuid}"/>
</c:url>


<c:choose>
    <c:when test="${not empty param.integrationMode}">
        <c:set var="viewMode" value="${param.integrationMode}"/>
    </c:when>
    <c:otherwise>
        <c:set var="viewMode" value="widget"/>        
    </c:otherwise>
</c:choose>

<div class="row">  
    <div class="span12 offset1" >


        <c:if test="${viewMode eq 'integrated'}">

            <div class="form-buttons pull-left">
                <a class="btn" href="<c:url value='/main/file/integrated/list/${model.integrationCode}'/>">&#8592; Back to list</a>
            </div>

        </c:if>


        <jsp:include page="/WEB-INF/jsp/templates/common/preview.jsp" >
            <jsp:param name="downloadLink" value="download"/>
            <jsp:param name="integrationMode" value="${viewMode}"/>  
            <jsp:param name="targetNew" value="1"/>
            <jsp:param name="menuTargetNew" value="1"/>

        </jsp:include>


        <c:if test="${viewMode eq 'integrated'}">

            <div class="form-buttons pull-left">

                <a class="btn btn-primary btn-large" target="_blank" href="<c:out value='${downloadUrl}'/>">
                    <img style="display: inline;" src="<c:url value='/images/download.png'/>"/>
                    <fmt:message key="file.download"/></a>

            </div>

        </c:if>
        
    </div>

</div>
      
        
        <c:if test="${viewMode eq 'integrated'}">

          
            <jsp:include
                page="/WEB-INF/jsp/templates/common/comments.jsp">
                <jsp:param name="model" value="${model}" />
                <jsp:param name="modelName" value="file" />
            </jsp:include>

        </c:if>



  