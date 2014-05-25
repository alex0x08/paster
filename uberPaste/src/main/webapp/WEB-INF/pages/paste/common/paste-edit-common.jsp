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
    <div class="column grid-15"   >
        <form:form id="editForm" action="${url}" cssClass="perk"
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


            <div class="row">
                <div class="column grid-12" >

                    <div class="new_tabs_head"  >
                        <ul class="new_tabs">
                            <li>
                                <c:choose>
                                    <c:when test="${model.blank}">
                                        <fmt:message key="paste.new"/>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="font-size:1.5em;">#<c:out value="${model.id}"/></span>
                                    </c:otherwise>
                                </c:choose>

                            </li>
                            <li><fmt:message key="paste.source.title"/></li>
                            <li><fmt:message key="paste.editor.title"/></li>
                        </ul>
                    </div>
                    <div class="new_tabs_frame">
                        <div class="tabdata">

                            <div class="row">

                                <div class="column grid-8">

                                    <c:choose>
                                        <c:when test="${model.blank}">
                                            <form:hidden path="integrationCode"  />
                                        </c:when>
                                        <c:otherwise>
                                            <form:hidden path="id"  />
                                        </c:otherwise>
                                    </c:choose>

                                    <fmt:message var="pasteTitle" key="paste.title"/>

                                    <form:label path="name"><c:out value="${pasteTitle}"/>
                                        <a id="cleanTitleBtn" onclick="cleanTitle();" href="javascript:void(0);" title="<fmt:message key='button.clear'/>">
                                            <span class="i">d</span>
                                        </a> </form:label>
                                    <fmt:message key="paste.edit.title.placeholder" var="titlePlaceHolder"/>
                                    <form:input  cssClass="notice" cssErrorClass="error" path="name" name="title"
                                                 id="pname" cssStyle="width:97%;" maxlength="255" title="${pasteTitle}" placeholder="${titlePlaceHolder}"  />
                                    <form:errors path="name" cssClass="error" />
                                </div>

                                <div class="column grid-3" style="float:right;">



                                    <form:label path="priority">
                                        <fmt:message key="paste.priority"/>:
                                    </form:label>
                                    <form:select cssStyle="display:inline;" path="priority" multiple="false" id="pprior">
                                        <c:forEach items="${availablePriorities}" var="prior">
                                            <form:option value="${prior.code}" cssClass="${prior.cssClass}" x-css-class-name="${prior.cssClass}">
                                                <fmt:message key="${prior.name}"/>
                                            </form:option>
                                        </c:forEach>
                                    </form:select>
                                    <span id="priorPreview" class="i priority_normal" style="font-size:1.5em;">/</span>

                                    <form:errors path="priority" cssClass="error" />

                                </div>



                            </div>

                            <div class="row">

                                <div class="column grid-6">

                                    <form:label path="tagsAsString"><span  class="i" >T</span><fmt:message key="paste.tags"/></form:label>
                                    <fmt:message key="paste.edit.tags.placeholder" var="tagsPlaceHolder"/>
                                    <form:input id="ptags" path="tagsAsString" maxlength="155" cssStyle="width:95%;" 
                                                autocomplete="true" placeholder="${tagsPlaceHolder}"  />
                                    <form:errors path="tagsAsString" cssClass="error" />

                                </div>

                                <div class="column grid-1" >
                                    <form:label path="sticked" ><span class="i" >]</span></form:label>            
                                    <fmt:message var="titleStick" key='paste.stick'/>            
                                    <form:checkbox path="sticked" style="display:inline;" title="${titleStick}"/>
                                </div>

                                <div class="column grid-3" >
                                    <form:label path="normalized" ><fmt:message key="paste.normalize"/></form:label>
                                    <fmt:message key='paste.normalize' var="labelNormalize"/>
                                    <form:checkbox id="normalized" path="normalized" style="display:inline;" title="${labelNormalize}"/>
                                </div>

                            </div>

                        </div>
                                <div class="tabdata">
                                    <form:input path="remoteUrl" maxlength="1024" id="loadUrl"/>
                            <button id="loadUrlBtn"
                                name="load_btn"
                                class="p-btn-save" >
                                        Load
                            </button>
         
                                    
                                </div>
                        <div class="tabdata">


                            <div class="row">

                                <div class="column grid-4">
                                    <form:label path="codeType"><fmt:message key="paste.syntax.title"/></form:label>
                                    <form:select path="codeType" multiple="false" id="ptype">                    
                                        <c:forEach items="${availableCodeTypes}" var="codeType">
                                            <form:option value="${codeType.code}" editCode="${codeType.editType}" >
                                                <fmt:message key="${codeType.name}"/>
                                            </form:option>
                                        </c:forEach>
                                    </form:select> 
                                    <form:errors path="codeType" cssClass="error" />

                                </div>


                                <div class="column grid-5">

                                    <label for="theme"><fmt:message key="paste.editor.theme.title"/></label>
                                    <select id="theme" size="1">
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


                                </div>


                            </div>

                        </div>
                    </div>

                </div>

                <div class="column grid-4 right" style="margin-top: 3em;">

                    <button id="addCommentBtn" class="p-btn-save submitBtn" name="submit_btn" type="submit">
                        <span class="i">S</span>
                        <span id="btnCaption"><c:out value='${submit_button_text}'/></span>
                        <img id="btnIcon" style="display:none;" src="<c:url value='/main/static/${appVersion}/images/gear_sml.gif'/>"/>
                    </button>


                    <a href="<c:out value="${urlCancel}"/>">
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


            <div  class="row">
                <div class="column grid-16">

                    <form:label path="text"><fmt:message key="paste.text"/>:</form:label>
                    <form:textarea path="text" cssErrorClass="error" cssClass="notice" cssStyle="display:none;"
                                   name="text" id="ptext" placeHolder="paste text"
                                   cols="120" rows="10" />
                    <div id="editor" style="height: 50em;">
                    </div>

                    <form:errors path="text" cssClass="error" />


                    <div class="form-buttons">
                        <div class="button">

                            <button
                                name="submit_btn"
                                class="p-btn-save submitBtn" type="submit">

                                <img id="btnIcon" style="display:none;" src="<c:url value='/main/static/${appVersion}/images/gear_sml.gif'/>"/>
                                <span class="i">S</span>
                                <span id="btnCaption"><c:out value='${submit_button_text}'/></span>
                            </button>

                            <a href="<c:out value="${urlCancel}"/>">
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
   
    var max_length= 100;

    window.addEvent('paste', function(e){
        if (e.event.clipboardData) {
            
            /*console.log(e.event.clipboardData);*/
            
            var text = e.event.clipboardData.getData('text/plain');

            var block = (text.length<max_length-2) ? text.substring(0, text.length) : text.substring(0, max_length-2)+'..';
             

             var ptitle = $("pname");
             
             if (ptitle.get('value') == '') {
                 ptitle.set('value',block);
             }
          
            //e.stop();
        }
    });

    window.addEvent('domready', function(){

        var counter = new WordCount('wordCount', {
                countWordsTo: $('wordsCount'),
                countSymbolsTo: $('symbolsCount'),            
		inputName: null,				//The input name from which text should be retrieved, defaults null
		countWords: true,				//Whether or not to count words
		countChars: true,				//Whether or not to count characters
		charText: '<fmt:message key="paste.edit.word.counter.charText"/>',			//The text that follows the number of characters
		wordText: '<fmt:message key="paste.edit.word.counter.wordText"/>',				//The text that follows the number of words
		separator: ', ',				//The text that separates the number of words and the number of characters
		liveCount: false,				//Whether or not to use the event trigger, set false if you'd like to call the getCount function separately
		eventTrigger: 'keyup'			//The event that triggers the count update
	});
            
        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/chrome");
        editor.getSession().setMode("ace/mode/${model.codeType.editType}");

        var textarea = document.getElementById("ptext");

        editor.getSession().setValue(textarea.get('value'));
        counter.getCount(textarea.get('value'));


        //To focus the ace editor
        editor.focus();
        //Get the number of lines
        var  count = editor.getSession().getLength();
        //Go to end of the last line
        editor.gotoLine(count, editor.getSession().getLine(count-1).length);


        editor.getSession().on('change', function(){
            var text = editor.getSession().getValue();
            textarea.set('value',text);
            counter.getCount(text);
        });

     
       $('normalized').addEvent('click', function() {
            if(this.get('checked')) {
                var col = 80;
                editor.getSession().setUseWrapMode(true);
                editor.getSession().setWrapLimitRange(col, col);
                editor.renderer.setPrintMarginColumn(col);
            } else {
          editor.getSession().setUseWrapMode(false);
                editor.renderer.setPrintMarginColumn(80);
            }
        });

        $('theme').addEvent('change',function(event) {
            editor.setTheme(this.getElement(':selected').value);
        });


        $('ptype').addEvent('change',function(event) {
            editor.getSession().setMode("ace/mode/"+this.getElement(':selected').get("editCode"));
        });

        $('pprior').addEvent('change',function(event) {      
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

            onrendered: function(canvas) {

                var img= Pixastic.process(canvas, "crop", {
                    rect : {
                        left : 15, top : 0, width : 400, height : 300
                    }
                });

                //img =Pixastic.process(img, "blurfast", {amount:0.5});

                img = Canvas2Image.saveAsJPEG(img, true, 300, 200);
                document.body.appendChild(img);
                thumbImg.set('value',img.src);
                $('editForm').submit();
            }
        });
    }

    function cleanTitle() {
            $('pname').set('value','');
    }

    window.addEvent('domready', function(){

        $('loadUrlBtn').addEvent('click', function(event){
            event.stop();

        $('editForm').set('action','<c:url value='/main/paste/loadFrom'/>');

        $('editForm').submit();
        });


        $$('.submitBtn').addEvent('click',function(event){
            this.getElementById('btnCaption').set('text', transmitText).disabled = true;
            this.getElementById('btnIcon').setStyle('display','');

            event.stop();
            onSave();
        });

    });

</script>

