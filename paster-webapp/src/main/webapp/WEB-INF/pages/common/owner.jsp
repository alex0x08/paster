<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />


<c:choose>
    <c:when test="${not empty model.owner and model.owner ne currentUser}">
        
        
      
        <span style="display: inline;  ">
            <a title="Contact ${model.owner.name}"  href="mailto:${model.owner.username}?subject=<c:out value='${model.text}' escapeXml="true"/>"><c:out value="${model.owner.name}" /></a>
        </span>


    </c:when>
     <c:when test="${not empty model.owner and model.owner eq currentUser}">
         <span style="display: inline;  ">
             <fmt:message key="user.you"/>
         </span>
     </c:when>
    <c:otherwise>
        <span style="font-size: 2em;" class="i" title="<fmt:message key='user.anonymous'/>">x</span>
    </c:otherwise>

</c:choose>
