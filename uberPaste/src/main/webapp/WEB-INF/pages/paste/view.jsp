<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<div class="row">
    <div class="column grid-1" style="text-align:right;padding-right: 0;margin-top: -1em;" >
        <a class="mainLinkLine" href="<c:url value="/main/paste/new"></c:url>" title="<fmt:message key='paste.create.new'/>"><span class="i" style="font-size: 4em;">/</span></a>
    </div>
    <div class="column grid-12" style="padding-left: 0;margin-left: -1em;" >
        <jsp:include page="/WEB-INF/pages/template/search.jsp"/>
    </div>
</div>


<c:set var="priorTitle"><fmt:message key="${model.priority.name}"/></c:set>


        <h4 class="f-h4" style="padding-top: 0;margin-top:0;"><span class="i ${model.priority.cssClass}" style="font-size:2em;" title="${priorTitle}" >/</span>
            <c:if test="${model.sticked}">
                <span class="i">]</span>
            </c:if>
            <c:out value="${model.name}" escapeXml="true"/>
           <span style="font-weight: normal; font-size: 12px;">

                  <tiles:insertDefinition name="common/tags" >
                      <tiles:putAttribute name="model" value="${model}"/>
                      <tiles:putAttribute name="modelName" value="paste"/>
                  </tiles:insertDefinition>



                <tiles:insertDefinition name="common/owner" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                </tiles:insertDefinition>


               <tiles:insertDefinition name="common/commentCount" >
                   <tiles:putAttribute name="model" value="${model}"/>
                   <tiles:putAttribute name="modelName" value="paste"/>
               </tiles:insertDefinition>


                <span style="font-size: 9px;">
                ,<kc:prettyTime date="${model.lastModified}" locale="${pageContext.response.locale}"/>

                </span>

               <sec:authorize ifAnyGranted="ROLE_ADMIN">
                 |  <a href="<c:url value='/main/paste/delete'><c:param name="id" value="${model.id}"/> </c:url>"><fmt:message key='button.delete'/></a>
               </sec:authorize>


           </span>
        </h4>



<a href="<c:url value="/main/paste/${model.id-1}"/>" title="Previous paste">&#8592;</a>
        <a href="<c:url value="/main/paste/list"/>" title="Go back to list">list</a> |
        <a href="<c:url value="/main/paste/edit/${model.id}"/>" title="Edit paste">edit</a> |
        <a href="<c:url value="/main/paste/xml/${model.id}"/>" title="View as XML">xml</a> |
        <a href="<c:url value="/main/paste/${model.id}.json"/>" title="View as JSON">json</a> |
        <a href="<c:url value="/main/paste/plain/${model.id}"/>" title="View as plain text">plain</a> |

        <a id="ctrlc_link" data-clipboard-target="pasteTextPlain" href="javascript:void(0);" title="Copy to clipboard" >Ctrl-C</a> |

        <a href="<c:url value="/main/paste/${model.id+1}"/>" title="Next paste">&#8594;</a>

        <br/>

  <div style="padding-left: 2em;">
      <span style="vertical-align: top;font-size: larger;" class="i" title="Comments">C</span>
      <a href="javascript:void(0);" onclick="SyntaxHighlighter.toggleComments(0);" title="hide comments">hide</a>|
      <a href="javascript:void(0);" onclick="SyntaxHighlighter.toggleComments(1);" title="show comments">show</a>

  </div>

            <div>

                <pre id="pasteText" class="brush: ${model.codeType.code};toolbar: false; auto-links:false;" ><c:out value="${model.text}" escapeXml="true" /></pre>
                <code id="pasteTextPlain" style="display:none;"><c:out value="${model.text}" escapeXml="true" /></code>
            </div>

    <div id="commentsList">

        <c:forEach var="comment" items="${model.comments}" varStatus="loopStatus">

            <div id="numSpace_l${comment.lineNumber}" class="listSpace" >
            </div>



            <div id="comment_l${comment.id}" lineNumber="${comment.lineNumber}"  parentCommentId="${comment.parentId}" class="commentBlock" >
                <div class="commentInner">

                <div class="row">
                    <div class="column grid-1" style="padding-top: 0.2em;">

                        <c:choose>
                            <c:when test="${not empty comment.owner}">
                                <a href="http://ru.gravatar.com/site/check/${comment.owner.username}" title="GAvatar">
                                    <img style="vertical-align: top;padding-bottom: 2px;" src="<c:out value='http://www.gravatar.com/avatar/${comment.owner.avatarHash}?s=32'/>"/>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span style="font-size: 2em;" class="i" title="Anonymous">x</span>
                        </c:otherwise>
                        </c:choose>

                    </div>
                    <div class="column grid-14" style="font-size: small;padding-left: 0.1em; margin: 0;  ">
                        <c:choose>
                            <c:when test="${not empty comment.owner}">
                                    <a title="Contact ${comment.owner.name}"  href="mailto:${comment.owner.username}?subject=${model.name}"><c:out value="${comment.owner.name}" /></a>
                            </c:when>
                            <c:otherwise>
                                Anonymous
                            </c:otherwise>

                        </c:choose>
                        , <kc:prettyTime date="${comment.lastModified}" locale="${pageContext.response.locale}"/>

                    </div>

                </div>

                <div class="row">
                    <div class="column grid-1">
                        #
                    </div>
                    <div class="column grid-14">
                            <c:out value=" ${comment.text}" escapeXml="true"/>
                    </div>

                    <c:if test="${comment.parentId==null}">
                        <div class="column grid-1 right" style="font-size: 1em;text-align: right;">
                            <a  href="javascript:void(0);"  class="linkLine" title="Comment this"
                                onclick="SyntaxHighlighter.insertEditForm(${comment.lineNumber},${comment.id});"><span class="i">C</span></a>
                        </div>
                    </c:if>

                </div>

               </div>
            </div>


        </c:forEach>

    </div>



