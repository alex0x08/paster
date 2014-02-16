<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<h1>

             <fmt:message key="settings.edit.title">
                <fmt:param value="${model.name}"/>
            </fmt:message>

</h1>


<c:url var="url" value='/main/admin/settings/save' />

<form:form action="${url}" 
           modelAttribute="model"
           method="POST" enctype="multipart/form-data">

    <form:errors path="*" cssClass="errorblock" element="div"/>


 
    <fieldset>
        <div class="form-row">
            <label for="ptitle"><fmt:message key="user.name"/>:</label>
            <span class="input">
                <form:input path="name" name="title" id="ptitle"  />
                <form:errors path="name" cssClass="error" /> 

            </span>
        </div>

        <div class="form-row">
            <label for="pdesc"><fmt:message key="project.description"/>:</label>
            <span class="input">
                <form:input path="description" name="description" id="pdesc"  />
                <form:errors path="description" cssClass="error" />

            </span>
        </div>

                
      

        <div class="form-buttons">
            <div class="button">
                <input name="cancel" type="submit" value="<fmt:message key="button.cancel"/>" />
                <fmt:message var="submit_button_text" key="button.save"/>
                <input name="submit" type="submit" value="${submit_button_text}" />
            </div>
        </div>
    </fieldset>
</form:form>



<a href="<c:url value="/main/paste/list"/>"><fmt:message key="paste.list.title"/></a> /
