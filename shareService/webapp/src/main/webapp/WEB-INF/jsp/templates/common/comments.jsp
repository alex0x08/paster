<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<div class="row" style="padding-top: 1em;">

    <div class="span6 offset1">

        <c:forEach var="comment" items="${model.comments}">

            <div class="well">

                <div class="row-fluid">
                    <c:out value="${comment.message}"/>    
                </div>

                <div class="row-fluid">
                    <div class=" pull-right">
                        <img  src="<c:out value='http://www.gravatar.com/avatar/${comment.author.avatarHash}?s=32'/>"/>
                        <c:out value="${comment.author.name}"/>

                        <kc:prettyTime var="" date="${comment.created}" locale="${pageContext.response.locale}"/>

                    </div>

                </div>

            </div>

        </c:forEach>
    </div>  
</div>  

<c:if test="${not empty param.addComment}">

    <div class="row">  
    <div class="span12 offset1" >

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

