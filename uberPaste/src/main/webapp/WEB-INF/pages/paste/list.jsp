<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<h1><span class="i">/</span> Pastaman</h1>

<jsp:include page="/WEB-INF/pages/template/search.jsp"/>

 <div style="float:right; background-color: gold; " >
          <a href="<c:url value="/main/paste/list/limit/5"/>">5</a> |
          <a href="<c:url value="/main/paste/list/limit/10"/>">10</a> |
          <a href="<c:url value="/main/paste/list/limit/50"/>">50</a> |
          <a href="<c:url value="/main/paste/list/limit/100"/>">100</a>
  </div>
    
   <div class="paging" style="margin: auto; text-align: center;" >

    <c:if test="${!pageItems.firstPage}">
      <a href="<c:url value="/main/paste/list/prev"/>">&#8592;</a>
    </c:if>
    <c:if test="${pageItems.pageCount > 1}">
        <c:forEach begin="1" end="${pageItems.pageCount}" step="1" var="pnumber">
             <c:choose>
                 <c:when test="${pnumber==pageItems.page+1}">
                     ${pnumber}
                 </c:when>
                 <c:otherwise>
                     <small>
                         <a href="<c:url value='/main/paste/list/${pnumber}'/>">${pnumber}</a>
                     </small>
                 </c:otherwise>
             </c:choose>
             &nbsp;
         </c:forEach>
        
    </c:if>
        
    <c:if test="${!pageItems.lastPage}">
        <a href="<c:url value='/main/paste/list/next'/>">&#8594;</a>
    </c:if>

</div>


<div id="pastas">
    <c:forEach var="paste" items="${pageItems.pageList}" varStatus="status">
        <c:set var="priorTitle"><fmt:message key="${paste.priority.name}"/></c:set>

        <div>
                <a class="i ${paste.priority.cssClass}" style="font-size:2em;"
                   title="<c:out value="${paste.id}"/>: ${priorTitle}"
                   href="<c:url value='/main/paste/list/search?query=priority:${paste.priority.code}'/>">/</a>


            <a href="<c:url value="/main/paste/${paste.id}"></c:url>"><c:out value="${paste.name}" escapeXml="true"  /></a>

            ( <c:forEach var="tag" items="${paste.tags}" varStatus="loopStatus">
            <a href="<c:url value='/main/paste/list/search?query=tags:${tag}'/>"><c:out value=" ${tag}"/></a>
            <c:if test="${!loopStatus.last}"> , </c:if>
        </c:forEach> )

           <small>

               <c:if test="${paste.owner ne null}">
                   ,  by
                       <a href="http://ru.gravatar.com/site/check/${paste.owner.username}" title="GAvatar">
                           <img style="vertical-align: middle; " src="<c:out value='http://www.gravatar.com/avatar/${paste.owner.avatarHash}?s=32'/>"/>
                       </a>

                <span style="display: inline;  ">
                        <a title="Contact ${paste.owner.name}"  href="mailto:${paste.owner.username}?subject=${paste.name}">
                            <c:out value="${paste.owner.name}" /></a>
                </span>


               </c:if>

               ,<kc:prettyTime date="${paste.lastModified}" locale="${pageContext.response.locale}"/>
           </small>


            <div style="padding: 1em;">
            <c:out value="${paste.title}"  escapeXml="true"/>
        </div>

    </div>
        
    </c:forEach>

</div>

<c:if test="${pageItems.nrOfElements == 0}">
    <center>
        No pastas
    </center>
</c:if>    
    
    
