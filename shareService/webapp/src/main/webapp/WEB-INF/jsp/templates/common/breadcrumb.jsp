<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<ul class="breadcrumb">
    
    <li><a href="<c:url value='/main/welcome'/>"><fmt:message key="site.title"/></a> <span class="divider">/</span></li>
    <li><a href="<c:url value='/main/file/list'/>"><fmt:message key="files.title"/></a> <span class="divider">/</span></li>
    <li class="active">
        <c:choose>
            <c:when test="${model.blank}">
                <fmt:message key="file.upload.title"/>
            </c:when>
            <c:otherwise>
               <img style="text-align: left; display: inline; " src="<c:url value='/images/mime/${model.icon}'/>"/>
               <c:out value="${model.name}"/>
            </c:otherwise>
        </c:choose>
    </li>
</ul>
