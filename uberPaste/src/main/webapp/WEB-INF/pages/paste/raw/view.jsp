<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div id="${model.id}_pagedPaste">
        
    
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
            <tiles:putAttribute name="mode" value="raw"/>
        </tiles:insertDefinition>

    </div>
</div>

<div class="row">
    <div class="column grid-12">
    &nbsp;
        <c:if test="${not empty model.commentCount and model.commentCount>0}">

            <span style="vertical-align: top;font-size: larger;" class="i" title="<fmt:message key="comments.title"/>">C</span>
                <a id="toggleCommentsCtl" href="javascript:void(0);" onclick="SyntaxHighlighter.toggleComments(${model.id},this);" title="<fmt:message key="button.hide"/>">
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
            <a id="${model.id}_rightPanelCtrl" href="javascript:void(0);" onclick="toggleRight(${model.id});" title="toggle right panel">
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

    <div id="${model.id}_centerPanel" class="column ${centerGridSize}" style="min-width:650px;">
    
      
    <pre id="${model.id}_pasteText" class="brush: ${model.codeType.code};toolbar: false; auto-links:false;highlight: [${commentedLinesList}]; " style="display:none; overflow-y: hidden;" >
        <c:out value="${model.text}" escapeXml="true" /></pre>
    <code id="${model.id}_pasteTextPlain" style="display:none;"><c:out value="${model.text}" escapeXml="true" /></code>

    </div>


    <c:if test="${shareIntegration}">

        <div id="${model.id}_rightPanel" class="column grid-4" style="min-width:150px;" >

        <iframe id="${model.id}_shareFrame" src="${shareUrl}/main/file/integrated/list/paste_${model.id}"
                scrolling="auto" frameborder="0"
                style="width:340px;height:1000px; "  allowTransparency="true"   >

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

    <div id="${model.id}_commentsList" style="display:none;">

        <c:forEach var="comment" items="${model.comments}" varStatus="loopStatus">

            <div id="${model.id}_numSpace_l${comment.id}" class="listSpace" >
            </div>

            <div id="${model.id}_comment_l${comment.id}" commentId="${comment.id}"
                 lineNumber="${comment.lineNumber}"  parentCommentId="${comment.parentId}" class="commentBlock" >
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


 <div id="${model.id}_commentForm" class="editForm p-comment"  style="display:none;" >

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



    
</div>
