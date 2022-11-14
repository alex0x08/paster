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
<div class="row">     
    <div class="col-md-10">        
         <span style="font-weight: normal; font-size: 12px;">
                <tiles:insertDefinition name="/common/tags" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                </tiles:insertDefinition>
               <fmt:message key="paste.createdBy" />
                   <tiles:insertDefinition name="/common/owner" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                </tiles:insertDefinition>,
                <tiles:insertDefinition name="/common/commentCount" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                </tiles:insertDefinition>
                <span style="font-size: 9px;">
                    <fmt:message key="${'code.type.'.concat(model.codeType)}"/>
                    ,<kc:prettyTime date="${model.lastModifiedDt}"
                            locale="${pageContext.response.locale}"/>
                </span>
                 <c:if test="${not empty model.integrationCode}">
                (integrated with <c:out value="${model.integrationCode}"/>)
            </c:if>                
            </span>  
    </div>
    
    <div class="col-md-2">
        <a class="img-map img-xml"
                href="<c:url value='/main/paste/${model.id}.xml'/>"
                target="_blank" title="View as XML">
        </a> |
        <a class="img-map img-json"
                href="<c:url value='/main/paste/${model.id}.json'/>"
                target="_blank" title="View as JSON">
        </a> |
        <a href="<c:url value='/main/paste/${model.id}.txt'/>"
                target="_blank"
                title="View as plain text">
            <span style="font-size: larger;" class="i">k</span>
        </a>
    </div>
</div>
<div  class="row">
    <div class="col-md-2">  
        <c:if test="${mode ne 'raw' and not empty prev}">
            <a href="<c:url value='/${prev.id}'/>"
                target="${target}" title="<fmt:message key="button.prev"/>">&#8592;</a>
        </c:if>       
        <span class="f-h4">${model.id}</span>
        <c:if test="${mode ne 'raw' and not empty next}">
            <a href="<c:url value='/${next.id}'/>"
                target="${target}" title="<fmt:message key="button.next"/>">&#8594;</a>
        </c:if>
    </div>
</div>
