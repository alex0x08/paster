<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div class="row">
    <div class="column grid-1" style="text-align:right;padding-right: 0;margin-top: -1em;" >
        <a class="mainLinkLine" href="<c:url value="/main/paste/new"></c:url>" title="<fmt:message key='paste.create.new'/>"><span class="i" style="font-size: 4em;">/</span></a>
    </div>
    <div class="column grid-12" style="padding-left: 0;margin-left: -1em;" >
        <jsp:include page="/WEB-INF/pages/template/search.jsp"/>
    </div>
</div>

<div class="row">
    <div class="column grid-16">
        <c:set var="priorTitle"><fmt:message key="${model.priority.name}"/></c:set>

        <h4 class="f-h4" style="padding-top: 0;margin-top:0;"><span class="i ${model.priority.cssClass}" style="font-size:2em;" title="${priorTitle}" >/</span>
            <c:if test="${model.sticked}">
                <span class="i">]</span>
            </c:if>
            <c:out value="${model.name}" escapeXml="true"/>

            <c:if test="${not empty model.integrationCode}">
                (integrated with <c:out value="${model.integrationCode}"/>)
            </c:if>

           <span style="font-weight: normal; font-size: 12px;">

                  <tiles:insertDefinition name="/common/tags" >
                      <tiles:putAttribute name="model" value="${model}"/>
                      <tiles:putAttribute name="modelName" value="paste"/>
                  </tiles:insertDefinition>

                <tiles:insertDefinition name="/common/owner" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                </tiles:insertDefinition>

               <tiles:insertDefinition name="/common/commentCount" >
                   <tiles:putAttribute name="model" value="${model}"/>
                   <tiles:putAttribute name="modelName" value="paste"/>
               </tiles:insertDefinition>

                       <tiles:insertDefinition name="/common/deleteLink" >
                           <tiles:putAttribute name="model" value="${model}"/>
                           <tiles:putAttribute name="modelName" value="paste"/>
                           <tiles:putAttribute name="currentUser" value="${currentUser}"/>
                       </tiles:insertDefinition>


                <span style="font-size: 9px;">
                ,<kc:prettyTime date="${model.lastModified}" locale="${pageContext.response.locale}"/>

                </span>


           </span>
        </h4>



        <tiles:insertDefinition name="/common/pasteControls" >
            <tiles:putAttribute name="model" value="${model}"/>

            <tiles:putAttribute name="next" value="${availableNext}"/>
            <tiles:putAttribute name="prev" value="${availablePrev}"/>

        </tiles:insertDefinition>



    </div>
</div>

<div class="row">
    <div class="column grid-12">
    &nbsp;
        <c:if test="${not empty model.commentCount and model.commentCount>0}">

            <span style="vertical-align: top;font-size: larger;" class="i" title="Comments">C</span>
            <a id="toggleCommentsCtl" href="javascript:void(0);" onclick="SyntaxHighlighter.toggleComments(this);" title="hide comments">
                <span  class="i" >-</span>
            </a>



        </c:if>
        <c:if test="${!model.blank and not empty availableRevisions}">
            <jsp:include
                    page="/WEB-INF/pages/common/revisions.jsp">
                <jsp:param name="modelName" value="paste" />
            </jsp:include>
        </c:if>

    </div>

    <div class="column grid-3">
        <c:if test="${shareIntegration}">
            <a id="rightPanelCtrl" href="javascript:void(0);" onclick="toggleRight();" title="toggle right panel">
                <span class="i">-</span>
            </a>
        </c:if>

    </div>
</div>


<div class="row">

    <c:choose>
        <c:when test="${shareIntegration}">
              <c:set var="centerGridSize" value="grid-10"/>
        </c:when>
        <c:otherwise>
            <c:set var="centerGridSize" value="grid-15"/>
    </c:otherwise>
    </c:choose>

    <div id="centerPanel" class="column ${centerGridSize}" style="min-width:650px;">
    <pre id="pasteText" class="brush: ${model.codeType.code};toolbar: false; auto-links:false;" style=" overflow-y: hidden;" >
        <c:out value="${model.text}" escapeXml="true" /></pre>
    <code id="pasteTextPlain" style="display:none;"><c:out value="${model.text}" escapeXml="true" /></code>

    </div>


    <c:if test="${shareIntegration}">

        <div id="rightPanel" class="column grid-4" style="min-width:150px;" >

        <iframe id="shareFrame" src="${shareUrl}/main/file/integrated/list/paste_${model.id}"
                scrolling="auto" frameborder="0"
                style="width:340px; "  allowTransparency="true"   >

        </iframe>

        </div>
    </c:if>

