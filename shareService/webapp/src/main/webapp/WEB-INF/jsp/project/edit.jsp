
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>



<c:url var="url" value='/main/project/save' />

<div class="row">

            <div class="col-md-2">
               
            </div>
    

            <div class="col-md-6">

        <form:form action="${url}" cssClass="form-horizontal"
                   modelAttribute="model" 
                   method="POST" enctype="multipart/form-data">

            <form:errors  cssClass="alert alert-error" element="div"/>

            <form:hidden path="id" />

            <fieldset>
                <legend>
                    <c:choose>
                        <c:when test="${model.blank}">
                                <fmt:message key="new.project"/>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${model.avatarSet}">
                                <img src="data:image/png;base64,${model.avatar.icon}" alt="Red dot" />
                            </c:if>
                            <c:out value="${model.name} : ${model.description}"/>   
                        </c:otherwise>
                    </c:choose>
                    
                </legend>

                <div class="form-group ">

                    <form:label cssClass="control-label" path="name">
                        <fmt:message key="struct.name"/>:</form:label>

                        <div class="input-group input-group-sm">
                            <span class="input-group-addon"><span class="glyphicon glyphicon-tag"></span></span>
                            <form:input path="name" cssStyle="width:10em;" 
                                        cssErrorClass="form-control alert alert-danger" 
                                        cssClass="form-control"/>
                    </div>                    
                    <form:errors path="name" cssClass="help-block alert alert-danger" /> 
                </div>    

                <div class="form-group ">

                    <form:label cssClass="control-label" path="description">
                        <fmt:message key="struct.desc"/>:</form:label>

                        <div class="input-group input-group-sm">
                           <form:input path="description" cssStyle="width:30em;" 
                                        cssErrorClass="form-control alert alert-danger" 
                                        cssClass="form-control"/>
                    </div>                    
                    <form:errors path="description" cssClass="help-block alert alert-danger" /> 
                </div>    

               
              <div class="form-group ">

                    <form:label cssClass="control-label" path="name">
                        <fmt:message key="avatar.icon"/>:</form:label>

                        <div class="input-group input-group-sm">
                            <span class="input-group-addon">

                                <c:choose>
                                    <c:when test="${model.avatarSet}">
                                        <img src="data:image/png;base64,${model.avatar.icon}" 
                                             alt="<c:out value='${model.name}'/>" />              
                                    </c:when>
                                    <c:otherwise>
                                        <span class="glyphicon glyphicon-picture"></span>
                                    </c:otherwise>
                                </c:choose>
                               
                                
                            </span>
                            <form:input path="file" type="file" cssStyle="width:20em;" 
                                        cssErrorClass="form-control alert alert-danger" 
                                        cssClass="form-control"/>
                    </div>                    
                    <form:errors path="name" cssClass="help-block alert alert-danger" /> 
                </div>    


               
                <div class="form-group">
                    <input name="submit" type="submit" class="btn btn-primary" value="<fmt:message key="button.save"/>" />

                    <input name="cancel" type="submit" class="btn btn-default" value="<fmt:message key="button.cancel"/>" />

                </div>   

            </form:form>
    </div>

</div>
