<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:url var="url" value='/main/file/list/search' />

<div class="btn-group" style="min-width: 5em;">

    <form:form action="${url}" commandName="query" 
               class="form-inline"  
               method="POST" >
        <form:errors path="*" cssClass="errorblock" element="span"/>

        <div class="form-group">

            <fmt:message var="searchPlaceholder" key='search.placeholder.default'/>

            <form:input path="query" type="text" placeholder="${searchPlaceholder}" />
        </div>
        

            <sec:authorize ifAnyGranted="ROLE_ADMIN">
                <div class="form-group">

                    <form:select path="userId" cssClass="btn" >
                        <form:options  items="${availableUsers}" itemValue="id" itemLabel="name"  />
                    </form:select>
                </div>
            </sec:authorize>
                  

            <button type="submit" class="btn btn-sm btn-info">
                <span class="glyphicon glyphicon-search" title="<fmt:message key="button.search"/>"></span>
            </button>
        

    </form:form>     
</div>
<div class="btn-group" style="min-width: 5em;">

    <div  style="font-size: 11px;margin: 0.0em;padding:0.0em;padding-left: 0.5em;line-height: 1.8em;">

        <fmt:message key="search.query.samples"/>:

        "<a rel="tooltip" data-placement="bottom" data-toggle="tooltip" title="<fmt:message key='search.byid.tooltip'/>" href="<c:url value='/main/file/list/search?query=id:1'/>">id:1</a>",

        "<a rel="tooltip" data-placement="bottom" data-toggle="tooltip" title="<fmt:message key='search.byname.tooltip'/>" href="<c:url value='/main/file/list/search?query=name:test'/>">name:test</a>" ,
        <br/> 
        "<a rel="tooltip" data-placement="bottom" data-toggle="tooltip" title="<fmt:message key='search.bytype.tooltip'/>" href="<c:url value='/main/file/list/search?query=type:pdf'/>">type:pdf</a>" ,

        "<a rel="tooltip" data-placement="bottom" data-toggle="tooltip" title="<fmt:message key='search.byowner.tooltip'/>" href="<c:url value='/main/file/list/search?query=ownerName:alex'/>">ownerName:alex</a>" ,

        <c:url  var="lm_sample_url" value="/main/file/list/search">
            <c:param name='query' value='lastModified:["201303" TO "201304"]'/>
        </c:url>
        "<a rel="tooltip" data-placement="bottom" data-toggle="tooltip" title="<fmt:message key='search.bylastmodified.tooltip'/>" href='<c:out value="${lm_sample_url}"/>'> lastModified:["201303" TO "201304"]</a>"

    </div>

</div>
