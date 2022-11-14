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


<jsp:include page="/WEB-INF/pages/paste/common/paste-view-top.jsp"/>
<jsp:include page="/WEB-INF/pages/paste/common/paste-view-common.jsp"/>

<div id="numSpace" class="line" >
</div>

<span id="pasteLineCopyBtn" style="display:none; white-space: normal;">
    <a id="ctrlc_line" 
       data-clipboard-target="pasteLineToCopy" href="javascript:void(0);" 
       style="float:left;" title="Copy to clipboard" >
        <span class="img-map img-clip"></span>
    </a> 
</span>

<span id="pasteLineToCopy" style="display:none;">
    NONE
</span>

<div id="pageLoadSpinner" style='display:none;' >
    <i class="fa fa-spinner"></i>
    <fmt:message key="action.loading"/>
</div>
<div id="morePages"></div>

<%--
<c:if test="${availablePrevList.count > 0}">
 
</c:if>
--%>

<c:choose>
    <c:when test="${model.reviewImgData!=null}">
        <c:url var="drawImg"
            value='/main/paste-resources/${appId}/r/${model.lastModifiedDt.time}/paste_content/${model.reviewImgData}' />
    </c:when>
    <c:otherwise>
        <c:set var="drawImg" value="" />
    </c:otherwise>
</c:choose>

<span id="${model.id}_drawImg" style="display:none">
    <c:out value="${drawImg}"/>
</span>
