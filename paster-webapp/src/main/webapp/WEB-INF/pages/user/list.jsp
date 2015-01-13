<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class="row">
    <div class="column grid-10">
        
        <h4 class="f-h4">
            <fmt:message key="user.list.title"/> 
            <a href="<c:url value='/main/user/new'/>" title="<fmt:message key="user.create.new"/>">
                <span style="font-size: larger;" class="i">+</span>
            </a>
        </h4>
    </div>
    
    <div class="column grid-10">
  
         <c:forEach var="user" items="${items}" varStatus="status">
       <div>
           <span style="background-color: darksalmon;" ><c:out value="${user.id}"/></span>
                 
           <span>
           <a href="<c:url value="/main/user/edit/${user.id}"/>"><c:out value="${user.name}"/></a>
     
           </span>
           
               
       </div>
   </c:forEach>
        
    </div>
    
    
</div>

