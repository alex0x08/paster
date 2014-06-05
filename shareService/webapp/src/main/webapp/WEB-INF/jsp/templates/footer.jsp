<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:if test="${not empty usersOnline}">
    <div class="alert alert-info pull-left ">
        <a class="close" data-dismiss="alert" href="#">×</a>
        <h4 class="alert-heading">Online:</h4>
        <c:forEach var="user" items="${usersOnline}">
            
             <c:set var="usermodel" value="${user}" scope="request"></c:set>
                   <jsp:include page="/WEB-INF/jsp/templates/common/user-dropdown.jsp" >
                       <jsp:param name="mode" value="USER"/>
                   </jsp:include>
           
        </c:forEach>
    </div>
</c:if>


<blockquote class="pull-right">
    
    <sec:authorize access="isAuthenticated()">
        
    <p>
        <c:if test="${currentUser.relatedProject.avatarSet}">
                    <img src="data:image/png;base64,${currentUser.relatedProject.avatar.icon}" 
                         alt="<c:out value='${currentUser.relatedProject.name}'/>" title="<c:out value='${currentUser.relatedProject.description}'/>" />                
            </c:if>
         <c:out value="${currentUser.relatedProject.description}"/>
    </p>
    </sec:authorize>
    
  <p title="<c:out value="${currentSettings.appVersion.implVersionFull}"/>">
    <c:out value="${currentSettings.appVersion.implVersion}"/>
  </p>
  <small>2013. (c) Alex</small>
</blockquote>

            

