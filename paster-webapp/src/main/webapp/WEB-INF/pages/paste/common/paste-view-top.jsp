<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<%--

    Paste's view top panel (control buttons)

--%>

<c:set var="priorTitle">
    <fmt:message key="${model.priority.name}" />
</c:set>


<div class="row">
    <div class="col-md-10">

        <%--

            back to list button
        --%>

        <a href="<c:url value='/main/paste/list'/>" target="${target}" title="
        <fmt:message key=" paste.list.title" />">
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

        <c:out value="${model.name}" escapeXml="true" />


    </div>

    <div class='col-md-2'>


        <div class="btn-group" style="padding-top: 0.8em;">


            <c:if test="${not empty currentUser or allowAnonymousCommentsCreate}">


                <a class="btn btn-primary" href="<c:url value='/main/paste/edit/${model.id}'/>"
                    title="<fmt:message key='button.edit' />">
                    <fmt:message key='button.edit' />
                </a>


                <tiles:insertDefinition name="/common/deleteLink">
                    <tiles:putAttribute name="model" value="${model}" />
                    <tiles:putAttribute name="modelName" value="paste" />
                    <tiles:putAttribute name="currentUser" value="${currentUser}" />
                </tiles:insertDefinition>

            </c:if>


        </div>

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