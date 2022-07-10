<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<c:url var="url" value='/act/doAuth' />

<div class='row'>
    <div class='col-md-offset-3 col-xs-4 col-md-5'>

        <form:form  role="form" action="${url}"  method="POST" >

            <legend><fmt:message key="login.title"/></legend>

            <c:if test="${param.authfailed ne null}">
                <div class="alert alert-block alert-danger" style="width:20em;">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    <h4 class="alert-heading"><fmt:message key="login.msg.failure"/></h4>
                    <fmt:message key="login.failure.reason"/>: <c:out value='${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}'/>
                </div>
            </c:if>

            <div class='row'>
                <div class='col-lg-8 col-md-8'>
                    <div class="input-group">
                        <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span></span>
                        <input class="form-control"  name="username" autofocus="true"
                               type="text" placeholder="<fmt:message key="login.username"/>">
                    </div>
                </div>
            </div>  

            <div class='row' style="padding-top: 0.5em; padding-bottom: 0.5em;">
                <div class='col-lg-8 col-md-8'>
                    <div class="input-group">
                        <span class="input-group-addon"><span class="glyphicon glyphicon-lock"></span></span>
                        <input class="form-control"  name="password" 
                               type="password" placeholder="<fmt:message key="login.password"/>">
                    </div>
                </div>
            </div>

            <div class='row' style="padding-top: 0; padding-left: 1em; padding-bottom: 0.5em;">
                <div class='col-lg-8 col-md-8'>        
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" name="remember-me" id="remember_me" checked="true" /> <fmt:message key="login.rememberMe"/>
                        </label>
                    </div>
                </div>
            </div>

            <button type="submit" class="btn btn-primary" ><fmt:message key="button.login"/></button>


        </form:form>


    </div>
</div>


