
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>



<c:url var="url" value='/main/project/save' />

<div class="row">

   

            <div class="col-md-4">

        <form:form action="${url}" cssClass="form-horizontal"
                   modelAttribute="model" 
                   method="POST" enctype="multipart/form-data">

            <form:errors  cssClass="alert alert-error" element="div"/>

            <form:hidden path="id" />

            <fieldset>
                <legend><fmt:message key="login.title"/></legend>

                <div class="form-group ">

                    <form:label cssClass="control-label" path="name">
                        <fmt:message key="struct.name"/>:</form:label>

                        <div class="input-group input-group-sm">
                            <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span></span>
                            <form:input path="name" cssStyle="width:10em;" 
                                        cssErrorClass="form-control alert alert-danger" 
                                        cssClass="form-control"/>
                    </div>                    
                    <form:errors path="name" cssClass="help-block alert alert-danger" /> 
                </div>    

                <div class="form-group ">

                    <form:label cssClass="control-label" path="description">
                        <fmt:message key="user.login"/>:</form:label>

                        <div class="input-group input-group-sm">
                            <span class="input-group-addon"><span class="glyphicon glyphicon-log-in"></span></span>
                            <form:input path="description" cssStyle="width:10em;" 
                                        cssErrorClass="form-control alert alert-danger" 
                                        cssClass="form-control"/>
                    </div>                    
                    <form:errors path="description" cssClass="help-block alert alert-danger" /> 
                </div>    

               
              

               
                <div class="form-group">
                    <input name="submit" type="submit" class="btn btn-primary" value="<fmt:message key="button.save"/>" />

                    <input name="cancel" type="submit" class="btn btn-default" value="<fmt:message key="button.cancel"/>" />

                </div>   

            </form:form>
    </div>

</div>
