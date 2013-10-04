<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<div class="row" style="padding-top: 1em;">

    <div  class="col-md-8">

        <c:if test="${empty model.comments}">
        <div class="well">
            <fmt:message key="comments.not-found"/>
        </div>
        </c:if>
        <c:forEach var="comment" items="${model.comments}">

            <div class="well">

                <div class="row">
                    <c:out escapeXml="false" value="${comment.message}" />    
                </div>

                <div class="row">
                    
                    <div class=" pull-right">
                        <img  src="<c:out value='http://www.gravatar.com/avatar/${comment.author.avatarHash}?s=32'/>"/>
                        <c:out value="${comment.author.name}"/>

                        <kc:prettyTime var="" date="${comment.created}" locale="${pageContext.response.locale}"/>

                        <c:if test="${comment.author eq currentUser or currentUser.admin}">
                          
                            <a class="btn-mini alert-danger" href="<c:url value='/main/file/deleteComment'>
                                     <c:param name="modelId" value="${model.id}"/>
                                     <c:param name="commentId" value="${comment.id}"/></c:url>">X</a>
          
                          </c:if>
                          
                    </div>

                </div>

            </div>

        </c:forEach>
    </div>  
</div>  

<c:if test="${not empty param.addComment}">

    <div class="row">  
    <div class="col-xs-4 col-md-6" >

        <c:url var="commentUrl" value='/main/${param.modelName}/addComment' />

        <form:form action="${commentUrl}" modelAttribute="newComment" 
                   method="POST" id="add_comment" cssClass="form-vertical"
                   enctype="multipart/form-data">
            <fieldset>

                <div class="control-group" >

                    <form:errors path="*" cssClass="errorblock" element="div"/>

                    <input type="hidden" name="modelId" value="${model.id}"/>

                    <form:label cssClass="control-label" path="message"><fmt:message key="comment.message"/>:</form:label>

                        <div class="controls">
                        <form:textarea path="message" id="commentMessage" cssStyle="width:30em;" cols="20" rows="3"/>
                        <p class="help-block"> <form:errors path="message" cssClass="error" /></p>
                    </div>
                </div>

                <div class="form-buttons">

                    <input name="add" type="submit" class="btn btn-primary"
                           value="<fmt:message key="button.add"/>" />
                </div>
            </fieldset>
        </form:form>

    </div>          

</div>
   
</c:if>

