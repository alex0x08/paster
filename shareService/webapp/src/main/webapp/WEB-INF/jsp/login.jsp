<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


    <c:url var="url" value='/act/doLogin' />

<div class='row'>
    <div class='col-md-offset-3 col-xs-4 col-md-5'>
        
        <form:form  role="form" action="${url}"  method="POST" >
    
     
    
    <legend><fmt:message key="login.title"/></legend>
    
     <c:if test="${param.authfailed ne null}">
        <div class="alert alert-block alert-danger" style="width:20em;">
            <a class="close" data-dismiss="alert" href="#">×</a>
            <h4 class="alert-heading"><fmt:message key="login.msg.failure"/></h4>
            <fmt:message key="login.failure.reason"/>: <c:out value="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}"/>
        </div>
    </c:if>
    
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

  
</form:form>

        
    </div>
</div>


    

>>>>>>> dbf52a094d477965568a60d39b920438f36ce077
