<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="url" value='/main/paste/list/search' />

<form:form action="${url}"
           modelAttribute="query"
           method="POST" >
<div class="row">
    <div class="column grid-9" style="padding-top: 0.5em;margin-right: 0;">
        <form:input path="query" name="query" id="pquery" cssStyle="width:97%;margin-right:0.5em;min-width: 5em;" autocomplete="true"  />
     </div>
    <div class="column grid-3" style="">
        <input name="submit" type="submit" value="Search" style="text-align:left;min-width: 5em;max-width: 5em;"  />
        <form:errors path="query" cssClass="error" element="div"/>
    </div>
</div>
<div class="row">
    <div class="column grid-10">

            <span style="font-size: 12px;display:block;padding-bottom: 10px;" >
            Query samples:
         "<a href="<c:url value='/main/paste/list/search?query=id:1'/>">id:1</a>",
         "<a href="<c:url value='/main/paste/list/search?query=name:test'/>">name:test</a>" ,

        <c:url  var="lm_sample_url" value="/main/paste/list/search">
            <c:param name='query' value='lastModified:["30.01.2013" TO "31.01.2013"]'/>
        </c:url>
        "<a href='<c:out value="${lm_sample_url}"/>'> lastModified:["30.01.2013" TO "31.01.2013"]</a>"
 </span>

    </div>
</div>


</form:form>
