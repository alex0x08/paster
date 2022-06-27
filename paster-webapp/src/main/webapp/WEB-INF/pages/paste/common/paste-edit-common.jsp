<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<%--

        Common logic for paste's edit page
        
--%>


<fmt:message var="submit_button_text" key="button.save" />

<c:url var="url" value='/main/paste/save' />
<c:url var="urlCancel" value='/main/paste/list' />



<div class="row">
    <div class="col-md-12">

        <form:form cssClass="form-horizontal" id="editForm" action="${url}" role="form" modelAttribute="model"
            method="POST">


            <form:input id="thumbImg" path="thumbImage" cssStyle="display:none;" />
            <form:input id="wordsCount" path="wordsCount" cssStyle="display:none;" />
            <form:input id="symbolsCount" path="symbolsCount" cssStyle="display:none;" />

            <div class="form-group">

                <div class="col-md-8 col-xs-10">


                    <ul class="nav nav-tabs" id="pasteTab" role="tablist">
                        <li class="nav-item" role="presentation">



                            <a class="nav-link active" id="home-tab" data-bs-toggle="tab" data-bs-target="#home"
                                aria-controls="home" aria-current="page" href="#">
                                <c:choose>
                                    <c:when test="${model.blank}">
                                        <fmt:message key="paste.new" />
                                    </c:when>
                                    <c:otherwise>
                                        <span>#
                                            <c:out value="${model.id}" /></span>
                                    </c:otherwise>
                                </c:choose>
                            </a>
                        </li>
                        <li class="nav-item dropdown" role="presentation">
                            <a class="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#" role="button"
                                aria-expanded="false">
                                <fmt:message key="paste.editor.title" /><span class="caret"></span>

                            </a>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" id="syntax-tab-header" href="#" data-bs-toggle="tab"
                                        data-bs-target="#syntaxTab" aria-controls="profile">
                                        <fmt:message key="paste.syntax.title" /></a>

                                    </a>
                                </li>

                                <li><a class="dropdown-item" id="color-tab-header" href="#" data-bs-toggle="tab"
                                        data-bs-target="#colorTab" aria-controls="profile">
                                        <fmt:message key="paste.color.title" /></a>

                                    </a>
                                </li>

                                <li>
                                    <hr class="dropdown-divider" />
                                </li>
                                <li>
                                    <a class="dropdown-item" id="remote-file-tab-header" href="#" data-bs-toggle="tab"
                                        data-bs-target="#openRemoteFileTab" aria-controls="profile">
                                        Remote url
                                    </a>

                                </li>
                                <li>
                                    <a class="dropdown-item" id="local-file-tab-header" href="#" data-bs-toggle="tab"
                                        data-bs-target="#openLocalFileTab" aria-controls="profile">
                                        Local file
                                    </a>

                                </li>

                            </ul>
                        </li>
                        <li class="nav-item" role="presentation">
                            <a class="nav-link" href="#" id="contact-tab" data-bs-target="#contact" data-bs-toggle="tab"
                                aria-controls="contact">Link</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link disabled" href="#" tabindex="-1" aria-disabled="true">Disabled</a>
                        </li>
                    </ul>


                    <div class="tab-content" id="pasteTabContent">
                        <div class="tab-pane fade show active" id="home" role="tabpanel" aria-labelledby="home-tab">
                        

                            <div class="col-xs-12 col-sm-8 col-md-8 col-lg-8">

                                <c:choose>
                                    <c:when test="${model.blank}">
                                        <form:hidden path="integrationCode"  />
                                    </c:when>
                                    <c:otherwise>
                                        <form:hidden path="id"  />
                                    </c:otherwise>
                                </c:choose>

                                <fmt:message var="pasteTitle" key="paste.title"/>

                                <form:label path="name">
                                    <c:out value="${pasteTitle}"/>
                                    <a id="cleanTitleBtn" onclick="cleanTitle();" href="javascript:void(0);" title="<fmt:message key='button.clear'/>">
                                        <span class="i">d</span>
                                    </a> </form:label>
                                <fmt:message key="paste.edit.title.placeholder" var="titlePlaceHolder"/>
                                <form:input  cssClass="form-control" 
                                             path="name" name="title"
                                             id="pname"  maxlength="255" title="${pasteTitle}" placeholder="${titlePlaceHolder}"  />


                                <form:errors element="div" path="name" cssClass="alert alert-danger" />


                            </div>

                            <div class="col-xs-8 col-sm-6 col-md-3 col-lg-3 " >


                                <form:label cssClass="control-label" path="priority">
                                    <fmt:message key="paste.priority"/>

                                </form:label>

                                <p>

                                    <form:select 
                                        path="priority" multiple="false" id="pprior">
                                        <c:forEach items="${availablePriorities}" var="prior">
                                            <form:option value="${prior.code}" cssClass="${prior.cssClass}" x-css-class-name="${prior.cssClass}">
                                                <fmt:message key="${prior.name}"/>
                                            </form:option>
                                        </c:forEach>
                                    </form:select> 

                                    <span id="priorPreview" 
                                          class="i priority_normal"
                                          style="font-size:4em;line-height: 18px;vertical-align: top;" >/</span>

                                </p>


                            </div>


                            <div class="col-xs-6 col-sm-4 col-md-4 col-lg-2 " >


                                <form:label cssClass="control-label" path="channel">
                                    <fmt:message key="paste.channel"/>

                                </form:label>

                                <p>

                                    <form:select 
                                        path="channel" multiple="false" id="pchan">
                                        <c:forEach items="${availableChannels}" var="channel">
                                            <form:option value="${channel.code}"  >
                                                <fmt:message key="${channel.name}"/>
                                            </form:option>
                                        </c:forEach>
                                    </form:select> 


                                </p>


                            </div>        

                            <div class="col-md-4 col-xs-10">
                                <form:label
                                    cssClass="control-label" path="tagsAsString">
                                    <span  class="i" >T</span><fmt:message key="paste.tags"/>
                                </form:label>

                                <c:url var="tagsUrl" value='/main/paste/tags/names.json'/>

                                <form:input cssClass="form-control" 
                                            data-behavior="Autocomplete" 
                                            data-autocomplete-url="${tagsUrl}"
                                            data-autocomplete-options="'allowDupes':false"
                                            id="ptags" path="tagsAsString" maxlength="155" 
                                            />
                            </div>

                            <div class="col-md-4 col-xs-6" >

                                <fmt:message var="titleStick" key='paste.stick'/> 
                                <form:label  cssClass="control-label" path="sticked" title="${titleStick}" >
                                    <span class="i" >]</span> <c:out value="${titleStick}"/>
                                </form:label>     

                                <div class="checkbox">

                                    <form:checkbox path="sticked"  
                                                   title="${titleStick}"/>
                                </div>

                            </div>

                            <div class="col-md-3 col-xs-6" >
                                <fmt:message key='paste.normalize' var="labelNormalize"/>

                                <form:label path="normalized" cssClass="control-label" title="${labelNormalize}" >
                                    ${labelNormalize}
                                </form:label>

                                <div class="checkbox">
                                    <form:checkbox id="normalized" path="normalized"  title="${labelNormalize}"/>
                                </div>
                            </div>

                        </div>

                        <div class="tab-pane fade" id="openLocalFileTab" role="tabpanel"
                            aria-labelledby="local-file-tab-header">

                            <div class="col-xs-4 col-sm-3 col-md-3 col-lg-2">


                                <form:label path="codeType" cssClass="control-label" title="Select syntax">
                                    Select file
                                </form:label>

                                <span class="btn btn-primary btn-file">
                                    Browse
                                    <input type="file" accept="text/*" id="select-file-btn" />

                                </span>



                                <p class="help-block">
                                    Supported extensions: .txt, .java

                                </p>
                            </div>

                        </div>


                        <div class="tab-pane fade" id="openRemoteFileTab" role="tabpanel"
                            aria-labelledby="remote-file-tab-header">

                            <div class="col-xs-4 col-sm-3 col-md-3 col-lg-2">

                                <form:label path="codeType" cssClass="control-label" title="Select syntax">
                                    Enter url
                                </form:label>


                                <p class="help-block">
                                    <a class="btn btn-xs" href="#">
                                        <i class="fa fa-check-square-o"></i>Set as default</a>
                                </p>
                            </div>

                        </div>

                        <div class="tab-pane fade" id="colorTab" role="tabpanel" aria-labelledby="color-tab-header">

                            <div class="col-xs-6 col-sm-4 col-md-4 col-lg-3">

                                <label for="ptheme" class="control-label">
                                    Select color theme
                                </label>

                                <select id="ptheme" size="1">
                                    <optgroup label="Bright">
                                        <option value="ace/theme/chrome">Chrome</option>
                                        <option value="ace/theme/clouds">Clouds</option>
                                        <option value="ace/theme/crimson_editor">Crimson Editor</option>
                                        <option value="ace/theme/dawn">Dawn</option>
                                        <option value="ace/theme/dreamweaver">Dreamweaver</option>
                                        <option value="ace/theme/eclipse">Eclipse</option>
                                        <option value="ace/theme/github">GitHub</option>
                                        <option value="ace/theme/solarized_light">Solarized Light</option>
                                        <option value="ace/theme/textmate" selected="selected">TextMate</option>
                                        <option value="ace/theme/tomorrow">Tomorrow</option>
                                        <option value="ace/theme/xcode">XCode</option>
                                    </optgroup>
                                    <optgroup label="Dark">
                                        <option value="ace/theme/ambiance">Ambiance</option>
                                        <option value="ace/theme/chaos">Chaos</option>
                                        <option value="ace/theme/clouds_midnight">Clouds Midnight</option>
                                        <option value="ace/theme/cobalt">Cobalt</option>
                                        <option value="ace/theme/idle_fingers">idleFingers</option>
                                        <option value="ace/theme/kr_theme">krTheme</option>
                                        <option value="ace/theme/merbivore">Merbivore</option>
                                        <option value="ace/theme/merbivore_soft">Merbivore Soft</option>
                                        <option value="ace/theme/mono_industrial">Mono Industrial</option>
                                        <option value="ace/theme/monokai">Monokai</option>
                                        <option value="ace/theme/pastel_on_dark">Pastel on dark</option>
                                        <option value="ace/theme/solarized_dark">Solarized Dark</option>
                                        <option value="ace/theme/twilight">Twilight</option>
                                        <option value="ace/theme/tomorrow_night">Tomorrow Night</option>
                                        <option value="ace/theme/tomorrow_night_blue">Tomorrow Night Blue</option>
                                        <option value="ace/theme/tomorrow_night_bright">Tomorrow Night Bright</option>
                                        <option value="ace/theme/tomorrow_night_eighties">Tomorrow Night 80s</option>
                                        <option value="ace/theme/vibrant_ink">Vibrant Ink</option>
                                    </optgroup>
                                </select>
                                <p class="help-block">
                                    <a class="btn btn-xs" href="#"><i class="fa fa-check-square-o"></i> Set as
                                        default</a></p>

                            </div>
                            <div class="col-xs-4 col-sm-3 col-md-3 col-lg-2">

                                <label for="fontsize">Font Size</label>

                                <select id="fontsize" size="1">
                                    <option value="10px">10px</option>
                                    <option value="11px">11px</option>
                                    <option value="12px" selected="selected">12px</option>
                                    <option value="13px">13px</option>
                                    <option value="14px">14px</option>
                                    <option value="16px">16px</option>
                                    <option value="18px">18px</option>
                                    <option value="20px">20px</option>
                                    <option value="24px">24px</option>
                                </select>

                            </div>

                        </div>
                        <div class="tab-pane fade" id="syntaxTab" role="tabpanel" aria-labelledby="syntax-tab-header">

                            <div class="col-xs-4 col-sm-3 col-md-3 col-lg-2">

                                <form:label path="codeType" cssClass="control-label" title="Select syntax">
                                    Select syntax
                                </form:label>

                                <form:select path="codeType" multiple="false" id="ptype">
                                    <c:forEach items="${availableCodeTypes}" var="codeType">
                                        <form:option value="${codeType.code}" editCode="${codeType.editType}">
                                            <fmt:message key="${codeType.name}" />
                                        </form:option>
                                    </c:forEach>
                                </form:select>
                                <p class="help-block">
                                    <a class="btn btn-xs" href="#">
                                        <i class="fa fa-check-square-o"></i>Set as default</a>
                                </p>



                            </div>




                        </div>
                    </div>



                </div>
                <div class="col-md-3 col-sm-2 hidden-xs">

                    <c:choose>
                        <c:when test="${empty currentUser and !allowAnonymousCommentsCreate}">

                            <div class="alert alert-warning" role="alert">
                                <fmt:message key="auth.login-to-save-changes" />

                            </div>

                        </c:when>
                        <c:otherwise>

                            <button id="addCommentBtn" class="btn btn-primary submitBtn" name="submit_btn"
                                type="submit">
                                <span class="i">S</span>
                                <span id="btnCaption">
                                    <c:out value='${submit_button_text}' /></span>
                                <i id="btnIcon" style="display:none;" class="fa fa-spinner"></i>
                            </button>


                        </c:otherwise>
                    </c:choose>



                    <a class="btn btn-default btn-xs" href="<c:out value=" ${urlCancel}" />"><i class="fa fa-ban"></i>
                    <fmt:message key='button.cancel' /></a>

                    <c:if test="${!model.blank}">
                        <tiles:insertDefinition name="/common/deleteLink">
                            <tiles:putAttribute name="model" value="${model}" />
                            <tiles:putAttribute name="modelName" value="paste" />
                            <tiles:putAttribute name="currentUser" value="${currentUser}" />
                        </tiles:insertDefinition>

                    </c:if>

                </div>

            </div>


            <div class="form-group">
                <div class="col-md-10">

                    <form:label path="text">
                        <fmt:message key="paste.text" />
                    </form:label>
                    <form:textarea path="text" cssErrorClass="error" cssClass="notice" cssStyle="display:none;"
                        name="text" id="ptext" placeHolder="paste text" cols="120" rows="10" />
                    <div id="editor" style="height: 50em;">
                    </div>

                    <form:errors path="text" cssClass="alert alert-danger" element="div" />


                    <div class="form-buttons">
                        <div class="button">

                            <c:choose>
                                <c:when test="${empty currentUser and !allowAnonymousCommentsCreate}">

                                    <div class="alert alert-warning" role="alert">
                                        <fmt:message key="auth.login-to-save-changes" />

                                    </div>
                                </c:when>
                                <c:otherwise>

                                    <button name="submit_btn" class="btn btn-primary submitBtn" type="submit">
                                        <i id="btnIcon" style="display:none;" class="fa fa-spinner"></i>
                                        <span class="i">S</span>
                                        <span id="btnCaption">
                                            <c:out value='${submit_button_text}' /></span>
                                    </button>


                                </c:otherwise>
                            </c:choose>
                            <a class="btn btn-default btn-xs" href="<c:out value=" ${urlCancel}" />">
                            <i class="fa fa-ban"></i>
                            <fmt:message key='button.cancel' /></a>

                            <c:if test="${!model.blank}">


                                <tiles:insertDefinition name="/common/deleteLink">
                                    <tiles:putAttribute name="model" value="${model}" />
                                    <tiles:putAttribute name="modelName" value="paste" />
                                    <tiles:putAttribute name="currentUser" value="${currentUser}" />
                                </tiles:insertDefinition>

                            </c:if>

                            <div style="float:right;margin-top: 2em;">

                                <div id="wordCount"></div>

                                <c:if test="${!model.blank}">
                                    <kc:prettyTime date="${model.lastModified}"
                                        locale="${pageContext.response.locale}" />

                                </c:if>

                            </div>


                        </div>
                    </div>

                </div>
            </div>

        </form:form>
    </div>
</div>


<script type="text/javascript">

    var pasterEdit = new PasterEdit();

    window.addEventListener('load', function () {

        pasterEdit.init('${model.codeType.editType}');

        window.addEventListener('paste', function (e) {
            pasterEdit.onPaste(e);
        });

    });

</script>