<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class="row">

   
    <div class="col-md-10 col-lg-10 ">

        <c:if test="${not empty model.commentCount and model.commentCount>0}">

            <span style="vertical-align: top;font-size: larger;" class="i" 
                  title="<fmt:message key="comments.title"/>">C</span>
            <a id="toggleCommentsCtl" href="javascript:void(0);" 
               onclick="SyntaxHighlighter.toggleComments(${model.id}, this);" 
               title="<fmt:message key="button.hide"/>">
                <span  class="i" >-</span>
            </a>
        </c:if>
    </div>

</div>


<div class="row">

  
    <div id="${model.id}_centerPanel" class="col-md-12" 
         style="min-width:650px;">

        <c:url var="drawImg" 
               value='/main/resources/${appId}/r/${model.lastModified.time}/${model.reviewImgData}'/>


        <a href="javascript:showAll(${model.id});" >all</a> |
        <a href="javascript:showComments(${model.id});" >comments</a> |
        <a href="javascript:showDrawArea(${model.id});">draw</a>
   
          <c:set var="backgroundReviewStyle" 
                 value="${model.reviewImgData==null ? '' : 'pointer-events:none;background: url('.concat(drawImg).concat(') no-repeat top left;') }"/>
      
       
        <div id="${model.id}_all" style="display:none;z-index:500;position:absolute;pointer-events:none;">
          
            <canvas id="${model.id}_sketch_ro" with="400" height="200" 
                    style="${backgroundReviewStyle}" >
            </canvas>
        </div>
         
   
        <div id="${model.id}_drawBlock" style="display:none;">
            <div class="tools">
               <a href="#${model.id}_sketch" data-tool="marker">Marker</a> |
                <a href="#${model.id}_sketch" data-tool="eraser">Eraser</a>


                <c:url var="urlDrawSave" value='/main/paste/saveReviewDraw' />


                <form:form cssClass="form-horizontal" 
                           action="${urlDrawSave}" id="${model.id}_saveReviewDraw"

                           method="POST" >
                    <input type="hidden" name="pasteId" value="${model.id}"/>
                    <input id="${model.id}_reviewDrawImg" name="reviewImgData" type="hidden" value=""/>

                    <input id="${model.id}_thumbImg" name="thumbImgData" type="hidden" value=""/>

                    
                    <button id="${model.id}_saveReviewBtn" class='sbtn p-btn-save' type="submit"  >
                        <span class="i" style="font-size:larger;">S</span>
                        <span id="btnCaption"><fmt:message key="button.save"/></span>
                        <i id="btnIcon" style="display:none;" class="fa fa-spinner fa-spin"></i>       
                    </button>

                </form:form>
            </div>
            <div id="${model.id}_drawArea" style="z-index:5000;position:absolute;
                 background-color:rgba(100,70,0,0.1);">


                <canvas id="${model.id}_sketch" with="400" height="200"  style="cursor:crosshair;"
                         >
                </canvas>


            </div>

        </div>

                         <div>
        <pre id="${model.id}_pasteText" 
             class="brush: ${model.codeType.code};toolbar: false; auto-links:false;highlight: [${commentedLinesList}]; " style="display:none; overflow-y: hidden;" >
            <c:out value="${model.text}" escapeXml="true" />
        </pre>                     
                         </div>                  
        
    </div>
</div>


