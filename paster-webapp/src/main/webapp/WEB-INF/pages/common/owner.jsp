<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


        <%--

        Renders paste's owner
        
        --%>    

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />


<c:choose>
    <c:when test="${not empty model.author and model.author ne currentUser}">
     
        <span style="display: inline;  ">
            <a title="Contact ${model.author}"
            href="mailto:${model.author}?subject=<c:out value='${model.text}' escapeXml="true"/>"><c:out value="${model.author}" /></a>
        </span>


    </c:when>
     <c:when test="${not empty model.author and model.author eq currentUser}">
         <span style="display: inline;  ">
             <fmt:message key="user.you"/>
         </span>
     </c:when>
    <c:otherwise>
        <span style="font-size: 2em;" class="i" title="<fmt:message key='user.anonymous'/>">x</span>
    </c:otherwise>

</c:choose>
