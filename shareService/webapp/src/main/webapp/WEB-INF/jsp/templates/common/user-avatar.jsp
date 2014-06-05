<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<c:choose>
    <c:when test="${usermodel.avatarType eq 'FILE'}">

        <img src="data:image/png;base64,${param.size eq 'full' ? usermodel.avatar.picture : usermodel.avatar.icon}" 
             alt="<c:out value='${usermodel.name}'/>" />  
    </c:when>
    <c:when test="${usermodel.avatarType eq 'GAVATAR'}">

        <c:choose>

            <c:when test="${gavatarEnabled}">
                <img src="<c:out value='${gavatarlUrl}/${usermodel.avatarHash}'/>?s=${param.size eq 'full' ? 128 : 16}"/>

            </c:when>
            <c:otherwise>
                <span class="glyphicon glyphicon-remove-circle"></span>
            </c:otherwise>

        </c:choose>


    </c:when>
    <c:otherwise>
        <span class="glyphicon glyphicon-user"></span>
    </c:otherwise>
</c:choose>