</div>
<%--
<div class="row">
    <div class="column grid-12">

    </div>
</div>
  --%>

    <div id="commentsList" style="display:none;">

        <c:forEach var="comment" items="${model.comments}" varStatus="loopStatus">

            <div id="numSpace_l${comment.id}" class="listSpace" >
            </div>

            <div id="comment_l${comment.id}" commentId="${comment.id}"
                 lineNumber="${comment.lineNumber}"  parentCommentId="${comment.parentId}" class=" commentBlock" >
                <div id="innerBlock" class="commentInner p-comment">

                <div class="row">
                   
                    <div class="column grid-12" style="font-size: small;padding-left: 0.1em; margin: 0;  ">
                        
                        <tiles:insertDefinition name="/common/owner" >
                            <tiles:putAttribute name="model" value="${comment}"/>
                            <tiles:putAttribute name="modelName" value="comment"/>
                        </tiles:insertDefinition>

                      
                        , <kc:prettyTime date="${comment.lastModified}" locale="${pageContext.response.locale}"/>

                    </div>
                        
                        <div class="column grid-3 right">
                        <a href="#comment_l${comment.id}" title="<c:out value="${comment.id}"/>">#${model.id}.${comment.id}</a>
                    
                          <c:if test="${comment.parentId==null}">
                            <a  href="javascript:void(0);" class="linkLine" title="Comment this"
                                onclick="SyntaxHighlighter.insertEditForm(${comment.lineNumber},${comment.id});"><span class="i">C</span></a>
                        </c:if>
                        <sec:authorize access="${currentUser !=null and (currentUser.admin or ( comment.hasOwner  and comment.owner eq currentUser)) }">
                            <a href="<c:url value='/main/paste/removeComment'>
                                   <c:param name="pasteId" value="${model.id}"/>
                                   <c:param name="commentId" value="${comment.id}"/>
                                   <c:param name="lineNumber" value="${comment.lineNumber}"/>
                               </c:url>" title="<fmt:message key='button.delete'/>">
                                <span style="font-size: larger;" class="i">d</span>
                            </a>

                        </sec:authorize>
                         
                         </div>

                </div>

                <div class="row">
                   
                    <div class="column grid-16">
                            <c:out value=" ${comment.text}" escapeXml="true"/>
                    </div>


                </div>

               </div>
            </div>


        </c:forEach>

    </div>

<c:url var="url" value='/main/paste/saveComment' />

<div id="numSpace" class="listEditSpace" >
</div>

<span id="pasteLineCopyBtn" style="display:none; white-space: normal;">
    <a id="ctrlc_line" data-clipboard-target="pasteLineToCopy" href="javascript:void(0);" style="float:left;" title="Copy to clipboard" >
        <img src="<c:url value='/images/ctrlc.png'/>"/></a>
</span>

<span id="pasteLineToCopy" style="display:none;">
                              NONE
</span>

 <div id="commentForm" class="editForm p-comment"  style="display:none;" >

     <form:form action="${url}" id="addCommentForm"
                modelAttribute="comment"
                method="POST" >


     <div class="row" >
         <div class="column grid-15"  >


                 <input type="hidden" name="pasteId" value="${model.id}"/>
                 <form:hidden path="lineNumber" id="lineNumber"/>
                 <form:hidden path="parentId" id="commentParentId"/>

                 <form:textarea path="text" id="commentText" cssErrorClass="error"  />

         </div>
         <div style="margin:0.5em;padding-right:0.5em;float:right;" >
           <a class="btn sbtn" title="Cancel" href="javascript:void(0);" onclick="SyntaxHighlighter.hideEditForm();">
               X
           </a>

         </div>
     </div>

         <div class="row">
         <div class="column grid-16" >

             <button id="addCommentBtn" type="submit">
                 <img id="btnIcon" style="display:none;" src="<c:url value='/images/gear_sml.gif'/>"/>
                <span id="btnCaption">Add comment</span>
             </button>

             to line <span id="pageNum"></span>

             <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
                 
              <span class="right"  style="padding-top: 0.5em;" >
                <span  style="padding-top: 1em;">
                    <c:out escapeXml="true" value="${currentUser.name}" />
                </span>

                  <a href="http://ru.gravatar.com/site/check/${currentUser.username}" title="GAvatar">
                        <img style="vertical-align: top;padding-bottom: 2px;" src="<c:out value='http://www.gravatar.com/avatar/${currentUser.avatarHash}?s=32&d=monsterid'/>"/>
                   </a>

               </span>

             </sec:authorize>

         </div>
             </div>

         <div class="row">
             <div class="column grid-16" >

                 <form:errors path="text" cssClass="error"    />

             </div>
         </div>



     </form:form>

     </div>

