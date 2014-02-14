<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>


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

            <h4 class="f-h4" style="padding-top: 0;margin-top:0;">
                <span class="i ${model.priority.cssClass}" style="font-size:2em;" title="${priorTitle}" >/</span>
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
                    <fmt:message key="${model.codeType.name}"/>    
                    ,<kc:prettyTime date="${model.lastModified}" locale="${pageContext.response.locale}"/>
                </span>

            </span>
        </h4>


        <tiles:insertDefinition name="/common/pasteControls" >
            <tiles:putAttribute name="model" value="${model}"/>

            <c:if test="${not empty availableNext}">
                <tiles:putAttribute name="next" value="${availableNext}" />
            </c:if>
            <c:if test="${availablePrev!=null}">
                <tiles:putAttribute name="prev" value="${availablePrev}"  />
            </c:if>
        </tiles:insertDefinition>

    </div>
</div>

<div class="row">
    <div class="column grid-12">
        &nbsp;
        <c:if test="${not empty model.commentCount and model.commentCount>0}">

            <span style="vertical-align: top;font-size: larger;" class="i" title="<fmt:message key="comments.title"/>">C</span>
            <a id="toggleCommentsCtl" href="javascript:void(0);" onclick="SyntaxHighlighter.toggleComments(${model.id}, this);" title="<fmt:message key="button.hide"/>">
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
            <a id="${model.id}_rightPanelCtrl" href="javascript:void(0);" onclick="toggleRight(${model.id});" title="toggle right panel">
                <span class="i">-</span>
            </a>
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

    <div id="${model.id}_centerPanel" class="column ${centerGridSize}" style="min-width:650px;">

        <div id="pasteLoadSpinner" style="">
            <img src="<c:url value='/main/static/${appVersion}/images/gear_sml.gif'/>"/>  
            <fmt:message key="action.loading"/>   
            <img style="border: 2px saddlebrown;" src="${model.thumbImageRead}"/>

        </div>    

        <pre id="pasteText" class="brush: ${model.codeType.code};toolbar: false; auto-links:false;highlight: [${commentedLinesList}]; " style="display:none; overflow-y: hidden;" >
            <c:out value="${model.text}" escapeXml="true" /></pre>
        <code id="pasteTextPlain" style="display:none;"><c:out value="${model.text}" escapeXml="true" /></code>

    </div>


        <div id="${model.id}_rightPanel" class="column grid-4" style="min-width:150px;" >

            <c:if test="${not empty model.thumbImage}">
                
                 <div style="max-width:250px;" >

                     <c:if test="${not empty availableNext and not empty availableNext.thumbImage}">
                         <a href="<c:url value="/${availableNext.id}"/>"  title="<fmt:message key="button.next"/>">
                             <img style="border: 2px saddlebrown;alignment-adjust: middle; width: 200px; height: 100px;" src="${availableNext.thumbImageRead}"/>
                         </a>
                     </c:if>

                <img style="border: 2px solid saddlebrown;alignment-adjust: middle" src="${model.thumbImageRead}"/>

                <c:if test="${availablePrev!=null and not empty availablePrev.thumbImage}">
                    <a href="<c:url value="/${availablePrev.id}"/>"  title="<fmt:message key="button.prev"/>">
                        <img style="border: 2px saddlebrown;alignment-adjust: middle; width: 200px; height: 100px;" src="${availablePrev.thumbImageRead}"/>
                    </a>
                </c:if>

            </div>
                
            </c:if>
            
           

            <c:if test="${shareIntegration}">
                <iframe id="${model.id}_shareFrame" src="${shareUrl}/main/file/integrated/list/paste_${model.id}"
                        scrolling="auto" frameborder="0"
                        style="width:340px; "  allowTransparency="true"   >
                </iframe>
            </c:if>

        </div> 
</div>

<div id="${model.id}_commentsList" style="display:none;">

    <c:forEach var="comment" items="${model.comments}" varStatus="loopStatus">

        <div id="${model.id}_numSpace_l${comment.id}" class="listSpace" >
        </div>

        <div id="${model.id}_comment_l${comment.id}" class=" commentBlock " commentId="${comment.id}"
             lineNumber="${comment.lineNumber}"  parentCommentId="${comment.parentId}"  >
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
                            <a  href="javascript:void(0);" class="linkLine" title="<fmt:message key="comments.sub"/>"
                                onclick="SyntaxHighlighter.insertEditForm(${model.id},${comment.lineNumber},${comment.id});"><span class="i">C</span></a>
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
                        <c:out value=" ${comment.text}" escapeXml="false"/>
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
        <img src="<c:url value='/main/static/${appVersion}/images/ctrlc.png'/>"/></a>
</span>

<span id="pasteLineToCopy" style="display:none;">
    NONE
</span>

