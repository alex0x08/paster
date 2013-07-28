
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<div class="row">

    <div class="span2 ">
        <img src="<c:out value='http://www.gravatar.com/avatar/${user.avatarHash}?s=128'/>"/>

    </div>

    <div class="span6"> 



        <c:url var="url" value='/main/profile/save' />
        <form:form action="${url}" cssClass="form-horizontal"
                   modelAttribute="user" 
                   method="POST" enctype="multipart/form-data">
            <fieldset>
                <form:errors  cssClass="alert alert-error" element="div" />
                <form:hidden path="id" />    



                <div class="control-group">
                    <form:label cssClass="control-label" path="name">
                        <fmt:message key="user.name"/>:</form:label>
                        <div class="controls">


                        <form:input cssErrorClass="alert alert-error" cssClass="input-xlarge" path="name"/>
                        <form:errors path="name" cssClass="alert alert-error" /> 

                    </div>
                </div>
                <div class="control-group">
                    <form:label cssClass="control-label" path="login">
                        <fmt:message key="user.login"/>:</form:label>
                        <div class="controls">
                            <span class="input">
                            <c:out value="${user.login}"/>
                        </span>

                    </div> 
                </div>
                <div class="control-group">

                    <form:label cssClass="control-label" path="password">
                        <fmt:message key="user.pass"/>:</form:label>
                    <div class="controls">
                        <span class="input">

                            <form:password path="newPassword"/>
                            <form:errors path="newPassword" cssClass="alert alert-error" /> 

                        </span>

                    </div>

                </div>    


                <div class="control-group">

                    <form:label cssClass="control-label" path="password">
                        <fmt:message key="user.pass.repeat"/>:</form:label>
                    <div class="controls">
                        <span class="input">

                             <form:password path="repeatPassword"/>
                            <form:errors path="repeatPassword" cssClass="error" /> 

                        </span>

                    </div>

                </div>    


                <div class="control-group">
                    <form:label cssClass="control-label" path="roles">
                        <fmt:message key="user.roles"/>:</form:label>
                        <div class="controls">
                            <span class="input">                    
                            <c:forEach items="${user.roles}" var="role">
                               <fmt:message key="${role.desc}"/>
                            </c:forEach>               
                        </span>

                    </div>
                </div>

                <div class="control-group">
                    <form:label cssClass="control-label" path="email">
                        <fmt:message key="user.email"/>:</form:label>
                        <div class="controls">
                        <form:input path="email" cssClass="input" cssErrorClass="alert alert-error"/>
                        <form:errors path="email" cssClass="alert alert-error" /> 
                    </div>
                </div>

                <div class="form-actions">
                    <input class="btn btn-primary" name="submit" type="submit" 
                           value="<fmt:message key="button.save"/>" />
                    <a href="<c:url value='/main/file/list'/>"><fmt:message key="button.cancel"/></a>
                </div>

            </fieldset>

        </form:form>

    </div>

</div>


