<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class="row">
    <div class="column grid-10">

        <h4 class="f-h4">

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

        </h4>
    </div>
</div>


<div class="row">
    <div class="column grid-10">

        
        
        <c:if test="${model.remoteUser}">
            <div class="error">
            <fmt:message key="warn.user.remote"/>
            </div>
        </c:if>
        
        
        
        <c:url var="url" value='/main/user/save' />

        <form:form action="${url}" 
                   modelAttribute="model"
                   method="POST" enctype="multipart/form-data">

            <form:errors path="*" cssClass="errorblock" element="div"/>

            <c:if test="${!model.blank}">
                <form:hidden path="id"  />
            </c:if>

            <div class="row">
                <label for="ptitle"><fmt:message key="user.name"/>:</label>
                <span class="input">
                    <form:input path="name" name="title" id="ptitle" readonly="${model.remoteUser}"  />
                    <form:errors path="name" cssClass="error" /> 
                </span>
            </div>

            <div class="row">
                <label for="pmail"><fmt:message key="user.email"/>:</label>
                <span class="input">
                    <form:input path="email" name="email" id="pmail" readonly="${model.remoteUser}" />
                    <form:errors path="email" cssClass="error" />
                </span>
            </div>

            <div class="row">
                <label for="username"><fmt:message key="user.login"/>:</label>
                <span class="input">
                    <form:input path="username" name="username" id="login" readonly="${model.remoteUser}"  />
                    <form:errors path="username" cssClass="error" /> 
                </span>
            </div>

            <div class="row">
                <label for="password"><fmt:message key="user.password"/>:</label>
                <span class="input">
                    <form:password path="password" name="password" id="password" readonly="${model.remoteUser}" />
                    <form:errors path="password" cssClass="error" /> 
                </span>
            </div>
            <div class="row">
                <label for="passwordRepeat"><fmt:message key="password.repeat"/>:</label>
                <span class="input">
                    <form:password path="passwordRepeat" name="passwordRepeat" id="passwordRepeat" readonly="${model.remoteUser}" /> 
                    <form:errors path="passwordRepeat" cssClass="error" /> 
                </span>
            </div>

            <div class="row">
                <label for="roles"><fmt:message key="user.roles"/>:</label>
                <span class="input">
                    <form:select path="roles" multiple="true">
                        <c:forEach items="${availableRoles}" var="role">
                            <form:option  value="${role.code}"  >
                                <fmt:message key="${role.name}"/>
                            </form:option>
                        </c:forEach>
                    </form:select> 
                    <form:errors path="roles" cssClass="error" /> 
                </span>
            </div>       

                <div class="row">
                <label for="disabledFlag"><fmt:message key="dbobject.disabled"/>:</label>
                <span class="input">
                    <form:checkbox id="disabledFlag"  path="disabled"/>
                </span>
                </div> 
                
                <div class="row">
                 <button class="btn p-btn-save  " id="doSearchBtn" type="submit">
                    <fmt:message key="button.save"/>
                    </button>
              
                    <button class="sbtn" name="cancel" type="submit">
                    <fmt:message key="button.cancel"/>
                    </button>
                 
                </div>
               

        </form:form>

    </div>
</div>

