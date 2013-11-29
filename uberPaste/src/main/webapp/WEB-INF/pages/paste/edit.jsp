<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<fmt:message var="submit_button_text" key="button.save"/>

<c:url var="url" value='/main/paste/save' />


 <div class="row">
        <div class="column grid-14"  >

            
<fieldset class="perk">
    <legend><span class="i" style="font-size:2em;">/</span>
        ${requestScope.title}
        <c:if test="${not empty model.integrationCode}">
             (integrated with <c:out value="${model.integrationCode}"/>)
        </c:if>
    </legend>



<form:form id="editForm" action="${url}" cssClass="perk"
           modelAttribute="model"
           method="POST" >

    <div class="row">
        <div class="column grid-14">
            <form:errors  cssClass="errorblock" element="div" />
        </div>
    </div>

    <div class="row">
        <div class="column grid-6">

            <form:input id="thumbImg" path="thumbImage" cssStyle="display:none;"  />

            <c:choose>
        <c:when test="${model.blank}">
            <form:hidden path="integrationCode"  />
        </c:when>
        <c:otherwise>
            <form:hidden path="id"  />
        </c:otherwise>
    </c:choose>
            <form:label path="name"><fmt:message key="paste.title"/>
                <a id="cleanTitleBtn" onclick="cleanTitle();" href="javascript:void(0);" title="Clean title">
                <span class="i">d</span>
                </a>  : </form:label>
                <form:input  cssClass="notice" cssErrorClass="error" path="name" name="title"
                            id="pname" cssStyle="width:97%;" maxlength="255" title="Paste title" placeholder="enter paste title"  />

                <form:errors path="name" cssClass="error" />

    </div>

    <div class="column grid-1" >
            <form:label path="sticked" ><span class="i" >]</span></form:label>
            <form:checkbox path="sticked" style="display:inline;" title="Stick paste"/>
    </div>

    <div class="column grid-2" >
           <form:label path="normalized" >Normalize</form:label>
            <form:checkbox id="normalized" path="normalized" style="display:inline;" title="Normalize paste"/>
    </div>

        <div class="column grid-4 right">

            <button id="addCommentBtn" class="p-btn-save submitBtn" name="submit_btn" type="submit">
                <img id="btnIcon" style="display:none;" src="<c:url value='/main/static/${appVersion}/images/gear_sml.gif'/>"/>
                     <span class="i">S</span>
                    <span id="btnCaption"><c:out value='${submit_button_text}'/></span>
            </button>


            <a href="<c:url value="/main/paste/list"/>">
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

    <div class="row">
        <div class="column grid-5">

        <form:label path="tagsAsString"><span  class="i" >T</span><fmt:message key="paste.tags"/>:</form:label>
    <form:input id="ptags" path="tagsAsString" maxlength="155" cssStyle="width:97%;" autocomplete="true" placeholder="enter space-separated tags here"  />
    <form:errors path="tagsAsString" cssClass="error" />

        </div>
        <div class="column grid-3">


        <form:label path="codeType">Hightlight like:</form:label>
        <form:select path="codeType" multiple="false" id="ptype">
                    
                    <c:forEach items="${availableCodeTypes}" var="codeType">
				    <form:option value="${codeType.code}" editCode="${codeType.editType}" >
                                    <fmt:message key="${codeType.name}"/>
                                </form:option>
                    </c:forEach>
                </form:select> 
                <form:errors path="codeType" cssClass="error" />

        </div>


        <div class="column grid-4">

            <label for="theme">Theme</label>
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

                <a href="<c:url value="/main/paste/list"/>">
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
    </fieldset>
            
        </div>
     </div>




<script type="text/javascript" src="<c:url value="/main/static/${appVersion}/libs/word-count.js"/>"></script>

<script src="<c:url value='/main/assets/${appVersion}/ace/07.31.2013/src-noconflict/ace.js'/>" type="text/javascript" charset="utf-8"></script>

<script src="<c:url value='/main/static/${appVersion}/libs/html2canvas.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/main/static/${appVersion}/libs/canvas-to-blob.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/main/static/${appVersion}/libs/base64.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/main/static/${appVersion}/libs/canvas2image.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/main/static/${appVersion}/libs/pixastic/pixastic.core.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/main/static/${appVersion}/libs/pixastic/actions/crop.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/main/static/${appVersion}/libs/pixastic/actions/blurfast.js'/>" type="text/javascript" charset="utf-8"></script>

<script type="text/javascript">

    if (this.MooTools.build=='ab8ea8824dc3b24b6666867a2c4ed58ebb762cf0') {
        delete Function.prototype.bind;

        Function.implement({

            /*<!ES5-bind>*/
            bind: function(that){
                var self = this,
                        args = arguments.length > 1 ? Array.slice(arguments, 1) : null,
                        F = function(){};

                var bound = function(){
                    var context = that, length = arguments.length;
                    if (this instanceof bound){
                        F.prototype = self.prototype;
                        context = new F;
                    }
                    var result = (!args && !length)
                            ? self.call(context)
                            : self.apply(context, args && length ? args.concat(Array.slice(arguments)) : args || arguments);
                    return context == that ? result : context;
                };
                return bound;
            }
            /*</!ES5-bind>*/
        });
    }

   
    var max_length= 100;

    window.addEvent('paste', function(e){
        if (e.event.clipboardData) {
            
            /*console.log(e.event.clipboardData);*/
            
            var text = e.event.clipboardData.getData('text/plain');

            var block = '';

             if (text.length<max_length-2) {
             block = text.substring(0, text.length);
             } else {
             block  = text.substring(0, max_length-2)+'..';
             }

             var ptitle = $("pname"),ptags = $("");
             
             if (ptitle.get('value') == '') {
                 ptitle.set('value',block);
             }
          
            //e.stop();
        }
    });

    window.addEvent('domready', function(){

        var counter = new WordCount('wordCount');

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

        var normalizedCheck = $('normalized');

        normalizedCheck.addEvent('click', function() {
            if(normalizedCheck.get('checked')) {
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
            //alert(this.getElement(':selected').value);
            editor.setTheme(this.getElement(':selected').value);
        });


        $('ptype').addEvent('change',function(event) {
            // alert(this.getElement(':selected').get("editCode"));
            editor.getSession().setMode("ace/mode/"+this.getElement(':selected').get("editCode"));

        });

        $('pprior').addEvent('change',function(event) {
            // alert(this.getElement(':selected').get("x-css-class-name"));

         var prPreview = $('priorPreview');

            prPreview.erase('class');
            prPreview.addClass('i');
            prPreview.addClass(this.getElement(':selected').get("x-css-class-name"));

        });


    });



    /*window.addEvent('domready', function() {

     document.addEvent('keydown', function(event){
     // the passed event parameter is already an instance of the Event type.
     alert(event.key);   // returns the lowercase letter pressed.
     alert(event.shift); // returns true if the key pressed is shift.
     if (event.key == 's' && event.control) alert('Document saved.'); //executes if the user presses Ctr+S.
     });

     });*/
</script>


<script type="text/javascript">

    function onSave() {

        var peditor = ace.edit("editor");

        var editForm = document.getElementById('editForm');
        //  var submitBtn = document.getElementById('submitBtn');
        var editor = document.getElementById('pasteText');
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

                img = Canvas2Image.saveAsPNG(img, true, 300, 200);

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

        $$('.submitBtn').addEvent('click',function(){
            this.getElementById('btnCaption').set('text','Submitting...').disabled = true;
            this.getElementById('btnIcon').setStyle('display','');

            event.preventDefault();
            onSave();
        });


    });

</script>

