<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<div class="hero-unit">
  <h1><fmt:message key="index.title"/></h1>
  <p><fmt:message key="index.message"/></p>
  <p>
    <a class="btn btn-primary btn-large" href="<c:url value='/main/file/list' />">
      Files
    </a>
      
      <c:url var="url" value='/main/file/list' />

<form:form cssClass="well form-search" action="${url}" commandName="query" method="POST" >

    <form:errors path="query" cssClass="errorblock" element="div"/>
    <form:input path="query" cssClass="input-medium search-query" />
    <sec:authorize ifAnyGranted="ROLE_ADMIN">
        <form:select path="userId" >
            <form:options items="${availableUsers}" itemValue="id" itemLabel="name"  />
        </form:select> 
    </sec:authorize>
    <input name="submit" type="submit" class="btn" value="<fmt:message key="button.search"/>" />
</form:form>
  </p>
</div>


