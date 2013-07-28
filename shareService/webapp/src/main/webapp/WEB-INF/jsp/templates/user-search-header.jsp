<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:url var="url" value='/main/user/list/search' />

   
<form:form action="${url}" commandName="query" 
           class="navbar-form pull-left"  
           method="POST" >
    <form:errors path="*" cssClass="errorblock" element="span"/>

    <div class="input-append">
            
            <fmt:message var="searchPlaceholder" key='search.placeholder.default'/>
            
            <form:input path="query" type="text" placeholder="${searchPlaceholder}" />


            <button type="submit" class="btn">
                <i class="icon-search" title="<fmt:message key="button.search"/>"></i>
            </button>
            </div>
                       
</form:form>     

