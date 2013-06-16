<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<tiles:importAttribute name="model" />
<tiles:importAttribute name="mode" />
<tiles:importAttribute name="next" />
<tiles:importAttribute name="prev" />

 <c:choose>
     <c:when test="${mode eq 'widget' or mode eq 'icon'}">

         <c:set var="target" value="_blank"/>
     </c:when>
     <c:otherwise>
         <c:set var="target" value="_self"/>

     </c:otherwise>
 </c:choose>


<div  class="row">
    <div class="column grid-4">

        <a href="<c:url value="/main/paste/list"/>" target="${target}" title="Go back to list">list</a> |
        <a href="<c:url value="/main/paste/edit/${model.id}"/>" target="${target}" title="Edit paste">edit</a> |
        <a href="<c:url value="/main/paste/xml/${model.id}"/>" target="${target}" title="View as XML">xml</a> |
        <a href="<c:url value="/main/paste/${model.id}.json"/>" target="${target}" title="View as JSON">json</a> |
        <a href="<c:url value="/main/paste/plain/${model.id}"/>" target="${target}" title="View as plain text">plain</a> |


    </div>

    <div class="column grid-4">

        <c:if test="${mode ne 'icon'}">
            <a id="ctrlc_link" data-clipboard-target="pasteTextPlain" href="javascript:void(0);" title="Copy to clipboard" ><img src="<c:url value='/images/ctrlc.png'/>"/></a> |
        </c:if>


    </div>

    <div class="column grid-4 right">

        <c:if test="${prev}">
            <a href="<c:url value="/main/paste/${model.id-1}"/>" target="${target}" title="Previous paste">&#8592;</a>
        </c:if>

        <span class="f-h4">${model.id}</span>

        <c:if test="${next}">
            <a href="<c:url value="/main/paste/${model.id+1}"/>" target="${target}" title="Next paste">&#8594;</a>
        </c:if>


    </div>
</div>




