<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<h1>

 <c:choose>
        <c:when test="${model.blank}">
            <fmt:message key="user.new"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="user.edit.title">
                <fmt:param value="${model.name}"/>
            </fmt:message>

        </c:otherwise>
    </c:choose>
    
</h1>


<c:url var="url" value='/main/user/save' />

<form:form action="${url}" 
           modelAttribute="model"
           method="POST" enctype="multipart/form-data">

    <form:errors path="*" cssClass="errorblock" element="div"/>


    <c:choose>
        <c:when test="${model.blank}">


        </c:when>
        <c:otherwise>
            <form:hidden path="id"  />

        </c:otherwise>
    </c:choose>

    <fieldset>
        <div class="form-row">
            <label for="ptitle"><fmt:message key="user.name"/>:</label>
            <span class="input">
                <form:input path="name" name="title" id="ptitle"  />
                <form:errors path="name" cssClass="error" /> 

            </span>
        </div>

        <div class="form-row">
            <label for="pmail"><fmt:message key="user.email"/>:</label>
            <span class="input">
                <form:input path="email" name="email" id="pmail"  />
                <form:errors path="email" cssClass="error" />

            </span>
        </div>


        <div class="form-row">
            <label for="username"><fmt:message key="user.login"/>:</label>
            <span class="input">
                <form:input path="username" name="username" id="login"  />
                <form:errors path="username" cssClass="error" /> 

            </span>
        </div>
      
        <div class="form-row">
            <label for="password"><fmt:message key="user.password"/>:</label>
            <span class="input">
                <form:input path="password" name="password" id="password"  />
                <form:errors path="password" cssClass="error" /> 
            </span>
        </div>
        <div class="form-row">
            <label for="passwordRepeat"><fmt:message key="password.repeat"/>:</label>
            <span class="input">
                <form:input path="passwordRepeat" name="passwordRepeat" id="passwordRepeat"  />
                <form:errors path="passwordRepeat" cssClass="error" /> 
            </span>
        </div>
      
                
          <div class="form-row">
            <label for="roles"><fmt:message key="user.roles"/>:</label>
            <span class="input">
                <form:select path="roles" multiple="true">
                    
                    <c:forEach items="${availableRoles}" var="role">
                     
                      
                               <form:option  value="${role.code}"   >
                                    <fmt:message key="${role.name}"/>
                                </form:option>
        
                        
	            </c:forEach>
               
                       
                    
                    
                </form:select> 
                <form:errors path="roles" cssClass="error" /> 
            </span>
        </div>       
                
      

        <div class="form-buttons">
            <div class="button">
                <input name="cancel" type="submit" value="<fmt:message key="button.cancel"/>" />
                <fmt:message var="submit_button_text" key="button.save"/>
                <input name="submit" type="submit" value="${submit_button_text}" />
            </div>
        </div>
    </fieldset>
</form:form>



<a href="<c:url value="/main/paste/list"/>"><fmt:message key="paste.list.title"/></a> /
