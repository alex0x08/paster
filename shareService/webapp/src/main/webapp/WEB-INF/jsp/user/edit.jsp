
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<h1><fmt:message key="users.title"/></h1>

<c:if test="${not empty statusMessage}">
    <p><fmt:message key="${statusMessage}"/></p>
</c:if>

<c:url var="url" value='/main/user/save' />
<form:form action="${url}" 
           modelAttribute="model" 
           method="POST" enctype="multipart/form-data">

    <form:errors  cssClass="alert alert-error" element="div"/>

    <form:hidden path="id" />

    <fieldset>
        <legend><fmt:message key="login.title"/></legend>


        <div class="input-prepend">
            <span class="add-on"><i class="icon-user"></i></span>
            <fmt:message key="user.name" var="userName" />
            <form:input path="name" placeholder="${userName}"/>
            <form:errors path="name" cssClass="error" /> 
        </div>


        <div class="control-group">
            <label for="login"><fmt:message key="user.login"/>:</label>
            <span class="input">
                <form:input path="login"/>
                <form:errors path="login" cssClass="error" /> 
            </span>
        </div>

        <div class="control-group">

            <form:label cssClass="control-label" path="password">
                <fmt:message key="user.pass"/>:</form:label>
                <div class="controls">
                    <div class="input-append">
                        <form:password path="newPassword"/>
                        <span class="add-on">Repeat</span>
                        <form:password path="repeatPassword"/>
                </div>

            </div>

        </div>    


        <div class="control-group">
            <label for="roles"><fmt:message key="user.roles"/>:</label>
            <div class="controls">
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
        </div>

        <div class="control-group">
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

    </form:form>