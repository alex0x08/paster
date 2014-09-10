<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

             
<div class="row">
    <div class="column grid-16">
        
        
        <c:set var="priorTitle"><fmt:message key="${model.priority.name}"/></c:set>

            <h4 class="f-h4" style="padding-top: 0;margin-top:0;">
                <span class="i ${model.priority.cssClass}" style="font-size:2em;" title="${priorTitle}" >/</span>
            <c:if test="${model.sticked}">
                <span class="i">]</span>
            </c:if>
                
             <a href="<c:url value="/main/paste/edit/${model.id}"/>" 
               title="<fmt:message key="button.edit"/>">
                    <c:out value="${model.name}" escapeXml="true"/>
            </a>    
                    
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

    
           <c:set var="centerGridSize" value="grid-10"/>
     
    
    <div id="${model.id}_centerPanel" class="column ${centerGridSize}" style="min-width:650px;">

        <pre id="${model.id}_pasteText" class="brush: ${model.codeType.code};toolbar: false; auto-links:false;highlight: [${commentedLinesList}]; " style="display:none; overflow-y: hidden;" >
            <c:out value="${model.text}" escapeXml="true" /></pre>
     
    </div>


        <div id="${model.id}_rightPanel" class="column grid-4" style="min-width:150px;" >

            <c:if test="${not empty model.thumbImage}">
                
                 <div style="max-width:250px;" >

                     <c:if test="${not empty availableNext and not empty availableNext.thumbImage}">
                         <a href="<c:url value="/${availableNext.id}"/>"  title="<fmt:message key="button.next"/>">
                             <img width="300" height="200" class="p-comment" style="alignment-adjust: middle; width: 200px; height: 100px;"
                                  src="<c:url value='/main/resources/${appVersion}/t/${availableNext.lastModified.time}/${availableNext.thumbImage}.jpg' >
                                  </c:url>" />
                         </a>
                     </c:if>

                <img width="300" height="200" class="p-comment" style="border: 1px solid black;alignment-adjust: middle;width: 250px; height: 150px;" 
                     src="<c:url value='/main/resources/${appVersion}/t/${model.lastModified.time}/${model.thumbImage}.jpg' >
                 </c:url>"/>

                <c:if test="${availablePrev!=null and not empty availablePrev.thumbImage}">
                    <a href="<c:url value="/${availablePrev.id}"/>"  title="<fmt:message key="button.prev"/>">
                        <img width="300" height="200" class="p-comment" style="alignment-adjust: middle; width: 200px; height: 100px;" 
                             src="<c:url value='/main/resources/${appVersion}/t/${availablePrev.lastModified.time}/${availablePrev.thumbImage}.jpg' >
                             </c:url>"/>
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

        <div id="${model.id}_comment_l${comment.id}" class=" commentBlock ${comment.parentId==null ? 'parentComment' :'subComment'}" commentId="${comment.id}"
             lineNumber="${comment.lineNumber}"  parentCommentId="${comment.parentId}"  >
            <div id="innerBlock" class="commentInner p-comment">

                
                 <div class="row">
                    <div class="column grid-16">
                        <c:out value=" ${comment.text}" escapeXml="false"/>
                    </div>
                </div>
                <div class="row">

                    <div class="column grid-10" style="font-size: small;padding-left: 0.1em; margin: 0;  ">

                        <tiles:insertDefinition name="/common/owner" >
                            <tiles:putAttribute name="model" value="${comment}"/>
                            <tiles:putAttribute name="modelName" value="comment"/>
                        </tiles:insertDefinition>                      
                        , <kc:prettyTime date="${comment.lastModified}" locale="${pageContext.response.locale}"/>
                    </div>

                    <div class="column" style="width:10%;float:right;padding:0;margin:0;" >
                        <a href="#comment_l${comment.id}" title="<c:out value="${comment.id}"/>">#</a>
                        <c:if test="${comment.parentId==null}">
                            <a  href="javascript:void(0);" class="linkLine" title="<fmt:message key="comments.sub"/>"
                                onclick="SyntaxHighlighter.insertEditForm(${model.id},${comment.lineNumber},${comment.id});"><span class="i">C</span></a>
                        </c:if>
                        <sec:authorize access="${currentUser !=null and (currentUser.admin or ( comment.hasOwner  and comment.owner eq currentUser)) }">
                            <a href="<c:url value='/main/paste/removeComment'>
                                   <c:param name="pasteId" value="${model.id}"/>
                                   <c:param name="pasteRev" value="${lastRevision}"/>
                                   <c:param name="commentId" value="${comment.id}"/>
                                   <c:param name="lineNumber" value="${comment.lineNumber}"/>
                               </c:url>" title="<fmt:message key='button.delete'/>">
                                <span style="font-size: larger;" class="i">d</span>
                            </a>
                        </sec:authorize>
                    </div>
                </div>

               
            </div>
        </div>


    </c:forEach>

</div>

<c:url var="url" value='/main/paste/saveComment' />



<div id="${model.id}_commentForm" class="editForm p-comment"  style="display:none;" >

    <c:choose>
        <c:when test="${empty currentUser and !allowAnonymousCommentsCreate}">
            
   <fmt:message key="auth.login-to-add-comment"/>
  
   <form id="${model.id}_addCommentForm" style="display:none;">
                 <input type="hidden"  id="lineNumber"/>
                 <input type="hidden"  id="parentId"/>
            <fmt:message key="comments.line">
                    <fmt:param value="<span id='pageNum'></span>"/>
                </fmt:message>
   
            </form>
        </c:when>
        <c:otherwise>
            
            
    <form:form action="${url}" id="${model.id}_addCommentForm"
               modelAttribute="comment"
               method="POST" >
    <form:hidden path="lineNumber" id="lineNumber"/>
    <form:hidden path="parentId" id="parentId"/>
    <form:hidden path="pasteRev"/>
    <form:hidden path="pasteId"/>
                   
        <div class="row" >
            <div class="column grid-15"  >
                <form:errors path="text" cssClass="error" /> 
                <form:textarea path="text" id="commentText" cssErrorClass="error"  />
            </div>
            <div class="column grid-1" style="float:right;padding-top: 0.5em;" >

                <a class="disableOnSubmit" title="<fmt:message key="button.cancel"/>" 
                   href="javascript:void(0);" onclick="SyntaxHighlighter.hideEditForm(${model.id});">
                    <i class="fa fa-times" style="font-size:1.2em;font-weight: bold;"></i>
                </a>

            </div>
        </div>

                    <div class="row">
                        <div class="column grid-16">
                             <fmt:message key="comments.line">
                    <fmt:param value="<span id='pageNum'></span>"/>
                </fmt:message>
                        
                        </div>
                    </div>            
                    
        <div class="row">
            
            
            <div class="column grid-16"  >
                <button id="${model.id}_addCommentBtn" class='sbtn p-btn-save' type="submit"  >
                    <span class="i" style="font-size:larger;">S</span>
                    <span id="btnCaption"><fmt:message key="button.add"/></span>
                    <i id="btnIcon" style="display:none;" class="fa fa-spinner fa-spin"></i>       
                </button>
                
                <a class="disableOnSubmit" title="<fmt:message key="button.cancel"/>" 
                   href="javascript:void(0);" onclick="SyntaxHighlighter.hideEditForm(${model.id});">
                    <span ><fmt:message key="button.cancel"/></span>
                </a>
            
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

    </form:form>

            
        </c:otherwise>
    </c:choose>
    
</div>