<div id="${model.id}_commentsList" style="display:none;">

    <c:forEach var="comment" items="${model.comments}" varStatus="loopStatus">

        <div id="${model.id}_numSpace_l${comment.id}" class="line" >
            <a href="#comment_l${comment.id}" title="<c:out value="${comment.id}"/>">#</a>
        </div>



        <div id="${model.id}_comment_l${comment.id}"  
             class="commentBlock ${comment.parentId==null ? 'parentComment' :'subComment'}" 
             commentId="${comment.id}"
             lineNumber="${comment.lineNumber}"  parentCommentId="${comment.parentId}"

             >
            <div id="innerBlock" class="commentInner p-comment" 
                 >


                <div class="row" >
                    <div class="col-md-12" >
                        <div id="commentMarkedText">
                        <c:out value=" ${comment.text}" escapeXml="false"/>
                            
                        </div>
                    </div>
                </div>
                <div class="row">

                    <div class="col-md-8"  >
                        <small>
                            <tiles:insertDefinition name="/common/owner" >
                                <tiles:putAttribute name="model" value="${comment}"/>
                                <tiles:putAttribute name="modelName" value="comment"/>
                            </tiles:insertDefinition>                      
                            , <kc:prettyTime date="${comment.lastModified}" locale="${pageContext.response.locale}"/>
                        </small>
                    </div>

                    <div class="col-md-2 pull-right" style="text-align:right;margin-right: 0.5em;"  >

                        <c:if test="${comment.parentId==null}">
                            <a  href="javascript:void(0);" class="linkLine" title="<fmt:message key="comments.sub"/>"
                                onclick="SyntaxHighlighter.insertEditForm(${model.id},${comment.lineNumber},${comment.id});"><span class="i">C</span></a>
                        </c:if>
                        <sec:authorize access="${currentUser !=null and (currentUser.admin or ( comment.hasOwner  and comment.owner eq currentUser)) }">


                            <a class="deleteBtn" 
                               id="deleteCommentBtn_${model.id}_${comment.id}" 
                               href="<c:url value='/main/paste/removeComment'>
                                   <c:param name="pasteId" value="${model.id}"/>

                                   <c:param name="commentId" value="${comment.id}"/>
                                   <c:param name="lineNumber" value="${comment.lineNumber}"/>
                               </c:url>"
                               title="<fmt:message key='button.delete'/>">
                                <span style="font-size: larger;" class="i">d</span>

                                <div style="display:none;" id="dialogMsg">


                                    <fmt:message  key="dialog.confirm.paste.remove.comment">
                                        <fmt:param value="${model.id}"/>
                                    </fmt:message>    
                                </div>
                            </a>


                        </sec:authorize>
                    </div>
                </div>


            </div>
        </div>


    </c:forEach>

</div>

<c:url var="url" value='/main/paste/saveComment' />


<div id="${model.id}_commentForm" class="row  p-comment"  style="display:none;max-width: 60em;margin-left:-10px;">
    <div class="col-md-11"  >

        <div class="panel panel-info" style="margin-bottom:0;">

            <div class="panel-body bg-info" style="padding:0.1em;">

                <c:choose>
                    <c:when test="${empty currentUser and !allowAnonymousCommentsCreate}">

                        <fmt:message key="auth.login-to-add-comment"/>

                        <form id="${model.id}_addCommentForm" style="display:none;max-width:30em;" >
                            <input type="hidden"  id="lineNumber"/>
                            <input type="hidden"  id="parentId"/>
                             <input id="${model.id}_thumbImgComment" 
                                    name="thumbImage" type="hidden" value=""/>

                            <fmt:message key="comments.line">
                                <fmt:param value="<span id='pageNum'></span>"/>
                            </fmt:message>
                            <textarea id="commentText"></textarea>
                            <button id="${model.id}_addCommentBtn">      
                            </button>
                        </form>
                    </c:when>
                    <c:otherwise>

                        <form:form cssClass="form-horizontal" 
                                   action="${url}" id="${model.id}_addCommentForm"

                                   modelAttribute="comment"
                                   method="POST" >
                            <form:hidden path="lineNumber" id="lineNumber"/>
                            <form:hidden path="parentId" id="parentId"/>
                            <input id="${model.id}_thumbImgComment" 
                                    name="thumbImage" type="hidden" value=""/>
                            <form:hidden path="pasteId"/>

                            <div class="form-group" style="margin-bottom:0.1em;">
                                <div class="col-md-12"  >


                                    <form:errors path="text" cssClass="control-label" element="label" for="commentText" /> 
                                    
                                    <div class="disableOnSubmit" id="epiceditor-${model.id}"></div>
                                    
                                    <form:textarea cssClass="form-control" path="text"
                                                   id="commentText-${model.id}" 
                                                   cssStyle="display:none;"
                                                   cssErrorClass="form-control"                                       
                                                   />

                                </div>
                            </div>

                            <div class="form-group" style="margin-bottom:0.1em;">


                                <div class="col-md-4"  >
                                    <button id="${model.id}_addCommentBtn" class='sbtn p-btn-save' type="submit"  >
                                        <span class="i" style="font-size:larger;">S</span>
                                        <span id="btnCaption"><fmt:message key="button.add"/></span>
                                        <i id="btnIcon" style="display:none;" class="fa fa-spinner fa-spin"></i>       
                                    </button>

                                    <a class="disableOnSubmit" title="<fmt:message key="button.cancel"/>" 
                                       href="javascript:void(0);" onclick="SyntaxHighlighter.hideEditForm(${model.id});">
                                        <span ><fmt:message key="button.cancel"/></span>
                                    </a>


                                </div>

                                <div class="col-md-3 pull-right">
                                    <fmt:message key="comments.line">
                                        <fmt:param value="<span id='pageNum'></span>"/>
                                    </fmt:message>

                                </div>    
                            </div>

                        </form:form>


                    </c:otherwise>
                </c:choose>


            </div>
        </div>


    </div>
</div>

