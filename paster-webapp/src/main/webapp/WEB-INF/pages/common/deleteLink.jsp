<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<%--

        Renders [delete] button for pastas
        
        
--%>

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />
<tiles:importAttribute name="currentUser" ignore="true" />


<sec:authorize
    access="${currentUser !=null and (currentUser.admin or ( model.hasOwner  and model.owner eq currentUser)) }">

    <a class="btn btn-danger btn-sm deleteBtn" id="deleteBtn_${model.id}"
        href="<c:url value='/main/${modelName}/delete'><c:param name="id" value="${model.id}" /></c:url>"
        title="<fmt:message key='button.delete' />">
        <span class="i">d</span>
        <fmt:message key='button.delete' />
    </a>

    <div style="display:none;" id="dialogMsg">
        <img width="300" height="200" class="p-comment" style="width: 250px; height: 150px; float: left; margin: 5px;"
            src="<c:url value='/main/resources/${appId}/t/static/paste_content/${model.thumbImage}' />" />
        <fmt:message key="dialog.confirm.paste.remove.message">
            <fmt:param value="${model.id}" />
        </fmt:message>
    </div>




</sec:authorize>