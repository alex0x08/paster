<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />
<tiles:importAttribute name="currentUser" ignore="true" />


<sec:authorize access="${currentUser !=null and (currentUser.admin or ( model.hasOwner  and model.owner eq currentUser)) }">
    |  <a href="<c:url value='/main/${modelName}/delete'><c:param name="id" value="${model.id}"/> </c:url>"
          title="<fmt:message key='button.delete'/>">
    <span style="font-size: larger;" class="i">d</span>
    </a>
</sec:authorize>
