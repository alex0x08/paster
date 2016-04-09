<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<fmt:message var="submit_button_text" key="button.save"/>

<c:url var="url" value='/main/paste/save' />

<c:choose>
    <c:when test="${empty param.frameMode}">
        <c:url var="urlCancel" value='/main/paste/list' />
    </c:when>
    <c:otherwise>
        <c:url var="urlCancel" value='javascript: window.parent.createNewPasteDlg.hide();' />
    </c:otherwise>
</c:choose>


<div class="row">
    <div class="col-md-12"   >

        <form:form cssClass="form-horizontal" id="editForm" action="${url}" role="form"
                   modelAttribute="model"
                   method="POST" >

            <c:if test="${not empty param.integratedMode}">
                <input type="hidden" name="integrationMode" value="true"/>
            </c:if>

            <c:if test="${not empty param.frameMode}">
                <input type="hidden" name="frameMode" value="true"/>
            </c:if>


            <form:input id="thumbImg" path="thumbImage" cssStyle="display:none;"  />
            <form:input id="wordsCount" path="wordsCount" cssStyle="display:none;"  />
            <form:input id="symbolsCount" path="symbolsCount" cssStyle="display:none;"  />

            <div class="form-group">

                <div class="col-md-8 col-xs-10">

                    <ul class="nav nav-tabs nav-append-content" data-behavior="BS.Dropdown BS.Tabs">
                        <li>
                            <a class="tab" aria-controls="mainTab">
                                <c:choose>
                                    <c:when test="${model.blank}">
                                        <fmt:message key="paste.new"/>
                                    </c:when>
                                    <c:otherwise>
                                        <span >#<c:out value="${model.id}"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </a>
                        </li>


                        <li  class="dropdown">
                            <a class="dropdown-toggle"

                               data-toggle="dropdown" >
                                <fmt:message key="paste.editor.title"/><span class="caret"></span>
                            </a>

                            <ul class="dropdown-menu" role="menu" id="theme">

                                <li role="presentation"  aria-controls="syntaxTab">
                                    <a role="menuitem"   ><fmt:message key="paste.syntax.title"/></a>
                                </li>
                                <li role="presentation" class="divider"></li>

                                <li role="presentation"  aria-controls="colorTab">
                                    <a role="menuitem"  ><fmt:message key="paste.editor.theme.title"/></a>
                                </li>
                            </ul>
                        </li>
                        
                         <li  class="dropdown">
                            <a class="dropdown-toggle"

                               data-toggle="dropdown" >
                                Load from<span class="caret"></span>
                            </a>

                            <ul class="dropdown-menu" role="menu" id="theme">

                                <li role="presentation"  aria-controls="openLocalFileTab">
                                    <a role="menuitem"   >Local file</a>
                                </li>
                                <li role="presentation" class="divider"></li>

                                <li role="presentation"  aria-controls="openRemoteFileTab">
                                    <a role="menuitem"  >Remote url</a>
                                </li>
                            </ul>
                        </li>


                    </ul>
                    <div class="tab-content">


                        <div class="tab-pane" id="mainTab">

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

                                <form:input cssClass="form-control" 
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

                        <div class="tab-pane" id="syntaxTab" >

                            <div class="col-xs-4 col-sm-3 col-md-3 col-lg-2">

                                <form:label path="codeType" cssClass="control-label" title="Select syntax" >
                                    Select syntax
                                </form:label>

                                <form:select
                                    path="codeType" 
                                    multiple="false" id="ptype">                    
                                    <c:forEach items="${availableCodeTypes}" var="codeType">
                                        <form:option value="${codeType.code}" editCode="${codeType.editType}" >
                                            <fmt:message key="${codeType.name}"/>
                                        </form:option>
                                    </c:forEach>
                                </form:select> 
                                <p class="help-block"> 
                                    <a class="btn btn-xs" href="#">
                                        <i class="fa fa-check-square-o"></i>Set as default</a>
                                </p>



                            </div>


                        </div>
                        <div class="tab-pane" id="colorTab">

                            <div class="col-xs-6 col-sm-4 col-md-4 col-lg-3">

                                <label for="ptheme" class="control-label"  >
                                    Select color theme
                                </label>

                                <select  id="ptheme" size="1" >
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
                                    <a class="btn btn-xs" href="#"><i class="fa fa-check-square-o"></i> Set as default</a></p>

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
                                
                          <div class="tab-pane" id="openLocalFileTab" >

                            <div class="col-xs-4 col-sm-3 col-md-3 col-lg-2">

                                                             
                                <form:label path="codeType" cssClass="control-label" title="Select syntax" >
                                    Select file
                                </form:label>

                                    <input type="file" id="select-file-btn"/>
                                    
                                
                                <p class="help-block">
                                    Supported extensions: .txt, .java
                                  
                                </p>
                            </div>
                        </div>
                              
                         <div class="tab-pane" id="openRemoteFileTab" >

                            <div class="col-xs-4 col-sm-3 col-md-3 col-lg-2">
                                                             
                                <form:label path="codeType" cssClass="control-label" title="Select syntax" >
                                   Enter url
                                </form:label>
                                   
                                
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
                                <fmt:message key="auth.login-to-save-changes"/>

                            </div>

                        </c:when>
                        <c:otherwise>

                            <button id="addCommentBtn" class="btn btn-primary submitBtn" name="submit_btn" type="submit">
                                <span class="i">S</span>
                                <span id="btnCaption"><c:out value='${submit_button_text}'/></span>
                                <i id="btnIcon" style="display:none;" class="fa fa-spinner"></i>
                            </button>


                        </c:otherwise>
                    </c:choose>



                    <a class="btn btn-default btn-xs" href="<c:out value="${urlCancel}"/>"><i  class="fa fa-ban"></i>
                        <fmt:message key='button.cancel'/></a>

                    <c:if test="${!model.blank}">
                        <tiles:insertDefinition name="/common/deleteLink" >
                            <tiles:putAttribute name="model" value="${model}"/>
                            <tiles:putAttribute name="modelName" value="paste"/>
                            <tiles:putAttribute name="currentUser" value="${currentUser}"/>
                        </tiles:insertDefinition>

                    </c:if>

                </div>       

            </div>


            <div  class="form-group">
                <div class="col-md-10">

                    <form:label path="text"><fmt:message key="paste.text"/></form:label>
                    <form:textarea path="text" cssErrorClass="error" cssClass="notice" cssStyle="display:none;"
                                   name="text" id="ptext" placeHolder="paste text"
                                   cols="120" rows="10" />
                    <div id="editor" style="height: 50em;">
                    </div>

                    <form:errors path="text"  cssClass="alert alert-danger" element="div" />


                    <div class="form-buttons">
                        <div class="button">

                            <c:choose>
                                <c:when test="${empty currentUser and !allowAnonymousCommentsCreate}">

                                    <div class="alert alert-warning" role="alert">
                                        <fmt:message key="auth.login-to-save-changes"/>

                                    </div>  
                                </c:when>
                                <c:otherwise>

                                    <button
                                        name="submit_btn"
                                        class="btn btn-primary submitBtn" type="submit">
                                        <i id="btnIcon" style="display:none;" class="fa fa-spinner"></i>
                                        <span class="i">S</span>
                                        <span id="btnCaption"><c:out value='${submit_button_text}'/></span>
                                    </button>


                                </c:otherwise>
                            </c:choose>
                            <a class="btn btn-default btn-xs" href="<c:out value="${urlCancel}"/>">
                                <i  class="fa fa-ban"></i>
                                <fmt:message key='button.cancel'/></a>

                            <c:if test="${!model.blank}">


                                <tiles:insertDefinition name="/common/deleteLink" >
                                    <tiles:putAttribute name="model" value="${model}"/>
                                    <tiles:putAttribute name="modelName" value="paste"/>
                                    <tiles:putAttribute name="currentUser" value="${currentUser}"/>
                                </tiles:insertDefinition>

                            </c:if>

                            <div style="float:right;margin-top: 2em;">

                                <div id="wordCount"></div>

                                <c:if test="${!model.blank}">
                                    <kc:prettyTime date="${model.lastModified}" locale="${pageContext.response.locale}"/>

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

    var max_length = 100;

    var editor;

    function readSingleFile(e) {
        
        var file = e.target.files[0];
       
        if (!file) {
            return;
        }
        var reader = new FileReader();
        reader.onload = function (e) {
            var contents = e.target.result;
            displayContents(contents);
        };
        reader.readAsText(file);
    }

    function displayContents(contents) {
        editor.getSession().setValue(contents);
    }


    window.addEvent('paste', function (e) {
        if (e.event.clipboardData) {

            /*console.log(e.event.clipboardData);*/

            var text = e.event.clipboardData.getData('text/plain');

            var block = (text.length < max_length - 2) ? text.substring(0, text.length) : text.substring(0, max_length - 2) + '..';

            var ptitle = $("pname");

            if (ptitle.get('value') == '') {
                ptitle.set('value', block);
            }

            //e.stop();
        }
    });

    window.addEvent('load', function () {

        var counter = new WordCount('wordCount', {
            countWordsTo: $('wordsCount'),
            countSymbolsTo: $('symbolsCount'),
            inputName: null, //The input name from which text should be retrieved, defaults null
            countWords: true, //Whether or not to count words
            countChars: true, //Whether or not to count characters
            charText: '<fmt:message key="paste.edit.word.counter.charText"/>', //The text that follows the number of characters
            wordText: '<fmt:message key="paste.edit.word.counter.wordText"/>', //The text that follows the number of words
            separator: ', ', //The text that separates the number of words and the number of characters
            liveCount: false, //Whether or not to use the event trigger, set false if you'd like to call the getCount function separately
            eventTrigger: 'keyup'			//The event that triggers the count update
        });

        editor = ace.edit("editor");
        editor.setTheme("ace/theme/chrome");
        editor.setOptions({
            autoScrollEditorIntoView: true
        });
        editor.getSession().setMode("ace/mode/${model.codeType.editType}");

        var textarea = document.getElementById("ptext");

        editor.getSession().setValue(textarea.get('value'));
        counter.getCount(textarea.get('value'));

    
        $('select-file-btn').removeEvents("change");

        $('select-file-btn').addEvent('change', function (e) {
                    readSingleFile(e);
             });      
        
          

        //To focus the ace editor
       // editor.focus();
        //Get the number of lines
        var count = editor.getSession().getLength();
        //Go to end of the last line
        editor.gotoLine(count, editor.getSession().getLine(count - 1).length);

        editor.getSession().on('change', function () {
            var text = editor.getSession().getValue();
            textarea.set('value', text);
            counter.getCount(text);
        });
      
        $('ptheme').addEvent('change', function (event) {
            editor.setTheme(this.getElement(':selected').value);
        });
        
        $('normalized').addEvent('click', function () {
            if (this.get('checked')) {
                var col = 80;
                editor.getSession().setUseWrapMode(true);
                editor.getSession().setWrapLimitRange(col, col);
                editor.renderer.setPrintMarginColumn(col);
            } else {
                editor.getSession().setUseWrapMode(false);
                editor.renderer.setPrintMarginColumn(80);
            }
        });


        $('fontsize').addEvent('change', function (event) {
            editor.setFontSize(this.getElement(':selected').get("value"));
        });

        $('ptype').addEvent('change', function (event) {
            editor.getSession().setMode("ace/mode/" + this.getElement(':selected').get("editCode"));
        });

        $('pprior').addEvent('change', function (event) {
            var prPreview = $('priorPreview');
            prPreview.erase('class');
            prPreview.addClass('i');
            prPreview.addClass(this.getElement(':selected').get("x-css-class-name"));
        });
    });


</script>


<script type="text/javascript">

    function onSave() {

        var thumbImg = document.getElementById('thumbImg');

        html2canvas($('editor'), {
            allowTaint: true,
            taintTest: false,
            onrendered: function (canvas) {

                var img = Pixastic.process(canvas, "crop", {
                    rect: {
                        left: 15, top: 0, width: 600, height: 800
                    }
                });

 
                img = Canvas2Image.saveAsPNG(canvas, true, 300, 200);
                thumbImg.set('value', img.src);
                $('editForm').submit();
            }
        });
    }

    function cleanTitle() {
        $('pname').set('value', '');
    }

    window.addEvent('load', function () {

        $$('.submitBtn').each(function (el, i) {

            el.addEvent('click', function (event) {
                this.getElementById('btnCaption').set('text', transmitText).disabled = true;
                this.getElementById('btnIcon').toggle();

                event.stop();
                onSave();
            });
        });
    });

</script>

