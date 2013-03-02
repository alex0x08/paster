<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<jsp:include page="/WEB-INF/pages/template/search.jsp"/>


<c:set var="priorTitle"><fmt:message key="${model.priority.name}"/></c:set>


        <h4 class="f-h4"><span class="i ${model.priority.cssClass}" style="font-size:2em;" title="${priorTitle}" >/</span>
            <c:if test="${model.sticked}">
                <span class="i">]</span>
            </c:if>
            <c:out value="${model.name}" escapeXml="true"/>
           <span style="font-weight: normal; font-size: 12px;">
                 ( <c:forEach var="tag" items="${model.tags}" varStatus="loopStatus">
               <a href="<c:url value='/main/paste/list/search?query=tags:${tag}'/>"><c:out value=" ${tag}"/></a>
               <c:if test="${!loopStatus.last}"> , </c:if>
                  </c:forEach> )
               <c:if test="${model.owner ne null}">
                ,  by
                   <a href="http://ru.gravatar.com/site/check/${model.owner.username}" title="GAvatar">
                       <img style="vertical-align: middle;padding-bottom: 2px;" src="<c:out value='http://www.gravatar.com/avatar/${model.owner.avatarHash}?s=32'/>"/>
                   </a>
                <span style="display: inline;  ">
                        <a title="Contact ${model.owner.name}"  href="mailto:${model.owner.username}?subject=${model.name}"><c:out value="${model.owner.name}" /></a>
                </span>
                </c:if>

                   <c:if test="${not empty model.commentCount and model.commentCount>0}">
                       , <span style="vertical-align: middle;" class="i">C</span>
                       <span style="font-weight: normal;font-size: 8px;"> x <c:out value="${model.commentCount}"/></span>
                   </c:if>
                <span style="font-size: 9px;">
                ,<kc:prettyTime date="${model.lastModified}" locale="${pageContext.response.locale}"/>

                </span>

           </span>
        </h4>



<a href="<c:url value="/main/paste/${model.id-1}"/>">&#8592;</a>
        <a href="<c:url value="/main/paste/list"/>">list</a> |
        <a href="<c:url value="/main/paste/edit/${model.id}"/>">edit</a> |
        <a href="<c:url value="/main/paste/xml/${model.id}"/>">xml</a> |
        <a href="<c:url value="/main/paste/${model.id}.json"/>">json</a> |
        <a href="<c:url value="/main/paste/plain/${model.id}"/>">plain</a> |

        <a id="ctrlc_link" data-clipboard-target="pasteTextPlain" href="javascript:void(0);" >Ctrl-C</a> |

        <a href="<c:url value="/main/paste/${model.id+1}"/>">&#8594;</a>

        <br/>

<a href="javascript:void(0);" onclick="SyntaxHighlighter.toggleComments(1);">hide comments</a>
<a href="javascript:void(0);" onclick="SyntaxHighlighter.toggleComments(0);">show comments</a>





            <div>

                <pre id="pasteText" class="brush: ${model.codeType.code};toolbar: false; auto-links:false;" ><c:out value="${model.text}" /></pre>
                <code id="pasteTextPlain" style="display:none;"><c:out value="${model.text}" /></code>
            </div>

    <div id="commentsList">

        <c:forEach var="comment" items="${model.comments}" varStatus="loopStatus">

            <div id="numSpace_l${comment.lineNumber}" class="listSpace" >
            </div>


            <div id="comment_l${comment.lineNumber}" class="commentBlock" >

                <div style="min-width:15em; margin-top: 0; vertical-align: text-top;  background-color: #ff7f50;">
                    <c:out value=" ${comment.text}"/>
                </div>

                <c:choose>
                    <c:when test="${not empty comment.owner}">

                        <div style="font-size: small;padding-top: 0.5em; ">

                            <a href="http://ru.gravatar.com/site/check/${comment.owner.username}" title="GAvatar">
                                <img style="vertical-align: top;padding-bottom: 2px;" src="<c:out value='http://www.gravatar.com/avatar/${comment.owner.avatarHash}?s=32'/>"/>
                            </a>


                            <div style="display: inline;font-size: small;  ">
                                <a title="Contact ${comment.owner.name}"   href="mailto:${comment.owner.username}?subject=${model.name}"><c:out value="${comment.owner.name}" /></a>
                                , <kc:prettyTime date="${comment.lastModified}" locale="${pageContext.response.locale}"/>
                            </div>

                        </div>


                    </c:when>
                    <c:otherwise>


                        <div style="display: inline;font-size: small;  ">
                            <a title="Contact ${comment.owner.name}"   href="mailto:${comment.owner.username}?subject=${model.name}"><c:out value="${comment.owner.name}" /></a>
                            <kc:prettyTime date="${comment.lastModified}" locale="${pageContext.response.locale}"/>
                        </div>


                    </c:otherwise>
                </c:choose>

            </div>
        </c:forEach>

    </div>



<c:url var="url" value='/main/paste/saveComment' />


<div id="numSpace" class="listSpace" >
</div>

 <div id="commentForm" class="editForm" ">
     <form:form action="${url}"
                modelAttribute="comment"
                method="POST" >
         <form:errors path="*" cssClass="errorblock" element="div"/>
         <input type="hidden" name="pasteId" value="${model.id}"/>
         <form:hidden path="lineNumber" id="lineNumber"/>
         <form:textarea path="text" id="commentText"   />
         <input  name="submit" type="submit" value="Add comment"  />
         <div style="display:inline;">to page <span id="pageNum"></span></div>

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
        

