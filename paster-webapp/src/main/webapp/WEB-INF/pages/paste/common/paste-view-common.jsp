<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<c:set var="priorTitle"><fmt:message key="${model.priority.name}"/></c:set>


    <div class="row">
        <div class="col-md-12">

            <a href="<c:url value="/main/paste/list"/>" target="${target}"
           title="<fmt:message key="paste.list.title"/>">
            <span style="font-size: larger;" class="i">(</span></a>

        <span class="i ${model.priority.cssClass}" style="font-size:2em;" title="${priorTitle}" >/</span>
        <c:if test="${model.sticked}">
            <span class="i">]</span>
        </c:if>

        <a href="<c:url value="/main/paste/edit/${model.id}"/>" 
           title="<fmt:message key="button.edit"/>">
            <c:out value="${model.name}" escapeXml="true"/>
        </a>    
        
                <tiles:insertDefinition name="/common/deleteLink" >
                    <tiles:putAttribute name="model" value="${model}"/>
                    <tiles:putAttribute name="modelName" value="paste"/>
                    <tiles:putAttribute name="currentUser" value="${currentUser}"/>
                </tiles:insertDefinition>


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

    <div class="col-md-2 hidden-sm hidden-xs">
        <a id="${model.id}_rightPanelCtrl" href="javascript:void(0);" onclick="toggleRight(${model.id});" title="toggle right panel">
            <span class="i">-</span>
        </a>
    </div>


    <div class="col-md-10 col-lg-10 ">
        &nbsp;
        <c:if test="${not empty model.commentCount and model.commentCount>0}">

            <span style="vertical-align: top;font-size: larger;" class="i" title="<fmt:message key="comments.title"/>">C</span>
            <a id="toggleCommentsCtl" href="javascript:void(0);" onclick="SyntaxHighlighter.toggleComments(${model.id}, this);" title="<fmt:message key="button.hide"/>">
                <span  class="i" >-</span>
            </a>
        </c:if>
       



    </div>


</div>

        
<div class="row">


    <c:set var="centerGridSize" value="col-md-8"/>

    <div id="${model.id}_rightPanel" class="col-md-3 hidden-sm hidden-xs" style="min-width:150px;" >

        <c:if test="${not empty model.thumbImage}">

            <div style="max-width:250px;" >

                <c:if test="${not empty availableNext and not empty availableNext.thumbImage}">
                    <a href="<c:url value="/${availableNext.id}"/>"  title="<fmt:message key="button.next"/>">
                        <img width="300" height="200" class="img-thumbnail img-responsive p-comment" style="alignment-adjust: middle; width: 200px; height: 100px;"
                             src="<c:url value='/main/resources/${appVersion}/t/${availableNext.lastModified.time}/${availableNext.thumbImage}.jpg' >
                             </c:url>" />
                    </a>
                </c:if>

                <img width="300" height="200" class="img-thumbnail img-responsive p-comment" style="border: 1px solid black;alignment-adjust: middle;width: 250px; height: 150px;" 
                     src="<c:url value='/main/resources/${appVersion}/t/${model.lastModified.time}/${model.thumbImage}.jpg' >
                     </c:url>"/>

                <c:if test="${availablePrev!=null and not empty availablePrev.thumbImage}">
                    <a href="<c:url value="/${availablePrev.id}"/>"  title="<fmt:message key="button.prev"/>">
                        <img width="300" height="200" class="img-thumbnail img-responsive p-comment" style="alignment-adjust: middle; width: 200px; height: 100px;" 
                             src="<c:url value='/main/resources/${appVersion}/t/${availablePrev.lastModified.time}/${availablePrev.thumbImage}.jpg' >
                             </c:url>"/>
                    </a>
                </c:if>

            </div>

        </c:if>

       

    </div> 

    <div id="${model.id}_centerPanel" class="${centerGridSize}" style="min-width:650px;">

        <pre id="${model.id}_pasteText" class="brush: ${model.codeType.code};toolbar: false; auto-links:false;highlight: [${commentedLinesList}]; " style="display:none; overflow-y: hidden;" >
            <c:out value="${model.text}" escapeXml="true" /></pre>

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
                    <div class="col-md-12">
                        <c:out value=" ${comment.text}" escapeXml="false"/>
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
               
                <form:hidden path="pasteId"/>

                <div class="form-group" style="margin-bottom:0.1em;">
                    <div class="col-md-12"  >
                        
                        
                        <form:errors path="text" cssClass="control-label" element="label" for="commentText" /> 
                        <form:textarea cssClass="form-control" path="text"
                                       id="commentText" cssErrorClass="form-control"                                       
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
