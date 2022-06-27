<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<%--

    Paste's view top panel (control buttons)

--%>

<c:set var="priorTitle">
    <fmt:message key="${model.priority.name}" />
</c:set>


<div class="row">
    <div class="col-md-11">

        <%--

            back to list button
        --%>

        <a href="<c:url value=" /main/paste/list" />" target="${target}"
        title="
        <fmt:message key="paste.list.title" />">
        <span style="font-size: larger;" class="i">(</span>
        </a>


        <%--

            Priority
        --%>

        <span class="i ${model.priority.cssClass}" style="font-size:2em;" title="${priorTitle}">/</span>
        <c:if test="${model.sticked}">
            <span class="i">]</span>
        </c:if>



        <%--


            Paste's title

        --%>

        <a href="<c:url value=" /main/paste/edit/${model.id}" />"
        title="
        <fmt:message key="button.edit" />">
        <c:out value="${model.name}" escapeXml="true" />
        </a>

    </div>

    <div class='col-md-1'>

        <tiles:insertDefinition name="/common/deleteLink">
            <tiles:putAttribute name="model" value="${model}" />
            <tiles:putAttribute name="modelName" value="paste" />
            <tiles:putAttribute name="currentUser" value="${currentUser}" />
        </tiles:insertDefinition>
    </div>



</div>

<tiles:insertDefinition name="/common/pasteControls">
<tiles:putAttribute name="model" value="${model}" />

<c:if test="${not empty availableNext}">
    <tiles:putAttribute name="next" value="${availableNext}" />
</c:if>
<c:if test="${availablePrev!=null}">
    <tiles:putAttribute name="prev" value="${availablePrev}" />
</c:if>
</tiles:insertDefinition>
