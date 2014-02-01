<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<tiles:importAttribute name="model" />
<tiles:importAttribute name="mode" />
<tiles:importAttribute name="next" ignore="true" />
<tiles:importAttribute name="prev" ignore="true" />

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

        <a href="<c:url value="/main/paste/list"/>" target="${target}"
           title="<fmt:message key="paste.list.title"/>"><span style="font-size: larger;" class="i">(</span></a> |
        <a href="<c:url value="/main/paste/edit/${model.id}"/>" target="${target}"
           title="<fmt:message key="button.edit"/>"><span style="font-size: larger;" class="i">E</span></a>


    </div>

    <div class="column grid-4">

        <a href="<c:url value="/main/paste/xml/${model.id}"/>" target="${target}" title="View as XML">
            <img src="<c:url value='/main/static/${appVersion}/images/xml.png'/>" title="xml" alt="xml"/>
        </a> |
        <a href="<c:url value="/main/paste/${model.id}.json"/>" target="${target}" title="View as JSON">
            <img src="<c:url value='/main/static/${appVersion}/images/json.png'/>" title="json" alt="json"/>
        </a> |
        <a href="<c:url value="/main/paste/plain/${model.id}"/>" target="${target}" title="View as plain text">
            <span style="font-size: larger;" class="i">k</span>
        </a> |


        <c:if test="${mode ne 'icon'}">
            <a id="ctrlc_link" data-clipboard-target="pasteTextPlain" href="javascript:void(0);"
               title="Copy to clipboard" ><img src="<c:url value='/main/static/${appVersion}/images/ctrlc.png'/>"/></a>
        </c:if>


    </div>

    <div class="column grid-4 right">

        <c:if test="${not empty prev}">
            <a href="<c:url value="/${prev.id}"/>" target="${target}" title="<fmt:message key="button.prev"/>">&#8592;</a>
        </c:if>

        <span class="f-h4">${model.id}</span>

        <c:if test="${not empty next}">
            <a href="<c:url value="/${next.id}"/>" target="${target}" title="<fmt:message key="button.next"/>">&#8594;</a>
        </c:if>


    </div>
</div>




