<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:url var="url" value='/main/file/list/search' />

    
<form:form action="${url}" commandName="query" 
           class="navbar-form pull-left"  
           method="POST" >
    <form:errors path="*" cssClass="errorblock" element="span"/>

    <div class="input-append">
            
            <fmt:message var="searchPlaceholder" key='search.placeholder.default'/>
            
            <form:input path="query" type="text" placeholder="${searchPlaceholder}" />

            <sec:authorize ifAnyGranted="ROLE_ADMIN">
                <form:select path="userId" cssClass="btn" >
                    <form:options  items="${availableUsers}" itemValue="id" itemLabel="name"  />
                </form:select>
            </sec:authorize>

            <button type="submit" class="btn">
                <i class="icon-search" title="<fmt:message key="button.search"/>"></i>
            </button>
            </div>
                       
</form:form>     

<div class="btn-group" style="min-width: 5em;">
                
                <span  style="font-size: 11px;">
                  
                   <fmt:message key="search.query.samples"/>:
                
                "<a rel="tooltip" data-placement="bottom" data-toggle="tooltip" title="<fmt:message key='search.byid.tooltip'/>" href="<c:url value='/main/file/list/search?query=id:1'/>">id:1</a>",
                "<a rel="tooltip" data-placement="bottom" data-toggle="tooltip" title="<fmt:message key='search.byname.tooltip'/>" href="<c:url value='/main/file/list/search?query=name:test'/>">name:test</a>" ,
                "<a rel="tooltip" data-placement="bottom" data-toggle="tooltip" title="<fmt:message key='search.bytype.tooltip'/>" href="<c:url value='/main/file/list/search?query=type:pdf'/>">type:pdf</a>" ,
                "<a rel="tooltip" data-placement="bottom" data-toggle="tooltip" title="<fmt:message key='search.byowner.tooltip'/>" href="<c:url value='/main/file/list/search?query=ownerName:alex'/>">ownerName:alex</a>" ,

                <c:url  var="lm_sample_url" value="/main/file/list/search">
                    <c:param name='query' value='lastModified:["201303" TO "201304"]'/>
                </c:url>
                "<a rel="tooltip" data-placement="bottom" data-toggle="tooltip" title="<fmt:message key='search.bylastmodified.tooltip'/>" href='<c:out value="${lm_sample_url}"/>'> lastModified:["201303" TO "201304"]</a>"
             
              </span>            
    
                
            </div>
 
            
              

        
 