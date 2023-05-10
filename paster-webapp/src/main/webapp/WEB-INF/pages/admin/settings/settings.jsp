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
<h1>
             <fmt:message key="settings.edit.title">
                <fmt:param value="${model.name}"/>
            </fmt:message>
</h1>



<c:url var="url" value='/main/admin/settings/saveSettings' />

<div class="row">
    <div class="col-md-12">


  <div  th:if="${restartRequired}" class="alert alert-danger" role="alert">
                            <h4>Требуется перезагрузка</h4>
                            <p>
                                Обновленные настройки системы будут применены только после перезагрузки системы
                            </p>

                            <form  sec:authorize="hasRole('ROLE_ADMIN')"
                                    action="@{/input-api/restart}" method="POST">
                                <input class="btn btn-sm btn-danger" value="Перезагрузка" type="submit" />
                            </form>

                        </div>

<form:form action="${url}"
           modelAttribute="model"
           method="POST" enctype="multipart/form-data">

    <form:errors path="*" cssClass="errorblock" element="div"/>



    <fieldset>
        <div class="form-row">
            <label for="ptitle"><fmt:message key="user.name"/>:</label>
            <span class="input">
                <form:input path="name"  id="ptitle"  />
                <form:errors path="name" cssClass="error" />

            </span>
        </div>

        <div class="form-row">
            <label for="pdesc"><fmt:message key="project.description"/>:</label>
            <span class="input">
                <form:textarea path="description" name="description" id="pdesc" cols="40" rows="6"  />
                <form:errors path="description" cssClass="error" />

            </span>
        </div>


        <div class="form-row">
            <label for="iconImageFile">
                <fmt:message key="project.iconImage"/>
                            <c:if test="${not empty model.iconImage}">
                                <img src="${model.iconImage}" alt="Icon image" />
                            </c:if>
                :</label>
            <span class="input">

                <form:input  path="iconImageFile" type="file" name="iconImageFile" id="iconImageFile"  />
                <form:errors path="iconImageFile" cssClass="error" />

            </span>
        </div>


         <div class="form-row">
            <label for="clientImageFile">
                 <c:choose>
                        <c:when test="${model.blank}">
                <fmt:message key="project.clientImage"/>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${not empty model.clientImage}">
                                <img src="${model.clientImage}" alt="Background" />
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                :</label>
            <span class="input">
                <input type="checkbox" name="scaleClientImg"  />
                <form:input  path="clientImageFile" type="file" name="clientImageFile" id="clientImageFile"  />
                <form:errors path="clientImageFile" cssClass="error" />

            </span>
        </div>

        <div class="form-buttons">
            <div class="button">
                <input name="submit" type="submit" value="<fmt:message key='button.save'/>" />
                <input name="cancel" type="submit" value="<fmt:message key='button.cancel'/>" />
                <input name="reset" type="submit" value="<fmt:message key='button.reset'/>" />

            </div>
        </div>
    </fieldset>
</form:form>


    </div>

</div>



