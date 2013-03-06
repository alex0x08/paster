<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class="row">
    <div class="column grid-1" style="text-align:right;padding-right: 0;margin-top: -1em;" >
        <a class="mainLinkLine" href="<c:url value="/main/paste/new"></c:url>" title="<fmt:message key='paste.create.new'/>"><span class="i" style="font-size: 4em;">/</span></a>
    </div>
    <div class="column grid-12" style="padding-left: 0;margin-left: -1em;" >
         <jsp:include page="/WEB-INF/pages/template/search.jsp"/>
     </div>
</div>

<div class="row">
    <div class="column grid-12">
       from
    <c:forEach var="source" items="${availableSourceTypes}" varStatus="loopStatus">

        <c:choose>
            <c:when test="${source.codeLowerCase eq sourceType}">
                <span style="font-size: 14px; "><c:out value="${source.code}"/> </span>
            </c:when>
            <c:otherwise>
                <a href="<c:url value="/main/paste/list/${source.codeLowerCase}"/>"><fmt:message key="${source.name}"/></a>
            </c:otherwise>
        </c:choose>

        <c:if test="${!loopStatus.last}"> | </c:if>
    </c:forEach>
   </div>
 </div>

<div class="row">
    <div class="column grid-12">

        <div class="paging" style="margin: auto; text-align: center;" >

            <c:if test="${!pageItems.firstPage}">
                <a href="<c:url value="/main/paste/list/${sourceType}/prev"/>">&#8592;</a>
            </c:if>
            <c:if test="${pageItems.pageCount > 1}">
                <c:forEach begin="1" end="${pageItems.pageCount}" step="1" var="pnumber">
                    <c:choose>
                        <c:when test="${pnumber==pageItems.page+1}">
                            <c:out value="${pnumber}"/>
                        </c:when>
                        <c:otherwise>
                            <small>
                                <a href="<c:url value='/main/paste/list/${sourceType}/${pnumber}'/>">${pnumber}</a>
                            </small>
                        </c:otherwise>
                    </c:choose>
                    &nbsp;
                </c:forEach>

            </c:if>

            <c:if test="${!pageItems.lastPage}">
                <a href="<c:url value='/main/paste/list/${sourceType}/next'/>">&#8594;</a>
            </c:if>

        </div>


        <div class="paging" style="margin: auto; text-align: center;float:right;  " >

            <c:forEach var="page" items="${pageSet}" varStatus="loopStatus">

                <c:choose>
                    <c:when test="${pageItems.pageSize eq page}">
                        <span style="font-size: larger; "><c:out value="${page}"/> </span>
                    </c:when>
                    <c:otherwise>
                        <a href="<c:url value="/main/paste/list/${sourceType}/limit/${page}"/>">${page}</a>
                    </c:otherwise>
                </c:choose>

                <c:if test="${!loopStatus.last}"> | </c:if>
            </c:forEach>

                    <span style="padding-left: 3em;">
            <c:choose>
                <c:when test="${sortDesc}">
                    <span style="font-size: larger; "> &#x2191; </span>
                    <a href="<c:url value="/main/paste/list/${sourceType}/older"/>"> &#8595;</a>
                </c:when>
                <c:otherwise>
                    <span style="font-size: larger; "> &#8595; </span>
                    <a href="<c:url value="/main/paste/list/${sourceType}/earlier"/>">&#x2191; </a>
                </c:otherwise>
            </c:choose>
        </span>

        </div>



    </div>
</div>


<div class="row">
    <div class="column grid-14">


    <div id="pastas">
    <c:forEach var="paste" items="${pageItems.pageList}" varStatus="status">
        <c:set var="priorTitle"><fmt:message key="${paste.priority.name}"/></c:set>

        <div>
            <c:if test="${paste.sticked}">
                <span class="i" title="Paste sticked">]</span>
            </c:if>

                <a class="i ${paste.priority.cssClass}" style="font-size:2em;"
                   title="<c:out value="${paste.id}"/>: ${priorTitle}. Click to search with same priority."
                   href="<c:url value='/main/paste/list/search?query=priority:${paste.priority.code}'/>">/</a>


            <a href="<c:url value="/main/paste/${paste.id}"></c:url>" title="Click to view paste vol. ${paste.id}">
             <span  class="pasteTitle"><c:out value="${paste.name}" escapeXml="true"  /></span>
            </a>

            <tiles:insertDefinition name="common/tags" >
                <tiles:putAttribute name="model" value="${paste}"/>
                <tiles:putAttribute name="modelName" value="paste"/>
            </tiles:insertDefinition>

            <tiles:insertDefinition name="common/commentCount" >
                <tiles:putAttribute name="model" value="${paste}"/>
                <tiles:putAttribute name="modelName" value="paste"/>
            </tiles:insertDefinition>


            <small>
               <tiles:insertDefinition name="common/owner" >
                    <tiles:putAttribute name="model" value="${paste}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                </tiles:insertDefinition>


               ,<kc:prettyTime date="${paste.lastModified}" locale="${pageContext.response.locale}"/>
           </small>


            <div class="pasteTitle" style="padding: 1em;">
                <a class="listLinkLine" href="<c:url value="/main/paste/${paste.id}"></c:url>" title="Click to view paste vol. ${paste.id}"><c:out value="${paste.title}"  escapeXml="true"/></a>
        </div>

    </div>
        
    </c:forEach>

</div>

<c:if test="${pageItems.nrOfElements == 0}">
    <center>
        No pastas
    </center>
</c:if>

    </div>
</div>


<script type="text/javascript">

    window.addEvent('domready', function() {

        document.body.getElements('.pasteTitle').each(function(el, i)
        {
           // alert(el);
            el.set(
                    'html', el.get('html').replace(/\[result[^\]]*\]([\s\S]*?)\[\/result\]/gi,"<span style='background-color: #e3e658; '>$1</span>")
            );


           // el.set("html", 'FUCK');
          //  alert(el.text);
            // do something
        });
    });
</script>
