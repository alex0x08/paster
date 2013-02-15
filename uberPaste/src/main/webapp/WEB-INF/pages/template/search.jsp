<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="url" value='/main/paste/list/search' />

<form:form action="${url}"
           modelAttribute="query"
           method="POST" >
    <form:errors path="*" cssClass="errorblock" element="div"/>
    <form:input path="query" name="query" id="pquery" style="display: inline;" size="100em;" />
    <input name="submit" type="submit" value="Search"  />
    <span style="font-size: 12px;display:block;padding-bottom: 10px;" >
            Query samples:
         "<a href="<c:url value='/main/paste/list/search?query=id:1'/>">id:1</a>",
         "<a href="<c:url value='/main/paste/list/search?query=name:test'/>">name:test</a>" ,

        <c:url  var="lm_sample_url" value="/main/paste/list/search">
            <c:param name='query' value='lastModified:["30.01.2013" TO "31.01.2013"]'/>
        </c:url>
        "<a href='<c:out value="${lm_sample_url}"/>'> lastModified:["30.01.2013" TO "31.01.2013"]</a>"


    </span>
</form:form>
