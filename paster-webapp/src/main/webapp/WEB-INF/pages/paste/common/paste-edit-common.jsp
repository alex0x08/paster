<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%--
        Common logic for paste's edit page      
--%>
<fmt:message var="submit_button_text" key="button.save" />
<c:url var="url" value='/main/paste/save' />
<c:url var="urlCancel" value='/main/paste/list' />
<div class="row">
    <div class="col-md-12">
        <form:form cssClass="form-horizontal" id="editForm" 
        action="${url}" role="form" modelAttribute="model"
            method="POST">
            <form:input id="thumbImg" path="thumbImage" cssStyle="display:none;" />
            <form:input id="wordsCount" path="wordsCount" cssStyle="display:none;" />
            <form:input id="symbolsCount" path="symbolsCount" cssStyle="display:none;" />
            <div class="form-group">
                <ul class="nav nav-tabs" id="pasteTab" role="tablist">
                    <li class="nav-item" role="presentation">
                        <a class="nav-link active" id="home-tab" data-bs-toggle="tab" data-bs-target="#home"
                            aria-controls="home" aria-current="page" href="#">
                            <c:choose>
                                <c:when test="${model.blank}">
                                    <fmt:message key="paste.new" />
                                </c:when>
                                <c:otherwise>
                                    <span>Paste #<c:out value="${model.id}" /></span>
                                </c:otherwise>
                            </c:choose>
                        </a>
                    </li>
                    <li class="nav-item dropdown" role="presentation">
                        <a class="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#" role="button"
                            aria-expanded="false">
                            <fmt:message key='paste.menu.loadFrom' />
                            <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu">
                            <li>
                                <a class="dropdown-item" id="remote-file-tab-header" href="#" data-bs-toggle="tab"
                                    data-bs-target="#openRemoteFileTab" aria-controls="profile">
                                    <fmt:message key='paste.menu.loadFrom.remoteUrl' />
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" id="local-file-tab-header" href="#" data-bs-toggle="tab"
                                    data-bs-target="#openLocalFileTab" aria-controls="profile">
                                    <fmt:message key='paste.menu.loadFrom.localFile'/>
                                </a>
                            </li>
                        </ul>
                    </li>
                </ul>
                <div class="tab-content" id="pasteTabContent">
                    <div class="tab-pane fade show active" 
                                id="home" 
                                role="tabpanel" aria-labelledby="home-tab">
                        <div class="row justify-content-between">
                            <div class="col-md-6">
                                <c:choose>
                                    <c:when test="${model.blank}">
                                        <form:hidden path="integrationCode" />
                                    </c:when>
                                    <c:otherwise>
                                        <form:hidden path="id" />
                                    </c:otherwise>
                                </c:choose>
                                <fmt:message var="pasteTitle" key="paste.title" />
                                <form:label path="title">
                                    <c:out value="${pasteTitle}" />
                                    <a id="cleanTitleBtn"  href="#"
                                        title="<fmt:message key='button.clear'/>">
                                        <span class="i">d</span>
                                    </a> </form:label>
                                <fmt:message key="paste.edit.title.placeholder" var="titlePlaceHolder" />
                                <form:input cssClass="form-control" path="title" name="title" id="pname" maxlength="255"
                                    title="${pasteTitle}" placeholder="${titlePlaceHolder}" />
                                <form:errors element="div" path="title" cssClass="alert alert-danger" />
                            </div>
                            <div class="col-auto">
                                <form:label cssClass="control-label" path="priority">
                                    <fmt:message key="paste.priority" />
                                </form:label>
                                <p>
                                     <form:select path="priority" multiple="false" id="pprior" cssClass="form-select" style="display: inline;width: 8em;">
                                        <c:forEach items="${availablePriorities}" var="prior">
                                            <form:option value="${prior}" cssClass="${prior}"
                                                x-css-class-name="${ 'priority_'.concat(prior.toLowerCase())}">
                                                <c:out value="${prior}"/>
                                            </form:option>
                                        </c:forEach>
                                    </form:select>
                                    <span id="priorPreview" class="i priority_normal"
                                    style="padding-left:0.1em;font-size:4em;line-height: 18px;vertical-align: top; ">/</span>
                                </p>
                            </div>

                            <div class="col-md-2 me-auto">
                                <form:label cssClass="control-label" path="channel">
                                    <fmt:message key="paste.channel" />
                                </form:label>
                                <p>
                                    <form:select path="channel" multiple="false" id="pchan"  cssClass="form-select">
                                        <c:forEach items="${availableChannels}" var="channel">
                                            <form:option value="${channel}">
                                                <c:out value="${channel}"/>
                                            </form:option>
                                        </c:forEach>
                                    </form:select>
                                </p>
                            </div>
                            <div class="col-md-2">
                                <div class="pull-right" style='padding-top:1.5em;'>
                                    <c:choose>
                                        <c:when test="${empty currentUser and !allowAnonymousCommentsCreate}">
                                        <p>
                                        <fmt:message key="auth.login-to-save-changes" />
                                        </p>
                                        </c:when>
                                        <c:otherwise>
                                            <button id="addCommentBtn" class="btn btn-primary submitBtn"
                                                name="submit_btn" type="submit">
                                                <span class="i">S</span>
                                                <span id="btnCaption">
                                                    <c:out value='${submit_button_text}' /></span>
                                                <i id="btnIcon" style="display:none;" class="fa fa-spinner"></i>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                    <a class="btn btn-default btn-xs" 
                                            href="<c:out value='${urlCancel}' />">
                                            <i class="fa fa-ban"></i>
                                            <fmt:message key='button.cancel' />
                                    </a>

                                    <c:if test="${!model.blank}">
                                         <sec:authorize
                                            access="${currentUser !=null and (currentUser.admin or ( model.hasAuthor  and model.author eq currentUser)) }">
                                                <a class="btn btn-danger btn-sm deleteBtn" 
                                                        id="deleteBtn_${model.id}"
                                                        href="<c:url value='/main/paste/delete'>
                                                                        <c:param name='id' value='${model.id}' /></c:url>"
                                                                title="<fmt:message key='button.delete' />">
                                                            <span class="i">d</span>
                                                            <fmt:message key='button.delete' />
                                                </a>
                                        </sec:authorize>
                                            <div style="display:none;" id="dialogMsg">
                                                    <img width="300" height="200" class="p-comment"
                                                        style="width: 250px; height: 150px; float: left; margin: 5px;"
                                                        src="<c:url value='/main/paste-resources/${appId}/t/${model.lastModifiedDt.time}/paste_content/${model.thumbImage}' />" />
                                                    <fmt:message key="dialog.confirm.paste.remove.message">
                                                        <fmt:param value="${model.id}" />
                                                    </fmt:message>
                                                </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-1">
                                <form:label path="codeType" cssClass="control-label" title="Select syntax">
                                    <fmt:message key='paste.syntax.title' />
                                </form:label>
                                <form:select path="codeType" multiple="false" id="ptype" cssClass="form-select">
                                    <c:forEach items="${availableCodeTypes}" var="codeType">
                                        <form:option value="${codeType}" >
                                            <fmt:message key="${'code.type.'.concat(codeType)}" />
                                        </form:option>
                                    </c:forEach>
                                </form:select>
                            </div>  
                            <div class="col-md-4">
                                <form:label cssClass="control-label" path="tagsAsString">
                                    <span class="i">T</span>
                                    <fmt:message key="paste.tags" />
                                </form:label>
                                <c:url var="tagsUrl" value='/main/paste/tags/names.json' />
                                <form:input cssClass="form-control" data-behavior="Autocomplete"
                                    data-autocomplete-url="${tagsUrl}" data-autocomplete-options="'allowDupes':false"
                                    id="ptags" path="tagsAsString" maxlength="155" />
                            </div>
                            <div class="col-auto">
                                <fmt:message var="titleStick" key='paste.stick' />
                                <form:label cssClass="control-label" path="stick" title="${titleStick}">
                                    <span class="i">]</span>
                                    <c:out value="${titleStick}" />
                                </form:label>
                                <div class="checkbox">
                                    <form:checkbox path="stick" title="${titleStick}" />
                                </div>
                            </div>
                            <div class="col-md-2">
                                <fmt:message key='paste.normalize' var="labelNormalize" />
                                <form:label path="normalized" cssClass="control-label" title="${labelNormalize}">
                                    ${labelNormalize}
                                </form:label>
                                <div class="checkbox">
                                    <form:checkbox id="normalized" path="normalized" title="${labelNormalize}" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane fade" id="openLocalFileTab" role="tabpanel"
                        aria-labelledby="local-file-tab-header">
                        <div class="col-md-4">
                        <div class="mb-3">
                          <label for="formFile" class="form-label">Select local file</label>
                            <input class="form-control" type="file" accept="text/*,application/json" id="select-file-btn" />
                        </div>
                            <p class="help-block">
                                Supported text and source files.
                            </p>
                        </div>
                    </div>


                </div>
            </div>
            <div class="form-group">
                <form:label path="text">
                    <fmt:message key="paste.text" />
                </form:label>
                <form:textarea path="text"
                            cssErrorClass="error"
                            cssClass="notice"
                            cssStyle="display:none;"
                            name="text"
                            id="ptext"
                            placeHolder="paste text" cols="120" rows="10" />
                <div id="editor" style="height: 50em;">
                </div>
                <form:errors path="text" cssClass="alert alert-danger" element="div" />
            </div>
        </form:form>
    </div>
</div>

