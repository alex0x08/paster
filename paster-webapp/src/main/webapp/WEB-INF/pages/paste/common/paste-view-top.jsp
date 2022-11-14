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
    Paste's view top panel (control buttons)
--%>
<c:set var="priorTitle">
    <fmt:message key="${model.priority}" />
</c:set>
<div class="row">
    <div class="col-md-10">
        <%--
            back to list button
        --%>
        <c:url var='backToListUrl' value='/main/paste/list'/>
        <a href="${backToListUrl}"
                target="${target}"
                title="<fmt:message key=" paste.list.title" />">
                    <span style="font-size: larger;" class="i">(</span>
        </a>

        <%--
            Priority
        --%>

        <span class="i ${model.priority}"
                style="font-size:2em;" title="${priorTitle}">/</span>
        <c:if test="${model.stick}">
            <span class="i">]</span>
        </c:if>

        <%--
            Paste's title
        --%>
        <c:out value="${model.title}" escapeXml="true" />
    </div>
    <div class='col-md-2'>
        <div class="btn-group" style="padding-top: 0.8em;">
            <c:if test="${not empty currentUser or allowAnonymousCommentsCreate}">
                <c:url var='pasteEditUrl' value='/main/paste/edit/${model.id}'/>

                <a class="btn btn-primary" href="${pasteEditUrl}"
                    title="<fmt:message key='button.edit' />">
                    <fmt:message key='button.edit' />
                </a>
                <sec:authorize
                    access="${currentUser !=null and (currentUser.admin or ( model.hasAuthor  and model.author eq currentUser)) }">
                    <c:url var='pasteDeleteUrl' value='/main/paste/delete'>
                            <c:param name='id' value='${model.id}' />
                    </c:url>
                    <a class="btn btn-danger btn-sm deleteBtn" id="deleteBtn_${model.id}"
                        href="${pasteDeleteUrl}"
                    title="<fmt:message key='button.delete' />">
                    <span class="i">d</span>
                    <fmt:message key='button.delete' />
                    </a>
                </sec:authorize>
            </c:if>
        </div>
        <div style="display:none;" id="dialogMsg">
            <c:url var='pastePreviewImgUrl'
                    value='/main/paste-resources/${appId}/t/${model.lastModifiedDt.time}/paste_content/${model.thumbImage}' />
            <img width="300" height="200" class="p-comment"
                style="width: 250px; height: 150px; float: left; margin: 5px;"
                src="${pastePreviewImgUrl}" />
            <fmt:message key="dialog.confirm.paste.remove.message">
                <fmt:param value="${model.id}" />
            </fmt:message>
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
