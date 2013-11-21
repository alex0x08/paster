<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<h1><fmt:message key="users.title"/></h1>

<c:url var="url" value='/main/user/list' />



<div class="row" >                

     <div class="col-md-10"  > 
        <c:if test="${pageItems.pageCount > 1}">  
            <jsp:include
                page="/WEB-INF/jsp/templates/common/pagination.jsp">
                <jsp:param name="modelName" value="user" />
            </jsp:include>
        </c:if>
    </div>
    
    
    <div class="col-md-2">  
        <jsp:include
            page="/WEB-INF/jsp/templates/common/page-size.jsp">
            <jsp:param name="modelName" value="user" />
        </jsp:include>

    </div>
    
</div>
<br/>
<div class="row">
    
    <table class="table table-striped table-bordered" >
    <tr style="">
        <th style="width:5%" >
            <button type="button" class="btn btn-default sortColumnKey" sortColumn="id">ID  
                <span class="glyphicon"></span></button>
        </th>
        <th style="width:10%" >
              <button type="button" class="btn btn-default sortColumnKey" sortColumn="login">
                <fmt:message key="user.login"/>
                <span class="glyphicon"></span></button>
            </th>
        <th style="width:20%;">
            <button type="button" class="btn btn-default sortColumnKey" sortColumn="name">
                <fmt:message key="user.name"/>
                <span class="glyphicon"></span></button>
        </th>
        <th style="width:10%;"><fmt:message key="user.roles"/></th>
        <th style="width:10%;">
            <button type="button" class="btn btn-default sortColumnKey" sortColumn="lastModified">
                <fmt:message key="struct.lastModified"/>
                <span class="glyphicon"></span></button>
        </th>
        <th style="width:1%;"></th>
    </tr>
    <c:forEach var="user" items="${items}" varStatus="status">
        <tr >
            <td>
                <a href="<c:url value="/main/user/edit">
                       <c:param name="id" value="${user.id}"/>
                   </c:url>">${user.id}</a> 
            </td> 

            <td>
                <a href="<c:url value="/main/user/edit">
                       <c:param name="id" value="${user.id}"/>
                   </c:url>">${user.login}</a> 
            </td> 

            <td>
                
                    <c:set var="usermodel" value="${user}" scope="request"></c:set>
        <jsp:include page="/WEB-INF/jsp/templates/common/user-avatar.jsp" >
            <jsp:param name="size" value="small" />
        </jsp:include>
                
                <a href="<c:url value="/main/user/edit">
                       <c:param name="id" value="${user.id}"/>
                   </c:url>">${user.name}</a> 
            </td>
            <td>
                <c:forEach items="${user.roles}" var="role">
                    <fmt:message key="${role.desc}"/>

                </c:forEach>
            </td>
            <td>
                <fmt:formatDate value="${user.lastModified}" pattern="${dateTimePattern}"/>
            </td>

            <td>
                <a class="fileDeleteBtn"
                   targetTitle="<c:out value='${user.name}'/>
                   &nbsp;  
                   <b><c:forEach items="${user.roles}" var="role">
                       <fmt:message key="${role.desc}"/>
                   </c:forEach>
                   </b> &nbsp; 
                   <fmt:formatDate value="${user.lastModified}" pattern="${dateTimePattern}"/>"
                   title="<fmt:message key="button.delete"/>"  
                   deleteLink="<c:url value="/main/user/delete">
                       <c:param name="id" value="${user.id}"/>
                   </c:url>">
                    <span class="glyphicon glyphicon-remove" title="<fmt:message key="button.delete"/>"></span>
                </a>   
            </td>
        </tr>
    </c:forEach>
</table>
        
        </div>

      <jsp:include
            page="/WEB-INF/jsp/templates/common/sort-table.jsp">
            <jsp:param name="modelName" value="user" />
        </jsp:include>          
                
        