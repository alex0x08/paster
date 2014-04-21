<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<tiles:importAttribute name="model" />
<tiles:importAttribute name="mode" />
<tiles:importAttribute name="next" ignore="true" />
<tiles:importAttribute name="prev" ignore="true" />

 <c:choose>
     <c:when test="${mode eq 'widget' or mode eq 'icon'}">
         <c:set var="target" value="_blank"/>
     </c:when>
     <c:otherwise>
         <c:set var="target" value="_self"/>
     </c:otherwise>
 </c:choose>


<div  class="row">
    <c:if test="${mode ne 'raw'}">

        <div class="column grid-2">
            <a href="<c:url value="/main/paste/list"/>" target="${target}"
               title="<fmt:message key="paste.list.title"/>"><span style="font-size: larger;" class="i">(</span></a> |
            <a href="<c:url value="/main/paste/edit/${model.id}"/>" target="${target}"
               title="<fmt:message key="button.edit"/>"><span style="font-size: larger;" class="i">E</span>
            </a>
        </div>

    </c:if>
   
    <div class="column grid-4">
        
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

                <tiles:insertDefinition name="/common/deleteLink" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                    <tiles:putAttribute name="currentUser" value="${currentUser}"/>
                </tiles:insertDefinition>

                <span style="font-size: 9px;">
                    <fmt:message key="${model.codeType.name}"/>    
                    ,<kc:prettyTime date="${model.lastModified}" locale="${pageContext.response.locale}"/>
                </span>

                 <c:if test="${not empty model.integrationCode}">
                (integrated with <c:out value="${model.integrationCode}"/>)
            </c:if>

                
            </span>    
        
    </div>
    
    <div class="column grid-4">

        <a href="<c:url value="/main/paste/xml/${model.id}"/>" target="${target}" title="View as XML">
            <img src="<c:url value='/main/static/${appVersion}/images/xml.png'/>" title="xml" alt="xml"/>
        </a> |
        <a href="<c:url value="/main/paste/${model.id}.json"/>" target="${target}" title="View as JSON">
            <img src="<c:url value='/main/static/${appVersion}/images/json.png'/>" title="json" alt="json"/>
        </a> |
        <a href="<c:url value="/main/paste/plain/${model.id}"/>" target="${target}" title="View as plain text">
            <span style="font-size: larger;" class="i">k</span>
        </a> |


        <c:if test="${mode ne 'icon'}">
            <a id="ctrlc_link" data-clipboard-target="${model.id}_pasteText" href="javascript:void(0);"
               title="Copy to clipboard" >
                <i class="fa fa-clipboard"></i></a>
        </c:if>


    </div>

    <div class="column grid-4 right">
  
        <c:if test="${mode ne 'raw' and not empty prev}">
            <a href="<c:url value="/${prev.id}"/>" target="${target}" title="<fmt:message key="button.prev"/>">&#8592;</a>
        </c:if>
       
        <span class="f-h4">${model.id}</span>

        <c:if test="${mode ne 'raw' and not empty next}">
            <a href="<c:url value="/${next.id}"/>" target="${target}" title="<fmt:message key="button.next"/>">&#8594;</a>
        </c:if>

    </div>
</div>




