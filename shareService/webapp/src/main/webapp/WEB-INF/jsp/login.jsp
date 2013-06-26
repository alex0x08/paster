<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaFactory" %>




<form  action="<c:url value="/j_spring_security_check" />"  method="POST">
    <fieldset>
        <legend><fmt:message key="login.title"/></legend>


        <div class="input-prepend">
            <span class="add-on"><i class="icon-user"></i></span>
            <input class="span10"  name="j_username" type="text" placeholder="Username">
        </div>

        <div class="input-prepend">
            <span class="add-on"><i class="icon-password"></i></span>
            <input class="span10"  name="j_password" type="password" placeholder="Password">
        </div>

      
            
           
    <c:if test="${param.authfailed}">
        <div class="alert alert-block alert-error" style="width:20em;">
                <a class="close" data-dismiss="alert" href="#">×</a>
                <h4 class="alert-heading"><fmt:message key="login.msg.failure"/></h4>
                <fmt:message key="login.failure.reason"/>: <c:out value="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}"/>
            </div>
    </c:if>

            <button type="submit" class="btn"><fmt:message key="button.login"/></button>
            
          
            
    </fieldset>
 
            
        
            
            
</form>
