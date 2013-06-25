<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:url var="url" value='/main/user/list' />

<form:form  cssClass="navbar-form pull-right"  action="${url}" commandName="query"    
           
           method="POST" enctype="multipart/form-data">

    <form:errors path="*" cssClass="errorblock" element="div"/>
    <form:input path="query" />    
    <input name="submit" type="submit" value="<fmt:message key="button.search"/>" />
</form:form>
