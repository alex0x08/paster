<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<h1><fmt:message key="login.title"/></h1>

<div style="width: 300px;margin-left: auto; margin-right: auto;" >
    

<div class="section">
    <div class="errors">
        <c:if test="${param.error}">
        <fmt:message key="login.msg.failure"/><br /><br />
        <fmt:message key="login.failure.reason"/>:  ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}
          
        </c:if>
      </div>

</div>

<div class="section">




    <form name="f" action="<c:url value="/j_spring_security_check" />" method="POST">
        <fieldset>
            <div class="field">
                <div class="label"><label for="j_username"><fmt:message key="login.username"/>:</label></div>
                <div class="output">
                    <input type="text" name="j_username" id="j_username"  />
                    ${sessionScope.SPRING_SECURITY_LAST_USERNAME_KEY}  
                </div>
            </div>
            <div class="field">
                <div class="label"><label for="j_password"><fmt:message key="login.password"/>:</label></div>
                <div class="output">
                    <input type="password" name="j_password" id="j_password" />
                </div>
            </div>
            <div class="field">
                <div class="label"><label for="remember_me"><fmt:message key="login.rememberMe"/>:</label></div>
                <div class="output">
                    <input type="checkbox" name="_spring_security_remember_me" id="remember_me" />
                </div>
            </div>
                <div class="field">
                         

                    
                </div>    
            <div class="form-buttons">
                <div class="button">
                    <input name="submit" id="submit" type="submit" value="<fmt:message key="button.login"/>" />
                </div>
            </div>
        </fieldset>
    </form>
</div>
                
                
</div>
