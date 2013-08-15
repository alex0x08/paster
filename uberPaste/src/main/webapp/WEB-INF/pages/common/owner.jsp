<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />


<c:choose>
    <c:when test="${not empty model.owner}">
        
        <a href="http://ru.gravatar.com/site/check/${model.owner.email}" title="GAvatar">
        <img style="vertical-align: middle;padding-bottom: 2px;" src="
             <c:url value='http://www.gravatar.com/avatar/${model.owner.avatarHash}'>
                 <c:param name='s' value='32'/>
                 <c:param name='d' value='monsterid'/>
             </c:url>"/>
    </a>
                <span style="display: inline;  ">
                        <a title="Contact ${model.owner.name}"  href="mailto:${model.owner.username}?subject=<c:out value='${model.name}' escapeXml="true"/>"><c:out value="${model.owner.name}" /></a>
                </span>
  
        
    </c:when>
    <c:otherwise>
        <span style="font-size: 2em;" class="i" title="Anonymous">x</span>
    </c:otherwise>

</c:choose>
             