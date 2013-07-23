<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


 <h1>Users</h1>


<a href="<c:url value='/main/user/new'/>">Create new user</a>

<div>
   <c:forEach var="user" items="${items}" varStatus="status">
       <div>
           <span style="background-color: darksalmon;" ><c:out value="${user.id}"/></span>
                 
           <span>
           <a href="<c:url value="/main/user/edit/${user.id}"/>"><c:out value="${user.name}"/></a>
     
           </span>
           
               
       </div>
   </c:forEach>
 </div>
