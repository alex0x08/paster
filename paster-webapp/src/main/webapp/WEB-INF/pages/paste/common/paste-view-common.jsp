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

    Shared logic (between normal and raw output) of paste's view page
    Paste's view main block
    
--%>
<div class="row">
    <div id="${model.id}_centerPanel" class="col-md-12" style="min-width:650px;">
        <c:url var="drawImg"
            value='/main/paste-resources/${appId}/r/${model.lastModifiedDt.time}/paste_content/${model.reviewImgData}' />
<%--
        All |Comments|Draw top toggle
--%>
<div id='pasteViewControls' class="btn-group float-end paste-view-controls" role="group" aria-label="Controls">
    <button id="${model.id}_btnShowAll" type="button"
            title="<fmt:message key='button.paste.all' />"
            class="btn btn-secondary btn-sm active">
        <i class="fa fa-eye" aria-hidden="true"></i>
    </button>
    <button id="${model.id}_btnShowComments" type="button"
        title=" <fmt:message key='button.paste.comments' />"
        class="btn btn-sm btn-secondary">
        <i class="fa fa-comments" aria-hidden="true"></i>
    </button>
    <button id="${model.id}_btnShowDraw" type="button"
        title=" <fmt:message key='button.paste.draw' />"
        class="btn btn-sm btn-secondary">
        <i class="fa fa-paint-brush" aria-hidden="true"></i>
    </button>
</div>
<%--
    Review image render (if exist)
--%>
<c:set var="backgroundReviewStyle"
            value="${model.reviewImgData==null ? '' : 'pointer-events:none;background: url('.concat(drawImg).concat(') no-repeat top left;') }" />
    <div id="${model.id}_all" style="display:none;z-index:500;position:absolute;pointer-events:none;">
        <canvas id="${model.id}_sketch_ro"
                with="400"
                height="200" style="${backgroundReviewStyle}">
        </canvas>
    </div>
<%--
    Draw mode, paint options
--%>
<div id="${model.id}_drawBlock" style="display:none;padding-top: 1em;">
            <div class="row justify-content-between">
                <div class="col-md-8">
                    <div id="${model.id}_tools" class="tools">
                    </div>
                </div>
                <div class="col-auto">
                    <c:if test="${not empty currentUser or allowAnonymousCommentsCreate}">
                        <c:url var="urlDrawSave" value='/main/paste/saveReviewDraw' />
                        <form:form cssClass="form-horizontal"
                                   action="${urlDrawSave}"
                                   id="${model.id}_saveReviewDraw"
                            method="POST">
                            <input type="hidden" name="pasteId" value="${model.id}" />
                            <input id="${model.id}_reviewDrawImg" name="reviewImgData" type="hidden" value="" />
                            <input id="${model.id}_thumbImg" name="thumbImgData" type="hidden" value="" />
                            <button id="${model.id}_saveReviewBtn"
                                    class='btn btn-danger p-btn-save' type="button">
                                <span class="i" style="font-size:larger;">S</span>
                                <span id="btnCaption">
                                    <fmt:message key="button.save" /></span>
                                <i id="btnIcon" style="display:none;" class="fa fa-spinner fa-spin"></i>
                            </button>    
                        </form:form>
                    </c:if>              
                </div>
            </div>
    <%--
        Draw mode, draw area
    --%>
    <div id="${model.id}_drawArea"
        style="z-index:5000;position:absolute;cursor:crosshair;background-color:rgba(100,70,0,0.1);">
    </div>
</div>
<%--
        Paste's content
--%>
<div id="${model.id}_pasteBodyContent">
            <pre id="${model.id}_pasteText"
                class="brush: ${model.codeType};toolbar: false; auto-links:false;highlight: [${commentedLinesList}]; "
                style="display:none; overflow-y: hidden;">
                        <c:out value="${model.text}" escapeXml="true" />
            </pre>
        </div>
    </div>
</div>
<%--

    Comments