<c:url var="url" value='/main/paste/saveComment' />


<div id="numSpace" class="listEditSpace" >
</div>

 <div id="commentForm" class="editForm"  style="display:none;" >

     <form:form action="${url}"
           modelAttribute="comment"
           method="POST" >

    <input type="hidden" name="pasteId" value="${model.id}"/>
    <form:hidden path="lineNumber" id="lineNumber"/>
    <form:hidden path="parentId" id="commentParentId"/>

    <form:textarea path="text" id="commentText" cssErrorClass="error"    />
    <div class="commentOuter" style="">
        <form:errors path="text" cssClass="error"   />
    <input  name="submit" type="submit" value="Add comment"  />
        to line <span id="pageNum"></span>

        <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
          <span class="right"  style="padding-top: 0.5em;" >
              <span  style="padding-top: 1em;">
               <c:out escapeXml="true" value="${currentUser.name}" />

              </span>
            <a href="http://ru.gravatar.com/site/check/${currentUser.username}" title="GAvatar">
                <img style="vertical-align: top;padding-bottom: 2px;" src="<c:out value='http://www.gravatar.com/avatar/${currentUser.avatarHash}?s=32'/>"/>
            </a>

  </span>

        </sec:authorize>

    </div>



</form:form>


 </div>



<script type="text/javascript" src="<c:url value='/libs/zeroclipboard/ZeroClipboard.js'/>"></script>

                       <script type="text/javascript">

window.addEvent('domready', function() {

    ZeroClipboard.setDefaults( { moviePath: "<c:url value='/libs/zeroclipboard/ZeroClipboard.swf'/>" } );

    var clip = new ZeroClipboard(document.id("ctrlc_link"));
        //alert('Done! '+clip.ready());

    clip.on( 'load', function(client) {
    //     alert( "movie is loaded" );
    } );

    clip.on( 'complete', function(client, args) {
        growl.notify(args.text.length+' symbols copied to clipboard.');
        //alert(args.text.length+' symbols copied to clipboard.');
        //new MooDialog.Alert(args.text.length+' symbols copied to clipboard.');

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
       
       
                //alert(result);
                return result
            };



        </script>
        

