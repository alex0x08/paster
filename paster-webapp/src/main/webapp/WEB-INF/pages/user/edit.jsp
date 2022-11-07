<%--

    Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class="row">
    <div class="col-md-12">

        <h4 >

            <c:choose>
                <c:when test="${model.blank}">
                    <fmt:message key="user.new"/>
                </c:when>
                <c:otherwise>
                    <fmt:message key="user.edit.title">
                        <fmt:param value="${model.name}"/>
                    </fmt:message>

                </c:otherwise>
            </c:choose>

        </h4>
    </div>
</div>

<c:if test="${model.remoteUser}">

    <div class="row">
        <div class="col-md-12">
            <div class="error">
                <fmt:message key="warn.user.remote"/>
            </div>
        </div>
    </div>
</c:if>
       
<div class="row">
    <div class="col-md-6 col-md-offset-1">

        <c:url var="url" value='/main/user/save' />

        <form:form action="${url}" cssClass="form"  
                   modelAttribute="model"
                   method="POST" enctype="multipart/form-data">

            <form:errors path="*" cssClass="errorblock" element="div"/>

            <c:if test="${!model.blank}">
                <form:hidden path="id"  />
            </c:if>

            <div class="form-group">
                <label for="ptitle"><fmt:message key="user.name"/>:</label>
                    <form:input path="name" name="title" id="ptitle" readonly="${model.remoteUser}"  />
                    <form:errors path="name" cssClass="error" /> 
            </div>

            <div class="form-group">
                <label for="pmail"><fmt:message key="user.email"/>:</label>
                    <form:input path="email" name="email" id="pmail" readonly="${model.remoteUser}" />
                    <form:errors path="email" cssClass="error" />
            </div>

            <div class="form-group">
                <label for="username"><fmt:message key="user.login"/>:</label>
                    <form:input path="username" name="username" id="login" readonly="${model.remoteUser}"  />
                    <form:errors path="username" cssClass="error" /> 
            </div>

            <div class="form-group">
                <label for="password"><fmt:message key="user.password"/>:</label>
                    <form:password path="password" name="password" id="password" readonly="${model.remoteUser}" />
                    <form:errors path="password" cssClass="error" /> 
            </div>
            <div class="form-group">
                <label for="passwordRepeat"><fmt:message key="password.repeat"/>:</label>
                    <form:password path="passwordRepeat" name="passwordRepeat" id="passwordRepeat" readonly="${model.remoteUser}" /> 
                    <form:errors path="passwordRepeat" cssClass="error" /> 
            </div>

            <div class="form-group">
                <label for="roles"><fmt:message key="user.roles"/>:</label>
                    <form:select path="roles" multiple="true">
                        <c:forEach items="${availableRoles}" var="role">
                            <form:option  value="${role.code}"  >
                                <fmt:message key="${role.name}"/>
                            </form:option>
                        </c:forEach>
                    </form:select> 
                    <form:errors path="roles" cssClass="error" /> 
            </div>       

                <div class="form-group">
                <label for="disabledFlag"><fmt:message key="dbobject.disabled"/>:</label>
                    <form:checkbox id="disabledFlag"  path="disabled"/>
                </div> 
                
                <div class="form-group">
                 <button class="btn p-btn-save  " id="doSearchBtn" type="submit">
                    <fmt:message key="button.save"/>
                    </button>
              
                    <button class="sbtn" name="cancel" type="submit">
                    <fmt:message key="button.cancel"/>
                    </button>
                 
                </div>
               

        </form:form>

    </div>
</div>

