<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:if test="${not empty usersOnline}">
    <div class="alert alert-info pull-left ">
        <a class="close" data-dismiss="alert" href="#">×</a>
        <h4 class="alert-heading">Online:</h4>
        <c:forEach var="user" items="${usersOnline}">

            <img  src="<c:out value='http://www.gravatar.com/avatar/${user.avatarHash}?s=16'/>"/>
            <span><c:out value="${user.name}"/></span>

        </c:forEach>
    </div>
</c:if>


<blockquote class="pull-right">
    <p>
        <c:if test="${currentUser.relatedProject.avatarSet}">
                    <img src="data:image/png;base64,${currentUser.relatedProject.avatar.icon}" 
                         alt="<c:out value='${currentUser.relatedProject.name}'/>" title="<c:out value='${currentUser.relatedProject.description}'/>" />                
            </c:if>
         <c:out value="${currentUser.relatedProject.description}"/>
    </p>  
    
  <p title="<c:out value="${currentSettings.appVersion.implVersionFull}"/>">
    <c:out value="${currentSettings.appVersion.implVersion}"/>
  </p>
  <small>2013. (c) Alex</small>
</blockquote>

            

