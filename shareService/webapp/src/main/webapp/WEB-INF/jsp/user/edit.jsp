
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>



<c:url var="url" value='/main/user/save' />

<div class="row">

    <div class="col-md-3" style="text-align:right;">
        <c:set var="usermodel" value="${model}" scope="request"></c:set>
        <jsp:include page="/WEB-INF/jsp/templates/common/user-avatar.jsp" >
            <jsp:param name="size" value="full" />
        </jsp:include>
        
     
        <div class="form-group">

            <c:out value="${model.login}"/> ,      
            <c:forEach items="${model.roles}" var="role"  >
                <fmt:message key="${role.desc}"/>
            </c:forEach>   
            <p>
                <c:out value="${model.relatedProject.name}"/>
            </p> 
        </div>
    </div>

            <div class="col-md-4">

        <form:form action="${url}" cssClass="form-horizontal" id="editUserForm"
                   modelAttribute="model" 
                   method="POST" enctype="multipart/form-data">

            <form:errors  cssClass="alert alert-error" element="div"/>

            <form:hidden path="id" />

            <fieldset>
                <legend><fmt:message key="login.title"/></legend>

                <div class="form-group ">

                    <form:label cssClass="control-label" path="name">
                        <fmt:message key="user.name"/>:</form:label>

                        <div class="input-group input-group-sm">
                            <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span></span>
                            <form:input path="name" cssStyle="width:10em;" 
                                        cssErrorClass="form-control alert alert-danger" 
                                        cssClass="form-control"/>
                    </div>                    
                    <form:errors path="login" cssClass="help-block alert alert-danger" /> 
                </div>    

                <div class="form-group ">

                    <form:label cssClass="control-label" path="login">
                        <fmt:message key="user.login"/>:</form:label>

                        <div class="input-group input-group-sm">
                            <span class="input-group-addon"><span class="glyphicon glyphicon-log-in"></span></span>
                            <form:input path="login" cssStyle="width:10em;" 
                                        cssErrorClass="form-control alert alert-danger" 
                                        cssClass="form-control"/>
                    </div>                    
                    <form:errors path="login" cssClass="help-block alert alert-danger" /> 
                </div>    

                <div class="form-group ">

                    <form:label cssClass="control-label" path="password">
                        <fmt:message key="user.pass"/>:</form:label>

                        <div class="input-group input-group-sm">
                            <span class="input-group-addon"><span class="glyphicon glyphicon-lock"></span></span>
                            <form:password path="newPassword" cssStyle="width:10em;" 
                                           cssErrorClass="form-control alert alert-danger" 
                                           cssClass="form-control"/>

                        <span class="input-group-addon"><fmt:message key="user.pass.repeat"/>
                        </span>
                        <form:password path="repeatPassword" cssStyle="width:10em;" 
                                       cssErrorClass="form-control alert alert-danger" 
                                       cssClass="form-control"/>
                    </div>                    
                    <form:errors path="newPassword" cssClass="help-block alert alert-danger" /> 
                </div>    

                <div class="form-group">
                    <label cssClass="control-label" for="roles"><fmt:message key="user.roles"/>:</label>
                    <div class="controls">
                        <span class="input">
                            <form:select path="roles" multiple="true"  cssErrorClass="form-control alert alert-danger" 
                                         cssStyle="width:10em;" 
                                         cssClass="form-control ">
                                <c:forEach items="${availableRoles}" var="role">
                                    <c:choose>
                                        <c:when test="${role eq 'ROLE_MANAGER'}">

                                                <form:option cssClass="disabled" disabled="true"  value="${role.code}"  >
                                                    <fmt:message key="${role.desc}"/>
                                            </form:option>
                                            
                                        </c:when>
                                        <c:otherwise>
                                            <form:option  value="${role.code}"  >
                                        <fmt:message key="${role.desc}"/>
                                    </form:option>
                                        </c:otherwise>
                                    </c:choose>
                                    
                                </c:forEach>
                            </form:select> 
                            <form:errors path="roles" cssClass="help-block alert alert-danger" /> 
                        </span>
                    </div>
                </div>
                        
                        
                <div class="form-group">
                    <label cssClass="control-label" for="roles"><fmt:message key="user.prefferedLocale"/>:</label>
                    <div class="controls">
                        <span class="input">
                            <form:select path="prefferedLocale" cssErrorClass="form-control alert alert-danger" 
                                         cssStyle="width:20em;" 
                                         cssClass="chosen_image_selectbox form-control">
                                <c:forEach items="${availableLocales}" var="locale">
                                    
                                    <c:url var="imgUrl" value='/main/static/${appVersion}/images/flags/flag_${locale.language}_${locale.country}.png'/>
                                    
                                    <form:option value="${locale.language}_${locale.country}" 
                                                 data-img-src="${imgUrl}">
                                        <c:out value="${locale.displayName}"/>
                                    </form:option>
                                </c:forEach>
                            </form:select> 
                            <form:errors path="prefferedLocale" cssClass="help-block alert alert-danger" /> 
                        </span>
                    </div>
                </div>        
                        
                        
                   <c:set var="usermodel" value="${model}" scope="request"></c:set>
        <jsp:include page="/WEB-INF/jsp/templates/common/select-avatar.jsp" >
            <jsp:param name="formId" value="editUserForm" />
        </jsp:include>       
                        
                 <div class="form-group">
                    <label cssClass="control-label" for="projects"><fmt:message key="user.relatedProject"/>:</label>
                    <div class="controls">
                        <span class="input">
                            <form:select id="projects" path="relatedProject" multiple="false"  cssErrorClass="form-control alert alert-danger" 
                                         cssStyle="width:20em;" 
                                         cssClass="form-control">
                                <c:forEach items="${availableProjects}" var="project">
                                    <form:option value="${project}" >
                                        <c:out value="${project.name}"/>
                                    </form:option>
                                </c:forEach>
                            </form:select> 
                            <form:errors path="relatedProject" cssClass="help-block alert alert-danger" /> 
                        </span>
                    </div>
                </div>        
                        

                <div class="form-group ">

                    <form:label cssClass="control-label" path="email">
                        <fmt:message key="user.email"/>:</form:label>

                        <div class="input-group input-group-sm">
                            <span class="input-group-addon"><span class="glyphicon glyphicon-envelope"></span></span>
                            <form:input path="email" cssStyle="width:10em;" 
                                        cssErrorClass="form-control alert alert-danger" 
                                        cssClass="form-control"/>
                    </div>                    
                    <form:errors path="email" cssClass="help-block alert alert-danger" /> 
                </div>    
                
                  <div class="form-group ">
                                <div class="controls">
                                    <div class="input-group">
                                        <span class="input-group-addon">
                                            <img src="<c:url value='/main/static/${appVersion}/images/skype.png'/>"/></span>
                                            <form:input cssErrorClass="form-control alert alert-danger" cssClass="form-control" 
                                                        path="skype" cssStyle="width:10em;" />
                                    </div>                    
                                    <form:errors path="skype" cssClass="help-block alert alert-danger" />                        
                                   </div>
                            </div>          
                                  
                
                    <div class="form-group ">
                                <div class="controls">
                                    <div class="input-group">
                                        <span class="input-group-addon"><span class="glyphicon glyphicon-earphone"></span></span>
                                            <form:input cssErrorClass="form-control alert alert-danger" cssStyle="width:12em;" cssClass="form-control" 
                                                        path="phone" />
                                    </div>                    
                                    <form:errors path="phone" cssClass="help-block alert alert-danger" />                        
                               </div>
                            </div>                  
                                                    
                                   
                <div class="form-group">
                    <input name="submit" type="submit" class="btn btn-primary" value="<fmt:message key="button.save"/>" />

                    <input name="cancel" type="submit" class="btn btn-default" value="<fmt:message key="button.cancel"/>" />

                </div>   

            </form:form>
    </div>

</div>
