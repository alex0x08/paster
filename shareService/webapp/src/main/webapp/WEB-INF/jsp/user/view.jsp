
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<h1><fmt:message key="files.title"/></h1>

<c:if test="${not empty statusMessage}">
    <p><fmt:message key="${statusMessage}"/></p>
</c:if>

<c:url var="url" value='/main/user/save' />
<form:form action="${url}" 
           modelAttribute="model" 
           method="POST" enctype="multipart/form-data">

    <form:errors path="*" cssClass="errorblock" element="div"/>

    <form:hidden path="id" />

    <fieldset>
        <div class="form-row">
            <label for="name"><fmt:message key="user.name"/>:</label>
            <span class="input">
                <form:input path="name"/>
                <form:errors path="name" cssClass="error" /> 
            </span>
        </div>

            
            
        <div class="form-row">
            <label for="login"><fmt:message key="user.login"/>:</label>
            <span class="input">
                <form:input path="login"/>
                <form:errors path="login" cssClass="error" /> 
            </span>
        </div>

        <div class="form-row">
            <label for="password"><fmt:message key="user.pass"/>:</label>
            <span class="input">
                <c:choose>
                    <c:when test="${model.passwordSet}">
                        [change]
                    </c:when>
                    <c:otherwise>
                        <form:input path="password"/>
                        <form:errors path="password" cssClass="error" /> 

                    </c:otherwise>
                </c:choose>
            </span>
        </div>
        <div class="form-row">
            <label for="roles"><fmt:message key="user.roles"/>:</label>
            <span class="input">
                <form:select path="roles" multiple="true">
                    
                    <c:forEach items="${availableRoles}" var="role">
				<form:option value="${role.code}" >
                                    <fmt:message key="${role.desc}"/>
                                </form:option>
                    </c:forEach>
                </form:select> 
                <form:errors path="roles" cssClass="error" /> 
            </span>
        </div>

          <div class="form-row">
            <label for="email"><fmt:message key="user.email"/>:</label>
            <span class="input">
                <form:input path="email"/>
                <form:errors path="email" cssClass="error" /> 
            </span>
        </div>   
            
        <div class="form-buttons">
            <div class="button">   
                  <input name="cancel" type="submit" value="<fmt:message key="button.cancel"/>" />

                <input name="submit" type="submit" value="<fmt:message key="button.save"/>" />
            </div>
        </div>
    </fieldset>
</form:form>