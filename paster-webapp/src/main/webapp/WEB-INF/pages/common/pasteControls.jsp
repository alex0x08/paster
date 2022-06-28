<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



        <%--

            Renders paste's control buttons
        
        --%>    

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
     
    <div class="col-md-10">
        
         <span style="font-weight: normal; font-size: 12px;">

                <tiles:insertDefinition name="/common/tags" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                </tiles:insertDefinition>

               Created by
                <tiles:insertDefinition name="/common/owner" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                </tiles:insertDefinition> ,

                <tiles:insertDefinition name="/common/commentCount" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
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
    
    <div class="col-md-2">

        <a class="img-map img-xml" href="<c:url value='/main/paste/${model.id}.xml'/>" target="${target}" title="View as XML">
        </a> |
        <a class="img-map img-json" href="<c:url value='/main/paste/${model.id}.json'/>" target="${target}" title="View as JSON">
        </a> |
        <a href="<c:url value='/main/paste/${model.id}.txt'/>" target="${target}" title="View as plain text">
            <span style="font-size: larger;" class="i">k</span>
        </a> |


        <c:if test="${mode ne 'icon'}">
            <a id="ctrlc_link" data-clipboard-target="${model.id}_pasteText" href="javascript:void(0);"
               title="Copy to clipboard" >
                <i class="img-map img-clip"></i></a>
        </c:if>


    </div>
</div>
<div  class="row">
    <div class="col-md-2">
  
        <c:if test="${mode ne 'raw' and not empty prev}">
            <a href="<c:url value='/${prev.id}'/>" target="${target}" title="<fmt:message key="button.prev"/>">&#8592;</a>
        </c:if>
       
        <span class="f-h4">${model.id}</span>

        <c:if test="${mode ne 'raw' and not empty next}">
            <a href="<c:url value='/${next.id}'/>" target="${target}" title="<fmt:message key="button.next"/>">&#8594;</a>
        </c:if>

    </div>
</div>