<img style="border: 2px saddlebrown;" src="${model.thumbImage}"/>

<script type="text/javascript" src="<c:url value='/libs/zeroclipboard/ZeroClipboard.js'/>"></script>

<c:if test="${shareIntegration}">

<script type="text/javascript">

    function toggleRight() {

        var rightPanel = document.getElementById('rightPanel');
        var centerPanel = document.getElementById('centerPanel');
        var rightPanelCtrl = document.getElementById('rightPanelCtrl');

       // alert(rightPanel.getStyle('display'));
        if (rightPanel.getStyle('display') != 'none') {
            rightPanel.setStyle('display','none');
            centerPanel.set('class','column grid-15');
            rightPanelCtrl.getElement('span').set('text','+');
        } else {
            centerPanel.set('class','column grid-12');
            rightPanel.setStyle('display','');
            rightPanelCtrl.getElement('span').set('text','-');
         }


    }

        window.addEvent('domready', function() {
        var sch = document.body.scrollHeight;
        if (sch < 1024)  {
            $('shareFrame').setStyle('height','1024px');
        } else {
            $('shareFrame').setStyle('height', document.body.scrollHeight+'px');
        }
    } );
    </script>
</c:if>

   <script type="text/javascript">
                           window.addEvent('domready', function() {

                               $('addCommentBtn').addEvent('click',function(){
                                   this.getElementById('btnCaption').set('text','Submitting...').disabled = true;
                                   this.getElementById('btnIcon').setStyle('display','');
                                   $("addCommentForm").submit();

                               });




                               /*    document.addEvent('keydown', function(event){
                                       // the passed event parameter is already an instance of the Event type.
                                       alert(event.key);   // returns the lowercase letter pressed.
                                       alert(event.shift); // returns true if the key pressed is shift.
                                       if (event.key == 's' && event.control) alert('Document saved.'); //executes if the user presses Ctr+S.
                                   });
                                 */
    ZeroClipboard.setDefaults( { moviePath: "<c:url value='/libs/zeroclipboard/ZeroClipboard.swf'/>" } );

    var clip = new ZeroClipboard(document.id("ctrlc_link"));

    clip.on( 'complete', function(client, args) {
        growl.notify(args.text.length+' symbols copied to clipboard.');

    } );

    var clipLine = new ZeroClipboard(document.id("ctrlc_line"));

    clipLine.on( 'complete', function(client, args) {
        growl.notify(args.text.length+' symbols copied to clipboard.');

    } );


    SyntaxHighlighter.config.tagName = "pre";
    SyntaxHighlighter.autoloader.apply(null, path(
            'applescript            @shBrushAppleScript.js',
            'actionscript3 as3      @shBrushAS3.js',
            'bash shell             @shBrushBash.js',
            'coldfusion cf          @shBrushColdFusion.js',
            'cpp c                  @shBrushCpp.js',
            'c# c-sharp csharp      @shBrushCSharp.js',
            'css                    @shBrushCss.js',
            'delphi pascal          @shBrushDelphi.js',
            'diff patch pas         @shBrushDiff.js',
            'erl erlang             @shBrushErlang.js',
            'groovy                 @shBrushGroovy.js',
            'java                   @shBrushJava.js',
            'jfx javafx             @shBrushJavaFX.js',
            'js jscript javascript  @shBrushJScript.js',
            'perl pl                @shBrushPerl.js',
            'php                    @shBrushPhp.js',
            'text plain             @shBrushPlain.js',
            'py python              @shBrushPython.js',
            'ruby rails ror rb      @shBrushRuby.js',
            'sass scss              @shBrushSass.js',
            'scala                  @shBrushScala.js',
            'sql                    @shBrushSql.js',
            'vb vbnet               @shBrushVb.js',
            'xml xhtml xslt html    @shBrushXml.js'
    ));

    SyntaxHighlighter.all();

    });
            function path()
            {
                var args = arguments,
                result = [];

                for(var i = 0; i < args.length; i++)
                    result.push(args[i].replace('@', '<c:url value="/libs/syntax-highlight/scripts/"/>'));
                return result
            };

        </script>
        

