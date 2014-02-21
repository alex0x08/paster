<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<h1>

             <fmt:message key="settings.edit.title">
                <fmt:param value="${model.name}"/>
            </fmt:message>

</h1>

<a href="<c:url value="/main/paste/list"/>"><fmt:message key="paste.list.title"/></a> /


<c:url var="url" value='/main/admin/settings/save' />

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
                
                <form:input  path="clientImageFile" type="file" name="clientImageFile" id="clientImageFile"  />
                <form:errors path="clientImageFile" cssClass="error" />

            </span>
        </div>

        <div class="form-buttons">
            <div class="button">
                <input name="submit" type="submit" value="<fmt:message key='button.save'/>" />
                <input name="cancel" type="submit" value="<fmt:message key="button.cancel"/>" />
                <input name="reset" type="submit" value="<fmt:message key="button.reset"/>" />

            </div>
        </div>
    </fieldset>
</form:form>



