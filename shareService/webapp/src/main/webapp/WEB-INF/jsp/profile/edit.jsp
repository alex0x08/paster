
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<div class="row">

    <div class="col-md-3" style="text-align:right;">
     
        <c:set var="usermodel" value="${user}" scope="request"></c:set>
        <jsp:include page="/WEB-INF/jsp/templates/common/user-avatar.jsp" >
            <jsp:param name="size" value="full" />
        </jsp:include>

        <div class="form-group">

            <c:out value="${user.login}"/> ,      
            <c:forEach items="${user.roles}" var="role"  >
                <fmt:message key="${role.desc}"/>
            </c:forEach> 
            <p>
                <c:out value="${user.relatedProject.name}"/>
            </p>    
        </div>                            

    </div>

                <div class="col-md-4"> 

                    <c:url var="url" value='/main/profile/save?${_csrf.parameterName}=${_csrf.token}' />

                    <form:form id="editProfileForm" action="${url}"  cssClass="form-horizontal"
                               modelAttribute="user" 
                               method="POST" enctype="multipart/form-data">
                        <fieldset>

                            <legend>
                                <c:out value="${user.name}"/>                     
                            </legend>

                            <form:errors  cssClass="alert alert-danger" element="div" />
                            <form:hidden path="id" />   


                            <div class="form-group">

                                <div class="input-group">
                                    <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span></span>
                                    <form:input cssErrorClass="form-control alert alert-danger" cssClass="form-control" path="name" htmlEscape="true"  />
                                </div>                    
                                <form:errors path="name" cssClass="help-block alert alert-danger" />                         
                            </div>


                            <div class="form-group ">

                                <div class="controls">
                                    <div class="input-group">
                                        <span class="input-group-addon"><span class="glyphicon glyphicon-envelope"></span></span>
                                            <form:input cssErrorClass="form-control alert alert-danger" cssStyle="width:20em;"
                                                        cssClass="form-control" path="email" />
                                    </div>                    
                                    <form:errors path="email" cssClass="help-block alert alert-danger" />                         

                                </div>
                            </div>

                          
                            <div class="form-group ">
                                <div class="controls">
                                    <div class="input-group">
                                        <span class="input-group-addon"><img src="<c:url value='/main/static/${appVersion}/images/skype.png'/>"/></span>
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
                                    
                                <c:set var="usermodel" value="${currentUser}" scope="request"></c:set>
        <jsp:include page="/WEB-INF/jsp/templates/common/select-avatar.jsp" >
            <jsp:param name="formId" value="editProfileForm" />
        </jsp:include>

                            
                                    
                            <div class="form-group">
                                <label cssClass="control-label" for="prefferedLocaleCode"><fmt:message key="user.prefferedLocale"/>:</label>
                                <div class="controls">
                                    <span class="input">
                                        <form:select path="prefferedLocaleCode" cssErrorClass="form-control alert alert-danger" 
                                                     cssStyle="width:20em;" 
                                                     cssClass="chosen_image_selectbox form-control">
                                            <c:forEach items="${availableLocales}" var="locale">
                                                <c:url var="imgUrl" value='/main/static/${appVersion}/images/flags/flag_${locale.language}_${locale.country}.png'/>

                                                <form:option value="${locale.language}_${locale.country}" data-img-src="${imgUrl}">
                                                    <c:out value="${locale.displayName}"/>
                                                </form:option>
                                            </c:forEach>
                                        </form:select> 
                                        <form:errors path="prefferedLocaleCode" cssClass="help-block alert alert-danger" /> 
                                    </span>
                                </div>
                            </div>      


                                        <div class="form-group ">

                                            <form:label cssClass="control-label" path="password">
                                                <fmt:message key="user.pass"/>:</form:label>

                                                <div class="input-group input-group-sm">
                                                    <span class="input-group-addon"><span class="glyphicon glyphicon-lock"></span></span>
                                                    <form:password path="newPassword" cssStyle="width:10em;" 
                                                                   cssErrorClass="form-control alert alert-danger" 
                                                                   cssClass="form-control"/>

                                                <span class="input-group-addon">Repeat</span>
                                                <form:password path="repeatPassword" cssStyle="width:10em;" 
                                                               cssErrorClass="form-control alert alert-danger" 
                                                               cssClass="form-control"/>
                                            </div>                    
                                            <form:errors path="newPassword" cssClass="help-block alert alert-danger" /> 
                                        </div>  

                                <div class="form-group">
                                <input class="btn btn-primary" name="submit" type="submit" 
                                       value="<fmt:message key="button.save"/>" />
                                <a class="btn btn-default" href="<c:url value='/main/file/list'/>"><fmt:message key="button.cancel"/></a>
                            </div>       

                        </fieldset>



                    </form:form>

                </div>

</div>