<div id="${model.id}_commentForm" class="editForm p-comment"  style="display:none;" >

    <form:form action="${url}" id="${model.id}_addCommentForm"
               modelAttribute="comment"
               method="POST" >

        <div class="row" >
            <div class="column grid-15"  >
                <input type="hidden" name="pasteId" value="${model.id}"/>
                <form:hidden path="lineNumber" id="lineNumber"/>
                <form:hidden path="parentId" id="commentParentId"/>
                <form:textarea path="text" id="commentText" cssErrorClass="error"  />
            </div>
            <div class="column grid-1" style="float:right;" >

                <a class="" title="<fmt:message key="button.cancel"/>" 
                   href="javascript:void(0);" onclick="SyntaxHighlighter.hideEditForm(${model.id});">
                    <span class="i" style="font-size:1.5em;">d</span>
                </a>

            </div>
        </div>

        <div class="row">
            <div class="column grid-16" >

                <button id="${model.id}_addCommentBtn" class='p-btn-save' type="submit">
                    <span class="i" style="font-size:larger;">S</span>
                    <span id="btnCaption"><fmt:message key="button.save"/></span>
                    <img id="btnIcon" style="display:none;" src="<c:url value='/main/static/${appVersion}/images/gear_sml.gif'/>"/>
                </button>


                <fmt:message key="comments.line">
                    <fmt:param value="<span id='pageNum'></span>"/>
                </fmt:message>

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




<script type="text/javascript">


    function toggleRight(modelId) {

                            var rightPanel = document.getElementById(modelId + '_rightPanel'),
                                    centerPanel = document.getElementById(modelId + '_centerPanel'),
                                    rightPanelCtrl = document.getElementById(modelId + '_rightPanelCtrl');

                            if (rightPanel.getStyle('display') != 'none') {
                                rightPanel.setStyle('display', 'none');
                                centerPanel.set('class', 'column grid-15');
                                rightPanelCtrl.getElement('span').set('text', '+');
                            } else {
                                centerPanel.set('class', 'column grid-12');
                                rightPanel.setStyle('display', '');
                                rightPanelCtrl.getElement('span').set('text', '-');
                            }
                        }

  window.addEvent('domready', function() {
      $(${model.id} + '_shareFrame').setStyle('height', document.body.scrollHeight< 1024 ?  '1024px' :  document.body.scrollHeight + 'px');
        });


    SyntaxHighlighter.config.tagName = "pre";

    window.addEvent('domready', function() {

        $(${model.id} + '_addCommentBtn').addEvent('click', function() {
            this.getElementById('btnCaption').set('text', transmitText).disabled = true;
            this.getElementById('btnIcon').setStyle('display', '');
            $('${model.id}_addCommentForm').submit();
            
        });

        ZeroClipboard.setDefaults({moviePath: "<c:url value='/main/static/${appVersion}/ZeroClipboard.swf'/>"});

        var clip = new ZeroClipboard(document.id("ctrlc_link"));

        clip.on('complete', function(client, args) {
            growl.notify(args.text.length + ' symbols copied to clipboard.');
        });

        var clipLine = new ZeroClipboard(document.id("ctrlc_line"));

        clipLine.on('complete', function(client, args) {
            growl.notify(args.text.length + ' symbols copied to clipboard.');
        });

        SyntaxHighlighter.highlight(${model.id}, {}, $('pasteText'), true);

        $('pasteLoadSpinner').setStyle('display', 'none');

    <c:if test="${availablePrevList.count > 1}">
        initLazy();
    </c:if>

         });

</script>


<c:if test="${availablePrevList.count > 1}">


    <c:url var="rawPageUrl" value="/main/paste/raw/view"/>
    <c:url var="userPageUrl" value="/main/paste"/>

    <script type="text/javascript">

            var userPageUrl = '${userPageUrl}', pageUrl = '${rawPageUrl}';

            function initLazy() {
                new LazyPagination(document, {
                    url: pageUrl,
                    method: 'get',
                    maxRequests: ${availablePrevList.count},
                    buffer: 100,
                    pageDataIndex: 'page',
                    idMode: true,
                    data: {
                        page: 0,
                        id: ${model.id}
                    },
                    idSet: [${availablePrevList.itemsAsString}],
                    inject: {
                        element: 'morePages',
                        where: 'before'
                    }, beforeLoad: function() {
                        $('pageLoadSpinner').setStyle('display', '');
                    }, afterAppend: function(block, page) {

                        var ptext = document.getElementById(page + '_pasteText');

                        SyntaxHighlighter.highlight(page, {}, ptext, false);

                        ptext.setStyle('display', 'none');

                        $(page + '_addCommentBtn').addEvent('click', function() {
                            this.getElementById('btnCaption').set('text', transmitText).disabled = true;
                            this.getElementById('btnIcon').setStyle('display', '');
                            $(page + "_addCommentForm").submit();
                        });

                        try {
                            history.pushState({id: page}, "Page " + page, userPageUrl + "/" + page);
                        } catch (e) {
                        }

                        $('pageLoadSpinner').setStyle('display', 'none');
                    }

                });
            }
            ;

    </script>
    <div id="pageLoadSpinner" style="display:none;">
        <img src="<c:url value='/main/static/${appVersion}/images/gear_sml.gif'/>"/>   
        <fmt:message key="action.loading"/>
    </div>
    <div id="morePages"></div>
</c:if>
