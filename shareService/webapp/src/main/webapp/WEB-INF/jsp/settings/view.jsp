<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<h1><fmt:message key="person.form.title"/></h1>

<c:if test="${not empty statusMessageKey}">
    <p><fmt:message key="${statusMessageKey}"/></p>
</c:if>

<c:url var="url" value="/file/view.html" /> 



<form:form action="${url}" modelAttribute="sharedFile" method="POST" enctype="multipart/form-data">

<form:errors path="*" cssClass="errorblock" element="div"/>

    <form:hidden path="id" />

    <fieldset>
        <div class="form-row">
            <label for="name"><fmt:message key="person.form.firstName"/>:</label>
            <span class="input"><form:input path="name"  /></span>
        </div>       
        <div class="form-row">
        
            
            <label for="file">Please select a file to upload:</label>
            <span class="input">
               <input type="file" name="file" />
               <form:errors path="file" cssClass="error" /> 
            </span>
        </div>
        <div class="form-buttons">
            <div class="button"><input name="submit" type="submit" value="<fmt:message key="button.save"/>" /></div>
        </div>
    </fieldset>
</form:form>