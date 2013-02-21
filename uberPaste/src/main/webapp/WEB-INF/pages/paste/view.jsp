<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<jsp:include page="/WEB-INF/pages/template/search.jsp"/>


<c:set var="priorTitle"><fmt:message key="${model.priority.name}"/></c:set>


        <h4 class="f-h4"><span class="i ${model.priority.cssClass}" style="font-size:2em;" title="${priorTitle}" >/</span>
            <c:out value="${model.name}" escapeXml="true"/>
           <span style="font-weight: normal; font-size: 10px;">
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
                ,<kc:prettyTime date="${model.lastModified}" locale="${pageContext.response.locale}"/>

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



            <div>

                <pre id="pasteText" class="brush: ${model.codeType.code};toolbar: false; auto-links:false;" ><c:out value="${model.text}" /></pre>
                <code id="pasteTextPlain" style="display:none;"><c:out value="${model.text}" /></code>
            </div>


 <c:forEach var="comment" items="${model.comments}" varStatus="loopStatus">
    <div id="comment_l${comment.lineNumber}" style="">
        <div style="min-width:15em;border-top: solid 1px #696969;padding-top:1em; padding-bottom: 1em;">
            <c:out value=" ${comment.text}"/>
         </div>

        <small>

            <a href="http://ru.gravatar.com/site/check/${comment.owner.username}" title="GAvatar">
                <img style="vertical-align: top;padding-bottom: 2px;" src="<c:out value='http://www.gravatar.com/avatar/${comment.owner.avatarHash}?s=32'/>"/>
            </a>

                <div style="display: inline;  ">
                        <a title="Contact ${comment.owner.name}"   href="mailto:${comment.owner.username}?subject=${model.name}"><c:out value="${comment.owner.name}" /></a>
                   , <kc:prettyTime date="${comment.lastModified}" locale="${pageContext.response.locale}"/>


                </div>

        </small>

    </div>
</c:forEach>


<c:url var="url" value='/main/paste/saveComment' />

<form:form action="${url}" id="commentForm" cssStyle="display:none;padding-right: 3em;padding-top:1em;"
           modelAttribute="comment"
           method="POST" >
    <form:errors path="*" cssClass="errorblock" element="div"/>
    <input type="hidden" name="pasteId" value="${model.id}"/>
    <form:hidden path="lineNumber" id="lineNumber"/>
    <form:textarea path="text" id="commentText" cols="40" rows="6"/>
    <input name="submit" type="submit" value="Add comment"  />

</form:form>


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
        

