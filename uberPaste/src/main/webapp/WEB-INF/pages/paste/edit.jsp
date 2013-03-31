<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<script type="text/javascript" src="<c:url value="/libs/word-count.js"/>"></script>
<script src="<c:url value='/libs/ace/src-noconflict/ace.js'/>" type="text/javascript" charset="utf-8"></script>


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

    var max_length= 255;

    window.addEvent('paste', function(e){
        if (e.event.clipboardData) {
            console.log(e.event.clipboardData);
            var text = e.event.clipboardData.getData('text/plain');

            var block = '';

            if (text.length<max_length-2) {
                block = text.substring(0, text.length);
            } else {
                block  = text.substring(0, max_length-2)+'..';
            }


            document.getElementById("pname").set('value',block);

            //e.stop();
        }
    });

    window.addEvent('domready', function(){

        var counter = new WordCount('wordCount');

        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/mono_industrial");
        editor.getSession().setMode("ace/mode/${model.codeType.editType}");

        var textarea = document.getElementById("ptext");

        editor.getSession().setValue(textarea.get('value'));
        counter.getCount(textarea.get('value'));

        editor.getSession().on('change', function(){
            var text = editor.getSession().getValue();
            textarea.set('value',text);
            counter.getCount(text);
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




        <c:url var="url" value='/main/paste/save' />

<fieldset class="perk">
    <legend><span class="i" style="font-size:2em;">/</span><c:out value="${requestScope.title}" escapeXml="true"/></legend>

<form:form action="${url}" cssClass="perk"
           modelAttribute="model"
           method="POST" >

    <div class="row">
        <div class="column grid-14">
            <form:errors  cssClass="errorblock" element="div" />
        </div>
    </div>

    <div class="row">
        <div class="column grid-6">

    <c:choose>
        <c:when test="${model.blank}">
        </c:when>
        <c:otherwise>
            <form:hidden path="id"  />
        </c:otherwise>
    </c:choose>
            <form:label path="name"><fmt:message key="paste.title"/>:</form:label>
                <form:input cssClass="notice" cssErrorClass="error" path="name" name="title"
                            id="pname" cssStyle="width:97%;" maxlength="255" title="Paste title" placeholder="enter paste title"  />
                <form:errors path="name" cssClass="error" />
    </div>

    <div class="column grid-1" >
        <div style="padding-top: 1em;vertical-align: middle;">
            <span class="i" >]</span>
            <form:checkbox path="sticked" style="display:inline;" title="Stick paste"/>
        </div>

        </div>


    <div class="column grid-2" >
           <form:label path="normalized" >Normalize</form:label>
            <form:checkbox path="normalized" style="display:inline;" title="Normalize paste"/>
    </div>
    </div>


    <div class="row">
        <div class="column grid-8">

        <form:label path="tagsAsString"><fmt:message key="paste.tags"/>:</form:label>
    <form:input path="tagsAsString" maxlength="155" cssStyle="width:97%;" autocomplete="true" placeholder="enter space-separated tags here"  />
    <form:errors path="tagsAsString" cssClass="error" />

        </div>
        <div class="column grid-3">


        <form:label path="codeType">Hightlight like:</form:label>
    <form:select path="codeType" multiple="false" id="ptype">
                    
                    <c:forEach items="${availableCodeTypes}" var="codeType">
				    <form:option value="${codeType.code}" >
                                    <fmt:message key="${codeType.name}"/>
                                </form:option>
                    </c:forEach>
                </form:select> 
                <form:errors path="codeType" cssClass="error" />

        </div>


        <div class="column grid-3">

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

        <div class="column grid-2" style="float:right;">

            <form:label path="priority"><fmt:message key="paste.priority"/>:</form:label>
            <form:select path="priority" multiple="false" id="pprior">
                <c:forEach items="${availablePriorities}" var="prior">
                    <form:option value="${prior.code}" >
                        <fmt:message key="${prior.name}"/>
                    </form:option>
                </c:forEach>
            </form:select>
            <form:errors path="priority" cssClass="error" />


        </div>
    </div>

    <div class="row">
        <div class="column grid-16">

        <form:label path="text"><fmt:message key="paste.text"/>:</form:label>
    <form:textarea path="text" cssErrorClass="error" cssClass="notice" cssStyle="display:none;"
                   name="text" id="ptext" placeHolder="paste text"
                   cols="120" rows="10" />

            <pre id="editor" style="height: 50em;">

            </pre>

    <form:errors path="text" cssClass="error" />


    <div class="form-buttons">
            <div class="button">

                <fmt:message var="submit_button_text" key="button.save"/>

                <form:button name="submit" id="submitBtn"   >
                    <c:out value="${submit_button_text}"/>
                </form:button>

                <a href="<c:url value="/main/paste/list"/>"><fmt:message key='button.cancel'/></a>



                <c:if test="${!model.blank}">
                    <sec:authorize ifAnyGranted="ROLE_ADMIN">
                     |  <a href="<c:url value='/main/paste/delete'><c:param name="id" value="${model.id}"/> </c:url>"><fmt:message key='button.delete'/></a>
                    </sec:authorize>


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


