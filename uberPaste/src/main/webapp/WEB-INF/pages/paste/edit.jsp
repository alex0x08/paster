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
        var counter = new WordCount('wordCount', {inputName:'text'});

        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/twilight");
        editor.getSession().setMode("ace/mode/scala");

        var textarea = document.getElementById("ptext");

        editor.getSession().setValue(textarea.get('value'));
        editor.getSession().on('change', function(){
            textarea.set('value',editor.getSession().getValue());
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
        <div class="column grid-4">


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


