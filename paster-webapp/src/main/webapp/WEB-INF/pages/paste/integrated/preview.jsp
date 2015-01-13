<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class="row">
    <div class="column grid-14">
        <c:set var="priorTitle"><fmt:message key="${model.priority.name}"/></c:set>

        <h4 class="f-h4" style="padding-top: 0;margin-top:0;"><span class="i ${model.priority.cssClass}" style="font-size:2em;" title="${priorTitle}" >/</span>
            <c:if test="${model.sticked}">
                <span class="i">]</span>
            </c:if>
            <c:out value="${model.name}" escapeXml="true"/>

            <c:if test="${not empty model.integrationCode}">
                (integrated with <c:out value="${model.integrationCode}"/>)
            </c:if>

           <span style="font-weight: normal; font-size: 12px;">

                  <tiles:insertDefinition name="/common/tags" >
                      <tiles:putAttribute name="model" value="${model}"/>
                      <tiles:putAttribute name="modelName" value="paste"/>
                  </tiles:insertDefinition>

                <tiles:insertDefinition name="/common/owner" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                </tiles:insertDefinition>

               <tiles:insertDefinition name="/common/commentCount" >
                   <tiles:putAttribute name="model" value="${model}"/>
                   <tiles:putAttribute name="modelName" value="paste"/>
               </tiles:insertDefinition>

                <span style="font-size: 9px;">
                ,<kc:prettyTime date="${model.lastModified}" locale="${pageContext.response.locale}"/>

                </span>

               <sec:authorize ifAnyGranted="ROLE_ADMIN">
                   |  <a href="<c:url value='/main/paste/delete'><c:param name="id" value="${model.id}"/> </c:url>"><fmt:message key='button.delete'/></a>
               </sec:authorize>


           </span>
        </h4>


        <tiles:insertDefinition name="/common/pasteControls" >
            <tiles:putAttribute name="model" value="${model}"/>
            <tiles:putAttribute name="mode" value="icon"/>

            <tiles:putAttribute name="next" value="${availableNext}"/>
            <tiles:putAttribute name="prev" value="${availablePrev}"/>

        </tiles:insertDefinition>

    </div>
</div>

<img style="border: 2px saddlebrown;" src="${model.thumbImageRead}"/>




