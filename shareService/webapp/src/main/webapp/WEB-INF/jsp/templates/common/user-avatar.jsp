<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<c:choose>
    <c:when test="${model.avatarType eq 'FILE'}">

        <img src="data:image/png;base64,${param.size eq 'full' ? model.avatar.picture : model.avatar.icon}" 
             alt="<c:out value='${model.name}'/>" />  
    </c:when>
    <c:when test="${model.avatarType eq 'GAVATAR'}">

        <c:choose>

            <c:when test="${gavatarEnabled}">
                <img src="<c:out value='${gavatarlUrl}/${model.avatarHash}'/>?s=${param.size eq 'full' ? 128 : 16}"/>

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
