<%-- 
    Document   : 403
    Created on : Nov 30, 2011, 5:27:23 AM
    Author     : alex
--%>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<% response.setStatus(403); %>


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <title>Access denied</title>
    </head>
    <body>
        <h1>Access denied</h1>
        
        
<div class='row'>
    <div class='col-md-offset-3 col-xs-4 col-md-5'>

        
<form role="form" action="<c:url value="/j_spring_security_check" />"  method="POST" >
    
    <legend><fmt:message key="login.title"/></legend>
    <%--
     <c:if test="${param.authfailed}">
        <div class="alert alert-block alert-danger" style="width:20em;">
            <a class="close" data-dismiss="alert" href="#">×</a>
            <h4 class="alert-heading"><fmt:message key="login.msg.failure"/></h4>
            <fmt:message key="login.failure.reason"/>: <c:out value="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}"/>
        </div>
    </c:if>
    --%>
    <div class='row'>
        <div class='col-lg-8 col-md-8'>
            <div class="input-group">
                <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span></span>
                <input class="form-control"  name="j_username" type="text" placeholder="Username">
            </div>
        </div>
    </div>  
    
    <div class='row' style="padding-top: 0.5em; padding-bottom: 0.5em;">
        <div class='col-lg-8 col-md-8'>
            <div class="input-group">
                <span class="input-group-addon"><span class="glyphicon glyphicon-lock"></span></span>
                <input class="form-control"  name="j_password" type="password" placeholder="Password">
            </div>
        </div>
    </div>
    
     <button type="submit" class="btn btn-primary" ><fmt:message key="button.login"/></button>

  
</form>

        
    </div>
</div>


        
    </body>
</html>
