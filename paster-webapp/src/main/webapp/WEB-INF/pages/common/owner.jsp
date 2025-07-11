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

        Renders author details
--%>    

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />
<div style="padding:0.2em;display: inline;">
<c:choose>
    <c:when test="${not empty model.author and model.author ne currentUser.username}">
        <span style="  ">
            <a title="Contact ${model.author}"
            href="mailto:${model.author}?subject=<c:out value='${model.title}'
                    escapeXml="true"/>">
                    <c:out value="${model.author}" />
            </a>
        </span>
    </c:when>
    <c:when test="${not empty model.author and model.author eq currentUser.username}">
         <span style="display: inline;  ">
             <fmt:message key="user.you"/>
         </span>
    </c:when>
    <c:otherwise>
        <span style="font-size: 1.5em;" class="i" title="<fmt:message key='user.anonymous'/>">x</span>
    </c:otherwise>
</c:choose>

 <c:if test="${'paste' eq modelName && model.reviewed}">
            ,<fmt:message key="paste.reviewedBy" />
<c:choose>
    <c:when test="${not empty model.reviewer and model.reviewer ne currentUser.username}">
        <span style="  ">
            <a title="Contact ${model.reviewer}"
            href="mailto:${model.reviewer}?subject=<c:out value='${model.title}'
                    escapeXml="true"/>">
                    <c:out value="${model.reviewer}" />
            </a>
        </span>
    </c:when>
      <c:when test="${not empty model.reviewer and model.reviewer eq currentUser.username}">
             <span style="display: inline;  ">
                 <fmt:message key="user.you"/>
             </span>
        </c:when>
    <c:otherwise>
        <span style="font-size: 1.5em;" class="i" title="<fmt:message key='user.anonymous'/>">x</span>
    </c:otherwise>
</c:choose>


 </c:if>
</div>