--%>
<div id="${model.id}_commentsList" style="display:none;">
    <c:forEach var="comment" items="${model.comments}" varStatus="loopStatus">
        <div id="${model.id}_numSpace_l${comment.id}" class="line">
            <a href="#comment_l${comment.id}"
                title="<c:out value=" ${comment.id}" />">#</a>
        </div>
        <div id="${model.id}_comment_l${comment.id}"
            class="commentBlock ${comment.parentId==null ? 'parentComment' :'subComment'}"
                    commentId="${comment.id}"
            lineNumber="${comment.lineNumber}" parentCommentId="${comment.parentId}">
            <div id="innerBlock" class="commentInner p-comment">
                <div class="row">
                    <div class="col-md-12">
                        <%--  should be exact inside div! otherwise marked will fail to parse --%>
                        <div id="commentMarkedText" class="previewer">
                            <c:out value="${comment.text}" escapeXml="false" />
                        </div>
                    </div>
                </div>
                <div class="row justify-content-between">
                    <div class="col-md-8">
                        <small>
                            <tiles:insertDefinition name="/common/owner">
                                <tiles:putAttribute name="model" value="${comment}" />
                                <tiles:putAttribute name="modelName" value="comment" />
                            </tiles:insertDefinition>
                            | <kc:prettyTime
                                    date="${comment.lastModifiedDt}"
                                    format="${dateTimePattern}"
                                    locale="${pageContext.response.locale}" />
                        </small>
                    </div>
                    <div class="col-auto" style="padding-right: 1em;" >
                        <c:if test="${(not empty currentUser or allowAnonymousCommentsCreate) && comment.parentId==null}">                        
                            <a href="#" class="linkLine pInsertCommentTrigger"
                                title="<fmt:message key='comments.sub' />"
                                plineNumber="${comment.lineNumber}" 
                                pCommentId="${comment.id}" >
                                <span class="i">C</span>
                            </a>
                        </c:if>
                        <sec:authorize
                            access="${currentUser !=null and (currentUser.admin or ( comment.hasAuthor and comment.author eq currentUser)) }">

                            <c:url var='removeCommentUrl' value='/main/paste/removeComment'>
                                                                <c:param name='pasteId' value='${model.id}' />
                                                                <c:param name='commentId' value='${comment.id}' />
                                                                <c:param name='lineNumber' value='${comment.lineNumber}' />
                                                        </c:url>
                            <a class="deleteBtn"
                                id="deleteCommentBtn_${model.id}_${comment.id}"
                                href="${removeCommentUrl}"
                            title="<fmt:message key='button.delete' />">
                                <span style="font-size: larger;" class="i">d</span>                          
                            </a>
                            <div style="display:none;" id="dialogMsg">
                                <fmt:message key="dialog.confirm.paste.remove.comment">
                                    <fmt:param value="${model.id}" />
                                </fmt:message>
                            </div>
                        </sec:authorize>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
<%--
    Comment form
--%>
<c:url var="url" value='/main/paste/saveComment' />
<div id="${model.id}_commentForm" 
        class="row p-comment" 
        style="display:none;max-width: 60em;margin-left:-10px;">
    <div class="col-md-11">
        <div class="panel panel-info" style="margin-bottom:0;">
            <div class="panel-body bg-info" style="padding:0.1em;">
                <c:choose>
                    <c:when test="${empty currentUser and !allowAnonymousCommentsCreate}">
                        <fmt:message key="auth.login-to-add-comment" />
                        <form id="${model.id}_addCommentForm" style="display:none;max-width:30em;">
                            <input type="hidden" id="lineNumber" />
                            <input type="hidden" id="parentId" />
                            <input id="${model.id}_thumbImgComment"
                                name="thumbImage" type="hidden" value="" />
                            <fmt:message key="comments.line">
                                <fmt:param value="<span id='pageNum'></span>" />
                            </fmt:message>
                            <textarea id="commentText"></textarea>
                            <button id="${model.id}_addCommentBtn">
                            </button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form:form cssClass="form-horizontal" action="${url}" id="${model.id}_addCommentForm"
                            modelAttribute="comment" method="POST">
                            <form:hidden path="lineNumber" id="lineNumber" />
                            <form:hidden path="parentId" id="parentId" />
                            <input id="${model.id}_thumbImgComment"
                                name="thumbImage" type="hidden" value="" />
                            <form:hidden path="pasteId" />
                            <div class="form-group" style="margin-bottom:0.1em;">
                                <div class="col-md-12">
                                    <form:errors path="text" cssClass="control-label" element="label"
                                        for="commentText" />
                                    <div class="disableOnSubmit" id="epiceditor-${model.id}"></div>
                                    <form:textarea cssClass="form-control"
                                            path="text" id="commentText-${model.id}"
                                            cssStyle="display:none;" cssErrorClass="form-control" />
                                </div>
                            </div>
                            <div class="form-group row justify-content-between" style="margin-bottom:0.1em;">
                                <div class="col-md-4">
                                    <button id="${model.id}_addCommentBtn"
                                            class='sbtn p-btn-save' type="submit">
                                        <span class="i" style="font-size:larger;">S</span>
                                        <span id="btnCaption">
                                            <fmt:message key="button.add" /></span>
                                        <i id="btnIcon" style="display:none;" class="fa fa-spinner fa-spin"></i>
                                    </button>
                                    <a id="${model.id}_closeCommentBtn" 
                                        class="disableOnSubmit" 
                                        title="<fmt:message key="button.cancel" />"  href="#" >
                                    <span>
                                        <fmt:message key="button.cancel" /></span>
                                    </a>
                                </div>
                                <div id="${model.id}_errorMessage"
                                    class="col-md-4" style="display: none;">
                                   error message 
                                </div>
                                <div class="col-auto">
                                    <span style="padding-right: 0.5em;">
                                        <fmt:message key="comments.line">
                                            <fmt:param value="<span id='pageNum'></span>" />
                                        </fmt:message>
                                    </span>
                                </div>
                            </div>
                        </form:form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
