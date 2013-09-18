<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<ol class="breadcrumb">
    
    <li><a href="<c:url value='/main/welcome'/>"><fmt:message key="site.title"/></a> </li>
    <li><a href="<c:url value='/main/file/list'/>"><fmt:message key="files.title"/></a> </li>
    <li class="active">
        <c:choose>
            <c:when test="${model.blank}">
                <fmt:message key="file.upload.title"/>
            </c:when>
            <c:otherwise>
               <img style="text-align: left; display: inline; " src="<c:url value='/main/static/${appVersion}/images/mime/${model.icon}'/>"/>
               <c:out value="${model.name}"/>
            </c:otherwise>
        </c:choose>
    </li>
</ol>
