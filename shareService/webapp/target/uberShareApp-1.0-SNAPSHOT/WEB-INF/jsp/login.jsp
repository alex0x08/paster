<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaFactory" %>


<div class="errors">
    <c:if test="${param.error}">
        <fmt:message key="login.msg.failure"/><br /><br />
        <fmt:message key="login.failure.reason"/>:  ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}
  </c:if>
</div>


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


        <c:if test="${userSession.captchaEnabled}">
            <label ><fmt:message key="login.captcha"/></label>

            <jsp:scriptlet>

                ReCaptcha c = ReCaptchaFactory.newReCaptcha("6LeJdb4SAAAAAPxK7fX5sz4gVzh2BIMU96mXfT1S", "6LeJdb4SAAAAAMB6JmsaURxYQxsDQaLb4QtS4gw6", false);
                out.print(c.createRecaptchaHtml(null, null));

            </jsp:scriptlet>

        </c:if>



        <label class="checkbox">
            <input type="checkbox" name="_spring_security_remember_me"> <fmt:message key="login.rememberMe"/>
        </label>
        <button type="submit" class="btn">Sign in</button>
    </fieldset>
</form>
